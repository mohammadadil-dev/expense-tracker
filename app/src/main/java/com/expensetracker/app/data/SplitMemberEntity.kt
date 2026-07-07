package com.expensetracker.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "split_members",
    indices = [Index(name = "idx_split_members_groupId", value = ["groupId"])]
)
data class SplitMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val name: String,
    @ColumnInfo(defaultValue = "#4CAF50") val colorHex: String = "#4CAF50",
    @ColumnInfo(defaultValue = "")       val emoji: String = "",
    /** True for the device owner's profile in this group. */
    @ColumnInfo(defaultValue = "0")      val isMe: Boolean = false
)
