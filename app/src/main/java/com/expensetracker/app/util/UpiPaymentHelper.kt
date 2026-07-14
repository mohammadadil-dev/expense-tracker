package com.expensetracker.app.util

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Locale

/**
 * Builds UPI ("Unified Payments Interface") deep links and QR codes for the Khata
 * "Request via UPI" flow — India-only, gated behind [com.expensetracker.app.data.CurrencyLocaleMapper.isInrSymbol]
 * everywhere this is used.
 *
 * Important: this class only ever *constructs a standard `upi://pay` URI* and hands it off
 * (via QR code or a shared link) to whatever UPI app the recipient has installed — the app
 * itself never touches money, never calls a payment gateway, and never learns whether the
 * payment actually happened. That's identical in kind to how a printed shop QR code works,
 * or how the existing WhatsApp reminder deep-link already works in this app. See
 * FEATURE_SPEC_KHATA_UPI_PAYMENTS.md for the full design rationale.
 */
object UpiPaymentHelper {

    /**
     * Builds a `upi://pay` deep link for [amount] payable to [payeeVpa].
     *
     * @param payeeVpa the payee's UPI ID (e.g. "name@bank") — this app's own [myUpiId], not the
     *   payer's, since the whole point is that the *other* person pays *this* app's user.
     * @param payeeName shown in the payer's UPI app as who they're paying.
     * @param amount the exact outstanding balance — always prefilled so the payer doesn't have
     *   to type it themselves.
     * @param note a short transaction note (e.g. the Khata party's name), shown in the payer's
     *   UPI app for their own reference.
     */
    fun buildUpiUri(payeeVpa: String, payeeName: String, amount: Double, note: String): Uri {
        return Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", payeeVpa)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("am", String.format(Locale.US, "%.2f", amount))
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tn", note)
            .build()
    }

    /**
     * Basic format guard for the "Your UPI ID" Settings field and the optional per-party UPI ID
     * field — not a real VPA-existence check (that would require a network call to a bank/PSP,
     * which this app deliberately never does), just enough to catch obvious typos before they're
     * baked into a QR code. A VPA is always "<handle>@<bank/psp>", no spaces.
     */
    fun isValidVpa(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty() || trimmed.contains(" ")) return false
        val atCount = trimmed.count { it == '@' }
        if (atCount != 1) return false
        val (handle, bank) = trimmed.split("@")
        return handle.isNotBlank() && bank.isNotBlank()
    }

    /**
     * Renders [content] (a UPI URI, as a string) as a square black-on-white QR code bitmap,
     * generated entirely on-device via ZXing — no network call, no external service.
     */
    fun generateQrBitmap(content: String, sizePx: Int): Bitmap {
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}
