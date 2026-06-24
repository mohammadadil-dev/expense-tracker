package com.expensetracker.app.util;

/**
 * Month keys are always "yyyy-MM" (matches [YearMonth.toString]); expense dates are
 * always "yyyy-MM-dd" (matches [LocalDate.toString]). Keeping these as plain ISO strings
 * makes the Room queries trivial and avoids any timezone ambiguity.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004J\u000e\u0010\b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004J\u000e\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004J\u0016\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0006J\u001e\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0006J\u001e\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u0006J\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u00162\u0006\u0010\f\u001a\u00020\rJ\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u00162\u0006\u0010\u0018\u001a\u00020\u0004J\u000e\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004J\u0016\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u001c\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0004J*\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u001e2\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u001f\u001a\u00020\u0006J\u0016\u0010 \u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020!J\u0016\u0010\"\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010#\u001a\u00020$2\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010%\u001a\u00020\u0004J\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00040\u00162\u0006\u0010\f\u001a\u00020\rJ\u0014\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00040\u00162\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010(\u001a\u00020\u0004\u00a8\u0006)"}, d2 = {"Lcom/expensetracker/app/util/DateUtils;", "", "()V", "currentMonthKey", "", "daysElapsedInMonth", "", "key", "daysInMonth", "firstWeekdayIndexOfMonth", "formatExpenseDate", "dateIso", "locale", "Ljava/util/Locale;", "hijriFirstWeekdayIndex", "hijriYear", "hijriMonth", "hijriMonthLabel", "hijriMonthLength", "hijriToIso", "hijriDay", "hijriWeekdayLabels", "", "last12MonthKeysEndingAt", "endKey", "monthKeyFromDate", "monthLabel", "monthLabelForDisplay", "previousMonthKey", "shiftHijriMonth", "Lkotlin/Pair;", "delta", "shiftMonthKey", "", "shortMonthLabel", "toHijri", "Lcom/expensetracker/app/util/HijriDate;", "todayIso", "weekdayMediumLabels", "weekdayShortLabels", "yesterdayIso", "app_debug"})
public final class DateUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.DateUtils INSTANCE = null;
    
    private DateUtils() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String todayIso() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String yesterdayIso() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String currentMonthKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String monthKeyFromDate(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String shiftMonthKey(@org.jetbrains.annotations.NotNull()
    java.lang.String key, long delta) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String monthLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String shortMonthLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    /**
     * Same "yyyy-MM" [key] as [monthLabel], but for Arabic shows the Hijri month/year that
     * the 1st of that Gregorian month falls in (e.g. "محرم 1448") instead of the Gregorian
     * name — matching how individual expense dates and the calendar picker already display
     * Hijri for Arabic. Since Hijri months don't align 1:1 with Gregorian ones, this is the
     * Hijri month containing day 1, not a strict month-for-month mapping; everything else
     * (the underlying month key, querying, stats) stays Gregorian — only this label changes.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String monthLabelForDisplay(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    public final int daysInMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return 0;
    }
    
    /**
     * How many blank leading cells a Sunday-first calendar grid needs before day 1 of
     * [key] — i.e. the day-of-week of the 1st, reindexed so Sunday = 0 … Saturday = 6.
     */
    public final int firstWeekdayIndexOfMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return 0;
    }
    
    /**
     * Narrow, localized weekday initials (Sunday-first) for a calendar grid header, e.g.
     * ["S","M","T","W","T","F","S"] in English. 2023-01-01 is used purely as a known
     * Sunday anchor to walk through one full week — the actual year/month is irrelevant.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> weekdayShortLabels(@org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    /**
     * For the current real-world month, returns how many days have elapsed so far (so a
     * "per day" average isn't diluted by future days). For any other month, returns the
     * full length of that month.
     */
    public final int daysElapsedInMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String previousMonthKey(@org.jetbrains.annotations.NotNull()
    java.lang.String key) {
        return null;
    }
    
    /**
     * 12 chronologically ascending month keys, the last one being [endKey].
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> last12MonthKeysEndingAt(@org.jetbrains.annotations.NotNull()
    java.lang.String endKey) {
        return null;
    }
    
    /**
     * Converts a stored Gregorian "yyyy-MM-dd" expense date to its Hijri equivalent,
     * using the Umm al-Qura calendar — the one used officially in Saudi Arabia/the Gulf,
     * and the same one Android's own "Islamic calendar" setting is based on.
     * java.time's [HijrahChronology] is itself built on Umm al-Qura data (its chronology
     * id is literally "Hijrah-umalqura"), so no extra calendar library is needed.
     *
     * Verified directly: 2026-06-21 (today, as of this change) converts to AH 1448-01-06,
     * i.e. 6 Muharram 1448 — matching the current real-world Hijri year.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.util.HijriDate toHijri(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    /**
     * Formats a single expense's stored "yyyy-MM-dd" date for display. In Arabic, this
     * shows the Hijri (Islamic, Umm al-Qura) calendar equivalent with Arabic month names
     * — e.g. "21 Jun 2026" becomes "6 محرم 1448". Everywhere else (storage, month
     * grouping/keys, stats, the trend chart) keeps using the Gregorian date untouched —
     * only this on-screen text changes.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatExpenseDate(@org.jetbrains.annotations.NotNull()
    java.lang.String dateIso, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    /**
     * Days in the given Hijri month (always 29 or 30).
     */
    public final int hijriMonthLength(int hijriYear, int hijriMonth) {
        return 0;
    }
    
    /**
     * Sunday-first blank-leading-cell count before day 1 of the given Hijri month.
     */
    public final int hijriFirstWeekdayIndex(int hijriYear, int hijriMonth) {
        return 0;
    }
    
    /**
     * Steps a Hijri (year, month) pair by [delta] months, wrapping the year as needed.
     * Hijri years always have exactly 12 months (leap years only add a day to month 12,
     * they never add a 13th month), so plain integer math is correct here — no need to
     * go through java.time's chronology-arithmetic API for this.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.Integer, java.lang.Integer> shiftHijriMonth(int hijriYear, int hijriMonth, int delta) {
        return null;
    }
    
    /**
     * e.g. "Muharram 1448" / "محرم 1448".
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String hijriMonthLabel(int hijriYear, int hijriMonth, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    /**
     * Converts a Hijri (year, month, day) back to a Gregorian "yyyy-MM-dd" ISO string for storage.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String hijriToIso(int hijriYear, int hijriMonth, int hijriDay) {
        return null;
    }
    
    /**
     * Sunday-first weekday labels for the Hijri picker grid. Hijri weekdays don't have an
     * established translated English form the way Gregorian weekdays do, so non-Arabic
     * locales get the common English transliteration of the Arabic names instead.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> hijriWeekdayLabels(@org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
    
    /**
     * Sunday-first, localized 3-letter Gregorian weekday labels (e.g. "Sun", "Mon", …).
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> weekdayMediumLabels(@org.jetbrains.annotations.NotNull()
    java.util.Locale locale) {
        return null;
    }
}