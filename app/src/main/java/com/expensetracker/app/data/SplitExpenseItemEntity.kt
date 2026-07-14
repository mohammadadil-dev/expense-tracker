package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One line item on an itemized [SplitExpenseEntity] — e.g. "Pizza" / SAR 45.00 on a
 * restaurant bill split by what each person actually ordered, rather than one flat
 * equal/exact/percentage split across the whole bill.
 *
 * Which members share this item's cost (split equally among them) is recorded in
 * [SplitExpenseItemMemberEntity]. The per-member totals derived from all of an expense's
 * items are what actually get written to [SplitExpenseShareEntity] — these two tables exist
 * purely so the itemized *breakdown* can be shown back to the user later (e.g. "Pizza —
 * split between Alice, Bob"), not to recompute anything at read time.
 */
@Entity(
    tableName = "split_expense_items",
    indices = [Index(name = "idx_split_items_expenseId", value = ["expenseId"])]
)
data class SplitExpenseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expenseId: Long,
    val name: String,
    val amount: Double
)

/**
 * Junction row: which member is in on a given [SplitExpenseItemEntity]. An item with 3 rows
 * here (for 3 different members) has its [SplitExpenseItemEntity.amount] split equally three
 * ways when the per-member share totals are computed.
 */
@Entity(
    tableName = "split_expense_item_members",
    indices = [Index(name = "idx_split_item_members_itemId", value = ["itemId"])]
)
data class SplitExpenseItemMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val memberId: Long
)
