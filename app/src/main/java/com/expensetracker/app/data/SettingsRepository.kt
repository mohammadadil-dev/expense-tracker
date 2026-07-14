package com.expensetracker.app.data

import android.content.Context

/**
 * Tiny SharedPreferences wrapper for the few persistent settings the app needs.
 * No cloud sync, no account, nothing leaves the device.
 */
class SettingsRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** "en" | "ar" | "hi" — defaults to English until the user picks otherwise. */
    var languagePref: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    /**
     * True only after the user has explicitly picked a currency on the setup screen.
     * Intentionally separate from KEY_CURRENCY so that existing installs with a
     * previously auto-detected/defaulted currency still see the picker on next launch.
     */
    var currencySetupDone: Boolean
        get() = prefs.getBoolean(KEY_CURRENCY_SETUP_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_CURRENCY_SETUP_DONE, value).apply()

    var currencySymbol: String
        get() = prefs.getString(KEY_CURRENCY, null) ?: "$"
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()

    /** Optional first name used to personalize the Home dashboard greeting
     * ("Good evening, Mohammad"). Empty by default — the greeting falls back to a
     * name-less phrasing until the user sets one in Settings. Never sent anywhere. */
    var displayName: String
        get() = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DISPLAY_NAME, value).apply()

    /** The user's own UPI ID (VPA, e.g. "name@bank") — India-only, used to build the
     * "Request via UPI" QR/link on Khata entries where someone owes the user money.
     * Stored locally only, same as every other setting here; never sent anywhere. */
    var myUpiId: String
        get() = prefs.getString(KEY_MY_UPI_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MY_UPI_ID, value).apply()

    var firstRunDone: Boolean
        get() = prefs.getBoolean(KEY_FIRST_RUN_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_RUN_DONE, value).apply()

    /**
     * True once the user has completed (or legitimately skipped) the first-launch onboarding
     * wizard. Defaults to [currencySetupDone] so existing installs (users who already picked a
     * currency before onboarding existed) bypass the wizard on their next open — we don't want
     * to interrupt a returning user who already configured the app.
     */
    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, currencySetupDone)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    /** Whether Family / Couple Mode is active. Off by default — premium opt-in. */
    var familyModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_FAMILY_MODE_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_FAMILY_MODE_ENABLED, value).apply()

    /** Off by default — SMS detection is opt-in. When on, the app listens for the next
     * incoming SMS via the system consent prompt and queues anything that looks like a debit
     * transaction for the user to review. */
    var smsDetectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_SMS_DETECTION_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_SMS_DETECTION_ENABLED, value).apply()

    /** Whether the daily evening reminder notification is enabled. On by default. */
    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_ENABLED, value).apply()

    /** Hour of day (0–23) at which the daily reminder fires. Default 21 = 9 PM. */
    var reminderHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, 21)
        set(value) = prefs.edit().putInt(KEY_REMINDER_HOUR, value).apply()

    /**
     * True once we've actually asked the user for POST_NOTIFICATIONS (Android 13+) — either
     * during onboarding or via the one-time catch-up check on Dashboard for installs that
     * predate this flag existing. [reminderEnabled] defaulting to true does NOT mean the OS
     * permission was ever granted; this flag is what prevents re-prompting every single launch
     * after a denial, while still guaranteeing every install gets asked at least once.
     */
    var notifPermissionRequested: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_PERMISSION_REQUESTED, false)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_PERMISSION_REQUESTED, value).apply()

    /** Optional second daily reminder (e.g. a morning nudge alongside the evening one).
     * Off by default — opt-in via Settings, independent of the main [reminderEnabled] toggle
     * but only actually scheduled while that master toggle is also on. */
    var reminder2Enabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER2_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER2_ENABLED, value).apply()

    /** Hour of day (0–23) for the optional second reminder. Default 9 = 9 AM, pairing with the
     * 9 PM default first reminder for a morning + evening pair. */
    var reminderHour2: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR2, 9)
        set(value) = prefs.edit().putInt(KEY_REMINDER_HOUR2, value).apply()

    /** Monthly take-home income / salary — shown on the Home dashboard alongside the budget cap
     * so the user can see both what they earn and what they intend to spend. 0 means "not set". */
    var monthlySalary: Double
        get() = java.lang.Double.longBitsToDouble(prefs.getLong(KEY_MONTHLY_SALARY, 0L))
        set(value) = prefs.edit().putLong(KEY_MONTHLY_SALARY, java.lang.Double.doubleToLongBits(value)).apply()

    /**
     * A short, locally generated identifier unique to this install — never transmitted
     * anywhere, just shown in Settings and stamped into exported reports so AG Tech can
     * reference a specific customer's install for support. Generated once, lazily, on first
     * read, then persisted forever (stays stable across app restarts/updates).
     */
    val customerId: String
        get() {
            val existing = prefs.getString(KEY_CUSTOMER_ID, null)
            if (existing != null) return existing
            val generated = "AGT-" + java.util.UUID.randomUUID().toString()
                .replace("-", "")
                .take(8)
                .uppercase()
            prefs.edit().putString(KEY_CUSTOMER_ID, generated).apply()
            return generated
        }

    /** When true, the app shows a biometric (fingerprint / face) or device-credential prompt
     *  every time it comes to the foreground. Off by default. */
    var biometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    /**
     * True once the user has completed (or skipped) the first-launch guided coachmark tour.
     * Defaults to false for new installs. Existing users who installed before the tour existed
     * will see it once, then it is permanently dismissed.
     */
    var hasSeenCoachmarks: Boolean
        get() = prefs.getBoolean(KEY_COACHMARKS_SEEN, false)
        set(value) = prefs.edit().putBoolean(KEY_COACHMARKS_SEEN, value).apply()

    /**
     * Persists the onboarding step to resume from after a mid-wizard locale change.
     *
     * When the user picks a non-English language on Step 1 and taps Continue, the app calls
     * [applyLanguagePreference] which triggers an Activity recreation. To avoid restarting the
     * wizard from Step 0, we save the resume step (2) and the entered name before the recreation,
     * then restore them when the wizard is next shown. Reset to 0 when onboarding completes.
     */
    var onboardingResumeStep: Int
        get() = prefs.getInt(KEY_ONBOARDING_RESUME_STEP, 0)
        set(value) = prefs.edit().putInt(KEY_ONBOARDING_RESUME_STEP, value).apply()

    /** Name typed on Step 0 — saved so it survives the mid-wizard Activity recreation. */
    var onboardingPendingName: String
        get() = prefs.getString(KEY_ONBOARDING_PENDING_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ONBOARDING_PENDING_NAME, value).apply()

    /**
     * Hides the banner ad when true — intended for taking clean Play Store screenshots.
     * Toggle via a long-press on the version label in Settings. Not exposed in UI otherwise.
     */
    var screenshotMode: Boolean
        get() = prefs.getBoolean(KEY_SCREENSHOT_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_SCREENSHOT_MODE, value).apply()

    // ── Daily logging streak ──────────────────────────────────────────────────

    /** Number of consecutive calendar days on which the user logged at least one expense. */
    var logStreak: Int
        get() = prefs.getInt(KEY_LOG_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_LOG_STREAK, value).apply()

    /** ISO date string (yyyy-MM-dd) of the last day an expense was logged. */
    var logStreakLastDate: String
        get() = prefs.getString(KEY_LOG_STREAK_LAST_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LOG_STREAK_LAST_DATE, value).apply()

    /** All-time highest streak. */
    var logStreakBest: Int
        get() = prefs.getInt(KEY_LOG_STREAK_BEST, 0)
        set(value) = prefs.edit().putInt(KEY_LOG_STREAK_BEST, value).apply()

    // ── Payday countdown ─────────────────────────────────────────────────────

    /** Day of month on which the user gets paid (1–31). 0 = not configured. */
    var paydayDayOfMonth: Int
        get() = prefs.getInt(KEY_PAYDAY_DAY, 0)
        set(value) = prefs.edit().putInt(KEY_PAYDAY_DAY, value).apply()

    /**
     * True once we've asked Google's in-app review API to show its native rating dialog.
     * Fires exactly once per install, after the 5th expense is logged — see
     * [com.expensetracker.app.util.InAppReviewManager]. Google's own review API is also
     * quota-limited on its end regardless of this flag, so this just avoids us re-requesting
     * every single time the user logs an expense after the 5th.
     */
    var hasRequestedReview: Boolean
        get() = prefs.getBoolean(KEY_HAS_REQUESTED_REVIEW, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_REQUESTED_REVIEW, value).apply()

    companion object {
        private const val PREFS_NAME = "expense_tracker_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_CURRENCY_SETUP_DONE = "currency_setup_done"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_MY_UPI_ID = "my_upi_id"
        private const val KEY_FIRST_RUN_DONE = "first_run_done"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_CUSTOMER_ID = "customer_id"
        private const val KEY_SMS_DETECTION_ENABLED = "sms_detection_enabled"
        private const val KEY_MONTHLY_SALARY = "monthly_salary"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_COACHMARKS_SEEN = "coachmarks_seen"
        private const val KEY_ONBOARDING_RESUME_STEP = "onboarding_resume_step"
        private const val KEY_ONBOARDING_PENDING_NAME = "onboarding_pending_name"
        private const val KEY_SCREENSHOT_MODE = "screenshot_mode"
        private const val KEY_LOG_STREAK = "log_streak"
        private const val KEY_LOG_STREAK_LAST_DATE = "log_streak_last_date"
        private const val KEY_LOG_STREAK_BEST = "log_streak_best"
        private const val KEY_PAYDAY_DAY = "payday_day_of_month"
        private const val KEY_FAMILY_MODE_ENABLED = "family_mode_enabled"
        private const val KEY_HAS_REQUESTED_REVIEW = "has_requested_review"
        private const val KEY_NOTIF_PERMISSION_REQUESTED = "notif_permission_requested"
        private const val KEY_REMINDER2_ENABLED = "reminder2_enabled"
        private const val KEY_REMINDER_HOUR2 = "reminder_hour2"
    }
}
