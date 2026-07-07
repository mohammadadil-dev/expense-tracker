package com.expensetracker.app.util

/**
 * Extracts the most likely payable total from OCR text scanned off a receipt or bill.
 *
 * Pre-processing:
 *   - Normalises Arabic-Indic numerals (٧٥ → 75) so Gulf receipts parse correctly.
 *   - Normalises apostrophe/backtick thousands separators (1'234 → 1234).
 *   - Strips lines that are provably non-monetary (barcodes, TRN, dates, change, discount …).
 *
 * Strategy — keyword-first, never blind:
 *   Tier 1 — unambiguous grand-total labels → take maximum of all matches.
 *   Tier 2 — broader labels ("total", "payable" …) → take maximum.
 *   Fallback — only lines that carry an explicit currency marker; largest result.
 *
 * All parsing is purely on-device — no network calls.
 */
object ReceiptAmountExtractor {

    // ── Tier 1: unambiguous grand-total / paid-total labels ──────────────────

    private val KEYWORDS_TIER1 = listOf(
        // English — payment-final
        "payed amount", "paid amount", "amount paid", "amount payed",
        "total paid", "total payed",
        "net payable", "total payable", "total to pay", "total to be paid",
        "grand total", "amount due", "amount payable", "balance due",
        "final total", "final amount", "net due", "due now",
        "total charges", "total cost", "total bill", "bill total",
        "invoice total", "your total", "order total", "checkout total",
        "total amount due", "total amount payable", "you owe",
        "to pay", "please pay",
        // VAT-inclusive variants (GCC / UK receipts)
        "total (in. vat)", "total (inc. vat)", "total (incl. vat)",
        "total inc vat",  "total inc. vat",
        "total incl vat", "total incl. vat",
        "total with vat", "total w/ vat", "total w/vat",
        "total including vat", "total including tax",
        "total after vat",  "total after tax",
        "total incl tax",   "total inc tax",
        // Arabic (GCC)
        "المبلغ المدفوع", "الإجمالي شامل ضريبة", "إجمالي مع الضريبة",
        "المبلغ الإجمالي", "إجمالي المبلغ", "مجموع الفاتورة",
        "صافي المبلغ", "المبلغ المستحق",
        // Hindi / Urdu
        "कुल राशि", "देय राशि", "भुगतान राशि", "कुल देय",
        "جملہ ادائیگی", "کل رقم", "واجب الادا",
    )

    // ── Tier 2: weaker but common labels ─────────────────────────────────────

    private val KEYWORDS_TIER2 = listOf(
        "total", "net total", "net amount", "subtotal", "sub-total", "sub total",
        "payable", "payment", "amount",
        // Arabic
        "مجموع", "الإجمالي", "المبلغ",
        // Hindi / Urdu
        "कुल", "जोड़", "جملہ",
        // Bengali
        "মোট", "সর্বমোট",
        // Filipino / Tagalog
        "kabuuan", "bayad", "halaga",
    )

    // ── Lines to skip — these NEVER contain the payable total ────────────────
    // "change" / "cash tendered": the money returned to the customer
    // "discount": the reduction, not the total
    // "vat amount" / "tax amount": just the tax component
    // "excl. vat": the pre-tax subtotal (not the final amount)

    private val SKIP_LINE_PATTERNS = listOf(
        // Cashier change / cash-back — amount returned to customer, NOT the bill
        "change", "your change", "cash change",
        "cash tendered", "cash given", "cash received",
        // Discounts — the discount value, not the final total
        "discount", "voucher", "coupon", "offer",
        // Tax component only — not the combined total
        "vat amount", "tax amount", "vat only", "vat charge",
        // Pre-tax subtotal — we want incl-VAT, not excl-VAT
        "excl. vat", "excl vat", "ex. vat", "ex vat",
        "excluding vat", "excluding tax", "before vat", "before tax",
        // Reference / metadata — never monetary
        "barcode", "bar code", "item code", "product code", "sku",
        "receipt no", "receipt #", "invoice no", "invoice #",
        "ref no", "ref #", "order no", "order #",
        "trn", "tax reg", "vat reg", "cr no", "cr #",
        "tel", "phone", "mobile", "fax", "email", "www", "http",
        "address", "street", "city", "zip", "p.o. box",
        "date", "time",
        "qty", "quantity", "pcs", "pieces", "unit price",
        "customer", "cashier", "served by",
    ).map { it.lowercase() }

    // ── Currency markers ──────────────────────────────────────────────────────

    private val CURRENCY_REGEX = Regex(
        """[₹${'$'}€£¥₩]|SAR|AED|USD|EUR|GBP|INR|PKR|QAR|BHD|KWD|OMR|ر\.س|د\.إ""",
        RegexOption.IGNORE_CASE
    )

