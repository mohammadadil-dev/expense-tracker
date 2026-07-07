package com.expensetracker.app.util

/**
 * Parses a free-form spoken phrase into a structured expense prefill.
 *
 * Examples handled:
 *   "spent 200 on tea"           → amount=200, desc="tea",       cat=cat_food
 *   "petrol 500"                 → amount=500, desc="petrol",    cat=cat_transport
 *   "paid 1500 rent"             → amount=1500, desc="rent",     cat=cat_housing
 *   "medicine 350 rupees"        → amount=350, desc="medicine",  cat=cat_healthcare
 *   "2.5k groceries"             → amount=2500, desc="groceries",cat=cat_food
 *   "دوائي 200"  (Arabic)        → amount=200, desc="دوائي",    cat=cat_healthcare
 *   "खाना 300"   (Hindi)         → amount=300, desc="खाना",      cat=cat_food
 */
object VoiceExpenseParser {

    data class Result(
        val amount: Double?,
        val description: String,
        val suggestedCategoryKey: String?
    )

    // ── Amount extraction ─────────────────────────────────────────────────────
    // Matches: 1234 / 1,234 / 1.5 / 1,234.50 / 2k / 2.5K  (optional k/K suffix)
    private val AMOUNT_REGEX = Regex(
        """(\d{1,3}(?:[,\s]\d{3})*(?:\.\d{1,2})?|\d+(?:\.\d{1,2})?)[\s]?[kK]?"""
    )

    // Noise words to strip when extracting description
    private val NOISE_WORDS = setOf(
        "spent", "spend", "paid", "pay", "bought", "buy", "purchased", "purchase",
        "on", "for", "of", "in", "at", "the", "a", "an", "rupees", "rupee",
        "rs", "pkr", "inr", "sar", "taka", "peso", "dollar", "bdt",
        "riyal", "riyals", "toman", "dirham",
        // Hindi/Urdu
        "ka", "ke", "ki", "par", "mein", "liye", "kharcha", "kharche",
        // Arabic
        "على", "في", "من", "لـ", "بـ", "ريال", "دولار", "روبية",
        // Bengali
        "এর", "জন্য", "টাকা",
        // Filipino/Tagalog
        "sa", "ng", "para", "piso", "bayad", "gastos"
    )

