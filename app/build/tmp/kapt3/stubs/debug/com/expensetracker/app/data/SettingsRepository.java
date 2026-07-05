package com.expensetracker.app.data;

/**
 * Tiny SharedPreferences wrapper for the few persistent settings the app needs.
 * No cloud sync, no account, nothing leaves the device.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0010\u0006\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u0000 D2\u00020\u0001:\u0001DB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R$\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\f\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\r\u0010\t\"\u0004\b\u000e\u0010\u000bR$\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u000f8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0012R$\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u000f8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u0018\u0010\u0012\"\u0004\b\u0019\u0010\u0014R$\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u001b\u0010\t\"\u0004\b\u001c\u0010\u000bR$\u0010\u001d\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u001e\u0010\t\"\u0004\b\u001f\u0010\u000bR$\u0010 \u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u000f8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b!\u0010\u0012\"\u0004\b\"\u0010\u0014R$\u0010$\u001a\u00020#2\u0006\u0010\u0005\u001a\u00020#8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b%\u0010&\"\u0004\b\'\u0010(R$\u0010)\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b*\u0010\t\"\u0004\b+\u0010\u000bR$\u0010,\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u000f8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b-\u0010\u0012\"\u0004\b.\u0010\u0014R$\u00100\u001a\u00020/2\u0006\u0010\u0005\u001a\u00020/8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b1\u00102\"\u0004\b3\u00104R\u0016\u00105\u001a\n 7*\u0004\u0018\u00010606X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u00108\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b9\u0010\t\"\u0004\b:\u0010\u000bR$\u0010;\u001a\u00020/2\u0006\u0010\u0005\u001a\u00020/8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b<\u00102\"\u0004\b=\u00104R$\u0010>\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b?\u0010\t\"\u0004\b@\u0010\u000bR$\u0010A\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\bB\u0010\t\"\u0004\bC\u0010\u000b\u00a8\u0006E"}, d2 = {"Lcom/expensetracker/app/data/SettingsRepository;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "value", "", "biometricEnabled", "getBiometricEnabled", "()Z", "setBiometricEnabled", "(Z)V", "currencySetupDone", "getCurrencySetupDone", "setCurrencySetupDone", "", "currencySymbol", "getCurrencySymbol", "()Ljava/lang/String;", "setCurrencySymbol", "(Ljava/lang/String;)V", "customerId", "getCustomerId", "displayName", "getDisplayName", "setDisplayName", "firstRunDone", "getFirstRunDone", "setFirstRunDone", "hasSeenCoachmarks", "getHasSeenCoachmarks", "setHasSeenCoachmarks", "languagePref", "getLanguagePref", "setLanguagePref", "", "monthlySalary", "getMonthlySalary", "()D", "setMonthlySalary", "(D)V", "onboardingDone", "getOnboardingDone", "setOnboardingDone", "onboardingPendingName", "getOnboardingPendingName", "setOnboardingPendingName", "", "onboardingResumeStep", "getOnboardingResumeStep", "()I", "setOnboardingResumeStep", "(I)V", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "reminderEnabled", "getReminderEnabled", "setReminderEnabled", "reminderHour", "getReminderHour", "setReminderHour", "screenshotMode", "getScreenshotMode", "setScreenshotMode", "smsDetectionEnabled", "getSmsDetectionEnabled", "setSmsDetectionEnabled", "Companion", "app_debug"})
public final class SettingsRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "expense_tracker_settings";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LANGUAGE = "language";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CURRENCY = "currency";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CURRENCY_SETUP_DONE = "currency_setup_done";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_DISPLAY_NAME = "display_name";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_FIRST_RUN_DONE = "first_run_done";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ONBOARDING_DONE = "onboarding_done";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CUSTOMER_ID = "customer_id";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SMS_DETECTION_ENABLED = "sms_detection_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_MONTHLY_SALARY = "monthly_salary";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_REMINDER_ENABLED = "reminder_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_REMINDER_HOUR = "reminder_hour";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_COACHMARKS_SEEN = "coachmarks_seen";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ONBOARDING_RESUME_STEP = "onboarding_resume_step";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ONBOARDING_PENDING_NAME = "onboarding_pending_name";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_SCREENSHOT_MODE = "screenshot_mode";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.SettingsRepository.Companion Companion = null;
    
    public SettingsRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLanguagePref() {
        return null;
    }
    
    public final void setLanguagePref(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final boolean getCurrencySetupDone() {
        return false;
    }
    
    public final void setCurrencySetupDone(boolean value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrencySymbol() {
        return null;
    }
    
    public final void setCurrencySymbol(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    public final void setDisplayName(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final boolean getFirstRunDone() {
        return false;
    }
    
    public final void setFirstRunDone(boolean value) {
    }
    
    public final boolean getOnboardingDone() {
        return false;
    }
    
    public final void setOnboardingDone(boolean value) {
    }
    
    public final boolean getSmsDetectionEnabled() {
        return false;
    }
    
    public final void setSmsDetectionEnabled(boolean value) {
    }
    
    public final boolean getReminderEnabled() {
        return false;
    }
    
    public final void setReminderEnabled(boolean value) {
    }
    
    public final int getReminderHour() {
        return 0;
    }
    
    public final void setReminderHour(int value) {
    }
    
    public final double getMonthlySalary() {
        return 0.0;
    }
    
    public final void setMonthlySalary(double value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCustomerId() {
        return null;
    }
    
    public final boolean getBiometricEnabled() {
        return false;
    }
    
    public final void setBiometricEnabled(boolean value) {
    }
    
    public final boolean getHasSeenCoachmarks() {
        return false;
    }
    
    public final void setHasSeenCoachmarks(boolean value) {
    }
    
    public final int getOnboardingResumeStep() {
        return 0;
    }
    
    public final void setOnboardingResumeStep(int value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOnboardingPendingName() {
        return null;
    }
    
    public final void setOnboardingPendingName(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final boolean getScreenshotMode() {
        return false;
    }
    
    public final void setScreenshotMode(boolean value) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/expensetracker/app/data/SettingsRepository$Companion;", "", "()V", "KEY_BIOMETRIC_ENABLED", "", "KEY_COACHMARKS_SEEN", "KEY_CURRENCY", "KEY_CURRENCY_SETUP_DONE", "KEY_CUSTOMER_ID", "KEY_DISPLAY_NAME", "KEY_FIRST_RUN_DONE", "KEY_LANGUAGE", "KEY_MONTHLY_SALARY", "KEY_ONBOARDING_DONE", "KEY_ONBOARDING_PENDING_NAME", "KEY_ONBOARDING_RESUME_STEP", "KEY_REMINDER_ENABLED", "KEY_REMINDER_HOUR", "KEY_SCREENSHOT_MODE", "KEY_SMS_DETECTION_ENABLED", "PREFS_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}