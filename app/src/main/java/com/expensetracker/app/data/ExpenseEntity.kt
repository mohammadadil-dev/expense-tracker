package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single expense record.
 *
 * [date] is stored as an ISO string "yyyy-MM-dd" and [monthKey] ("yyyy-MM") is kept as a
 * derived, indexed column purely so month-scoped queries are fast and simple.
 *
 * ## Recurring expenses
 * When [isRecurring] is true, this row acts as a **template**. On every app startup,
 * [ExpenseRepository.createRecurringExpensesForCurrentMonth] scans all templates and
 * auto-inserts a copy for the current month if one doesn't already exist.
 *
 * Auto-created copies have [isRecurring] = false and [recurringSourceId] set to the
 * template's [id]. Normal, non-recurring expenses have both fields at their defaults
 * (false / null).
 *
 * [recurringPeriod] is reserved for future daily/weekly cadences; for now only
 * "MONTHLY" is used.
 */
@Entity(tableName = "expenses", indices = [Index("monthKey"), Index("categoryId")])
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val description: String,
    val amount: Double,
    val date: String,
    val monthKey: String,
    val isRecurring: Boolean = false,
    val recurringPeriod: String? = null,   // "MONTHLY" when isRecurring = true
    val recurringSourceId: Long? = null,   // non-null on auto-created copies
    // Family / Couple Mode — null when family mode is off or expense is "shared household".
    val memberId: Long? = null
)
