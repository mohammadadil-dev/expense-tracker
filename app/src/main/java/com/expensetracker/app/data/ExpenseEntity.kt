package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single expense record.
 *
 * [date] is stored as an ISO string "yyyy-MM-dd" and [monthKey] ("yyyy-MM") is kept as a
 * derived, indexed column purely so month-scoped queries are fast and simple.
 */
@Entity(tableName = "expenses", indices = [Index("monthKey"), Index("categoryId")])
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val description: String,
    val amount: Double,
    val date: String,
    val monthKey: String
)
