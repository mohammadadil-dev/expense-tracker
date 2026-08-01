package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedBillDao {

    // ── Groups (apartments) ──────────────────────────────────────────────────
    @Query("SELECT * FROM shared_bill_groups ORDER BY createdAt DESC")
    fun allGroups(): Flow<List<SharedBillGroupEntity>>

    @Query("SELECT * FROM shared_bill_groups WHERE id = :id LIMIT 1")
    suspend fun groupById(id: Long): SharedBillGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: SharedBillGroupEntity): Long

    @Update
    suspend fun updateGroup(group: SharedBillGroupEntity)

    @Delete
    suspend fun deleteGroup(group: SharedBillGroupEntity)

    // ── Group members (template) ──────────────────────────────────────────────
    @Query("SELECT * FROM shared_bill_group_members WHERE groupId = :groupId ORDER BY id ASC")
    fun groupMembers(groupId: Long): Flow<List<SharedBillGroupMemberEntity>>

    @Query("SELECT * FROM shared_bill_group_members WHERE groupId = :groupId ORDER BY id ASC")
    suspend fun groupMembersOnce(groupId: Long): List<SharedBillGroupMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(member: SharedBillGroupMemberEntity): Long

    @Update
    suspend fun updateGroupMember(member: SharedBillGroupMemberEntity)

    @Delete
    suspend fun deleteGroupMember(member: SharedBillGroupMemberEntity)

    @Query("DELETE FROM shared_bill_group_members WHERE groupId = :groupId")
    suspend fun deleteGroupMembersForGroup(groupId: Long)

    // ── Bills ───────────────────────────────────────────────────────────────
    @Query("SELECT * FROM shared_bills WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun billsForGroup(groupId: Long): Flow<List<SharedBillEntity>>

    @Query("SELECT * FROM shared_bills WHERE groupId = :groupId ORDER BY createdAt DESC")
    suspend fun billsForGroupOnce(groupId: Long): List<SharedBillEntity>

    @Query("SELECT * FROM shared_bills ORDER BY createdAt DESC")
    fun allBills(): Flow<List<SharedBillEntity>>

    @Query("SELECT * FROM shared_bills WHERE id = :id LIMIT 1")
    suspend fun billById(id: Long): SharedBillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: SharedBillEntity): Long

    @Update
    suspend fun updateBill(bill: SharedBillEntity)

    @Delete
    suspend fun deleteBill(bill: SharedBillEntity)

    // ── Members ─────────────────────────────────────────────────────────────
    @Query("SELECT * FROM shared_bill_members WHERE billId = :billId ORDER BY id ASC")
    fun membersForBill(billId: Long): Flow<List<SharedBillMemberEntity>>

    @Query("SELECT * FROM shared_bill_members WHERE billId = :billId ORDER BY id ASC")
    suspend fun membersForBillOnce(billId: Long): List<SharedBillMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: SharedBillMemberEntity): Long

    @Update
    suspend fun updateMember(member: SharedBillMemberEntity)

    @Query("UPDATE shared_bill_members SET paid = :paid WHERE id = :memberId")
    suspend fun setMemberPaid(memberId: Long, paid: Boolean)

    @Query("DELETE FROM shared_bill_members WHERE billId = :billId")
    suspend fun deleteMembersForBill(billId: Long)
}
