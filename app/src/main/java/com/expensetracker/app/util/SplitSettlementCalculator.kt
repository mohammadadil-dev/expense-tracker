package com.expensetracker.app.util

import com.expensetracker.app.data.SplitExpenseEntity
import com.expensetracker.app.data.SplitExpenseShareEntity
import com.expensetracker.app.data.SplitMemberEntity
import kotlin.math.abs

/** A simplified transfer needed to settle a group's balances. */
data class Settlement(
    val fromMemberId: Long,
    val fromMemberName: String,
    val toMemberId: Long,
    val toMemberName: String,
    val amount: Double
)

/**
 * Computes the minimum-transfer settlement for a group using a greedy algorithm:
 *  1. Calculate each member's net balance (total paid − total owed).
 *  2. Separate into creditors (positive) and debtors (negative).
 *  3. Greedily match the largest debtor to the largest creditor until all are zeroed.
 *
 * The result is always a valid settlement, though not always the global minimum number
 * of transactions — that optimisation is NP-hard and unnecessary for typical group sizes.
 */
object SplitSettlementCalculator {

    fun calculate(
        members: List<SplitMemberEntity>,
        expenses: List<SplitExpenseEntity>,
        shares: List<SplitExpenseShareEntity>
    ): List<Settlement> {
        if (members.isEmpty() || expenses.isEmpty()) return emptyList()

        val memberMap = members.associateBy { it.id }

        // net[id] = total paid by member − total owed by member
        val net = mutableMapOf<Long, Double>()
        members.forEach { net[it.id] = 0.0 }

        for (expense in expenses) {
            net[expense.paidByMemberId] = (net[expense.paidByMemberId] ?: 0.0) + expense.amount
        }
        for (share in shares) {
            net[share.memberId] = (net[share.memberId] ?: 0.0) - share.shareAmount
        }

        // Separate creditors (net > 0) and debtors (net < 0)
        val creditors = net.filter { it.value > 0.005 }
            .map { it.key to it.value }.sortedByDescending { it.second }.toMutableList()
        val debtors = net.filter { it.value < -0.005 }
            .map { it.key to abs(it.value) }.sortedByDescending { it.second }.toMutableList()

        val result = mutableListOf<Settlement>()
        var ci = 0; var di = 0

        while (ci < creditors.size && di < debtors.size) {
            val (credId, credAmt) = creditors[ci]
            val (debtId, debtAmt) = debtors[di]
            val transfer = minOf(credAmt, debtAmt)
            val rounded = Math.round(transfer * 100) / 100.0

            if (rounded > 0.0) {
                result += Settlement(
                    fromMemberId   = debtId,
                    fromMemberName = memberMap[debtId]?.name.orEmpty(),
                    toMemberId     = credId,
                    toMemberName   = memberMap[credId]?.name.orEmpty(),
                    amount         = rounded
                )
            }

            creditors[ci] = credId to (credAmt - transfer)
            debtors[di]   = debtId to (debtAmt - transfer)

            if (creditors[ci].second < 0.005) ci++
            if (debtors[di].second < 0.005) di++
        }

        return result
    }

    /** Net balance per member. Positive = owed money; negative = owes money. */
    fun netBalances(
        members: List<SplitMemberEntity>,
        expenses: List<SplitExpenseEntity>,
        shares: List<SplitExpenseShareEntity>
    ): Map<Long, Double> {
        val net = mutableMapOf<Long, Double>()
        members.forEach { net[it.id] = 0.0 }
        for (expense in expenses) {
            net[expense.paidByMemberId] = (net[expense.paidByMemberId] ?: 0.0) + expense.amount
        }
        for (share in shares) {
            net[share.memberId] = (net[share.memberId] ?: 0.0) - share.shareAmount
        }
        return net
    }
}