    // ── Category keyword map ──────────────────────────────────────────────────
    // Each entry: lowercase keyword → category nameKey
    private val KEYWORDS: List<Pair<String, String>> = listOf(
        // Food & Drink (EN + multi-language common words)
        "food" to "cat_food", "eat" to "cat_food", "eating" to "cat_food",
        "lunch" to "cat_food", "dinner" to "cat_food", "breakfast" to "cat_food",
        "tea" to "cat_food", "coffee" to "cat_food", "chai" to "cat_food",
        "biryani" to "cat_food", "pizza" to "cat_food", "burger" to "cat_food",
        "rice" to "cat_food", "bread" to "cat_food", "snack" to "cat_food",
        "restaurant" to "cat_food", "cafe" to "cat_food", "dhaba" to "cat_food",
        "grocery" to "cat_food", "groceries" to "cat_food", "sabzi" to "cat_food",
        "swiggy" to "cat_food", "zomato" to "cat_food", "uber eats" to "cat_food",
        "khaana" to "cat_food", "khana" to "cat_food",       // Hindi
        "طعام" to "cat_food", "اكل" to "cat_food", "مطعم" to "cat_food", // Arabic
        "খাবার" to "cat_food", "ভোজন" to "cat_food",                      // Bengali
        "pagkain" to "cat_food", "kain" to "cat_food",                    // Filipino

        // Transport
        "petrol" to "cat_transport", "fuel" to "cat_transport",
        "diesel" to "cat_transport", "gas" to "cat_transport",
        "uber" to "cat_transport", "ola" to "cat_transport",
        "taxi" to "cat_transport", "cab" to "cat_transport",
        "bus" to "cat_transport", "auto" to "cat_transport",
        "rickshaw" to "cat_transport", "metro" to "cat_transport",
        "train" to "cat_transport", "ticket" to "cat_transport",
        "parking" to "cat_transport", "toll" to "cat_transport",
        "سيارة" to "cat_transport", "وقود" to "cat_transport", "بنزين" to "cat_transport", // Arabic
        "পরিবহন" to "cat_transport", "বাস" to "cat_transport",                             // Bengali
        "سواری" to "cat_transport", "پیٹرول" to "cat_transport",                           // Urdu

        // Housing / Rent
        "rent" to "cat_housing", "house" to "cat_housing", "home" to "cat_housing",
        "apartment" to "cat_housing", "flat" to "cat_housing",
        "kiraya" to "cat_housing", "किराया" to "cat_housing",              // Hindi/Urdu
        "إيجار" to "cat_housing", "ايجار" to "cat_housing",               // Arabic
        "ভাড়া" to "cat_housing",                                          // Bengali

        // Utilities
        "electricity" to "cat_utilities", "electric" to "cat_utilities",
        "water" to "cat_utilities", "internet" to "cat_utilities",
        "broadband" to "cat_utilities", "wifi" to "cat_utilities",
        "recharge" to "cat_utilities", "mobile" to "cat_utilities",
        "bill" to "cat_utilities", "bijli" to "cat_utilities",
        "بجلی" to "cat_utilities",                                         // Urdu
        "كهرباء" to "cat_utilities", "ماء" to "cat_utilities",            // Arabic
        "বিদ্যুৎ" to "cat_utilities",                                      // Bengali

        // Healthcare
        "medicine" to "cat_healthcare", "medical" to "cat_healthcare",
        "doctor" to "cat_healthcare", "hospital" to "cat_healthcare",
        "clinic" to "cat_healthcare", "pharmacy" to "cat_healthcare",
        "health" to "cat_healthcare", "dawai" to "cat_healthcare",
        "دوا" to "cat_healthcare", "دواء" to "cat_healthcare",            // Urdu/Arabic
        "ওষুধ" to "cat_healthcare", "ডাক্তার" to "cat_healthcare",       // Bengali
        "gamot" to "cat_healthcare", "ospital" to "cat_healthcare",       // Filipino

        // Entertainment
        "movie" to "cat_entertainment", "cinema" to "cat_entertainment",
        "film" to "cat_entertainment", "game" to "cat_entertainment",
        "games" to "cat_entertainment", "concert" to "cat_entertainment",
        "event" to "cat_entertainment", "show" to "cat_entertainment",
        "فيلم" to "cat_entertainment", "سينما" to "cat_entertainment",    // Arabic
        "সিনেমা" to "cat_entertainment", "চলচ্চিত্র" to "cat_entertainment", // Bengali

        // Shopping
        "shopping" to "cat_shopping", "clothes" to "cat_shopping",
        "shirt" to "cat_shopping", "shoes" to "cat_shopping",
        "amazon" to "cat_shopping", "flipkart" to "cat_shopping",
        "market" to "cat_shopping", "store" to "cat_shopping",
        "تسوق" to "cat_shopping", "ملابس" to "cat_shopping",             // Arabic
        "কেনাকাটা" to "cat_shopping",                                     // Bengali

        // Subscriptions
        "netflix" to "cat_subscriptions", "spotify" to "cat_subscriptions",
        "prime" to "cat_subscriptions", "youtube" to "cat_subscriptions",
        "subscription" to "cat_subscriptions"
    )

    // ── Public API ────────────────────────────────────────────────────────────

    fun parse(rawText: String): Result {
        val text = rawText.trim()
        val lower = text.lowercase()

        val amount = extractAmount(lower)
        val description = extractDescription(text, amount)
        val category = suggestCategory(lower)

        return Result(
            amount = amount,
            description = description.ifBlank { text },
            suggestedCategoryKey = category
        )
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun extractAmount(lower: String): Double? {
        // Try all number-like tokens, pick the most plausible one
        val matches = AMOUNT_REGEX.findAll(lower)
        for (match in matches) {
            val raw = match.value.trim()
            if (raw.isBlank()) continue
            val isK = raw.endsWith("k", ignoreCase = true)
            val numStr = raw
                .replace(Regex("[kK]$"), "")
                .replace(",", "")
                .replace(" ", "")
                .trim()
            val num = numStr.toDoubleOrNull() ?: continue
            if (num <= 0) continue
            return if (isK) num * 1000.0 else num
        }
        return null
    }

    private fun extractDescription(original: String, amount: Double?): String {
        // Remove the amount token and noise words from the original phrase
        var desc = original
        if (amount != null) {
            // Remove the numeric part (including optional k/K and commas)
            desc = desc.replace(Regex("""\b\d[\d,\.]*\s*[kK]?\b"""), " ")
        }
        // Remove noise words (case-insensitive, whole words only)
        val words = desc.split(Regex("\\s+")).filter { word ->
            word.isNotBlank() && word.lowercase() !in NOISE_WORDS && word.length > 1
        }
        return words.joinToString(" ").trim()
    }

    private fun suggestCategory(lower: String): String? {
        // Check multi-word keywords first, then single-word
        val sorted = KEYWORDS.sortedByDescending { it.first.length }
        for ((keyword, categoryKey) in sorted) {
            if (lower.contains(keyword)) return categoryKey
        }
        return null
    }
}
