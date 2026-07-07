package com.expensetracker.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "split_groups")
data class SplitGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(defaultValue = "🤝") val emoji: String = "🤝",
    val createdDate: String  // ISO yyyy-MM-dd
)
