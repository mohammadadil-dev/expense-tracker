package com.expensetracker.app.util;

/**
 * Applies the user's chosen app language regardless of the phone's system language,
 * using AppCompat's per-app language API (works back to API 26 via its compat backport).
 *
 * There is no "follow system" option — the app always speaks one of its own supported
 * languages, defaulting to English for "en" and any unrecognized/legacy stored value.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/expensetracker/app/util/LocaleHelper;", "", "()V", "applyLanguagePreference", "", "pref", "", "app_release"})
public final class LocaleHelper {
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.LocaleHelper INSTANCE = null;
    
    private LocaleHelper() {
        super();
    }
    
    public final void applyLanguagePreference(@org.jetbrains.annotations.NotNull()
    java.lang.String pref) {
    }
}