package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A transaction the SMS parser pulled out of an incoming bank/UPI/card alert the user has
 * already approved via the system SMS-consent prompt. It is never written into [ExpenseEntity]
 * automatically — it just sits here until the user reviews it from the Dashboard's pending-SMS
 * banner and either accepts it (turning it into a real expense) or dismisses it.
 *
 * [suggestedCategoryId] is a best-effort keyword guess and may be null if nothing matched;
 * the review screen falls back to the first available category in that case.
 */
@Entity(tableName = "pending_sms_expenses")
data class PendingSmsExpense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawMessage: String,
    val amount: Double,
    val description: String,
    val suggestedCategoryId: Long?,
    val date: String,
    val receivedAt: Long
)
