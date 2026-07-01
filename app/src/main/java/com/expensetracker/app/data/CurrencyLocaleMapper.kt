package com.expensetracker.app.data

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

/**
 * Picks a sensible *default* currency symbol from the phone's region setting, free and
 * completely offline (java.util.Locale only — no IP lookups, no paid geolocation APIs).
 *
 * This is a best-guess default, not a verified "nationality" — the user can always change
 * it later in Settings.
 */
object CurrencyLocaleMapper {

    private val countryToCurrency: Map<String, String> = mapOf(
        // Gulf / Arabic-speaking countries
        "SA" to "ر.س", "AE" to "د.إ", "EG" to "ج.م", "KW" to "د.ك", "QA" to "ر.ق",
        "BH" to "د.ب", "OM" to "ر.ع.", "JO" to "د.ا", "LB" to "ل.ل", "IQ" to "ع.د",
        "MA" to "د.م.", "TN" to "د.ت", "DZ" to "د.ج", "YE" to "ر.ي", "SY" to "ل.س",
        "LY" to "د.ل", "SD" to "ج.س", "PS" to "₪",
        // Other common regions
        "US" to "$", "CA" to "$", "AU" to "$", "NZ" to "$", "SG" to "$", "HK" to "$",
        "GB" to "£", "IN" to "₹", "PK" to "₨", "BD" to "৳", "JP" to "¥", "CN" to "¥",
        "BR" to "R$", "ZA" to "R", "RU" to "₽", "TR" to "₺", "KR" to "₩",
        "ID" to "Rp", "MY" to "RM", "PH" to "₱", "TH" to "฿", "VN" to "₫",
        "NG" to "₦", "GH" to "₵", "KE" to "KSh", "ET" to "Br"
    )

    private val eurozoneCountries = setOf(
        "DE", "FR", "IT", "ES", "NL", "BE", "AT", "IE", "PT", "FI",
        "GR", "LU", "SK", "SI", "EE", "LV", "LT", "CY", "MT", "HR"
    )

    private val arabicSpeakingCountries = setOf(
        "SA", "AE", "EG", "KW", "QA", "BH", "OM", "JO", "LB", "IQ",
        "MA", "TN", "DZ", "YE", "SY", "LY", "SD", "PS"
    )

    fun currencyForCountry(countryCode: String): String {
        val cc = countryCode.uppercase(Locale.ROOT)
        countryToCurrency[cc]?.let { return it }
        if (eurozoneCountries.contains(cc)) return "€"
        return "$"
    }

    fun isArabicSpeakingCountry(countryCode: String): Boolean =
        arabicSpeakingCountries.contains(countryCode.uppercase(Locale.ROOT))

    /**
     * Detects the user's physical country and returns a default currency symbol.
     *
     * Priority order:
     * 1. SIM country ISO  — where the SIM is registered (most reliable for expats)
     * 2. Network country ISO — the cellular network the device is currently on
     * 3. Device locale country — fallback if no SIM (Wi-Fi only / emulator)
     *
     * This means an Indian user living in Saudi Arabia with a Saudi SIM will correctly
     * get SAR (ر.س) instead of the locale-language country (e.g. GBP from "English UK").
     */
    fun detectFromDevice(context: Context? = null): String {
        if (context != null) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            // SIM country — most reliable; tells us where the SIM plan is registered
            val simCountry = tm?.simCountryIso?.uppercase(Locale.ROOT)?.takeIf { it.length == 2 }
            if (simCountry != null) return currencyForCountry(simCountry)
            // Network country — the tower the device is connected to right now
            val networkCountry = tm?.networkCountryIso?.uppercase(Locale.ROOT)?.takeIf { it.length == 2 }
            if (networkCountry != null) return currencyForCountry(networkCountry)
        }
        // Final fallback: device language region (least reliable for expats)
        val localeCountry = Locale.getDefault().country.takeIf { it.isNotEmpty() }
        return if (localeCountry != null) currencyForCountry(localeCountry) else "$"
    }

    /**
     * The textual Saudi Riyal abbreviation used as the stored currency symbol (unchanged from
     * before — custom currency, persistence, and the exchange-rate converter all keep working
     * with this plain string). UI call sites that want the new official symbol glyph instead
     * check [isSaudiRiyalSymbol] and swap in `R.drawable.ic_saudi_riyal` for just that one spot.
     */
    const val SAUDI_RIYAL_SYMBOL = "ر.س"

    fun isSaudiRiyalSymbol(symbol: String): Boolean = symbol == SAUDI_RIYAL_SYMBOL
}