    // ── Amount regex — four formats, optional currency prefix/suffix ──────────
    // Apostrophe as thousands (Swiss/European): 1'234.56 → normalised before regex
    private val AMOUNT_REGEX = Regex(
        """[₹${'$'}€£¥₩]?\s*""" +
        """(\d{1,3}(?:[,\s]\d{3})+(?:\.\d{1,2})?""" +   // 1,234.56 / 1 234.56
        """|\d{1,3}(?:\.\d{3})+(?:,\d{1,2})?""" +        // 1.234,56
        """|\d+(?:[.,]\d{1,2})?)""" +                     // 75.00 / 1234
        """\s*(?:SAR|AED|USD|EUR|GBP|INR|PKR|QAR|BHD|KWD|OMR)?"""
    )

    // Arabic-Indic → ASCII digit map
    private val ARABIC_INDIC = mapOf(
        '٠' to '0', '١' to '1', '٢' to '2', '٣' to '3', '٤' to '4',
        '٥' to '5', '٦' to '6', '٧' to '7', '٨' to '8', '٩' to '9',
        // Extended (Persian/Urdu)
        '۰' to '0', '۱' to '1', '۲' to '2', '۳' to '3', '۴' to '4',
        '۵' to '5', '۶' to '6', '۷' to '7', '۸' to '8', '۹' to '9',
    )

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @param ocrText Full text returned by ML Kit Text Recognition.
     * @return Extracted payable total as a [Double], or null if nothing plausible was found.
     */
    fun extract(ocrText: String): Double? {
        val normalised = normalise(ocrText)
        val lines = normalised.lines().filter { line ->
            val lower = line.lowercase()
            SKIP_LINE_PATTERNS.none { lower.contains(it) }
        }

        // Pass 1a — Tier 1 (unambiguous grand-total labels)
        val tier1 = collectKeywordAmounts(lines, KEYWORDS_TIER1)
        if (tier1.isNotEmpty()) return tier1.max()

        // Pass 1b — Tier 2 (broader labels). Max so incl-VAT beats excl-VAT.
        val tier2 = collectKeywordAmounts(lines, KEYWORDS_TIER2)
        if (tier2.isNotEmpty()) return tier2.max()

        // Pass 2 — Conservative fallback: only lines that explicitly carry a
        // currency marker. Barcodes, TRNs, and dates won't have one.
        return lines
            .filter { CURRENCY_REGEX.containsMatchIn(it) }
            .flatMap { AMOUNT_REGEX.findAll(it).toList() }
            .mapNotNull { parseAmount(it.groupValues[1]) }
            .filter { it in 0.01..99_999.0 }
            .maxOrNull()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Normalises the raw OCR text before any parsing:
     * 1. Arabic-Indic / Persian numerals → ASCII digits.
     * 2. Apostrophe / backtick used as thousands separator → removed (1'234 → 1234).
     */
    private fun normalise(text: String): String {
        val sb = StringBuilder(text.length)
        for (ch in text) {
            sb.append(ARABIC_INDIC[ch] ?: ch)
        }
        // Remove apostrophe used as thousands separator: e.g. 1'234 or 1`234
        return sb.toString().replace(Regex("""(\d)[`'](\d)"""), "$1$2")
    }

    /**
     * Scans [lines] for any line containing a [keywords] entry and collects
     * the first valid positive amount from that line (or the next 1–2 lines).
     * Returns all collected amounts so the caller can pick the maximum.
     */
    private fun collectKeywordAmounts(lines: List<String>, keywords: List<String>): List<Double> {
        val results = mutableListOf<Double>()
        for ((index, line) in lines.withIndex()) {
            val lower = line.lowercase()
            if (keywords.any { lower.contains(it) }) {
                val candidates = listOf(line) + lines.drop(index + 1).take(2)
                for (candidate in candidates) {
                    val amount = parseFirstAmount(candidate)
                    if (amount != null && amount > 0.0) {
                        results.add(amount)
                        break
                    }
                }
            }
        }
        return results
    }

    private fun parseFirstAmount(text: String): Double? =
        AMOUNT_REGEX.find(text)?.let { parseAmount(it.groupValues[1]) }

    private fun parseAmount(raw: String): Double? {
        val cleaned = raw.trim().replace("\\s".toRegex(), "")
        if (cleaned.isEmpty()) return null
        return try {
            val lastDot   = cleaned.lastIndexOf('.')
            val lastComma = cleaned.lastIndexOf(',')
            when {
                lastDot > 0 && lastComma > 0 ->
                    if (lastDot > lastComma) cleaned.replace(",", "").toDouble()
                    else cleaned.replace(".", "").replace(",", ".").toDouble()
                lastComma > 0 ->
                    if (cleaned.length - lastComma <= 3) cleaned.replace(",", ".").toDouble()
                    else cleaned.replace(",", "").toDouble()
                else -> cleaned.toDouble()
            }
        } catch (_: NumberFormatException) { null }
    }
}
