package com.expensetracker.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "split_expenses",
    indices = [Index(name = "idx_split_expenses_groupId", value = ["groupId"])]
)
data class SplitExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val description: String,
    val amount: Double,
    val paidByMemberId: Long,
    val date: String,               // ISO yyyy-MM-dd
    @ColumnInfo(defaultValue = "")  val note: String = "",
    /** True when this expense was auto-inserted to record a settlement payment. */
    @ColumnInfo(defaultValue = "0") val isSettlement: Boolean = false,
    /** Non-null when the device owner ("me") participates in this (non-settlement) split —
     *  points at the auto-generated personal [ExpenseEntity] row for *my own share* of the
     *  cost, so real money spent inside a group shows up in the Dashboard/budget the same
     *  way Khata and Debt payments already do. Null for settlement rows (those just move
     *  already-counted money between members) and for expenses "me" isn't part of. */
    val linkedExpenseId: Long? = null
)
