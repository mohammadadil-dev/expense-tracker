package com.expensetracker.app.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

/**
 * Schedules [WeeklyDigestWorker] to fire every Sunday at 20:00 local time.
 *
 * Uses KEEP so a pre-existing identical schedule isn't cancelled and rescheduled every
 * app launch — the work is only enqueued if it doesn't already exist.
 *
 * Call [schedule] once from Application.onCreate (or whenever notifications are enabled).
 * Call [cancel] if the user opts out of weekly digests.
 */
object WeeklyDigestScheduler {

    private const val WORK_TAG = WeeklyDigestWorker.WORK_TAG

    /** Target: next Sunday at 20:00. */
    private fun initialDelayMs(): Long {
        val now = LocalDateTime.now()
        var nextSunday = now
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .withHour(20)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        // If today is Sunday but already past 20:00, push to next Sunday
        if (!nextSunday.isAfter(now)) {
            nextSunday = nextSunday.plusWeeks(1)
        }

        return Duration.between(now, nextSunday).toMillis().coerceAtLeast(0)
    }

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<WeeklyDigestWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMs(), TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG)
    }
}
