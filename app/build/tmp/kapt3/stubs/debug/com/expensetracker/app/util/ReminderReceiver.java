package com.expensetracker.app.util;

/**
 * Fired by [AlarmManager.setAlarmClock] at the user's chosen reminder time.
 *
 * Responsibilities:
 * 1. Show the "Did you log today's expenses?" notification.
 * 2. Immediately re-schedule the next day's alarm so the reminder repeats daily
 *   without needing WorkManager (which is subject to Doze deferral).
 *
 * Also handles [Intent.ACTION_BOOT_COMPLETED] and [Intent.ACTION_MY_PACKAGE_REPLACED]
 * so the alarm survives device reboots and app updates.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016\u00a8\u0006\n"}, d2 = {"Lcom/expensetracker/app/util/ReminderReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "Companion", "app_debug"})
public final class ReminderReceiver extends android.content.BroadcastReceiver {
    
    /**
     * Intent action used when the alarm fires.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_NOTIFY = "com.expensetracker.app.DAILY_REMINDER";
    public static final int NOTIFICATION_ID = 1001;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.ReminderReceiver.Companion Companion = null;
    
    public ReminderReceiver() {
        super();
    }
    
    @java.lang.Override()
    public void onReceive(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.content.Intent intent) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/expensetracker/app/util/ReminderReceiver$Companion;", "", "()V", "ACTION_NOTIFY", "", "NOTIFICATION_ID", "", "showNotification", "", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Call from Settings to verify the channel + permission work without waiting for the alarm.
         */
        public final void showNotification(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
    }
}