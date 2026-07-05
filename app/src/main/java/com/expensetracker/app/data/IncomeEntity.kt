package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single income entry (salary, freelance payment, bonus, etc.) recorded by the user.
 * Income entries are scoped to a monthKey so they show up correctly on the dashboard.
 *
 * When [isRecurring] is true this row is a *template* — it auto-generates a copy at the start
 * of each new month (analogous to how recurring expenses work). The template itself is stored
 * with the month it was first created in; generated copies share [source]/[note]/[amount] but
 * get a fresh [date] and [monthKey].
 */
@Entity(
    tableName = "income_entries",
    indices = [Index("monthKey")]
)
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Positive amount in the user's currency. */
    val amount: Double,
    /** Free-text label: "Salary", "Freelance", "Bonus", etc. */
    val source: String = "",
    /** Optional extra note. */
    val note: String = "",
    /** ISO date YYYY-MM-DD. */
    val date: String,
    /** YYYY-MM derived from [date]. */
    val monthKey: String,
    /**
     * True if this is a recurring income template that should auto-generate a copy at the
     * start of every new month. Default false (one-time entry).
     */
    val isRecurring: Boolean = false
)
