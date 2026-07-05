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
 * Fired by [AlarmManager.setAlarmClock] at the user's chosen reminder time.
 *
 * Responsibilities:
 * 1. Show the "Did you log today's expenses?" notification.
 * 2. Immediately re-schedule the next day's alarm so the reminder repeats daily
 *    without needing WorkManager (which is subject to Doze deferral).
 *
 * Also handles [Intent.ACTION_BOOT_COMPLETED] and [Intent.ACTION_MY_PACKAGE_REPLACED]
 * so the alarm survives device reboots and app updates.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val settings = SettingsRepository(context)

        when (intent.action) {
            ACTION_NOTIFY -> {
                showNotification(context)
                // Re-arm for tomorrow — setAlarmClock fires once, so we must reschedule.
                if (settings.reminderEnabled) {
                    ReminderScheduler.schedule(context, settings.reminderHour)
                }
            }
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Alarms are cleared on reboot / update — re-register if the user has them on.
                if (settings.reminderEnabled) {
                    ReminderScheduler.schedule(context, settings.reminderHour)
                }
            }
        }
    }

    companion object {
        /** Intent action used when the alarm fires. */
        const val ACTION_NOTIFY = "com.expensetracker.app.DAILY_REMINDER"
        const val NOTIFICATION_ID = 1001

        /** Call from Settings to verify the channel + permission work without waiting for the alarm. */
        fun showNotification(context: Context) {
            val tapIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, ReminderWorker.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_body))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.reminder_notification_body))
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            runCatching {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            }.onFailure { e ->
                android.util.Log.w("ReminderReceiver", "Could not show notification: ${e.message}")
            }
        }
    }
}
