package com.expensetracker.app.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.expensetracker.app.MainActivity
import com.expensetracker.app.R
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.SettingsRepository
import java.time.LocalDate

/**
 * Fires every Sunday evening with a personalised weekly spending summary.
 *
 * Computes the spend for the last 7 days vs. the 7 days before that and emits a
 * push notification: "This week you spent X — 12% less than last week 🎉"
 *
 * Scheduled by [WeeklyDigestScheduler] as a KEEP PeriodicWorkRequest that runs
 * weekly. The worker itself is idempotent — running it twice won't double-notify
 * because Android WorkManager deduplicates by the unique tag.
 */
class WeeklyDigestWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = SettingsRepository(context)
        val db = AppDatabase.getInstance(context)

        val today = LocalDate.now()
        val weekStart     = today.minusDays(6).toString()   // 7 days ago (inclusive)
        val prevWeekStart = today.minusDays(13).toString()  // 14 days ago

        // One-shot snapshot of all expenses
        val allExpenses = db.expenseDao().getAllOnce()

        val thisWeekTotal = allExpenses
            .filter { it.date >= weekStart && it.date <= today.toString() }
            .sumOf { it.amount }

        val lastWeekTotal = allExpenses
            .filter { it.date >= prevWeekStart && it.date < weekStart }
            .sumOf { it.amount }

        val currencySymbol = settings.currencySymbol
        val thisWeekFormatted = Formatters.money(thisWeekTotal, currencySymbol)

        val body = buildDigestBody(context, thisWeekTotal, lastWeekTotal, thisWeekFormatted)

        sendNotification(context, body)
        return Result.success()
    }

    private fun buildDigestBody(
        context: Context,
        thisWeek: Double,
        lastWeek: Double,
        thisWeekFormatted: String
    ): String {
        return when {
            lastWeek <= 0.0 -> context.getString(R.string.digest_body_first_week, thisWeekFormatted)
            thisWeek <= 0.0 -> context.getString(R.string.digest_body_no_spend)
            else -> {
                val diffPct = ((thisWeek - lastWeek) / lastWeek * 100).toInt()
                when {
                    diffPct <= -5  -> context.getString(R.string.digest_body_less, thisWeekFormatted, -diffPct)
                    diffPct >= 5   -> context.getString(R.string.digest_body_more, thisWeekFormatted, diffPct)
                    else           -> context.getString(R.string.digest_body_same, thisWeekFormatted)
                }
            }
        }
    }

    private fun sendNotification(context: Context, body: String) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, REQUEST_CODE, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.digest_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val CHANNEL_ID      = "weekly_digest"
        const val WORK_TAG        = "weekly_digest_work"
        private const val NOTIFICATION_ID = 1002
        private const val REQUEST_CODE    = 2001
    }
}
