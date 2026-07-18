package com.expensetracker.app.util

import com.expensetracker.app.data.CurrencyLocaleMapper
import java.util.Locale
import kotlin.math.abs

object Formatters {

    fun money(amount: Double, currencySymbol: String): String {
        val sign = if (amount < 0) "-" else ""
        val formatted = String.format(Locale.US, "%,.2f", abs(amount))
        return "$sign$currencySymbol$formatted"
    }

    /**
     * Plain-text money format for spots that can't render the vector currency icon MoneyText
     * draws (compact chip labels, list subtitles, etc. — anywhere too cramped or too plain to
     * host an Icon composable). The raw "ر.س" Saudi Riyal abbreviation reads as broken/garbled
     * text at small sizes, so it's swapped for the "SAR" ISO code instead — the same convention
     * already used for outbound WhatsApp reminder text (see KhataDetailScreen/KhataScreen's
     * `plainAmount` builders). Every other currency keeps its existing symbol, unaffected.
     */
    fun moneyWithCode(amount: Double, currencySymbol: String): String {
        val sign = if (amount < 0) "-" else ""
        val formatted = String.format(Locale.US, "%,.2f", abs(amount))
        val prefix = if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) "SAR " else currencySymbol
        return "$sign$prefix$formatted"
    }

    /** True if [formatted] (the output of [money]) was built with the Saudi Riyal symbol. */
    fun isSaudiRiyalAmount(formatted: String): Boolean =
        formatted.removePrefix("-").startsWith(CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL)

    /** Strips the "ر.س" prefix from [formatted], keeping any leading "-" sign intact. */
    fun stripSaudiRiyalSymbol(formatted: String): String =
        if (formatted.startsWith("-")) {
            "-" + formatted.removePrefix("-").removePrefix(CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL)
        } else {
            formatted.removePrefix(CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL)
        }
}
