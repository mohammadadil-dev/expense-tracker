package com.expensetracker.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Wraps Google's SMS User Consent API so the app can read the text of an incoming transaction
 * SMS without ever declaring the READ_SMS/RECEIVE_SMS permissions — Play Store restricts those
 * to apps whose core purpose is SMS handling, which a general expense tracker isn't.
 *
 * Every matching message still requires the user to tap "Allow" on a system-drawn bottom sheet;
 * there is no silent background reading. It also can't see SMS history — only messages that
 * arrive while [startListening] is active, which in practice means while the app is open, since
 * showing the consent prompt requires a foreground Activity.
 *
 * This is deliberately the app's only SMS detection method. A background alternative
 * (NotificationListenerService, reading the default SMS app's notifications) was built and then
 * reverted before release: it requires a broad, all-notifications "special app access" grant, and
 * Play Console review has historically treated that pattern — using notification access to read
 * SMS content instead of declaring the restricted READ_SMS/RECEIVE_SMS permission — as a policy
 * circumvention risk for finance apps specifically. Staying foreground-only (detection only works
 * while the app is open) is the trade-off accepted to avoid that risk.
 */
class SmsConsentManager(
    private val context: Context,
    private val launcher: ActivityResultLauncher<Intent>
) {
    private var isReceiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
            val extras = intent.extras ?: return
            val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status
            if (status?.statusCode == CommonStatusCodes.SUCCESS) {
                @Suppress("DEPRECATION")
                val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                consentIntent?.let { runCatching { launcher.launch(it) } }
            }
            // Any other status (timeout after ~5 minutes, etc.) just goes quiet until the
            // caller re-arms listening — there's nothing to show the user for that case.
        }
    }

    /** Starts (or restarts) the listening window for the next incoming SMS. */
    fun startListening() {
        if (!isReceiverRegistered) {
            runCatching {
                ContextCompat.registerReceiver(
                    context,
                    receiver,
                    IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                    ContextCompat.RECEIVER_EXPORTED
                )
                isReceiverRegistered = true
            }
        }
        // No-op failure listener: if Google Play services isn't available on this device, the
        // task just fails quietly and SMS detection simply won't fire — not worth surfacing as
        // an error for an opt-in convenience feature.
        runCatching { SmsRetriever.getClient(context).startSmsUserConsent(null) }
    }

    fun stopListening() {
        if (isReceiverRegistered) {
            runCatching { context.unregisterReceiver(receiver) }
            isReceiverRegistered = false
        }
    }
}
