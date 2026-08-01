package com.expensetracker.app.util

/**
 * Pure, offline zakat (زكاة) calculation. No network, no fatwa API — the user supplies the
 * current gold/silver price per gram (or takes the built-in default), the app does the rest on
 * device. Relevant in both of Baqaya's markets: it is a core annual obligation for Muslims in
 * India and Saudi Arabia alike, and it maps directly onto Vision 2030's Financial Sector
 * Development Program "savings culture / financial planning" pillar — a natural premium feature
 * layered onto a savings-and-expense app that already knows the user's balances.
 *
 * Zakat is 2.5% of net zakatable wealth, provided that wealth has (a) exceeded the nisab
 * threshold and (b) been held for one full lunar year (hawl). Hawl is a judgement the user
 * confirms — it can't be derived from a single balance snapshot — so it is passed in explicitly.
 *
 * Zakatable wealth (per the mainstream position) = cash + bank balances + savings + the market
 * value of gold/silver + investments + resale business inventory + receivables likely to be
 * repaid, MINUS immediately-due short-term liabilities. Personal-use items (home, car, everyday
 * belongings) are not zakatable and are intentionally not part of this model.
 */
object ZakatMath {

    /** The zakat rate on monetary wealth: 2.5%. */
    const val ZAKAT_RATE = 0.025

    /**
     * Classical nisab weights: 85 g of gold or 595 g of silver. Some contemporary authorities
     * cite ~87.48 g / ~612.36 g; the difference is a rounding of the historical dinar/dirham
     * conversion. These constants are the widely-used classical figures; expose them so the UI
     * can show the basis and let the user override if their local authority differs.
     */
    const val GOLD_NISAB_GRAMS = 85.0
    const val SILVER_NISAB_GRAMS = 595.0

    /** Which metal's threshold to measure nisab against. Silver is the lower (and thus more
     *  cautious, more widely recommended) threshold, so it is the default. */
    enum class NisabBasis { GOLD, SILVER }

    /**
     * A snapshot of the user's zakatable position. Every field is optional (defaults to 0) so a
     * caller can prefill whatever Baqaya already knows — cash/bank from payment accounts,
     * [savings] from savings-goal balances — and let the user fill the rest (gold, investments,
     * business inventory). [shortTermLiabilities] are debts immediately due, which are deducted.
     */
    data class ZakatableAssets(
        val cash: Double = 0.0,
        val bankBalances: Double = 0.0,
        val savings: Double = 0.0,
        val goldValue: Double = 0.0,
        val silverValue: Double = 0.0,
        val investments: Double = 0.0,
        val businessInventory: Double = 0.0,
        val receivables: Double = 0.0,
        val shortTermLiabilities: Double = 0.0
    )

    /** Net zakatable wealth = sum of zakatable assets − short-term liabilities. Never negative. */
    fun netZakatableWealth(a: ZakatableAssets): Double {
        val gross = a.cash + a.bankBalances + a.savings + a.goldValue + a.silverValue +
            a.investments + a.businessInventory + a.receivables
        return (gross - a.shortTermLiabilities).coerceAtLeast(0.0)
    }

    /** The nisab threshold in currency = nisab weight for [basis] × the current price per gram. */
    fun nisabValue(basis: NisabBasis, goldPricePerGram: Double, silverPricePerGram: Double): Double =
        when (basis) {
            NisabBasis.GOLD -> GOLD_NISAB_GRAMS * goldPricePerGram
            NisabBasis.SILVER -> SILVER_NISAB_GRAMS * silverPricePerGram
        }

    /** Zakat is only due once net wealth reaches (or exceeds) the nisab threshold. */
    fun isEligible(netWealth: Double, nisabValue: Double): Boolean =
        nisabValue > 0.0 && netWealth >= nisabValue

    /**
     * Zakat due on [assets]. Returns 0 when the wealth is below nisab, or when the lunar year
     * ([hawlCompleted]) has not elapsed — in both cases nothing is owed yet. Otherwise it is
     * exactly [ZAKAT_RATE] (2.5%) of the full net zakatable wealth (not merely the excess above
     * nisab — nisab is an eligibility gate, not a deductible allowance).
     */
    fun zakatDue(
        assets: ZakatableAssets,
        basis: NisabBasis,
        goldPricePerGram: Double,
        silverPricePerGram: Double,
        hawlCompleted: Boolean = true
    ): Double {
        if (!hawlCompleted) return 0.0
        val net = netZakatableWealth(assets)
        val nisab = nisabValue(basis, goldPricePerGram, silverPricePerGram)
        return if (isEligible(net, nisab)) net * ZAKAT_RATE else 0.0
    }
}
