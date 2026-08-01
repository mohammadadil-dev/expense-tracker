package com.expensetracker.app.util

/**
 * Maps the 2-digit SAMA bank code embedded in a Saudi IBAN to the bank's name (English +
 * Arabic). In a Saudi IBAN — "SA" + 2 check digits + 2-digit bank code + 18-digit account —
 * the bank code is at positions 5–6 (i.e. characters 3–4 of the part after "SA"). Codes and
 * names are the official SAMA list; Saudi National Bank owns two codes (10 and 40, the latter
 * inherited from the Samba merger). Returns null for an unknown/foreign code rather than
 * guessing, so a wrong bank name is never shown on a payment.
 */
object SaudiBanks {

    // code -> (English name, Arabic name)
    private val NAMES: Map<String, Pair<String, String>> = mapOf(
        "05" to ("Alinma Bank" to "مصرف الانماء"),
        "10" to ("Saudi National Bank" to "البنك الأهلي السعودي"),
        "15" to ("Bank AlBilad" to "بنك البلاد"),
        "20" to ("Riyad Bank" to "بنك الرياض"),
        "30" to ("Arab National Bank" to "البنك العربي الوطني"),
        "40" to ("Saudi National Bank" to "البنك الأهلي السعودي"),
        "45" to ("Saudi British Bank" to "البنك السعودي البريطاني"),
        "50" to ("Saudi Hollandi Bank" to "بنك الأول"),
        "55" to ("Banque Saudi Fransi" to "البنك السعودي الفرنسي"),
        "60" to ("Bank AlJazira" to "بنك الجزيرة"),
        "65" to ("Saudi Investment Bank" to "البنك السعودي للاستثمار"),
        "71" to ("National Bank of Bahrain" to "بنك البحرين الوطني"),
        "73" to ("First Abu Dhabi Bank" to "بنك أبوظبي الأول"),
        "75" to ("National Bank of Kuwait" to "بنك الكويت الوطني"),
        "76" to ("Bank Muscat" to "بنك مسقط"),
        "80" to ("Al Rajhi Bank" to "مصرف الراجحي"),
        "85" to ("BNP Paribas Saudi Arabia" to "بي ان بي ارابيا"),
        "86" to ("JPMorgan Chase Bank" to "بنك تشيز"),
        "90" to ("Gulf International Bank" to "بنك الخليج الدولي - السعودية"),
        "95" to ("Emirates Bank International" to "بنك الإمارات الدولي"),
    )

    /** The 2-digit bank code from [afterSa] — the 22 chars after "SA" (check ×2, code ×2, then
     *  account). Null if there aren't enough characters yet. */
    fun bankCode(afterSa: String): String? =
        if (afterSa.length >= 4) afterSa.substring(2, 4) else null

    /**
     * Bank name for the code inside [afterSa] (the part after "SA"), or null if the code isn't a
     * recognised Saudi bank. [arabic] picks the Arabic name.
     */
    fun nameForIbanBody(afterSa: String, arabic: Boolean): String? {
        val code = bankCode(afterSa) ?: return null
        val pair = NAMES[code] ?: return null
        return if (arabic) pair.second else pair.first
    }

    /**
     * Formats a full IBAN in groups of 4 ("SA80 7382 …") for display in shared messages — a
     * 24-character run of digits is very hard to read or verify otherwise. Banking apps accept
     * pasted spaces, and the value is stored unspaced regardless.
     */
    fun formatGrouped(iban: String): String =
        iban.filter { !it.isWhitespace() }.chunked(4).joinToString(" ")
}
