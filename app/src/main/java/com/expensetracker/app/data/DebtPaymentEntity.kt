package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One payment recorded against a [DebtEntity] — a partial or full repayment when
 * [DebtEntity.direction] is [DebtEntity.DIRECTION_OWE], or a partial/full collection when it's
 * [DebtEntity.DIRECTION_OWED]. A debt's current outstanding balance is always
 * `principal - sum(amount)` over its rows here (see [DebtEntity] doc) rather than a separately
 * stored running total, so it can never drift out of sync with the actual payment history.
 */
@Entity(tableName = "debt_payments", indices = [Index("debtId")])
data class DebtPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val debtId: Long,
    val amount: Double,
    val date: String,
    val note: String? = null,
    /**
     * The [ExpenseEntity] this payment auto-generated, if any — only set when the parent debt's
     * [DebtEntity.direction] is [DebtEntity.DIRECTION_OWE], since that's the only case where a
     * payment is real money leaving the user (and so should count toward monthly spend/budget).
     * Collections on [DebtEntity.DIRECTION_OWED] debts are money coming in, not an expense, so
     * this stays null for those. Kept in sync by [ExpenseRepository] on insert/delete so the
     * linked expense never outlives or duplicates its source payment.
     */
    val linkedExpenseId: Long? = null
)
