package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A named household member in Family / Couple Mode.
 *
 * [isMe] marks the profile that belongs to the device owner — this member is
 * pre-created when Family Mode is first enabled and cannot be deleted.
 *
 * [colorHex] is used to colour the avatar circle and per-member bars in the
 * dashboard breakdown card (e.g. "#4CAF50").
 *
 * [emoji] is an optional single character / emoji shown inside the avatar when
 * no photo is set (defaults to the first letter of [name]).
 */
@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#4CAF50",
    val emoji: String = "",          // blank → UI falls back to first letter of name
    val isMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
