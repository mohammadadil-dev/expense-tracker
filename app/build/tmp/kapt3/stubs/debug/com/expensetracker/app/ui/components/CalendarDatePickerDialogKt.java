package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\n\u001a:\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a^\u0010\n\u001a\u00020\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00030\t2\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00032\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a,\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00032\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u00a8\u0006\u0018"}, d2 = {"CalendarDatePickerDialog", "", "initialDateIso", "", "locale", "Ljava/util/Locale;", "onDismiss", "Lkotlin/Function0;", "onConfirm", "Lkotlin/Function1;", "CalendarGrid", "weekdayLabels", "", "firstWeekdayIndex", "", "daysInMonth", "dayToIso", "selectedDateIso", "todayIso", "onDaySelected", "MonthNavHeader", "label", "onPrevious", "onNext", "app_debug"})
public final class CalendarDatePickerDialogKt {
    
    /**
     * Custom Gregorian/Hijri date-picker dialog, replacing the OS-native (Gregorian-only)
     * Material3 DatePicker. Shows whichever calendar system matches [locale] — Hijri for
     * Arabic, Gregorian for everything else — with no manual toggle. Whatever's picked is
     * converted back to a plain Gregorian "yyyy-MM-dd" via [onConfirm], since that's what's
     * stored everywhere else in the app.
     */
    @androidx.compose.runtime.Composable()
    public static final void CalendarDatePickerDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String initialDateIso, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onConfirm) {
    }
    
    /**
     * Prev/next chevrons around the current month/year label, shared by both calendar systems.
     */
    @androidx.compose.runtime.Composable()
    private static final void MonthNavHeader(java.lang.String label, kotlin.jvm.functions.Function0<kotlin.Unit> onPrevious, kotlin.jvm.functions.Function0<kotlin.Unit> onNext) {
    }
    
    /**
     * The 7-column day grid itself, shared by both the Gregorian and Hijri branches above.
     */
    @androidx.compose.runtime.Composable()
    private static final void CalendarGrid(java.util.List<java.lang.String> weekdayLabels, int firstWeekdayIndex, int daysInMonth, kotlin.jvm.functions.Function1<? super java.lang.Integer, java.lang.String> dayToIso, java.lang.String selectedDateIso, java.lang.String todayIso, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onDaySelected) {
    }
}