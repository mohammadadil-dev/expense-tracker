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
 * Best-effort, bank-agnostic parser for debit/card/UPI transaction SMS across the two markets
 * this app ships to: Saudi Arabia (SAR — Al Rajhi, SNB, Riyad Bank, SABB, Alinma, stc pay,
 * urpay, and the generic mada point-of-sale alert, in both Arabic and English) and India
 * (INR — UPI/card/bank).
 *
 * It deliberately does not target any single bank's exact template. KSA is effectively cashless,
 * so almost every purchase throws off a "شراء/Purchase … SAR …" alert; instead of hard-coding
 * per-bank layouts (which break the moment a bank tweaks its wording) this normalises the text,
 * then looks for a currency-tagged amount plus a debit-ish verb anywhere in the message, and
 * skips anything that looks like an OTP, an incoming credit/salary/refund, a balance notice, or
 * marketing. False positives/negatives are expected — every match still lands in a review queue
 * before it can become a real expense, so being wrong is cheap and being silent about a real
 * transaction is the worse failure mode.
 */
object SmsExpenseParser {

    // ---- Amount extraction ------------------------------------------------------------------

    // A bare money number: 1,234.50 / 1234 / 99.00 — comma grouping and decimals both optional.
    private const val NUM = """[0-9][0-9,]*\.?[0-9]{0,2}"""

    // SAR can sit on either side of the number, and the currency token comes in many forms:
    //   "SAR 150.00", "SR 45", "150.00 SAR", "بقيمة 99.00 ريال", "مبلغ 300 ر.س", "﷼ 120"
    // Latin tokens (SAR/SR) are guarded so they can't match inside another word; the Arabic
    // tokens (ريال / ر.س) and the ﷼ symbol are unambiguous and left unconstrained.
    private const val SAR_TOKEN = """(?:(?<![A-Za-z])(?:sar|sr)(?![A-Za-z])|ر\.?\s?س\.?|ريال|﷼)"""
    private val SAR_BEFORE = Regex("""$SAR_TOKEN\s?($NUM)""", RegexOption.IGNORE_CASE)
    private val SAR_AFTER = Regex("""($NUM)\s?$SAR_TOKEN""", RegexOption.IGNORE_CASE)

    // India: Rs.1,234.50 / Rs 1234 / INR999 / ₹999.00 — kept working alongside the SAR paths.
    private val INR_REGEX = Regex(
        """(?:(?<![A-Za-z])(?:rs\.?|inr)(?![A-Za-z])|₹)\s?($NUM)""",
        RegexOption.IGNORE_CASE
    )

    // ---- Debit vs. non-debit signals --------------------------------------------------------

    private val DEBIT_KEYWORDS = listOf(
        // English
        "debited", "debit of", "spent", "paid", "payment of", "withdrawn", "purchase",
        "txn of", "transaction of", "sent", "deducted", "swiped", "charged", "used at",
        "pos purchase", "you paid",
        // Arabic — شراء (purchase), عملية شراء (purchase transaction), خصم/تم خصم (deducted),
        // سحب (withdrawal), دفع (payment), سداد (bill payment), مدى (mada, on POS alerts)
        "شراء", "خصم", "سحب", "دفع", "سداد", "مدى", "نقاط البيع", "عملية"
    )

    // Incoming-money or non-transaction signals. Specific phrases rather than bare words so a
    // debit alert that merely mentions where money went isn't wrongly discarded.
    private val EXCLUDE_KEYWORDS = listOf(
        // English
        "otp", "one time password", "verification code", "credit alert", "amount credited",
        "credited to your", "is credited", "salary credited", "refund", "reversed", "reversal",
        "cashback", "promo", "offer", "balance enquiry", "available balance", "bill generated",
        "minimum due", "due by", "will be debited", "requesting", "request money", "failed",
        "declined", "unsuccessful",
        // Arabic — راتب (salary), إيداع (deposit), أضيف/اضيف (added/credited), استرداد (refund),
        // مكافأة (reward/cashback), رمز التحقق / رمز الدخول (OTP), كلمة المرور (password),
        // رصيد (balance), عرض (offer), فشل (failed), لم تتم (did not complete)
        "راتب", "إيداع", "ايداع", "أضيف", "اضيف", "استرداد", "مكافأة", "مكافاة",
        "رمز التحقق", "رمز الدخول", "كلمة المرور", "الرصيد المتاح", "رصيدك", "عرض",
        "فشل", "لم تتم", "طلب"
    )

    // ---- Merchant capture -------------------------------------------------------------------

    // "at MERCHANT", "to MERCHANT", "for MERCHANT" — stop at the next punctuation/keyword.
    private val MERCHANT_REGEX_EN = Regex(
        """\b(?:at|to|for)[\s:]+([A-Za-z0-9&.\-'@ ]{2,30}?)(?:\s+on\b|\s+via\b|\.|,|;|\n|$)""",
        RegexOption.IGNORE_CASE
    )

