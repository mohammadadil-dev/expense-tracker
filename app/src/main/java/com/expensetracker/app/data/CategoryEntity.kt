package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A spending category.
 *
 * Built-in categories (Housing, Food, etc.) are seeded with [nameKey] set to a string
 * resource name (e.g. "cat_housing") so their display name automatically localizes when
 * the user switches the app language. Once a user renames a category, [nameKey] is cleared
 * and [customName] takes over permanently. User-created categories only ever use [customName].
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameKey: String? = null,
    val customName: String? = null,
    val colorHex: String,
    val sortOrder: Int = 0
)
