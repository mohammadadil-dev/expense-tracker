package com.expensetracker.app.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

/** A flat or member as entered in the editor (no id yet — bills are created fresh, not edited).
 *  [personCount] is 1 in member mode; in flat mode it's how many people live in the flat. */
data class SharedBillMemberDraft(
    val name: String,
    val isMe: Boolean,
    val daysAbsent: Int,
    val personCount: Int = 1,
    val included: Boolean = true
)

/**
 * Backs the Shared Bills feature — utility bills prorated between flatmates by person-days.
 * Fully local/offline. Each bill is a standalone record with its own member list; there is no
 * reusable "flat" to maintain.
 */
class SharedBillRepository(private val db: AppDatabase) {

    private val dao = db.sharedBillDao()

    // ── Groups ────────────────────────────────────────────────────────────────
    val allGroups: Flow<List<SharedBillGroupEntity>> = dao.allGroups()

    fun billsForGroup(groupId: Long): Flow<List<SharedBillEntity>> = dao.billsForGroup(groupId)

    suspend fun groupById(id: Long): SharedBillGroupEntity? = dao.groupById(id)

    fun groupMembers(groupId: Long): Flow<List<SharedBillGroupMemberEntity>> = dao.groupMembers(groupId)

    /** Creates a group with its template member list in one transaction. */
    suspend fun createGroup(
        name: String,
        emoji: String,
        splitMode: Int,
        members: List<SharedBillMemberDraft>
    ): Long = db.withTransaction {
        val groupId = dao.insertGroup(SharedBillGroupEntity(name = name, emoji = emoji, splitMode = splitMode))
        members.forEach { m ->
            dao.insertGroupMember(
                SharedBillGroupMemberEntity(
                    groupId = groupId,
                    name = m.name,
                    personCount = m.personCount.coerceAtLeast(1),
                    isMe = m.isMe
                )
            )
        }
        groupId
    }

    // Group member editing (someone joins/leaves the group).
    suspend fun addGroupMember(groupId: Long, name: String, personCount: Int, isMe: Boolean = false) =
        dao.insertGroupMember(SharedBillGroupMemberEntity(groupId = groupId, name = name, personCount = personCount.coerceAtLeast(1), isMe = isMe))

    suspend fun updateGroupMember(member: SharedBillGroupMemberEntity) = dao.updateGroupMember(member)

    suspend fun deleteGroupMember(member: SharedBillGroupMemberEntity) = dao.deleteGroupMember(member)

    /** Deletes a group and everything under it — members, every bill, and any logged shares. */
    suspend fun deleteGroup(group: SharedBillGroupEntity) = db.withTransaction {
        dao.billsForGroupOnce(group.id).forEach { bill ->
            bill.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
            dao.deleteMembersForBill(bill.id)
            dao.deleteBill(bill)
        }
        dao.deleteGroupMembersForGroup(group.id)
        dao.deleteGroup(group)
    }

    // ── Bills ───────────────────────────────────────────────────────────────
    fun membersForBill(billId: Long): Flow<List<SharedBillMemberEntity>> = dao.membersForBill(billId)

    suspend fun billById(id: Long): SharedBillEntity? = dao.billById(id)

    /** Creates a bill and its members in one transaction; returns the new bill id. */
    suspend fun createBill(
        groupId: Long,
        label: String,
        note: String,
        periodStart: String,
        periodEnd: String,
        total: Double,
        members: List<SharedBillMemberDraft>
    ): Long = db.withTransaction {
        val billId = dao.insertBill(
            SharedBillEntity(
                groupId = groupId,
                label = label,
                note = note,
                periodStart = periodStart,
                periodEnd = periodEnd,
                totalAmount = total
            )
        )
        members.forEach { m ->
            dao.insertMember(
                SharedBillMemberEntity(
                    billId = billId,
                    name = m.name,
                    personCount = m.personCount.coerceAtLeast(1),
                    isMe = m.isMe,
                    daysAbsent = m.daysAbsent,
                    included = m.included
                )
            )
        }
        billId
    }

    suspend fun setMemberPaid(memberId: Long, paid: Boolean) = dao.setMemberPaid(memberId, paid)

    /** Deletes the bill, its members, and any expense that was logged for the owner's share. */
    suspend fun deleteBill(bill: SharedBillEntity) = db.withTransaction {
        bill.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
        dao.deleteMembersForBill(bill.id)
        dao.deleteBill(bill)
    }

    /**
     * Logs the owner's own share as a real Utilities expense (so their personal budget stays
     * accurate) and links it back to the bill. Idempotent-ish: if a share was already logged,
     * the old expense is replaced. Returns true on success.
     */
    suspend fun logMyShare(bill: SharedBillEntity, amount: Double, date: String): Boolean = db.withTransaction {
        val catId = db.categoryDao().getAllOnce()
            .firstOrNull { it.nameKey == "cat_utilities" }?.id
            ?: db.categoryDao().getAllOnce().firstOrNull()?.id
            ?: return@withTransaction false
        // Replace any previously-logged share for this bill.
        bill.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
        val expenseId = db.expenseDao().insert(
            ExpenseEntity(
                categoryId = catId,
                description = bill.label,
                amount = amount,
                date = date,
                monthKey = date.substring(0, 7)
            )
        )
        dao.updateBill(bill.copy(linkedExpenseId = expenseId))
        true
    }
}
