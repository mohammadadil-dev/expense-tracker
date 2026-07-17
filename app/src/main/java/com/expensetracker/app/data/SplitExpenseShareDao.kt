package com.expensetracker.app.data

import androidx.room.*

@Dao
interface SplitExpenseShareDao {
    @Query("SELECT * FROM split_expense_shares WHERE expenseId = :expenseId")
    suspend fun getSharesForExpense(expenseId: Long): List<SplitExpenseShareEntity>

    @Query("""
        SELECT * FROM split_expense_shares
        WHERE expenseId IN (SELECT id FROM split_expenses WHERE groupId = :groupId)
    """)
    suspend fun getAllSharesForGroup(groupId: Long): List<SplitExpenseShareEntity>

    /** Unscoped snapshot of every share row across every expense — used by BackupManager's
     *  full-database export. */
    @Query("SELECT * FROM split_expense_shares")
    suspend fun getAllOnce(): List<SplitExpenseShareEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shares: List<SplitExpenseShareEntity>)

    @Query("DELETE FROM split_expense_shares WHERE expenseId = :expenseId")
    suspend fun deleteForExpense(expenseId: Long)

    /** Deletes every share row belonging to [memberId] regardless of which expense/group it's
     *  on — used when a member is removed from a group so their old share rows don't survive
     *  as dangling references (see SplitRepository.deleteMember). */
    @Query("DELETE FROM split_expense_shares WHERE memberId = :memberId")
    suspend fun deleteForMember(memberId: Long)

    @Query("""
        DELETE FROM split_expense_shares
        WHERE expenseId IN (SELECT id FROM split_expenses WHERE groupId = :groupId)
    """)
    suspend fun deleteAllForGroup(groupId: Long)
}
