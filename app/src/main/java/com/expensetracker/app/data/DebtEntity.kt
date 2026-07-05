package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One debt or loan being tracked — either money the user owes someone else
 * ([direction] == [DIRECTION_OWE]) or money someone else owes the user
 * ([direction] == [DIRECTION_OWED]). [direction] is stored as a plain string rather than a
 * Kotlin enum + Room TypeConverter, matching this project's existing minimal-entity style
 * (see [CategoryEntity.colorHex]).
 *
 * [principal] is the original loan amount. The *current* outstanding balance is never stored
 * here — it's computed live as `principal - sum(DebtPaymentEntity.amount for this debt)`, the
 * same "standing target vs. computed total" split already used by [BudgetEntity] (persisted
 * target, computed spend).
 *
 * [interestRatePercent] is an annual rate (e.g. 12.0 for 12%/year) — 0 for interest-free debts,
 * which covers most informal IOUs between friends/family. [minimumPayment] is the EMI / minimum
 * monthly payment used to project a payoff date; 0 means "no fixed schedule", and payoff
 * projection is skipped for that debt.
 *
 * [isClosed] is set once the debt is fully paid off (automatically, when a recorded payment
 * brings the outstanding balance to zero) or manually settled by the user (e.g. forgiven, or
 * settled informally for a different amount than originally tracked).
 */
@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val direction: String,
    val principal: Double,
    val interestRatePercent: Double = 0.0,
    val minimumPayment: Double = 0.0,
    val startDate: String,
    val notes: String? = null,
    val isClosed: Boolean = false,
    /** Optional loan type tag — one of the TYPE_* constants below, or null for unclassified. */
    val loanType: String? = null
) {
    companion object {
        /** Money the user owes someone else (a liability). */
        const val DIRECTION_OWE = "OWE"

        /** Money someone else owes the user (a receivable). */
        const val DIRECTION_OWED = "OWED"

        // Loan type constants — stored as plain strings so no TypeConverter is needed.
        const val TYPE_PERSONAL  = "personal"
        const val TYPE_HOME      = "home"
        const val TYPE_CAR       = "car"
        const val TYPE_EDUCATION = "education"
        const val TYPE_GOLD      = "gold"
        const val TYPE_BUSINESS  = "business"
        const val TYPE_INFORMAL  = "informal"
        const val TYPE_OTHER     = "other"

        /** Emoji for each loan type — used in the list card and add/edit chips. */
        fun loanTypeEmoji(type: String?): String = when (type) {
            TYPE_PERSONAL  -> "💳"
            TYPE_HOME      -> "🏠"
            TYPE_CAR       -> "🚗"
            TYPE_EDUCATION -> "🎓"
            TYPE_GOLD      -> "💛"
            TYPE_BUSINESS  -> "💼"
            TYPE_INFORMAL  -> "🤝"
            TYPE_OTHER     -> "📦"
            else           -> ""
        }

        /** All types in display order for the picker. */
        val ALL_TYPES = listOf(
            TYPE_PERSONAL, TYPE_HOME, TYPE_CAR, TYPE_EDUCATION,
            TYPE_GOLD, TYPE_BUSINESS, TYPE_INFORMAL, TYPE_OTHER
        )
    }
}
