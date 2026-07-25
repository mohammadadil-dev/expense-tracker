package com.expensetracker.app.util

import com.expensetracker.app.data.JamiyaContributionEntity
import com.expensetracker.app.data.JamiyaMemberEntity

/**
 * Pure, side-effect-free calculations for a Jam'iya (rotating savings circle). Kept separate
 * from the repository/DB so the round/pot math can be unit-tested in isolation and reasoned
 * about on its own — the same split-out-the-arithmetic approach as [SplitMath] and [DebtMath].
 */
object JamiyaMath {

    /** Total rounds in a circle = one per member (everyone collects the pot exactly once). */
    fun totalRounds(memberCount: Int): Int = memberCount

    /** The pot handed to one member each round = everyone's contribution combined. */
    fun potPerRound(contributionAmount: Double, memberCount: Int): Double =
        contributionAmount * memberCount

    /** Full cash a member pays across the whole circle = their contribution × number of rounds. */
    fun totalContributedByMemberOverCircle(contributionAmount: Double, memberCount: Int): Double =
        contributionAmount * totalRounds(memberCount)

    /** The member whose turn it is to collect in [round] (by [JamiyaMemberEntity.payoutPosition]). */
    fun recipientForRound(members: List<JamiyaMemberEntity>, round: Int): JamiyaMemberEntity? =
        members.firstOrNull { it.payoutPosition == round }

    /** Members who have NOT yet recorded a contribution for [round]. */
    fun unpaidMembersForRound(
        members: List<JamiyaMemberEntity>,
        contributions: List<JamiyaContributionEntity>,
        round: Int
    ): List<JamiyaMemberEntity> {
        val paidIds = contributions.filter { it.roundNumber == round }.map { it.memberId }.toSet()
        return members.filter { it.id !in paidIds }
    }

    /**
     * True when the whole circle is finished — every round from 1..memberCount has been fully
     * collected. This is what the UI uses to offer "mark circle as complete" once the last
     * payout has been made, so the user isn't left on a dead-end final round.
     */
    fun isCircleComplete(
        members: List<JamiyaMemberEntity>,
        contributions: List<JamiyaContributionEntity>
    ): Boolean {
        if (members.isEmpty()) return false
        return (1..totalRounds(members.size)).all { round ->
            isRoundFullyCollected(members, contributions, round)
        }
    }

    /** True once every member has recorded a contribution for [round]. */
    fun isRoundFullyCollected(
        members: List<JamiyaMemberEntity>,
        contributions: List<JamiyaContributionEntity>,
        round: Int
    ): Boolean = members.isNotEmpty() && unpaidMembersForRound(members, contributions, round).isEmpty()

    /** How much has actually been collected for [round] so far. */
    fun collectedForRound(contributions: List<JamiyaContributionEntity>, round: Int): Double =
        contributions.filter { it.roundNumber == round }.sumOf { it.amount }

    /**
     * Whole-circle progress as a fraction in 0.0..1.0: the number of contributions actually
     * recorded out of the total expected (memberCount rounds × memberCount members). Safe on an
     * empty circle (returns 0.0 rather than dividing by zero).
     */
    fun circleProgress(memberCount: Int, contributions: List<JamiyaContributionEntity>): Float {
        if (memberCount <= 0) return 0f
        val expected = memberCount * totalRounds(memberCount)
        if (expected <= 0) return 0f
        return (contributions.size.toFloat() / expected).coerceIn(0f, 1f)
    }

    /** True if every member has been given a distinct payout round covering 1..memberCount. */
    fun isRotationFullyAssigned(members: List<JamiyaMemberEntity>): Boolean {
        if (members.isEmpty()) return false
        val positions = members.map { it.payoutPosition }
        return positions.toSet() == (1..members.size).toSet()
    }

    /** A member's net position: (pot they receive on their turn) − (everything they pay in). */
    fun netForMember(contributionAmount: Double, memberCount: Int): Double =
        potPerRound(contributionAmount, memberCount) -
            totalContributedByMemberOverCircle(contributionAmount, memberCount)
}
