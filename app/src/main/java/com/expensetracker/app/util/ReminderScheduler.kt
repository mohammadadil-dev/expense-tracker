package com.expensetracker.app.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules or cancels the daily expense reminder notification.
 *
 * Uses WorkManager's unique periodic work so there's never more than one
 * outstanding reminder chain. Calling [schedule] with a new hour cancels
 * the old chain and re-queues with the correct initial delay.
 *
 * Initial delay calculation:
 *   - Find the next wall-clock occurrence of [hour]:00 (today if still in the
 *     future, tomorrow if that time has already passed today).
 *   - That delay is passed to WorkManager; after it fires, it repeats every 24 h.
 */
object ReminderScheduler {

    private const val WORK_NAME = "daily_expense_reminder"

    fun schedule(context: Context, hour: Int) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // If the target time has already passed today, aim for tomorrow
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }
        val initialDelayMs = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
