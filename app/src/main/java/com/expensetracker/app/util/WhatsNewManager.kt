package com.expensetracker.app.util

import android.content.Context

/**
 * Controls the "What's New" feature-discovery overlay.
 *
 * HOW TO USE FOR FUTURE RELEASES
 * ────────────────────────────────
 * When you publish a new version with notable new features:
 * 1. Bump [CURRENT_VERSION] to match the new versionCode (e.g. 16 for v1.6.0).
 * 2. Update the feature list in WhatsNewSheet.kt to reflect the new features.
 * 3. That's it — existing users who update will see the overlay once on next open.
 *    New users will never see it (they get the full onboarding instead).
 */
object WhatsNewManager {

    /** Match this to the versionCode in build.gradle.kts whenever you add new features. */
    private const val CURRENT_VERSION = 15  // v1.5.0

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_WHATS_NEW_VERSION = "whats_new_last_seen_version"

    /**
     * Returns true if the What's New overlay should be shown.
     *
     * Logic:
     * - lastSeen = -1  → key never written (existing user who updated, or first-ever launch)
     * - lastSeen < CURRENT_VERSION → user hasn't seen this version's overlay yet
     * - lastSeen == CURRENT_VERSION → already dismissed, don't show again
     *
     * New users never see it because markOnboardingDone() writes CURRENT_VERSION
     * to prefs BEFORE DashboardScreen is first composed, so shouldShow returns false.
     */
    fun shouldShow(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastSeen = prefs.getInt(KEY_WHATS_NEW_VERSION, -1)
        return lastSeen < CURRENT_VERSION
    }

    /** Call this when the user dismisses or completes the What's New overlay. */
    fun markSeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_WHATS_NEW_VERSION, CURRENT_VERSION)
            .apply()
    }

    /**
     * Call this on first successful onboarding completion so that future updates
     * can distinguish "new install" from "existing user who just updated".
     */
    fun markOnboardingDone(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Only write if no value exists yet — don't overwrite a real version number.
        if (prefs.getInt(KEY_WHATS_NEW_VERSION, -1) == -1) {
            prefs.edit().putInt(KEY_WHATS_NEW_VERSION, CURRENT_VERSION).apply()
        }
    }
}