    // Arabic markers: لدى (at), إلى/الى (to), من (from), في (at/in). The separator after the
    // marker may be a colon (Al Rajhi writes "لدى:MERCHANT") or whitespace. Saudi merchant names
    // on the alert are usually still in Latin (JARIR, AMAZON, LULU), so the captured span allows
    // both scripts and stops at the next field marker (في/عبر/بتاريخ) or punctuation.
    private val MERCHANT_REGEX_AR = Regex(
        """(?:لدى|إلى|الى|من|في)\s*:?\s*([A-Za-z0-9؀-ۿ&.\-'@ ]{2,30}?)(?:\s+في\b|\s+عبر\b|\s+بتاريخ\b|\.|,|;|،|\n|$)"""
    )

    // ---- Category guessing ------------------------------------------------------------------
    // Saudi merchants/keywords first (the primary market), then the India set. Matching is a
    // plain lowercase `contains`, so Latin brand names on Arabic alerts still hit.
    private val CATEGORY_KEYWORDS: List<Pair<String, String>> = listOf(
        // Food & groceries — KSA
        "hungerstation" to "cat_food", "jahez" to "cat_food", "mrsool" to "cat_food",
        "toyou" to "cat_food", "the chefz" to "cat_food",
        "panda" to "cat_food", "بنده" to "cat_food", "بندة" to "cat_food",
        "danube" to "cat_food", "الدانوب" to "cat_food", "othaim" to "cat_food", "العثيم" to "cat_food",
        "tamimi" to "cat_food", "التميمي" to "cat_food", "carrefour" to "cat_food", "كارفور" to "cat_food",
        "lulu" to "cat_food", "لولو" to "cat_food", "albaik" to "cat_food", "البيك" to "cat_food",
        "herfy" to "cat_food", "هرفي" to "cat_food", "kudu" to "cat_food", "كودو" to "cat_food",
        "starbucks" to "cat_food", "mcdonald" to "cat_food", "restaurant" to "cat_food",
        "مطعم" to "cat_food", "كافيه" to "cat_food", "قهوة" to "cat_food", "بقالة" to "cat_food",
        // Transport / fuel — KSA
        "careem" to "cat_transport", "كريم" to "cat_transport", "uber" to "cat_transport",
        "bolt" to "cat_transport", "aramco" to "cat_transport", "أرامكو" to "cat_transport",
        "petromin" to "cat_transport", "sasco" to "cat_transport", "ساسكو" to "cat_transport",
        "naft" to "cat_transport", "محطة" to "cat_transport", "وقود" to "cat_transport",
        "بنزين" to "cat_transport", "petrol" to "cat_transport", "fuel" to "cat_transport",
        "saptco" to "cat_transport", "مترو" to "cat_transport", "تاكسي" to "cat_transport",
        // Utilities / telecom — KSA
        "stc" to "cat_utilities", "mobily" to "cat_utilities", "موبايلي" to "cat_utilities",
        "zain" to "cat_utilities", "زين" to "cat_utilities", "sec" to "cat_utilities",
        "كهرباء" to "cat_utilities", "مياه" to "cat_utilities", "فاتورة" to "cat_utilities",
        "electricity" to "cat_utilities", "water bill" to "cat_utilities",
        // Subscriptions — KSA/global
        "netflix" to "cat_subscriptions", "shahid" to "cat_subscriptions", "شاهد" to "cat_subscriptions",
        "spotify" to "cat_subscriptions", "osn" to "cat_subscriptions", "prime" to "cat_subscriptions",
        "subscription" to "cat_subscriptions", "اشتراك" to "cat_subscriptions",
        // Healthcare — KSA
        "nahdi" to "cat_healthcare", "النهدي" to "cat_healthcare", "dawaa" to "cat_healthcare",
        "الدواء" to "cat_healthcare", "pharmacy" to "cat_healthcare", "صيدلية" to "cat_healthcare",
        "hospital" to "cat_healthcare", "مستشفى" to "cat_healthcare", "clinic" to "cat_healthcare",
        "عيادة" to "cat_healthcare", "طبي" to "cat_healthcare",
        // Shopping — KSA
        "amazon" to "cat_shopping", "noon" to "cat_shopping", "نون" to "cat_shopping",
        "jarir" to "cat_shopping", "جرير" to "cat_shopping", "extra" to "cat_shopping",
        "اكسترا" to "cat_shopping", "namshi" to "cat_shopping", "centrepoint" to "cat_shopping",
        "mall" to "cat_shopping", "store" to "cat_shopping", "سوق" to "cat_shopping",
        // Housing / rent
        "rent" to "cat_housing", "إيجار" to "cat_housing", "ايجار" to "cat_housing",
        // Entertainment — KSA
        "muvi" to "cat_entertainment", "vox" to "cat_entertainment", "cinema" to "cat_entertainment",
        "سينما" to "cat_entertainment", "ملاهي" to "cat_entertainment", "تذكرة" to "cat_entertainment",

        // ---- India set (kept for the existing INR audience) ----
        "swiggy" to "cat_food", "zomato" to "cat_food", "grocery" to "cat_food",
        "supermarket" to "cat_food", "bigbasket" to "cat_food",
        "ola" to "cat_transport", "rapido" to "cat_transport", "diesel" to "cat_transport",
        "irctc" to "cat_transport", "fastag" to "cat_transport",
        "broadband" to "cat_utilities", "recharge" to "cat_utilities", "dth" to "cat_utilities",
        "hotstar" to "cat_subscriptions",
        "apollo" to "cat_healthcare", "medical" to "cat_healthcare",
        "flipkart" to "cat_shopping", "myntra" to "cat_shopping",
        "emi" to "cat_savings", "mutual fund" to "cat_savings", "sip" to "cat_savings",
        "insurance" to "cat_savings",
        "movie" to "cat_entertainment", "pvr" to "cat_entertainment", "bookmyshow" to "cat_entertainment"
    )

