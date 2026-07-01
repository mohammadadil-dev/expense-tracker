package com.expensetracker.app.util

import java.time.LocalDate
import kotlin.math.ln

/**
 * Standard fixed-payment amortization math, used to project a payoff date for a debt/loan and
 * to power the "pay off N months sooner" insight. Pure arithmetic over numbers already in
 * memory — no network call, no external service, same on-device "AI" philosophy as
 * [FinancialInsights].
 */
object DebtMath {

    /**
     * How many monthly payments of [monthlyPayment] it takes to pay off [outstandingBalance]
     * at [annualRatePercent] annual interest, using the standard amortization closed form
     * `n = -ln(1 - P*r/A) / ln(1+r)` (P = balance, r = monthly rate, A = payment).
     *
     * Returns `null` when payoff is mathematically impossible (no balance, no payment, or the
     * payment doesn't even cover one month's interest — which would mean the balance grows
     * forever instead of shrinking) rather than a wrong or negative number.
     */
    fun monthsToPayoff(outstandingBalance: Double, annualRatePercent: Double, monthlyPayment: Double): Int? {
        if (outstandingBalance <= 0.0) return 0
        if (monthlyPayment <= 0.0) return null

        val monthlyRate = annualRatePercent / 100.0 / 12.0
        if (monthlyRate <= 0.0) {
            // Interest-free: balance shrinks by a flat amount each month.
            return Math.ceil(outstandingBalance / monthlyPayment).toInt()
        }

        val monthlyInterest = outstandingBalance * monthlyRate
        if (monthlyPayment <= monthlyInterest) return null // payment never outpaces interest

        val months = -ln(1 - (outstandingBalance * monthlyRate) / monthlyPayment) / ln(1 + monthlyRate)
        return Math.ceil(months).toInt()
    }

    /** [monthsToPayoff] converted into a calendar date, anchored at today. */
    fun projectedPayoffDate(outstandingBalance: Double, annualRatePercent: Double, monthlyPayment: Double): LocalDate? {
        val months = monthsToPayoff(outstandingBalance, annualRatePercent, monthlyPayment) ?: return null
        if (months <= 0) return LocalDate.now()
        return LocalDate.now().plusMonths(months.toLong())
    }

    /**
     * A sensible "add a bit more each month" suggestion for the accelerated-payoff insight —
     * roughly a fifth of the current payment, rounded to a nice figure so it reads naturally
     * regardless of currency or loan size (e.g. 23 -> 25, 480 -> 500, 4200 -> 4000).
     */
    fun suggestedExtraPayment(monthlyPayment: Double): Double {
        val raw = monthlyPayment * 0.2
        if (raw <= 0.0) return 0.0
        val magnitude = Math.pow(10.0, Math.floor(Math.log10(raw)))
        return (Math.round(raw / magnitude).toDouble() * magnitude).let { if (it <= 0.0) raw else it }
    }

    /**
     * How many fewer months it would take to pay off [outstandingBalance] if [extraPayment]
     * were added on top of [monthlyPayment] every month — the heart of the "pay off N months
     * sooner" insight. Returns `null` if either projection is impossible to compute.
     */
    fun monthsSavedByExtraPayment(
        outstandingBalance: Double,
        annualRatePercent: Double,
        monthlyPayment: Double,
        extraPayment: Double
    ): Int? {
        val current = monthsToPayoff(outstandingBalance, annualRatePercent, monthlyPayment) ?: return null
        val accelerated = monthsToPayoff(outstandingBalance, annualRatePercent, monthlyPayment + extraPayment) ?: return null
        val saved = current - accelerated
        return if (saved > 0) saved else null
    }
}
