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
 *
 * [recurringDayOfMonth] (template rows only) is the day of the month this recurs on —
 * used by [ExpenseRepository.createRecurringExpensesForCurrentMonth] to date each
 * auto-generated copy accurately (e.g. a subscription that renews on the 15th) instead
 * of always defaulting to the 1st. Clamped to the shorter month's last day when needed
 * (e.g. day 31 in a 30-day month). Null on pre-existing templates from before this field
 * was added — treated as day 1, matching their original behavior exactly.
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
    val recurringDayOfMonth: Int? = null,  // template rows only — see class doc
    // Family / Couple Mode — null when family mode is off or expense is "shared household".
    val memberId: Long? = null
)
