package com.expensetracker.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Storage-facing operations for the Jam'iya (rotating savings circle) feature. Pure
 * record-keeping — it never touches the expenses table or any money-movement path (see
 * [JamiyaCircleEntity]'s class doc for why that boundary matters). Cascade deletes are done by
 * hand because the child tables use plain Long columns, not `@ForeignKey`, exactly like
 * [SplitRepository].
 */
class JamiyaRepository(private val dao: JamiyaDao) {

    // ── Circles ────────────────────────────────────────────────────────────────

    val allCircles: Flow<List<JamiyaCircleEntity>> = dao.getAllCircles()

    suspend fun getCircle(id: Long): JamiyaCircleEntity? = dao.getCircleById(id)

    suspend fun createCircle(
        name: String,
        emoji: String,
        contributionAmount: Double,
        frequency: String,
        startDate: String,
        notes: String = ""
    ): Long = dao.insertCircle(
        JamiyaCircleEntity(
            name = name,
            emoji = emoji,
            contributionAmount = contributionAmount,
            frequency = frequency,
            startDate = startDate,
            notes = notes
        )
    )

    suspend fun updateCircle(circle: JamiyaCircleEntity) = dao.updateCircle(circle)

    /** Advances the circle to [round], clamped to at least 1. Does not validate against member
     *  count — the caller (UI) decides when the final round has been reached. */
    suspend fun setCurrentRound(circle: JamiyaCircleEntity, round: Int) =
        dao.updateCircle(circle.copy(currentRound = round.coerceAtLeast(1)))

    suspend fun setClosed(circle: JamiyaCircleEntity, closed: Boolean) =
        dao.updateCircle(circle.copy(isClosed = closed))

    /** Deletes a circle and everything under it (contributions, then members, then the circle),
     *  same explicit cascade order [SplitRepository.deleteGroup] uses. */
    suspend fun deleteCircle(circle: JamiyaCircleEntity) {
        dao.deleteAllContributionsForCircle(circle.id)
        dao.deleteAllMembersForCircle(circle.id)
        dao.deleteCircle(circle)
    }

    // ── Members ──────────────────────────────────────────────────────────────

    fun getMembersForCircle(circleId: Long): Flow<List<JamiyaMemberEntity>> =
        dao.getMembersForCircle(circleId)

    suspend fun getMembersSnapshot(circleId: Long): List<JamiyaMemberEntity> =
        dao.getMembersSnapshot(circleId)

    /**
     * Adds a member. If [payoutPosition] is left at 0, the member is auto-appended to the end of
     * the rotation (next free position after the current members), so a circle built by simply
     * adding people fills positions 1, 2, 3… in insertion order without the caller having to
     * track the count. Colour cycles through [PALETTE] by current member count.
     */
    suspend fun addMember(
        circleId: Long,
        name: String,
        phone: String = "",
        emoji: String = "",
        isMe: Boolean = false,
        payoutPosition: Int = 0
    ): Long {
        val existing = dao.getMembersSnapshot(circleId)
        val position = if (payoutPosition > 0) payoutPosition else existing.size + 1
        val color = PALETTE[existing.size % PALETTE.size]
        return dao.insertMember(
            JamiyaMemberEntity(
                circleId = circleId,
                name = name,
                phone = phone,
                colorHex = color,
                emoji = emoji,
                isMe = isMe,
                payoutPosition = position
            )
        )
    }

    suspend fun updateMember(member: JamiyaMemberEntity) = dao.updateMember(member)

    /** Removes a member and any contributions attributed to them. Their payout position is left
     *  as a gap on purpose — renumbering everyone else would silently reassign whose turn it is
     *  to collect, which is exactly the kind of surprise money reshuffle this feature must never
     *  do. The UI surfaces the gap so the organiser can consciously re-assign turns. */
    suspend fun deleteMember(member: JamiyaMemberEntity) {
        dao.deleteContributionsForMember(member.id)
        dao.deleteMember(member)
    }

    /** Reassigns payout turns wholesale from an ordered member-id list (index 0 → round 1). */
    suspend fun reorderPayout(circleId: Long, orderedMemberIds: List<Long>) {
        val members = dao.getMembersSnapshot(circleId).associateBy { it.id }
        orderedMemberIds.forEachIndexed { index, memberId ->
            members[memberId]?.let { dao.updateMember(it.copy(payoutPosition = index + 1)) }
        }
    }

    // ── Contributions ─────────────────────────────────────────────────────────

    fun getContributionsForCircle(circleId: Long): Flow<List<JamiyaContributionEntity>> =
        dao.getContributionsForCircle(circleId)

    suspend fun getContributionsSnapshot(circleId: Long): List<JamiyaContributionEntity> =
        dao.getContributionsSnapshot(circleId)

    /**
     * Marks [memberId] as having paid [round]. Idempotent: any existing record for the same
     * member+round is removed first, so ticking an already-ticked payment can't create a
     * duplicate row (and re-ticking updates the amount/date to the latest values).
     */
    suspend fun recordContribution(
        circleId: Long,
        memberId: Long,
        round: Int,
        amount: Double,
        date: String
    ) {
        dao.deleteContribution(circleId, memberId, round)
        dao.insertContribution(
            JamiyaContributionEntity(
                circleId = circleId,
                memberId = memberId,
                roundNumber = round,
                amount = amount,
                date = date
            )
        )
    }

    /** Un-ticks a payment: removes the "member paid round N" record. */
    suspend fun unrecordContribution(circleId: Long, memberId: Long, round: Int) =
        dao.deleteContribution(circleId, memberId, round)

    companion object {
        /** Same palette Splits uses, so member chips look consistent across the two features. */
        val PALETTE = listOf(
            "#4CAF50", "#2196F3", "#E91E63", "#FF9800",
            "#9C27B0", "#00BCD4", "#F44336", "#795548"
        )
    }
}
