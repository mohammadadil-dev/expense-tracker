package com.expensetracker.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Text-only SMS fallback for Khata WhatsApp reminders. Used only when WhatsApp (and WhatsApp
 * Business) aren't installed — WhatsApp stays the default channel everywhere in the app; this
 * exists purely so a customer without WhatsApp still gets a reminder instead of the app just
 * giving up with a "WhatsApp not installed" toast.
 *
 * Deliberately opens the device's own SMS app (`Intent.ACTION_SENDTO` + `smsto:`) rather than
 * sending via `SmsManager`: no `SEND_SMS` permission needed, nothing goes out silently, and the
 * user still taps Send themselves in their own messaging app — the same one-tap-confirmed
 * pattern already used for every WhatsApp send in this app (see KhataDetailScreen.kt /
 * KhataScreen.kt / KhataCollectionsScreen.kt).
 *
 * SMS is plain text, so any bill photo or UPI QR code that would have ridden along with a
 * WhatsApp share simply can't come along here — that's an inherent limit of the protocol, not
 * a missing feature, and it lines up with the audience this exists for: someone without
 * WhatsApp is very likely on a phone that can't scan a QR code either.
 */
object SmsFallback {

    /** Opens the device's SMS app pre-filled with [phone] and [message]. Returns true if an
     *  SMS app was found and launched, false if none exists on the device (callers should show
     *  their own "nothing to send with" message in that case). */
    fun send(context: Context, phone: String, message: String): Boolean {
        if (phone.isBlank()) return false
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
            putExtra("sms_body", message)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
