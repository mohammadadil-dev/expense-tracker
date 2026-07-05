package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM income_entries WHERE monthKey = :monthKey ORDER BY date DESC, id DESC")
    fun observeForMonth(monthKey: String): Flow<List<IncomeEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM income_entries WHERE monthKey = :monthKey")
    fun totalForMonth(monthKey: String): Flow<Double>

    @Query("SELECT * FROM income_entries ORDER BY date DESC, id DESC")
    suspend fun getAllOnce(): List<IncomeEntity>

    /** All recurring templates (isRecurring = 1) regardless of month. */
    @Query("SELECT * FROM income_entries WHERE isRecurring = 1")
    suspend fun getRecurringTemplates(): List<IncomeEntity>

    /**
     * Count how many income entries with the given [source] and [amount] already exist for
     * [monthKey]. Used to avoid generating duplicate recurring copies when the app opens
     * multiple times in the same month.
     */
    @Query(
        "SELECT COUNT(*) FROM income_entries " +
        "WHERE monthKey = :monthKey AND source = :source AND amount = :amount AND isRecurring = 0"
    )
    suspend fun countMonthlyInstance(monthKey: String, source: String, amount: Double): Int

    @Insert
    suspend fun insert(income: IncomeEntity): Long

    @Delete
    suspend fun delete(income: IncomeEntity)

    @Query("DELETE FROM income_entries")
    suspend fun deleteAll()
}
