package com.expensetracker.app.util

/**
 * Billing cadence for a recurring expense / subscription. Stored as a plain string in
 * [com.expensetracker.app.data.ExpenseEntity.recurringPeriod] (no schema change — the column
 * already existed) and used by the recurring-expense generator to decide how many months to
 * wait between auto-created copies.
 */
object RecurringPeriod {
    const val MONTHLY = "MONTHLY"
    const val QUARTERLY = "QUARTERLY"       // every 3 months
    const val HALF_YEARLY = "HALF_YEARLY"   // every 6 months
    const val YEARLY = "YEARLY"             // every 12 months

    /** Selectable cadences, in the order they should appear in the picker. */
    val ALL = listOf(MONTHLY, QUARTERLY, HALF_YEARLY, YEARLY)

    /** Months between occurrences. Unknown/null (older data) falls back to monthly. */
    fun months(period: String?): Int = when (period) {
        QUARTERLY -> 3
        HALF_YEARLY -> 6
        YEARLY -> 12
        else -> 1
    }
}
