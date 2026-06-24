package com.expensetracker.app.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale

/**
 * Hijri (Islamic) calendar components for a single date, e.g. year = 1448, month = 1,
 * day = 6, monthName = "محرم"/"Muharram". [month] is 1-based (1 = Muharram).
 */
data class HijriDate(val year: Int, val month: Int, val day: Int, val monthName: String)

/**
 * Month keys are always "yyyy-MM" (matches [YearMonth.toString]); expense dates are
 * always "yyyy-MM-dd" (matches [LocalDate.toString]). Keeping these as plain ISO strings
 * makes the Room queries trivial and avoids any timezone ambiguity.
 */
object DateUtils {

    fun todayIso(): String = LocalDate.now().toString()

    fun yesterdayIso(): String = LocalDate.now().minusDays(1).toString()

    fun currentMonthKey(): String = YearMonth.now().toString()

    fun monthKeyFromDate(dateIso: String): String = dateIso.substring(0, 7)

    fun shiftMonthKey(key: String, delta: Long): String =
        YearMonth.parse(key).plusMonths(delta).toString()

    fun monthLabel(key: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        return YearMonth.parse(key).atDay(1).format(formatter)
    }

    fun shortMonthLabel(key: String, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("MMM yy", locale)
        return YearMonth.parse(key).atDay(1).format(formatter)
    }

    /**
     * Same "yyyy-MM" [key] as [monthLabel], but for Arabic shows the Hijri month/year that
     * the 1st of that Gregorian month falls in (e.g. "محرم 1448") instead of the Gregorian
     * name — matching how individual expense dates and the calendar picker already display
     * Hijri for Arabic. Since Hijri months don't align 1:1 with Gregorian ones, this is the
     * Hijri month containing day 1, not a strict month-for-month mapping; everything else
     * (the underlying month key, querying, stats) stays Gregorian — only this label changes.
     */
    fun monthLabelForDisplay(key: String, locale: Locale): String {
        return if (locale.language == "ar") {
            val firstDayIso = YearMonth.parse(key).atDay(1).toString()
            val h = toHijri(firstDayIso, locale)
            "${h.monthName} ${h.year}"
        } else {
            monthLabel(key, locale)
        }
    }

    fun daysInMonth(key: String): Int = YearMonth.parse(key).lengthOfMonth()

    /**
     * How many blank leading cells a Sunday-first calendar grid needs before day 1 of
     * [key] — i.e. the day-of-week of the 1st, reindexed so Sunday = 0 … Saturday = 6.
     */
    fun firstWeekdayIndexOfMonth(key: String): Int {
        val first = YearMonth.parse(key).atDay(1)
        return first.dayOfWeek.value % 7
    }

    /**
     * Narrow, localized weekday initials (Sunday-first) for a calendar grid header, e.g.
     * ["S","M","T","W","T","F","S"] in English. 2023-01-01 is used purely as a known
     * Sunday anchor to walk through one full week — the actual year/month is irrelevant.
     */
    fun weekdayShortLabels(locale: Locale): List<String> {
        val formatter = DateTimeFormatter.ofPattern("EEEEE", locale)
        val sunday = LocalDate.of(2023, 1, 1)
        return (0..6).map { sunday.plusDays(it.toLong()).format(formatter) }
    }

    /**
     * For the current real-world month, returns how many days have elapsed so far (so a
     * "per day" average isn't diluted by future days). For any other month, returns the
     * full length of that month.
     */
    fun daysElapsedInMonth(key: String): Int =
        if (key == currentMonthKey()) LocalDate.now().dayOfMonth else daysInMonth(key)

    fun previousMonthKey(key: String): String = shiftMonthKey(key, -1)

