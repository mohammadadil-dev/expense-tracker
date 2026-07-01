package com.expensetracker.app.util;

/**
 * Schedules or cancels the daily expense reminder notification.
 *
 * Uses WorkManager's unique periodic work so there's never more than one
 * outstanding reminder chain. Calling [schedule] with a new hour cancels
 * the old chain and re-queues with the correct initial delay.
 *
 * Initial delay calculation:
 *  - Find the next wall-clock occurrence of [hour]:00 (today if still in the
 *    future, tomorrow if that time has already passed today).
 *  - That delay is passed to WorkManager; after it fires, it repeats every 24 h.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0016\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/expensetracker/app/util/ReminderScheduler;", "", "()V", "WORK_NAME", "", "cancel", "", "context", "Landroid/content/Context;", "schedule", "hour", "", "app_debug"})
public final class ReminderScheduler {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String WORK_NAME = "daily_expense_reminder";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.ReminderScheduler INSTANCE = null;
    
    private ReminderScheduler() {
        super();
    }
    
    public final void schedule(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int hour) {
    }
    
    public final void cancel(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}