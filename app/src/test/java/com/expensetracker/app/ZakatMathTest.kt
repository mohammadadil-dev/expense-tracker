package com.expensetracker.app

import com.expensetracker.app.util.ZakatMath
import com.expensetracker.app.util.ZakatMath.NisabBasis
import com.expensetracker.app.util.ZakatMath.ZakatableAssets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM tests for the offline zakat calculator. Uses illustrative metal prices (SAR/gram) —
 * the real app takes the current price from the user — so the assertions test the *math*, not a
 * live rate.
 */
class ZakatMathTest {

    // Illustrative prices per gram.
    private val goldPerGram = 300.0     // 85 g  → nisab 25,500
    private val silverPerGram = 3.5     // 595 g → nisab 2,082.5

    @Test
    fun nisabValue_goldAndSilver() {
        assertEquals(25_500.0, ZakatMath.nisabValue(NisabBasis.GOLD, goldPerGram, silverPerGram), 0.001)
        assertEquals(2_082.5, ZakatMath.nisabValue(NisabBasis.SILVER, goldPerGram, silverPerGram), 0.001)
    }

    @Test
    fun netZakatableWealth_sumsAssetsAndDeductsLiabilities() {
        val a = ZakatableAssets(
            cash = 1_000.0, bankBalances = 4_000.0, savings = 2_000.0,
            goldValue = 3_000.0, investments = 1_000.0, shortTermLiabilities = 1_000.0
        )
        // 1000+4000+2000+3000+1000 = 11,000 − 1,000 = 10,000
        assertEquals(10_000.0, ZakatMath.netZakatableWealth(a), 0.001)
    }

    @Test
    fun netZakatableWealth_neverNegative() {
        val a = ZakatableAssets(cash = 500.0, shortTermLiabilities = 5_000.0)
        assertEquals(0.0, ZakatMath.netZakatableWealth(a), 0.001)
    }

    @Test
    fun zakatDue_isTwoPointFivePercent_whenAboveNisab() {
        val a = ZakatableAssets(bankBalances = 10_000.0) // above silver nisab 2,082.5
        val due = ZakatMath.zakatDue(a, NisabBasis.SILVER, goldPerGram, silverPerGram)
        assertEquals(250.0, due, 0.001) // 2.5% of 10,000
    }

    @Test
    fun zakatDue_zero_whenBelowNisab() {
        val a = ZakatableAssets(cash = 1_500.0) // below silver nisab 2,082.5
        assertEquals(0.0, ZakatMath.zakatDue(a, NisabBasis.SILVER, goldPerGram, silverPerGram), 0.001)
    }

    @Test
    fun zakatDue_zero_whenHawlNotCompleted() {
        val a = ZakatableAssets(bankBalances = 50_000.0)
        val due = ZakatMath.zakatDue(a, NisabBasis.SILVER, goldPerGram, silverPerGram, hawlCompleted = false)
        assertEquals(0.0, due, 0.001)
    }

    @Test
    fun eligibility_boundary_isInclusive() {
        // Exactly at nisab counts as eligible.
        assertTrue(ZakatMath.isEligible(2_082.5, 2_082.5))
        assertFalse(ZakatMath.isEligible(2_082.49, 2_082.5))
    }

    @Test
    fun silverNisabIsLowerThanGold_soCatchesMorePayers() {
        val goldNisab = ZakatMath.nisabValue(NisabBasis.GOLD, goldPerGram, silverPerGram)
        val silverNisab = ZakatMath.nisabValue(NisabBasis.SILVER, goldPerGram, silverPerGram)
        assertTrue(silverNisab < goldNisab)
    }
}
