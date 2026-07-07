package com.expensetracker.app.data

import com.expensetracker.app.util.Settlement
import com.expensetracker.app.util.SplitSettlementCalculator
import kotlinx.coroutines.flow.Flow

class SplitRepository(
    private val groupDao: SplitGroupDao,
    private val memberDao: SplitMemberDao,
    private val expenseDao: SplitExpenseDao,
    private val shareDao: SplitExpenseShareDao
) {

    // ── Groups ───────────────────────────────────────────────────────────────

    val allGroups: Flow<List<SplitGroupEntity>> = groupDao.getAllGroups()

    suspend fun createGroup(name: String, emoji: String, createdDate: String): Long =
        groupDao.insert(SplitGroupEntity(name = name, emoji = emoji, createdDate = createdDate))

    suspend fun updateGroup(group: SplitGroupEntity) = groupDao.update(group)

    suspend fun deleteGroup(group: SplitGroupEntity) {
        // Cascade order: shares → expenses → members → group
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

    // ── Expenses ─────────────────────────────────────────────────────────────

    fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpenseEntity>> =
        expenseDao.getExpensesForGroup(groupId)

    /**
     * Adds an expense and its per-member shares.
     * [splitAmongIds] is the list of member IDs who share this expense equally.
     * If empty, the full amount falls on the payer (edge case — usually at least 2 members).
     */
    suspend fun addExpense(
        groupId: Long,
        description: String,
        amount: Double,
        paidByMemberId: Long,
        splitAmongIds: List<Long>,
        date: String,
        note: String = "",
        isSettlement: Boolean = false
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
        val effectiveSplitIds = splitAmongIds.ifEmpty { listOf(paidByMemberId) }
        val shareAmount = amount / effectiveSplitIds.size
        shareDao.insertAll(
            effectiveSplitIds.map { memberId ->
                SplitExpenseShareEntity(expenseId = expenseId, memberId = memberId, shareAmount = shareAmount)
            }
        )
    }

    suspend fun deleteExpense(expense: SplitExpenseEntity) {
        shareDao.deleteForExpense(expense.id)
        expenseDao.delete(expense)
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
     * removes this debt from the next settlement list.
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

    companion object {
        val PALETTE = listOf(
            "#4CAF50", "#2196F3", "#E91E63", "#FF9800",
            "#9C27B0", "#00BCD4", "#F44336", "#795548"
        )
    }
}
