package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Records which member bears what share of a [SplitExpenseEntity].
 * For an equal split of SAR 1200 among 4 people, there are 4 rows each with shareAmount = 300.
 */
@Entity(
    tableName = "split_expense_shares",
    indices = [Index(name = "idx_split_shares_expenseId", value = ["expenseId"])]
)
data class SplitExpenseShareEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expenseId: Long,
    val memberId: Long,
    val shareAmount: Double
)
