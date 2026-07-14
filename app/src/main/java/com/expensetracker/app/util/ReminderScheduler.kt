package com.expensetracker.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * Schedules or cancels the daily expense reminder via AlarmManager.setAndAllowWhileIdle().
 *
 * WHY setAndAllowWhileIdle() and NOT setAlarmClock():
 * ────────────────────────────────────────────────────
 * setAlarmClock() requires SCHEDULE_EXACT_ALARM (API 31+), a special permission that
 * triggers a Google Play policy review — apps without a valid "clock/calendar" use case
 * are rejected. setAndAllowWhileIdle() needs zero permissions, is Doze-compatible, and
 * fires within ~10 minutes of the target time — perfectly fine for a daily reminder nudge.
 *
 * Daily repeat: AlarmManager fires once. ReminderReceiver re-schedules the next day's
 * alarm immediately after firing (same pattern as a real alarm clock app).
 */
object ReminderScheduler {

    /** Legacy WorkManager tag — kept so cancel() can clean up any old enqueued work. */
    const val WORK_NAME = "daily_expense_reminder"

    /** Request code used to identify our PendingIntent — must be unique per alarm/slot. */
    private const val REQUEST_CODE_SLOT1 = 2001
    private const val REQUEST_CODE_SLOT2 = 2002

    /** Intent extra carrying which reminder slot (1 = main, 2 = optional second) fired, so
     * [ReminderReceiver] knows which hour setting to use when re-arming for tomorrow. */
    const val EXTRA_SLOT = "slot"

    /**
     * Schedule (or reschedule) the next daily alarm at [hour]:00 for the given [slot]
     * (1 = main reminder, 2 = optional second reminder — independent alarms, independent times).
     *
     * If the target time today has already passed the alarm is set for tomorrow.
     * Calling this again with a new hour replaces the existing alarm via
     * FLAG_UPDATE_CURRENT on the PendingIntent.
     */
    fun schedule(context: Context, hour: Int, slot: Int = 1, forceReschedule: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, slot)

        val now    = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // If the hour already passed today, aim for tomorrow.
        if (!target.after(now)) target.add(Calendar.DAY_OF_MONTH, 1)

        // setAndAllowWhileIdle() wakes the device from Doze and fires within ~10 minutes
        // of the target time — no special permission required.
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            target.timeInMillis,
            pendingIntent
        )
    }

    /** Cancel the pending daily reminder alarm for the given [slot] (1 = main, 2 = second). */
    fun cancel(context: Context, slot: Int = 1) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context, slot))

        if (slot == 1) {
            // Also clean up any WorkManager work left from the old implementation.
            try {
                androidx.work.WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            } catch (_: Exception) { /* WorkManager may not be initialised yet */ }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildPendingIntent(context: Context, slot: Int): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_NOTIFY
            putExtra(EXTRA_SLOT, slot)
        }
        val requestCode = if (slot == 2) REQUEST_CODE_SLOT2 else REQUEST_CODE_SLOT1
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
