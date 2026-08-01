package com.expensetracker.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A shared utility bill (electricity, water, …) split between flatmates by how many days each
 * person actually lived there over the billing period — NOT an equal split. This is the KSA
 * shared-apartment case: one person pays the bill, then everyone owes their prorated share.
 *
 * Each bill is a standalone saved record (there is no persistent "flat"/group to maintain —
 * flatmates are entered fresh per bill). [linkedExpenseId] holds the id of the expense created
 * when the user logs their own share, so it can be cleaned up if the bill is deleted.
 *
 * The split is by *person-days*: for the [periodStart]..[periodEnd] window (inclusive), each
 * member's present days = periodDays − [SharedBillMemberEntity.daysAbsent], and their share is
 * total × presentDays / Σ presentDays. See [com.expensetracker.app.util.SharedBillMath].
 */
/**
 * A group of shared bills — an apartment/building/office whose flats or people share utilities.
 * [splitMode] 0 = by person (each member is one person), 1 = by flat (each member is a flat with
 * a headcount). Both weight the split by headcount × days present. The group owns a template
 * member list ([SharedBillGroupMemberEntity]); every new bill snapshots that list.
 */
@Entity(tableName = "shared_bill_groups")
data class SharedBillGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(defaultValue = "🏠") val emoji: String = "🏠",
    @ColumnInfo(defaultValue = "0") val splitMode: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/** A flat/person that belongs to a [SharedBillGroupEntity] — the reusable template copied onto
 *  each new bill. Editable when someone joins/leaves the group. */
@Entity(
    tableName = "shared_bill_group_members",
    indices = [Index("groupId")]
)
data class SharedBillGroupMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val name: String,
    @ColumnInfo(defaultValue = "1") val personCount: Int = 1,
    @ColumnInfo(defaultValue = "0") val isMe: Boolean = false
)

@Entity(tableName = "shared_bills")
data class SharedBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Owning [SharedBillGroupEntity]. */
    @ColumnInfo(defaultValue = "0") val groupId: Long = 0,
    val label: String,
    /** ISO yyyy-MM-dd, inclusive. */
    val periodStart: String,
    /** ISO yyyy-MM-dd, inclusive. */
    val periodEnd: String,
    val totalAmount: Double,
    /** Optional short note to tell same-type bills apart (e.g. "with guest"). */
    @ColumnInfo(defaultValue = "") val note: String = "",
    /** Expense row created for the owner's own share, if logged; null otherwise. */
    val linkedExpenseId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * One flat on a [SharedBillEntity]. [name] is the flat identifier (e.g. "Flat 3"), [personCount]
 * is how many people live in it, and [daysAbsent] is how many days of the billing period the flat
 * was empty (entered in the editor as date ranges, stored as the day count). The flat's weight in
 * the split is personCount × present-days, so a fuller flat / longer stay pays proportionally
 * more. [paid] tracks whether the flat has settled with the payer.
 */
@Entity(
    tableName = "shared_bill_members",
    indices = [Index("billId")]
)
data class SharedBillMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billId: Long,
    val name: String,
    @ColumnInfo(defaultValue = "1") val personCount: Int = 1,
    @ColumnInfo(defaultValue = "0") val isMe: Boolean = false,
    @ColumnInfo(defaultValue = "0") val daysAbsent: Int = 0,
    @ColumnInfo(defaultValue = "0") val paid: Boolean = false,
    /** false = excluded from this bill (e.g. the flat was vacant that period) → zero share. */
    @ColumnInfo(defaultValue = "1") val included: Boolean = true
)
