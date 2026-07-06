package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A savings goal the user wants to reach — "Vacation fund", "New phone", "Eid gifts", etc.
 *
 * [savedAmount] is manually updated by the user (or auto-credited from surplus budget) rather
 * than derived from expenses, keeping goals independent of the spending tracker so users can
 * track goals for money kept in a separate envelope / jar.
 */
@Entity(tableName = "savings_goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** User-chosen label, e.g. "New Phone" or "Eid gifts". */
    val name: String,
    /** Single emoji chosen from the picker, e.g. "🏖️". Empty string = use default 🎯. */
    val emoji: String = "🎯",
    /** Target amount the user wants to save. */
    val targetAmount: Double,
    /** Amount already saved / contributed toward this goal. */
    val savedAmount: Double = 0.0,
    /** Optional ISO date string (yyyy-MM-dd) by which the user wants to reach the goal. Empty = no deadline. */
    val targetDate: String = "",
    /** Epoch millis when this goal was created — used for default sort order. */
    val createdAt: Long = System.currentTimeMillis(),
    /** True once the user manually marks the goal as reached. */
    val isCompleted: Boolean = false
)
