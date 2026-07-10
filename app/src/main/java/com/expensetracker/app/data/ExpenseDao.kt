package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE monthKey = :monthKey ORDER BY date DESC, id DESC")
    fun observeByMonth(monthKey: String): Flow<List<ExpenseEntity>>

    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    /** Deletes by id directly — used to clean up an auto-generated expense linked to a debt
     * payment without needing to load the full row first. */
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE categoryId = :categoryId)")
    suspend fun categoryInUse(categoryId: Long): Boolean

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    suspend fun getAllOnce(): List<ExpenseEntity>

    @Query("UPDATE expenses SET categoryId = :newCategoryId WHERE categoryId = :oldCategoryId")
    suspend fun reassignCategory(oldCategoryId: Long, newCategoryId: Long)

    @Query("UPDATE expenses SET amount = amount * :rate")
    suspend fun scaleAllAmounts(rate: Double)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    // ── Widget data helpers ──────────────────────────────────────────────────

    /** Total spend for a single ISO date (YYYY-MM-DD) — used by the home-screen widget. */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE date = :date AND isRecurring = 0")
    suspend fun sumForDay(date: String): Double

    /** Total spend for a month (YYYY-MM) — used by the home-screen widget. */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE monthKey = :monthKey AND isRecurring = 0")
    suspend fun sumForMonth(monthKey: String): Double

    // ── Recurring expense helpers ────────────────────────────────────────────

    /** All template rows — expenses the user has marked to repeat every month. */
    @Query("SELECT * FROM expenses WHERE isRecurring = 1")
    suspend fun recurringTemplatesOnce(): List<ExpenseEntity>

    /**
     * How many auto-generated copies of [sourceId] already exist for [monthKey].
     * Used by the scheduler to avoid duplicates on successive app launches.
     */
    @Query("SELECT COUNT(*) FROM expenses WHERE recurringSourceId = :sourceId AND monthKey = :monthKey")
    suspend fun countRecurringInstance(sourceId: Long, monthKey: String): Int

    /** Total number of expenses ever logged (all months). Used to time the one-shot
     * in-app review prompt — see [com.expensetracker.app.util.InAppReviewManager]. */
    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun count(): Int
}
