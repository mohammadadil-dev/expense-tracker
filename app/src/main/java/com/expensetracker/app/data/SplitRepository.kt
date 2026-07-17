package com.expensetracker.app.data

import com.expensetracker.app.util.Settlement
import com.expensetracker.app.util.SplitMath
import com.expensetracker.app.util.SplitSettlementCalculator
import kotlinx.coroutines.flow.Flow

/**
 * Takes the full [AppDatabase] (not just its own four DAOs) so it can also write to the
 * expenses table: a non-settlement expense the device owner ("me") participates in
 * auto-generates an [ExpenseEntity] row for *my own share* of the cost — the same
 * linked-expense pattern [KhataRepository] and debt payments already use — so real money
 * spent inside a group split actually counts toward the Dashboard/budget instead of being
 * invisible to them. Settlement rows never link (they just move already-counted money
 * between members, not new spend).
 */
/**
 * One line item on an itemized split expense — see [SplitExpenseItemEntity]. [memberIds] are
 * the members who shared this specific item (its [amount] splits equally among them).
 */
data class ItemDraft(val name: String, val amount: Double, val memberIds: List<Long>)

class SplitRepository(
    private val groupDao: SplitGroupDao,
    private val memberDao: SplitMemberDao,
    private val expenseDao: SplitExpenseDao,
    private val shareDao: SplitExpenseShareDao,
    private val db: AppDatabase,
    private val itemDao: SplitExpenseItemDao = db.splitExpenseItemDao()
) {

    // ── Groups ───────────────────────────────────────────────────────────────

    val allGroups: Flow<List<SplitGroupEntity>> = groupDao.getAllGroups()

    suspend fun createGroup(name: String, emoji: String, createdDate: String): Long =
        groupDao.insert(SplitGroupEntity(name = name, emoji = emoji, createdDate = createdDate))

    suspend fun updateGroup(group: SplitGroupEntity) = groupDao.update(group)

    suspend fun deleteGroup(group: SplitGroupEntity) {
        // Clean up any linked personal expenses before the cascade removes the expense rows —
        // otherwise the linkedExpenseId references are lost and phantom spend lingers, same
        // reasoning as KhataRepository.deleteParty.
        expenseDao.getExpensesSnapshot(group.id).forEach { expense ->
            expense.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
        }
        // Cascade order: item members → items → shares → expenses → members → group
        itemDao.deleteAllItemMembersForGroup(group.id)
        itemDao.deleteAllItemsForGroup(group.id)
        shareDao.deleteAllForGroup(group.id)
        expenseDao.deleteAllForGroup(group.id)
        memberDao.deleteAllForGroup(group.id)
        groupDao.delete(group)
    }

    // ── Members ──────────────────────────────────────────────────────────────

    fun getMembersForGroup(groupId: Long): Flow<List<SplitMemberEntity>> =
        memberDao.getMembersForGroup(groupId)

    suspend fun addMember(groupId: Long, name: String, colorHex: String, emoji: String = "", isMe: Boolean = false): Long =
        memberDao.insert(SplitMemberEntity(groupId = groupId, name = name, colorHex = colorHex, emoji = emoji, isMe = isMe))

    suspend fun updateMember(member: SplitMemberEntity) = memberDao.update(member)

    suspend fun deleteMember(member: SplitMemberEntity) = memberDao.delete(member)

    /** Sets (or clears, if blank) a member's UPI ID — used by the Settle Up sheet's inline
     * "add their UPI ID" prompt so a settlement can be paid directly instead of only ever
     * recorded via "Mark Paid". */
    suspend fun setMemberUpiId(member: SplitMemberEntity, upiId: String) {
        memberDao.update(member.copy(upiId = upiId.trim().takeIf { it.isNotBlank() }))
    }

    /** Sets (or clears, if blank) a member's phone number — used by the Settle Up sheet's
     * inline "add their phone number" prompt so an individual settlement can be nudged with
     * its own WhatsApp/SMS reminder instead of only ever sharing one combined group summary. */
    suspend fun setMemberPhone(member: SplitMemberEntity, phone: String) {
        memberDao.update(member.copy(phone = phone.trim().takeIf { it.isNotBlank() }))
    }

    // ── Expenses ─────────────────────────────────────────────────────────────

    fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpenseEntity>> =
        expenseDao.getExpensesForGroup(groupId)

    /**
     * Adds an expense and its per-member shares.
     *
     * [customShares], when supplied, is used verbatim as the exact per-member share amounts
     * (exact-amount or percentage-derived splits — see [com.expensetracker.app.ui.components.AddSplitExpenseSheet]).
     * When null (the default), [splitAmongIds] is divided equally — the original behavior.
     *
     * [items], when supplied (itemized/receipt splitting — see [ItemDraft]), takes priority
     * over [customShares]: the per-member shares are *derived* from the items (each item's
     * amount splits equally among the members assigned to it, summed per member), and the
     * items themselves are persisted alongside the expense so the breakdown can be shown
     * back to the user later.
     *
     * If the device owner ("me") is one of the resulting shares and this isn't a settlement,
     * a personal [ExpenseEntity] is created for *my own share* (not the full amount — if I
     * fronted the bill, the rest is a receivable I'll get back, not spend) and linked via
     * [SplitExpenseEntity.linkedExpenseId].
     */
    suspend fun addExpense(
        groupId: Long,
        description: String,
        amount: Double,
        paidByMemberId: Long,
        splitAmongIds: List<Long>,
        date: String,
        note: String = "",
        isSettlement: Boolean = false,
        customShares: Map<Long, Double>? = null,
        items: List<ItemDraft>? = null
    ) {
        val expenseId = expenseDao.insert(
            SplitExpenseEntity(
                groupId = groupId,
                description = description,
                amount = amount,
                paidByMemberId = paidByMemberId,
                date = date,
                note = note,
                isSettlement = isSettlement
            )
        )

        val sharesFromItems: Map<Long, Double>? = items?.takeIf { it.isNotEmpty() }?.let { itemDrafts ->
            val totals = mutableMapOf<Long, Double>()
            itemDrafts.forEach { item ->
                if (item.memberIds.isNotEmpty()) {
                    // SplitMath.splitEvenly (not item.amount / size) — see its doc for why: raw
                    // division leaves a repeating-decimal remainder that quietly shortchanges
                    // the item's total, and that error compounds across every itemized line.
                    val perMemberValues = SplitMath.splitEvenly(item.amount, item.memberIds.size)
                    item.memberIds.zip(perMemberValues).forEach { (memberId, perMember) ->
                        totals[memberId] = (totals[memberId] ?: 0.0) + perMember
                    }
                }
            }
            totals
        }

        val shares: Map<Long, Double> = sharesFromItems ?: customShares ?: run {
            val effectiveSplitIds = splitAmongIds.ifEmpty { listOf(paidByMemberId) }
            // SplitMath.splitEvenly (not amount / size) — plain division doesn't divide evenly
            // for most bills (SAR 100 / 3 = 33.33 × 3 = 99.99, a cent short of the real total),
            // which left group balances never fully zeroing out. See SplitMath's doc comment.
            val splitValues = SplitMath.splitEvenly(amount, effectiveSplitIds.size)
            effectiveSplitIds.zip(splitValues).toMap()
        }
        shareDao.insertAll(
            shares.map { (memberId, shareAmount) ->
                SplitExpenseShareEntity(expenseId = expenseId, memberId = memberId, shareAmount = shareAmount)
            }
        )

        // Persist the itemized breakdown itself (name + amount + who shared it) so it can be
        // displayed back to the user later — the shares above are what actually drive
        // settlement math; these rows are purely for showing "what was itemized".
        items?.takeIf { it.isNotEmpty() }?.forEach { item ->
            val itemId = itemDao.insertItem(
                SplitExpenseItemEntity(expenseId = expenseId, name = item.name, amount = item.amount)
            )
            if (item.memberIds.isNotEmpty()) {
                itemDao.insertItemMembers(
                    item.memberIds.map { memberId ->
                        SplitExpenseItemMemberEntity(itemId = itemId, memberId = memberId)
                    }
                )
            }
        }

        if (!isSettlement) {
            val meMember = memberDao.getMembersSnapshot(groupId).firstOrNull { it.isMe }
            val myShare = meMember?.let { shares[it.id] }
            if (meMember != null && myShare != null && myShare > 0.0) {
                val categoryId = ensureSplitsCategoryId()
                val monthKey = if (date.length >= 7) date.substring(0, 7) else date
                val linkedExpenseId = db.expenseDao().insert(
                    ExpenseEntity(
                        categoryId = categoryId,
                        description = description,
                        amount = myShare,
                        date = date,
                        monthKey = monthKey
                    )
                )
                expenseDao.update(
                    SplitExpenseEntity(
                        id = expenseId,
                        groupId = groupId,
                        description = description,
                        amount = amount,
                        paidByMemberId = paidByMemberId,
                        date = date,
                        note = note,
                        isSettlement = isSettlement,
                        linkedExpenseId = linkedExpenseId
                    )
                )
            }
        }
    }

    suspend fun deleteExpense(expense: SplitExpenseEntity) {
        shareDao.deleteForExpense(expense.id)
        itemDao.deleteItemMembersForExpense(expense.id)
        itemDao.deleteItemsForExpense(expense.id)
        expenseDao.delete(expense)
        // Remove the phantom personal expense so the Dashboard stays in sync — mirrors
        // KhataRepository.deleteEntry.
        expense.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
    }

    // ── Settlement ───────────────────────────────────────────────────────────

    suspend fun calculateSettlement(groupId: Long): List<Settlement> {
        val members  = memberDao.getMembersSnapshot(groupId)
        val expenses = expenseDao.getExpensesSnapshot(groupId)
        val shares   = shareDao.getAllSharesForGroup(groupId)
        return SplitSettlementCalculator.calculate(members, expenses, shares)
    }

    suspend fun getNetBalances(groupId: Long): Map<Long, Double> {
        val members  = memberDao.getMembersSnapshot(groupId)
        val expenses = expenseDao.getExpensesSnapshot(groupId)
        val shares   = shareDao.getAllSharesForGroup(groupId)
        return SplitSettlementCalculator.netBalances(members, expenses, shares)
    }

    /**
     * Records that [fromMemberId] paid [toMemberId] the given amount to settle their debt.
     * Internally inserts a settlement expense so the balance recalculation automatically
     * removes this debt from the next settlement list. Never linked to a personal expense —
     * see [addExpense]'s `isSettlement` handling.
     */
    suspend fun markSettlementPaid(
        groupId: Long,
        fromMemberId: Long,
        toMemberId: Long,
        amount: Double,
        date: String
    ) = addExpense(
        groupId = groupId,
        description = "Settlement",
        amount = amount,
        paidByMemberId = fromMemberId,
        splitAmongIds = listOf(toMemberId),
        date = date,
        isSettlement = true
    )

    // ── Snapshot helpers ─────────────────────────────────────────────────────

    suspend fun getMembersSnapshot(groupId: Long): List<SplitMemberEntity> =
        memberDao.getMembersSnapshot(groupId)

    suspend fun getExpensesSnapshot(groupId: Long): List<SplitExpenseEntity> =
        expenseDao.getExpensesSnapshot(groupId)

    suspend fun getSharesForGroup(groupId: Long): List<SplitExpenseShareEntity> =
        shareDao.getAllSharesForGroup(groupId)

    /** Itemized breakdown for one expense — empty if it wasn't itemized. */
    suspend fun getItemsForExpense(expenseId: Long): List<SplitExpenseItemEntity> =
        itemDao.getItemsForExpense(expenseId)

    /** Which members share each item on an itemized expense. */
    suspend fun getItemMembersForExpense(expenseId: Long): List<SplitExpenseItemMemberEntity> =
        itemDao.getItemMembersForExpense(expenseId)

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Returns (or creates) the "Splits" category and gives back its DB id. Fresh installs
     * never run MIGRATION_17_18 (Room creates the latest schema directly, with no rows), so
     * this is what actually seeds the category for a new user the first time it's needed. */
    private suspend fun ensureSplitsCategoryId(): Long {
        val existing = db.categoryDao().getAllOnce().firstOrNull { it.nameKey == SPLITS_CATEGORY_KEY }
        if (existing != null) return existing.id
        return db.categoryDao().insert(
            CategoryEntity(nameKey = SPLITS_CATEGORY_KEY, colorHex = SPLITS_CATEGORY_COLOR, sortOrder = 16)
        )
    }

    companion object {
        const val SPLITS_CATEGORY_KEY   = "cat_splits"
        const val SPLITS_CATEGORY_COLOR = "#00BFA5"   // teal — matches the Splits section accent

        val PALETTE = listOf(
            "#4CAF50", "#2196F3", "#E91E63", "#FF9800",
            "#9C27B0", "#00BCD4", "#F44336", "#795548"
        )
    }
}
