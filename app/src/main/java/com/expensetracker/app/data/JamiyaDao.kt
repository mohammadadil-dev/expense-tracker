package com.expensetracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Single DAO covering the whole Jam'iya feature (circles + members + contributions), the same
 * one-cohesive-DAO approach [KhataDao] takes for parties + entries. There are no `@ForeignKey`
 * cascades declared on the child tables — memberId/circleId are plain columns — so deletion
 * cascades are done explicitly in [JamiyaRepository], mirroring how [SplitRepository] cleans up
 * its own plain-column children.
 */
@Dao
interface JamiyaDao {

    // ── Circles ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM jamiya_circles ORDER BY isClosed ASC, createdAt DESC")
    fun getAllCircles(): Flow<List<JamiyaCircleEntity>>

    @Query("SELECT * FROM jamiya_circles WHERE id = :id")
    suspend fun getCircleById(id: Long): JamiyaCircleEntity?

    /** Unscoped snapshot — reserved for a future BackupManager full-database export. */
    @Query("SELECT * FROM jamiya_circles")
    suspend fun getAllCirclesOnce(): List<JamiyaCircleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircle(circle: JamiyaCircleEntity): Long

    @Update
    suspend fun updateCircle(circle: JamiyaCircleEntity)

    @Delete
    suspend fun deleteCircle(circle: JamiyaCircleEntity)

    // ── Members ──────────────────────────────────────────────────────────────

    @Query("SELECT * FROM jamiya_members WHERE circleId = :circleId ORDER BY payoutPosition ASC, createdAt ASC")
    fun getMembersForCircle(circleId: Long): Flow<List<JamiyaMemberEntity>>

    @Query("SELECT * FROM jamiya_members WHERE circleId = :circleId ORDER BY payoutPosition ASC, createdAt ASC")
    suspend fun getMembersSnapshot(circleId: Long): List<JamiyaMemberEntity>

    @Query("SELECT * FROM jamiya_members")
    suspend fun getAllMembersOnce(): List<JamiyaMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: JamiyaMemberEntity): Long

    @Update
    suspend fun updateMember(member: JamiyaMemberEntity)

    @Delete
    suspend fun deleteMember(member: JamiyaMemberEntity)

    @Query("DELETE FROM jamiya_members WHERE circleId = :circleId")
    suspend fun deleteAllMembersForCircle(circleId: Long)

    // ── Contributions ─────────────────────────────────────────────────────────

    @Query("SELECT * FROM jamiya_contributions WHERE circleId = :circleId")
    fun getContributionsForCircle(circleId: Long): Flow<List<JamiyaContributionEntity>>

    @Query("SELECT * FROM jamiya_contributions WHERE circleId = :circleId")
    suspend fun getContributionsSnapshot(circleId: Long): List<JamiyaContributionEntity>

    @Query("SELECT * FROM jamiya_contributions")
    suspend fun getAllContributionsOnce(): List<JamiyaContributionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: JamiyaContributionEntity): Long

    @Delete
    suspend fun deleteContribution(contribution: JamiyaContributionEntity)

    /** Removes a single "member paid round N" record (used when a payment is un-ticked). */
    @Query("DELETE FROM jamiya_contributions WHERE circleId = :circleId AND memberId = :memberId AND roundNumber = :round")
    suspend fun deleteContribution(circleId: Long, memberId: Long, round: Int)

    @Query("DELETE FROM jamiya_contributions WHERE circleId = :circleId")
    suspend fun deleteAllContributionsForCircle(circleId: Long)

    @Query("DELETE FROM jamiya_contributions WHERE memberId = :memberId")
    suspend fun deleteContributionsForMember(memberId: Long)

    /** Rescales each committee's per-round contribution when the user converts currency. */
    @Query("UPDATE jamiya_circles SET contributionAmount = contributionAmount * :rate")
    suspend fun scaleAllContributionAmounts(rate: Double)

    /** Rescales every recorded contribution amount when the user converts currency. */
    @Query("UPDATE jamiya_contributions SET amount = amount * :rate")
    suspend fun scaleAllContributions(rate: Double)
}
