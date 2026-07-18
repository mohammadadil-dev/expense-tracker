package com.expensetracker.app.util

/**
 * Splits a bill amount into equal shares that always sum back to *exactly* the original amount.
 *
 * The bug this exists to fix: naive `amount / count` division almost never divides evenly.
 * SAR 100 among 3 people is 33.333...  each — displayed and stored as 33.33, so the three
 * shares add up to 99.99, a cent short of the actual bill. That cent doesn't just vanish
 * cosmetically: [SplitSettlementCalculator] computes each member's balance as
 * (amount they paid) − (sum of their shares), so a bill that was split "short" leaves the
 * group's net balances never quite zeroing out — someone is perpetually owed or owing a
 * stray cent that Settle Up can't fully clear.
 *
 * Fix: work in integer cents (so there's no repeating-decimal drift at all), then hand out
 * the leftover 1-cent remainders to the first few shares in iteration order — the same
 * "someone has to get the odd cent" convention most bill-splitting apps use.
 */
object SplitMath {

    /**
     * Returns [count] amounts, each a fair (as equal as currency allows) share of
     * [totalAmount], that sum to exactly [totalAmount] to the cent. Order matches nothing in
     * particular — callers `zip` this against their own ordered list of member/item ids, so
     * which entries get the extra cent is simply "whichever ids come first in that list".
     */
    fun splitEvenly(totalAmount: Double, count: Int): List<Double> {
        if (count <= 0) return emptyList()
        val totalCents = Math.round(totalAmount * 100)
        val baseCents = totalCents / count
        val remainderCents = (totalCents % count).toInt()
        return List(count) { index ->
            val cents = if (index < remainderCents) baseCents + 1 else baseCents
            cents / 100.0
        }
    }
}
