package com.expensetracker.app.util

import com.expensetracker.app.data.CategoryEntity

/**
 * Result of successfully parsing a transaction out of an SMS. [merchantOrNote] and
 * [categoryNameKeyGuess] are both best-effort guesses — they're meant to pre-fill the review
 * queue, not to be trusted blindly, since the user always confirms/edits before anything is
 * saved as a real expense.
 */
data class ParsedSmsTransaction(
    val amount: Double,
    val merchantOrNote: String,
    val categoryNameKeyGuess: String?
)

/**
 * Best-effort, bank-agnostic parser for Indian-style debit/UPI/card transaction SMS.
 *
 * This deliberately does not target any single bank's exact template — the user said they get
 * SMS from "different bank, UPI, card etc.", so instead of hard-coding per-bank formats this
 * looks for a currency-prefixed amount plus a debit-ish verb anywhere in the message, and skips
 * anything that looks like an OTP, an incoming credit/refund/salary, a collect request, or a
 * marketing message. False positives/negatives are expected — every match still lands in a
 * review queue before it can become a real expense, so being wrong is cheap and being silent
 * about a real transaction is the worse failure mode.
 */
object SmsExpenseParser {

    // Rs.1,234.50 / Rs 1234 / INR999 / ₹999.00 — comma grouping and decimals both optional.
    // "rs"/"inr" must not be glued to surrounding letters (so "yours 500" or "inrush" can't
    // falsely match), but ₹ is an unambiguous symbol so it's left unconstrained.
    private val AMOUNT_REGEX = Regex(
        """(?:(?<![A-Za-z])(?:rs\.?|inr)(?![A-Za-z])|₹)\s?([0-9][0-9,]*\.?[0-9]{0,2})""",
        RegexOption.IGNORE_CASE
    )

    // "at MERCHANT", "to MERCHANT", "for MERCHANT" — stop at the next punctuation/keyword.
    private val MERCHANT_REGEX = Regex(
        """\b(?:at|to|for)\s+([A-Za-z0-9&.\-'@ ]{2,30}?)(?:\s+on\b|\s+via\b|\.|,|;|$)""",
        RegexOption.IGNORE_CASE
    )

    private val DEBIT_KEYWORDS = listOf(
        "debited", "debit of", "spent", "paid", "payment of", "withdrawn", "purchase",
        "txn of", "transaction of", "sent", "deducted", "swiped", "charged", "used at"
    )

    // Specific multi-word phrases rather than the bare word "credited" — real debit alerts
    // often legitimately mention where the money went (e.g. "...to VPA merchant@bank"), so a
    // bare "credited" would wrongly throw away genuine debits. These phrases are reliably
    // incoming-money or non-transaction signals instead.
    private val EXCLUDE_KEYWORDS = listOf(
        "otp", "one time password", "credit alert", "amount credited", "credited to your",
        "is credited", "salary credited", "refund", "reversed", "reversal", "cashback",
        "promo", "offer", "balance enquiry", "bill generated", "minimum due", "due by",
        "will be debited", "requesting", "request money", "failed", "declined", "unsuccessful"
    )

    private val CATEGORY_KEYWORDS: List<Pair<String, String>> = listOf(
        "swiggy" to "cat_food", "zomato" to "cat_food", "restaurant" to "cat_food",
        "grocery" to "cat_food", "supermarket" to "cat_food", "bigbasket" to "cat_food",
        "uber" to "cat_transport", "ola" to "cat_transport", "rapido" to "cat_transport",
        "fuel" to "cat_transport", "petrol" to "cat_transport", "diesel" to "cat_transport",
        "metro" to "cat_transport", "irctc" to "cat_transport", "fastag" to "cat_transport",
        "electricity" to "cat_utilities", "water bill" to "cat_utilities", "broadband" to "cat_utilities",
        "recharge" to "cat_utilities", "gas bill" to "cat_utilities", "dth" to "cat_utilities",
        "netflix" to "cat_subscriptions", "prime" to "cat_subscriptions", "spotify" to "cat_subscriptions",
        "hotstar" to "cat_subscriptions", "subscription" to "cat_subscriptions",
        "pharmacy" to "cat_healthcare", "hospital" to "cat_healthcare", "clinic" to "cat_healthcare",
        "medical" to "cat_healthcare", "apollo" to "cat_healthcare",
        "amazon" to "cat_shopping", "flipkart" to "cat_shopping", "myntra" to "cat_shopping",
        "mall" to "cat_shopping", "store" to "cat_shopping",
        "rent" to "cat_housing", "emi" to "cat_savings", "mutual fund" to "cat_savings",
        "sip" to "cat_savings", "insurance" to "cat_savings",
        "movie" to "cat_entertainment", "cinema" to "cat_entertainment", "pvr" to "cat_entertainment",
        "bookmyshow" to "cat_entertainment"
    )

    /** Returns null if [message] doesn't look like a completed debit/UPI/card transaction. */
    fun parse(message: String): ParsedSmsTransaction? {
        val lower = message.lowercase()
        if (EXCLUDE_KEYWORDS.any { lower.contains(it) }) return null
        if (DEBIT_KEYWORDS.none { lower.contains(it) }) return null

        val amountMatch = AMOUNT_REGEX.find(message) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", "").trimEnd('.').toDoubleOrNull() ?: return null
        if (amount <= 0.0) return null

        val merchant = MERCHANT_REGEX.find(message)?.groupValues?.get(1)?.trim()
        val description = merchant?.takeIf { it.isNotBlank() } ?: defaultDescription(lower)
        val categoryKey = CATEGORY_KEYWORDS.firstOrNull { (keyword, _) -> lower.contains(keyword) }?.second

        return ParsedSmsTransaction(amount = amount, merchantOrNote = description, categoryNameKeyGuess = categoryKey)
    }

    /** Maps a parsed category guess (by nameKey) to a real category id, if that category exists. */
    fun guessCategoryId(categoryNameKeyGuess: String?, categories: List<CategoryEntity>): Long? {
        if (categoryNameKeyGuess == null) return null
        return categories.firstOrNull { it.nameKey == categoryNameKeyGuess }?.id
    }

    private fun defaultDescription(lower: String): String = when {
        lower.contains("upi") -> "UPI payment"
        lower.contains("card") -> "Card payment"
        lower.contains("atm") -> "ATM withdrawal"
        else -> "Bank transaction"
    }
}
