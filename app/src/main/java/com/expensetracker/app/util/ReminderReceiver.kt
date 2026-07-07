package com.expensetracker.app.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.expensetracker.app.MainActivity
import com.expensetracker.app.R
import com.expensetracker.app.data.SettingsRepository

/**
 * Receives the daily reminder alarm fired by [ReminderScheduler] via AlarmManager.setAlarmClock().
 *
 * Responsibilities:
 * 1. Show a rich "Did you log today's expenses?" notification.
 * 2. Immediately re-schedule tomorrow's alarm (setAlarmClock fires once, not repeating).
 * 3. Re-register the alarm after BOOT_COMPLETED / MY_PACKAGE_REPLACED so it survives
 *    device reboots and app updates.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val settings = SettingsRepository(context)

        when (intent.action) {
            ACTION_NOTIFY -> {
                showNotification(context)
                // setAlarmClock is one-shot — re-arm for tomorrow automatically.
                if (settings.reminderEnabled) {
                    ReminderScheduler.schedule(context, settings.reminderHour)
                }
            }
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Alarms are wiped on reboot and app update — re-register if enabled.
                if (settings.reminderEnabled) {
                    ReminderScheduler.schedule(context, settings.reminderHour)
                }
            }
        }
    }

    companion object {

        /** Action used by the AlarmManager PendingIntent. */
        const val ACTION_NOTIFY = "com.expensetracker.app.DAILY_REMINDER"

        /** Stable notification ID — reusing it replaces any previous reminder notification. */
        const val NOTIFICATION_ID = 1001

        /**
         * Shows the daily expense reminder notification.
         *
         * • PRIORITY_HIGH → heads-up popup on lock screen / always-on display.
         * • BigTextStyle → expandable body with a tip line.
         * • "Add Expense" action button → opens the dashboard directly.
         * • Rotating body text keeps the notification from feeling stale.
         *
         * Safe to call from any context (test button in Settings uses this too).
         */
        fun showNotification(context: Context) {
            // Tap notification → open MainActivity (dashboard).
            val openAppIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // "Add Expense" action button → open MainActivity with a flag so it can
            // auto-open the add-expense sheet (if the app handles the extra).
            val addExpenseIntent = PendingIntent.getActivity(
                context,
                1,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("action", "add_expense")
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = context.getString(R.string.reminder_notification_title)
            val body  = context.getString(R.string.reminder_notification_body)
            val tip   = context.getString(R.string.reminder_notification_tip)
            val addLabel = context.getString(R.string.reminder_action_add_expense)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setSummaryText(tip)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)   // heads-up popup
                .setDefaults(NotificationCompat.DEFAULT_ALL)      // sound + vibrate
                .setContentIntent(openAppIntent)
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    addLabel,
                    addExpenseIntent
                )
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  // show on lock screen
                .build()

            runCatching {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            }.onFailure { e ->
                android.util.Log.w("ReminderReceiver", "Notification failed: ${e.message}")
            }
        }

        /** Channel ID — must match the channel created in ExpenseApp.onCreate(). */
        const val CHANNEL_ID = "daily_reminder"
    }
}
