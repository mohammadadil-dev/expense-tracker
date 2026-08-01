package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtPaymentDao {

    @Query("SELECT * FROM debt_payments ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<DebtPaymentEntity>>

    @Query("SELECT * FROM debt_payments ORDER BY date DESC, id DESC")
    suspend fun getAllOnce(): List<DebtPaymentEntity>

    @Query("SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY date DESC, id DESC")
    suspend fun getForDebt(debtId: Long): List<DebtPaymentEntity>

    /** Sum of all payments recorded against [debtId] — used to derive the outstanding balance. */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM debt_payments WHERE debtId = :debtId")
    suspend fun totalPaidFor(debtId: Long): Double

    @Insert
    suspend fun insert(payment: DebtPaymentEntity): Long

    @Delete
    suspend fun delete(payment: DebtPaymentEntity)

    @Query("DELETE FROM debt_payments WHERE debtId = :debtId")
    suspend fun deleteForDebt(debtId: Long)

    @Query("DELETE FROM debt_payments")
    suspend fun deleteAll()

    /** Rescales every debt payment when the user converts currency. */
    @Query("UPDATE debt_payments SET amount = amount * :rate")
    suspend fun scaleAllAmounts(rate: Double)
}
