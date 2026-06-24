package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A standing monthly budget target — either for one category ([categoryId] non-null) or the
 * overall monthly spending cap ([categoryId] == null).
 *
 * There is deliberately no `monthKey` here: per the "reset every month" model the user picked,
 * only the *spend* resets each month (computed live against that month's [ExpenseEntity] rows)
 * — the target itself is a standing value that persists across months until the user edits or
 * clears it. This keeps the schema minimal (no historical monthly budget snapshots to manage).
 */
@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long? = null,
    val amount: Double
)
