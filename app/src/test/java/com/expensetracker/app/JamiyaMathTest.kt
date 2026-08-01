package com.expensetracker.app

import com.expensetracker.app.data.JamiyaContributionEntity
import com.expensetracker.app.data.JamiyaMemberEntity
import com.expensetracker.app.util.JamiyaMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM tests for the Jam'iya (rotating savings circle) arithmetic. No Android/Room runtime
 * needed — the entities are plain data classes, so this runs under `./gradlew testDebugUnitTest`.
 */
class JamiyaMathTest {

    private fun member(id: Long, name: String, position: Int) =
        JamiyaMemberEntity(id = id, circleId = 1, name = name, payoutPosition = position)

    private fun contribution(memberId: Long, round: Int, amount: Double = 100.0) =
        JamiyaContributionEntity(id = 0, circleId = 1, memberId = memberId, roundNumber = round, amount = amount, date = "2026-07-24")

    private val members = listOf(
        member(1, "Adil", 1),
        member(2, "Sara", 2),
        member(3, "Omar", 3)
    )

    @Test
    fun totalRounds_equalsMemberCount() {
        assertEquals(3, JamiyaMath.totalRounds(3))
    }

    @Test
    fun potPerRound_isContributionTimesMembers() {
        assertEquals(300.0, JamiyaMath.potPerRound(100.0, 3), 0.001)
    }

    @Test
    fun aJamiyaIsNetZero_forEveryMember() {
        // A fair ROSCA is interest-free: the pot you collect once equals everything you pay in.
        assertEquals(0.0, JamiyaMath.netForMember(100.0, 3), 0.001)
    }

    @Test
    fun recipientForRound_returnsMemberWithThatPayoutPosition() {
        assertEquals("Sara", JamiyaMath.recipientForRound(members, 2)?.name)
        assertNull(JamiyaMath.recipientForRound(members, 99))
    }

    @Test
    fun unpaidMembersForRound_excludesThoseWhoPaid() {
        val contributions = listOf(contribution(1, 1), contribution(2, 1))
        val unpaid = JamiyaMath.unpaidMembersForRound(members, contributions, 1)
        assertEquals(1, unpaid.size)
        assertEquals("Omar", unpaid.first().name)
    }

    @Test
    fun isRoundFullyCollected_trueOnlyWhenAllPaid() {
        val partial = listOf(contribution(1, 1), contribution(2, 1))
        assertFalse(JamiyaMath.isRoundFullyCollected(members, partial, 1))
        val full = partial + contribution(3, 1)
        assertTrue(JamiyaMath.isRoundFullyCollected(members, full, 1))
    }

    @Test
    fun collectedForRound_sumsOnlyThatRound() {
        val contributions = listOf(
            contribution(1, 1, 100.0),
            contribution(2, 1, 100.0),
            contribution(1, 2, 100.0) // different round — must not count toward round 1
        )
        assertEquals(200.0, JamiyaMath.collectedForRound(contributions, 1), 0.001)
    }

    @Test
    fun circleProgress_isFractionOfExpectedContributions() {
        // 3 members × 3 rounds = 9 expected. 3 recorded → 1/3.
        val contributions = listOf(contribution(1, 1), contribution(2, 1), contribution(3, 1))
        assertEquals(1f / 3f, JamiyaMath.circleProgress(3, contributions), 0.001f)
        // Guard against divide-by-zero on an empty circle.
        assertEquals(0f, JamiyaMath.circleProgress(0, emptyList()), 0.001f)
    }

    @Test
    fun isRotationFullyAssigned_requiresDistinct1toN() {
        assertTrue(JamiyaMath.isRotationFullyAssigned(members))
        val gapped = listOf(member(1, "A", 1), member(2, "B", 1), member(3, "C", 3)) // dup 1, missing 2
        assertFalse(JamiyaMath.isRotationFullyAssigned(gapped))
        assertFalse(JamiyaMath.isRotationFullyAssigned(emptyList()))
    }
}
