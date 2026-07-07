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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shares: List<SplitExpenseShareEntity>)

    @Query("DELETE FROM split_expense_shares WHERE expenseId = :expenseId")
    suspend fun deleteForExpense(expenseId: Long)

    @Query("""
        DELETE FROM split_expense_shares
        WHERE expenseId IN (SELECT id FROM split_expenses WHERE groupId = :groupId)
    """)
    suspend fun deleteAllForGroup(groupId: Long)
}
