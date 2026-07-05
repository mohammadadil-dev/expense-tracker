package com.expensetracker.app.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

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
object ReminderScheduler {

    const val WORK_NAME = "daily_expense_reminder"

    fun schedule(context: Context, hour: Int, forceReschedule: Boolean = false) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) target.add(Calendar.DAY_OF_MONTH, 1)
        val initialDelayMs = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()

        val policy = if (forceReschedule)
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
        else
            ExistingPeriodicWorkPolicy.KEEP

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, policy, request)
    }

    /** Cancel a previously scheduled reminder. */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
