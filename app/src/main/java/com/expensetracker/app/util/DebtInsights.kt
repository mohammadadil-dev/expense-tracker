package com.expensetracker.app.util

import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.data.DebtPaymentEntity
import java.time.LocalDate

/**
 * On-device "AI" layer for debt/loan tracking — same philosophy as [FinancialInsights]: plain
 * arithmetic over [DebtEntity]/[DebtPaymentEntity] rows already loaded in memory, no network
 * call. Stays Compose-free so money/date formatting is left to the caller.
 */
object DebtInsights {

    /**
     * Everything the UI needs to render one debt's card: its live outstanding balance (never
     * stored — always [DebtEntity.principal] minus payments so far), payoff projection, and the
     * "pay off sooner" accelerated-payoff insight.
     */
    data class DebtProgress(
        val debt: DebtEntity,
        val totalPaid: Double,
        val outstandingBalance: Double,
        val paidFraction: Float,
        val monthsToPayoff: Int?,
        val payoffDate: LocalDate?,
        val suggestedExtraPayment: Double,
        val monthsSavedWithExtra: Int?
    )

    /** Aggregate across every open (not closed) debt — powers the dashboard tile + screen header. */
    data class NetPosition(
        val totalOwedByUser: Double,
        val totalOwedToUser: Double,
        val net: Double
    )

    /** Computes [DebtProgress] for one debt from its full payment history. */
    fun computeProgress(debt: DebtEntity, payments: List<DebtPaymentEntity>): DebtProgress {
        val totalPaid = payments.sumOf { it.amount }
        val outstanding = (debt.principal - totalPaid).coerceAtLeast(0.0)
        val paidFraction = if (debt.principal > 0) {
            (totalPaid / debt.principal).toFloat().coerceIn(0f, 1f)
        } else 1f

        val canProject = !debt.isClosed && outstanding > 0.0
        val monthsToPayoff = if (canProject) {
            DebtMath.monthsToPayoff(outstanding, debt.interestRatePercent, debt.minimumPayment)
        } else 0
        val payoffDate = if (canProject) {
            DebtMath.projectedPayoffDate(outstanding, debt.interestRatePercent, debt.minimumPayment)
        } else null

        val suggestedExtra = if (canProject) DebtMath.suggestedExtraPayment(debt.minimumPayment) else 0.0
        val monthsSaved = if (canProject && suggestedExtra > 0.0) {
            DebtMath.monthsSavedByExtraPayment(outstanding, debt.interestRatePercent, debt.minimumPayment, suggestedExtra)
        } else null

        return DebtProgress(
            debt = debt,
            totalPaid = totalPaid,
            outstandingBalance = outstanding,
            paidFraction = paidFraction,
            monthsToPayoff = monthsToPayoff,
            payoffDate = payoffDate,
            suggestedExtraPayment = suggestedExtra,
            monthsSavedWithExtra = monthsSaved
        )
    }

    /** Net position across every open debt: positive = others owe the user more than they owe out. */
    fun computeNetPosition(debts: List<DebtEntity>, payments: List<DebtPaymentEntity>): NetPosition {
        val paidByDebt = payments.groupBy { it.debtId }.mapValues { (_, rows) -> rows.sumOf { it.amount } }
        var owedByUser = 0.0
        var owedToUser = 0.0
        for (debt in debts) {
            if (debt.isClosed) continue
            val outstanding = (debt.principal - (paidByDebt[debt.id] ?: 0.0)).coerceAtLeast(0.0)
            if (outstanding <= 0.0) continue
            if (debt.direction == DebtEntity.DIRECTION_OWE) {
                owedByUser += outstanding
            } else {
                owedToUser += outstanding
            }
        }
        return NetPosition(owedByUser, owedToUser, owedToUser - owedByUser)
    }
}
