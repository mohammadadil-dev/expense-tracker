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
