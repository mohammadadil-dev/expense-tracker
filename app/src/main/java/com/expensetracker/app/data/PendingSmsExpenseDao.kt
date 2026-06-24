package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingSmsExpenseDao {

    @Query("SELECT * FROM pending_sms_expenses ORDER BY receivedAt DESC")
    fun observeAll(): Flow<List<PendingSmsExpense>>

    @Insert
    suspend fun insert(item: PendingSmsExpense): Long

    @Delete
    suspend fun delete(item: PendingSmsExpense)

    @Query("DELETE FROM pending_sms_expenses")
    suspend fun deleteAll()
}