    /** 12 chronologically ascending month keys, the last one being [endKey]. */
    fun last12MonthKeysEndingAt(endKey: String): List<String> {
        val end = YearMonth.parse(endKey)
        return (11 downTo 0).map { end.minusMonths(it.toLong()).toString() }
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
    fun toHijri(dateIso: String, locale: Locale): HijriDate {
        val hijri: HijrahDate = HijrahChronology.INSTANCE.date(LocalDate.parse(dateIso))
        val monthName = DateTimeFormatter.ofPattern("MMMM", locale)
            .withChronology(HijrahChronology.INSTANCE)
            .format(hijri)
        return HijriDate(
            year = hijri.get(ChronoField.YEAR),
            month = hijri.get(ChronoField.MONTH_OF_YEAR),
            day = hijri.get(ChronoField.DAY_OF_MONTH),
            monthName = monthName
        )
    }

    /**
     * Formats a single expense's stored "yyyy-MM-dd" date for display. In Arabic, this
     * shows the Hijri (Islamic, Umm al-Qura) calendar equivalent with Arabic month names
     * — e.g. "21 Jun 2026" becomes "6 محرم 1448". Everywhere else (storage, month
     * grouping/keys, stats, the trend chart) keeps using the Gregorian date untouched —
     * only this on-screen text changes.
     */
    fun formatExpenseDate(dateIso: String, locale: Locale): String {
        return if (locale.language == "ar") {
            val h = toHijri(dateIso, locale)
            "${h.day} ${h.monthName} ${h.year}"
        } else {
            DateTimeFormatter.ofPattern("d MMM yyyy", locale).format(LocalDate.parse(dateIso))
        }
    }

    // ---- Hijri calendar-grid helpers (for CalendarDatePickerDialog) ----------------------
    // Everything below mirrors the Gregorian helpers above (daysInMonth, firstWeekdayIndexOfMonth,
    // shiftMonthKey) but for a (hijriYear, hijriMonth) pair instead of a "yyyy-MM" key, since
    // Hijri months can't be represented by java.time's Gregorian-only YearMonth/"yyyy-MM" keys.

    /** Days in the given Hijri month (always 29 or 30). */
    fun hijriMonthLength(hijriYear: Int, hijriMonth: Int): Int =
        HijrahChronology.INSTANCE.date(hijriYear, hijriMonth, 1).lengthOfMonth()

    /** Sunday-first blank-leading-cell count before day 1 of the given Hijri month. */
    fun hijriFirstWeekdayIndex(hijriYear: Int, hijriMonth: Int): Int {
        val firstDayGregorian = LocalDate.from(HijrahChronology.INSTANCE.date(hijriYear, hijriMonth, 1))
        return firstDayGregorian.dayOfWeek.value % 7
    }

    /**
     * Steps a Hijri (year, month) pair by [delta] months, wrapping the year as needed.
     * Hijri years always have exactly 12 months (leap years only add a day to month 12,
     * they never add a 13th month), so plain integer math is correct here — no need to
     * go through java.time's chronology-arithmetic API for this.
     */
    fun shiftHijriMonth(hijriYear: Int, hijriMonth: Int, delta: Int): Pair<Int, Int> {
        val zeroBased = (hijriMonth - 1) + delta
        val newYear = hijriYear + Math.floorDiv(zeroBased, 12)
        val newMonth = Math.floorMod(zeroBased, 12) + 1
        return newYear to newMonth
    }

    /** e.g. "Muharram 1448" / "محرم 1448". */
    fun hijriMonthLabel(hijriYear: Int, hijriMonth: Int, locale: Locale): String {
        val hijrahDate = HijrahChronology.INSTANCE.date(hijriYear, hijriMonth, 1)
        val monthName = DateTimeFormatter.ofPattern("MMMM", locale)
            .withChronology(HijrahChronology.INSTANCE)
            .format(hijrahDate)
        return "$monthName $hijriYear"
    }

    /** Converts a Hijri (year, month, day) back to a Gregorian "yyyy-MM-dd" ISO string for storage. */
    fun hijriToIso(hijriYear: Int, hijriMonth: Int, hijriDay: Int): String =
        LocalDate.from(HijrahChronology.INSTANCE.date(hijriYear, hijriMonth, hijriDay)).toString()

    /**
     * Sunday-first weekday labels for the Hijri picker grid. Hijri weekdays don't have an
     * established translated English form the way Gregorian weekdays do, so non-Arabic
     * locales get the common English transliteration of the Arabic names instead.
     */
    fun hijriWeekdayLabels(locale: Locale): List<String> {
        return if (locale.language == "ar") {
            listOf("أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")
        } else {
            listOf("Ahad", "Ithnayn", "Thulatha", "Arbi'a", "Khamis", "Jum'ah", "Sabt")
        }
    }

    /** Sunday-first, localized 3-letter Gregorian weekday labels (e.g. "Sun", "Mon", …). */
    fun weekdayMediumLabels(locale: Locale): List<String> {
        val formatter = DateTimeFormatter.ofPattern("EEE", locale)
        val sunday = LocalDate.of(2023, 1, 1)
        return (0..6).map { sunday.plusDays(it.toLong()).format(formatter) }
    }
}
