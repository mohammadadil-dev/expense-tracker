package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001ar\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\t0\b2\b\u0010\n\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000b\u001a\u00020\u00032\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\r2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007\u00a8\u0006\u0012"}, d2 = {"MonthCalendarView", "", "monthKey", "", "firstWeekdayIndex", "", "daysInMonth", "dailyTotals", "", "", "selectedDay", "todayIso", "weekdayLabels", "", "onDaySelected", "Lkotlin/Function1;", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class MonthCalendarViewKt {
    
    /**
     * A Sunday-first month calendar grid. Each day with at least one expense gets a small dot;
     * tapping a day reports it via [onDaySelected] so the caller can show that day's expenses
     * below. Rendered eagerly (not lazily) since a month never has more than ~42 cells.
     */
    @androidx.compose.runtime.Composable()
    public static final void MonthCalendarView(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, int firstWeekdayIndex, int daysInMonth, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> dailyTotals, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedDay, @org.jetbrains.annotations.NotNull()
    java.lang.String todayIso, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> weekdayLabels, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onDaySelected, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}