package com.expensetracker.app.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Applies the user's chosen app language regardless of the phone's system language,
 * using AppCompat's per-app language API (works back to API 26 via its compat backport).
 *
 * There is no "follow system" option — the app always speaks one of its own supported
 * languages, defaulting to English for "en" and any unrecognized/legacy stored value.
 */
object LocaleHelper {

    fun applyLanguagePreference(pref: String) {
        val locales = when (pref) {
            "ar" -> LocaleListCompat.forLanguageTags("ar")
            "hi" -> LocaleListCompat.forLanguageTags("hi")
            "ur" -> LocaleListCompat.forLanguageTags("ur")
            // Android normalises "tl" (Tagalog) to the canonical BCP 47 tag "fil" (Filipino)
            // when resolving resources — so the locale tag must be "fil" or Android falls back
            // to English because it looks for values-fil/, not values-tl/.
            "tl" -> LocaleListCompat.forLanguageTags("fil")
            "bn" -> LocaleListCompat.forLanguageTags("bn")
            else -> LocaleListCompat.forLanguageTags("en")
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
