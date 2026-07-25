package com.expensetracker.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Jam'iya (جمعية) — the Saudi/Gulf rotating savings circle (ROSCA). A fixed group of people
 * each contribute the same amount every round; each round the whole pot goes to one member,
 * rotating until everyone has collected exactly once. This is the KSA-native equivalent of the
 * khata: a real, cash-based, deeply cultural money behaviour that today is tracked on paper,
 * Excel, or WhatsApp.
 *
 * IMPORTANT — this feature is pure record-keeping. The app never moves, holds, or routes any
 * money; it only tracks who has paid into each round and whose turn it is to collect. That is a
 * deliberate design constraint: money movement would make this a regulated activity under SAMA
 * (that space is already owned by licensed players like Hakbah), whereas an offline organiser
 * that just remembers the circle needs no licence, no backend, and no KYC — the same offline,
 * zero-infrastructure model the rest of Baqaya runs on.
 *
 * The model reuses the exact shape of the Splits feature:
 *   [JamiyaCircleEntity]        ≈ split_groups   — one named circle.
 *   [JamiyaMemberEntity]        ≈ split_members  — the people in it (isMe = the device owner).
 *   [JamiyaContributionEntity]  ≈ a ledger line  — one recorded "member paid round N" fact.
 */

/**
 * One rotating savings circle.
 *
 * The number of rounds always equals the number of members: everyone collects the pot exactly
 * once. The pot handed out each round is [contributionAmount] × memberCount. Neither the round
 * count nor the pot is stored — both are derived live from the member list, so adding/removing a
 * member can never leave a stale, contradictory total (the same "store the inputs, compute the
 * totals" rule the budgets and debt tables follow).
 */
@Entity(tableName = "jamiya_circles")
data class JamiyaCircleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(defaultValue = "🔄") val emoji: String = "🔄",
    /** What each member pays in per round (not the pot — the pot is this × memberCount). */
    val contributionAmount: Double,
    /** "MONTHLY" or "WEEKLY" — drives the cadence label and reminder scheduling only. */
    @ColumnInfo(defaultValue = "MONTHLY") val frequency: String = "MONTHLY",
    /** ISO yyyy-MM-dd of the first round. */
    val startDate: String,
    /** 1-based index of the round currently being collected. */
    @ColumnInfo(defaultValue = "1") val currentRound: Int = 1,
    @ColumnInfo(defaultValue = "") val notes: String = "",
    @ColumnInfo(defaultValue = "0") val isClosed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * A member of a circle. [payoutPosition] is the 1-based round in which this member collects the
 * whole pot (0 = not yet assigned a turn). [isMe] marks the device owner, so the app can surface
 * "your turn is round 4" and, later, tag the owner's own contributions as real spend — mirroring
 * how Splits treats the `isMe` member.
 */
@Entity(
    tableName = "jamiya_members",
    indices = [Index("circleId")]
)
data class JamiyaMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val circleId: Long,
    val name: String,
    @ColumnInfo(defaultValue = "") val phone: String = "",
    @ColumnInfo(defaultValue = "#4CAF50") val colorHex: String = "#4CAF50",
    @ColumnInfo(defaultValue = "") val emoji: String = "",
    @ColumnInfo(defaultValue = "0") val isMe: Boolean = false,
    /** Which round (1-based) this member receives the pot. 0 = unassigned. */
    @ColumnInfo(defaultValue = "0") val payoutPosition: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * One recorded contribution: "member X paid their share for round N." The mere existence of a
 * row means that member has paid that round — there is no isPaid flag, exactly like a khata
 * ledger line means the transaction happened. Deleting the row undoes the payment.
 *
 * [linkedExpenseId] is reserved (nullable, unused for now) so that when the device owner's own
 * contributions are later surfaced as real monthly spend — the linked-expense pattern Khata,
 * Debts and Splits all use — no second schema migration is needed. Captured now, wired later,
 * the same forward-compatibility move as khata_parties.upiId (MIGRATION_14_15).
 */
@Entity(
    tableName = "jamiya_contributions",
    indices = [Index("circleId"), Index("memberId")]
)
data class JamiyaContributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val circleId: Long,
    val memberId: Long,
    val roundNumber: Int,
    val amount: Double,
    val date: String,
    val linkedExpenseId: Long? = null
)
