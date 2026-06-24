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
            else -> LocaleListCompat.forLanguageTags("en")
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
