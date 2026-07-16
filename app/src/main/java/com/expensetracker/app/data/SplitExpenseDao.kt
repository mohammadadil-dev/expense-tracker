package com.expensetracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitExpenseDao {
    @Query("SELECT * FROM split_expenses WHERE groupId = :groupId ORDER BY date DESC, id DESC")
    fun getExpensesForGroup(groupId: Long): Flow<List<SplitExpenseEntity>>

    @Query("SELECT * FROM split_expenses WHERE groupId = :groupId ORDER BY date DESC, id DESC")
    suspend fun getExpensesSnapshot(groupId: Long): List<SplitExpenseEntity>

    /** Unscoped snapshot of every expense across every group — used by BackupManager's
     *  full-database export. */
    @Query("SELECT * FROM split_expenses")
    suspend fun getAllOnce(): List<SplitExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: SplitExpenseEntity): Long

    /** Used to stamp [SplitExpenseEntity.linkedExpenseId] onto a row right after inserting it. */
    @Update
    suspend fun update(expense: SplitExpenseEntity)

    @Delete
    suspend fun delete(expense: SplitExpenseEntity)

    @Query("DELETE FROM split_expenses WHERE groupId = :groupId")
    suspend fun deleteAllForGroup(groupId: Long)
}
