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

    companion object {
        private const val PREFS_NAME = "expense_tracker_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_CURRENCY_SETUP_DONE = "currency_setup_done"
        private const val KEY_DISPLAY_NAME = "display_name"
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
    }
}
