package com.expensetracker.app.util

import android.content.Context
import android.content.pm.PackageManager

/**
 * Controls force-update behaviour across the whole app.
 *
 * HOW TO FORCE AN UPDATE (one-time setup, never repeat)
 * ───────────────────────────────────────────────────────
 * The update system now works AUTOMATICALLY for every release with no extra steps:
 *
 * Layer 1 — Play IMMEDIATE update: triggers if Play Console priority is 4-5.
 *   Shows a full-screen blocking overlay. User cannot use the app until updated.
 *
 * Layer 2 — Play FLEXIBLE + auto-install (the automatic fallback):
 *   Always available regardless of Play Console priority. Download happens silently
 *   in the background while the user opens the app. When done, the app restarts
 *   automatically and installs the update. No user action needed, no Play Console
 *   steps needed. Works on day 0 of every release.
 *
 * Layer 3 — In-app blocking dialog (this class): fires when installed versionCode
 *   is below MIN_VERSION_CODE. Catches sideloaded APKs and Play Store cache lag.
 *
 * FOR EACH NEW RELEASE: only bump [MIN_VERSION_CODE] below. Nothing else needed.
 */
object ForceUpdateManager {

    /**
     * Change this whenever you publish a release that all users MUST install.
     * Set it to the versionCode of that release. Users on any lower code see the
     * blocking dialog.
     *
     * Current versionCode = 16 (v1.6.0). Users on versionCode 15 or below will see
     * the blocking dialog and cannot use the app until they update.
     */
    const val MIN_VERSION_CODE = 16   // v1.6.0 — all users on 15 or below must update

    /**
     * No longer used by the main update flow — kept for any legacy reference.
     * MainActivity now uses FLEXIBLE + auto-install as the fallback, so every
     * release is a forced update without any Play Console priority setting needed.
     */
    @Suppress("unused")
    const val STALE_DAYS_THRESHOLD = 0

    /** Play Store listing URL for this app. Update the package name if it ever changes. */
    const val PLAY_STORE_URL =
        "https://play.google.com/store/apps/details?id=com.expensetracker.app"

    /**
     * Returns true if the currently installed versionCode is below the minimum
     * required version. When true the app should show a blocking update dialog.
     */
    fun isUpdateRequired(context: Context): Boolean {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            val installed = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                info.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                info.versionCode
            }
            installed < MIN_VERSION_CODE
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
