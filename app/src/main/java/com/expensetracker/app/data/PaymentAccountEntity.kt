package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A payment account / method an expense can be tagged with (e.g. "Cash", "HDFC Bank",
 * a specific card). This is purely a label for where the money came from — it has no
 * effect on totals, budgets, or any other computation; an expense with no account set
 * ([ExpenseEntity.accountId] == null) behaves exactly as it did before this feature existed.
 *
 * Mirrors [CategoryEntity]'s nameKey/customName split: the three built-in accounts seeded
 * on first run (Cash, Bank Account, Card) use [nameKey] so their display name localizes
 * automatically; accounts the user adds themselves only ever set [customName].
 *
 * [type] drives which icon/emoji is shown next to the account (see accountEmoji()) — it is
 * NOT a category and has no bearing on spend calculations. Built-in seeds use "CASH"/"BANK"/
 * "CARD"; every user-created account is tagged "OTHER".
 */
@Entity(tableName = "payment_accounts")
data class PaymentAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameKey: String? = null,
    val customName: String? = null,
    val type: String = "OTHER",
    val colorHex: String = "#4CAF50",
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
