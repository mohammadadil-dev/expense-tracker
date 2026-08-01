package com.expensetracker.app.util

import com.expensetracker.app.data.SharedBillMemberEntity
import kotlin.math.floor
import kotlin.math.roundToLong

/**
 * Person-day proration for shared utility bills. A member who was present for more of the billing
 * period pays proportionally more. Shares are rounded to 2 decimals with a largest-remainder pass
 * so they always sum to EXACTLY the bill total (no lost/extra halalas).
 */
object SharedBillMath {

    /** Inclusive number of days in the billing window (both endpoints counted). Min 1. */
    fun periodDays(startIso: String, endIso: String): Int =
        try {
            (DateUtils.daysBetween(startIso, endIso) + 1).toInt().coerceAtLeast(1)
        } catch (_: Exception) {
            1
        }

    /** Days this member was actually present = period − days away, clamped to [0, periodDays]. */
    fun presentDays(member: SharedBillMemberEntity, periodDays: Int): Int =
        (periodDays - member.daysAbsent).coerceIn(0, periodDays)

    /**
     * Prorated amount per member, in the same order as [members], summing exactly to [total].
     * Weight = present days. If nobody has any present days (everyone marked fully absent) it
     * falls back to an equal split so the money is still fully distributed.
     */
    fun shares(total: Double, members: List<SharedBillMemberEntity>, periodDays: Int): List<Double> {
        if (members.isEmpty()) return emptyList()
        // Weight = people in the flat × days present. Excluded members (e.g. a vacant flat this
        // period) contribute nothing. In member mode personCount is 1, so it reduces to person-days.
        val present = members.map {
            if (!it.included) 0L
            else presentDays(it, periodDays).toLong() * it.personCount.coerceAtLeast(1)
        }
        val weights = if (present.sum() > 0L) present else List(members.size) { 1L }
        val weightSum = weights.sum()
        val totalCents = (total * 100).roundToLong()

        val exact = weights.map { totalCents.toDouble() * it / weightSum }
        val floors = exact.map { floor(it).toLong() }
        // Leftover halalas after flooring — always fewer than the member count, so each goes to a
        // distinct member. Hand them to the largest fractional remainders first (no indexed
        // assignment, so shares stays a pure transform).
        val leftover = (totalCents - floors.sum()).toInt().coerceAtLeast(0)
        val bump = exact.indices
            .sortedByDescending { exact[it] - floor(exact[it]) }
            .take(leftover)
            .toSet()
        return floors.mapIndexed { idx, c -> (c + if (idx in bump) 1L else 0L) / 100.0 }
    }
}
