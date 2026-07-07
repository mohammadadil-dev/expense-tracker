package com.expensetracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitExpenseDao {
    @Query("SELECT * FROM split_expenses WHERE groupId = :groupId ORDER BY date DESC, id DESC")
    fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpenseEntity>>

    @Query("SELECT * FROM split_expenses WHERE groupId = :groupId ORDER BY date DESC, id DESC")
    suspend fun getExpensesSnapshot(groupId: Long): List<SplitExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: SplitExpenseEntity): Long

    @Delete
    suspend fun delete(expense: SplitExpenseEntity)

    @Query("DELETE FROM split_expenses WHERE groupId = :groupId")
    suspend fun deleteAllForGroup(groupId: Long)
}
