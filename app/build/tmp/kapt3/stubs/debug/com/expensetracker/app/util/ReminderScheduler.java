package com.expensetracker.app.util;

/**
 * Schedules or cancels the daily expense reminder using WorkManager.
 *
 * WorkManager is used (rather than AlarmManager) because:
 * - No SCHEDULE_EXACT_ALARM / USE_EXACT_ALARM permissions needed — avoids Play Store rejection.
 * - WorkManager automatically re-enqueues after device reboots — no BroadcastReceiver required.
 * - A "did you log today?" nudge doesn't need split-second accuracy.
 *
 * The first firing is at the next occurrence of [hour]:00. After that WorkManager repeats
 * every 24 hours (may drift slightly in Doze — acceptable for a daily reminder nudge).
 *
 * Pass [forceReschedule] = true when the user actively changes the reminder hour so the
 * existing work is cancelled and re-queued with the corrected initial delay.
 * Leave it false on startup (KEEP keeps the existing schedule intact).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ \u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/expensetracker/app/util/ReminderScheduler;", "", "()V", "WORK_NAME", "", "cancel", "", "context", "Landroid/content/Context;", "schedule", "hour", "", "forceReschedule", "", "app_debug"})
public final class ReminderScheduler {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WORK_NAME = "daily_expense_reminder";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.ReminderScheduler INSTANCE = null;
    
    private ReminderScheduler() {
        super();
    }
    
    public final void schedule(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int hour, boolean forceReschedule) {
    }
    
    /**
     * Cancel a previously scheduled reminder.
     */
    public final void cancel(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}