    /** Returns null if [message] doesn't look like a completed debit/card/UPI transaction. */
    fun parse(rawMessage: String): ParsedSmsTransaction? {
        // Normalise Arabic-Indic / Persian digits and Arabic separators to ASCII so the numeric
        // regexes (and toDouble) work uniformly regardless of which keyboard the bank used.
        val message = normalizeDigits(rawMessage)
        val lower = message.lowercase()

        if (EXCLUDE_KEYWORDS.any { lower.contains(it) }) return null
        if (DEBIT_KEYWORDS.none { lower.contains(it) }) return null

        val amount = extractAmount(message) ?: return null
        if (amount <= 0.0) return null

        val merchant = (MERCHANT_REGEX_EN.find(message)?.groupValues?.get(1)
            ?: MERCHANT_REGEX_AR.find(message)?.groupValues?.get(1))
            ?.trim()
        val description = merchant?.takeIf { it.isNotBlank() } ?: defaultDescription(lower)
        val categoryKey = CATEGORY_KEYWORDS.firstOrNull { (keyword, _) -> lower.contains(keyword) }?.second

        return ParsedSmsTransaction(amount = amount, merchantOrNote = description, categoryNameKeyGuess = categoryKey)
    }

    /** Tries SAR (both orderings) first, then INR. Returns the parsed numeric amount or null. */
    private fun extractAmount(message: String): Double? {
        val match = SAR_BEFORE.find(message)
            ?: SAR_AFTER.find(message)
            ?: INR_REGEX.find(message)
            ?: return null
        return match.groupValues[1].replace(",", "").trimEnd('.').toDoubleOrNull()
    }

    /** Maps a parsed category guess (by nameKey) to a real category id, if that category exists. */
    fun guessCategoryId(categoryNameKeyGuess: String?, categories: List<CategoryEntity>): Long? {
        if (categoryNameKeyGuess == null) return null
        return categories.firstOrNull { it.nameKey == categoryNameKeyGuess }?.id
    }

    /**
     * Converts Arabic-Indic (٠-٩) and Extended/Persian (۰-۹) digits to ASCII 0-9, and the Arabic
     * decimal (٫) and thousands (٬) separators to '.' and ',' so downstream numeric parsing is
     * script-agnostic. Latin text passes through untouched.
     */
    private fun normalizeDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            val mapped = when (ch) {
                in '٠'..'٩' -> ('0' + (ch - '٠'))   // Arabic-Indic 0-9
                in '۰'..'۹' -> ('0' + (ch - '۰'))   // Extended Arabic-Indic 0-9
                '٫' -> '.'                                     // Arabic decimal separator
                '٬' -> ','                                     // Arabic thousands separator
                else -> ch
            }
            sb.append(mapped)
        }
        return sb.toString()
    }

    private fun defaultDescription(lower: String): String = when {
        lower.contains("مدى") || lower.contains("mada") -> "mada purchase"
        lower.contains("نقاط البيع") || lower.contains("pos") -> "POS purchase"
        lower.contains("stc pay") || lower.contains("urpay") -> "Wallet payment"
        lower.contains("upi") -> "UPI payment"
        lower.contains("بطاقة") || lower.contains("card") -> "Card payment"
        lower.contains("صراف") || lower.contains("atm") -> "ATM withdrawal"
        lower.contains("شراء") || lower.contains("purchase") -> "Purchase"
        else -> "Bank transaction"
    }
}
