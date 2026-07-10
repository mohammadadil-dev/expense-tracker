package com.expensetracker.app.util

import android.app.Notification
import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.expensetracker.app.ExpenseApp
import com.expensetracker.app.data.PendingSmsExpense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Reads notifications posted by the device's **default SMS app only** to catch bank/UPI
 * transaction messages even while Expense Tracker itself is closed.
 *
 * This exists because the older approach — Android's SMS User Consent API, wired up in
 * [SmsConsentManager] — can only ever catch a message in the few minutes the app happens to be
 * open in the foreground (see that file's doc comment), which in practice meant it almost never
 * fired for a real transaction SMS arriving at a random time of day. This service is the fix:
 * notification listeners run as long as the system allows, independent of whether this app's own
 * Activity is open.
 *
 * Scope is deliberately narrow — [onNotificationPosted] returns immediately for any notification
 * not posted by [Telephony.Sms.getDefaultSmsPackage], so this never reads notifications from any
 * other app on the device (no banking apps, no other messaging apps). Same privacy scope as the
 * old SMS-only approach, just captured through a mechanism that actually works in the background.
 *
 * The user must manually grant "Notification access" for this app in system settings — unlike
 * POST_NOTIFICATIONS there is no in-app runtime permission dialog for this; see the SMS detection
 * section of SettingsScreen for the flow that sends the user to that settings screen. The service
 * itself is declared in the manifest but does nothing at all unless (a) that access is granted
 * AND (b) [com.expensetracker.app.data.SettingsRepository.smsDetectionEnabled] is also on.
 *
 * Detected messages land in the exact same pending-review queue as the old flow — nothing is ever
 * saved as a real expense without the user reviewing it first in the SMS review sheet.
 */
class TransactionNotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val app = application as? ExpenseApp ?: return
        if (!app.settings.smsDetectionEnabled) return

        val defaultSmsPackage = runCatching { Telephony.Sms.getDefaultSmsPackage(this) }.getOrNull()
        if (defaultSmsPackage == null || sbn.packageName != defaultSmsPackage) return

        val extras = sbn.notification.extras
        val text = buildString {
            extras.getCharSequence(Notification.EXTRA_TITLE)?.let { append(it).append(' ') }
            extras.getCharSequence(Notification.EXTRA_TEXT)?.let { append(it) }
            // Some SMS apps post an expanded body here instead of (or in addition to) EXTRA_TEXT.
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.let { append(' ').append(it) }
        }.trim()
        if (text.isBlank()) return

        val parsed = SmsExpenseParser.parse(text) ?: return

        serviceScope.launch {
            val categories = app.repository.categories.first()
            val categoryId = SmsExpenseParser.guessCategoryId(parsed.categoryNameKeyGuess, categories)
            app.repository.addPendingSmsExpense(
                PendingSmsExpense(
                    rawMessage = text,
                    amount = parsed.amount,
                    description = parsed.merchantOrNote,
                    suggestedCategoryId = categoryId,
                    date = DateUtils.todayIso(),
                    receivedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
