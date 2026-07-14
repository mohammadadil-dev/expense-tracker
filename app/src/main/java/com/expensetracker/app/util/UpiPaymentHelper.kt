package com.expensetracker.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
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
     * @param payeeName shown in the payer's UPI app as who they're paying. Left out of the link
     *   entirely if blank — a fallback placeholder like "Me" would show up as the shop/person's
     *   own name on the payer's bank confirmation screen, which is worse than just omitting it
     *   (most UPI apps fall back to showing the VPA itself when `pn` is absent).
     * @param amount the exact outstanding balance — always prefilled so the payer doesn't have
     *   to type it themselves.
     * @param note a short transaction note (e.g. the Khata party's name), shown in the payer's
     *   UPI app for their own reference.
     */
    fun buildUpiUri(payeeVpa: String, payeeName: String, amount: Double, note: String): Uri {
        val builder = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", payeeVpa)
        if (payeeName.isNotBlank()) {
            builder.appendQueryParameter("pn", payeeName)
        }
        return builder
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
     * Pulls a VPA out of the raw text decoded from a scanned QR code — used by the "scan their
     * UPI QR" flow on the Khata "Their UPI ID" field, so the user never has to type or be told
     * someone else's VPA out loud.
     *
     * Handles the two shapes QR-encoded UPI payment info actually comes in:
     *  - A `upi://pay?pa=<vpa>&pn=...` deep link (the overwhelming majority of personal/merchant
     *    UPI QR codes, including the ones this app itself generates for "Request via UPI").
     *  - A bare VPA string with nothing else around it (some minimal/legacy QR generators).
     *
     * Returns null if [raw] doesn't parse into anything that passes [isValidVpa] — the caller is
     * expected to show a "couldn't find a UPI ID in that QR code" error in that case, not a crash.
     */
    fun extractVpaFromQrContent(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null

        if (trimmed.startsWith("upi://", ignoreCase = true)) {
            val uri = try { Uri.parse(trimmed) } catch (e: Exception) { null }
            val vpa = uri?.getQueryParameter("pa")?.trim()
            return vpa?.takeIf { isValidVpa(it) }
        }

        return trimmed.takeIf { isValidVpa(it) }
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

    /**
     * Saves [bitmap] to `cacheDir/qrcodes/` and returns a `content://` [Uri] via this app's
     * existing [androidx.core.content.FileProvider] (see `res/xml/file_paths.xml`) — needed
     * because WhatsApp (and every other share target) can't read a raw `file://` path or an
     * in-memory bitmap directly. Mirrors the same FileProvider pattern already used by
     * `PdfExporter`/`CsvExporter`/`BackupManager` for sharing generated files.
     *
     * Why this exists at all: a `upi://pay` link is not auto-linkified (tappable) inside
     * WhatsApp message bubbles the way an `https://` link is — WhatsApp only recognizes the
     * http(s) scheme for that. Sending the actual QR *image* alongside the link means the
     * recipient always has something they can scan or forward, even though the raw link text
     * shows up as plain, non-clickable text.
     */
    fun saveQrToCache(context: Context, bitmap: Bitmap): Uri {
        val dir = File(context.cacheDir, "qrcodes").apply { mkdirs() }
        val file = File(dir, "upi_qr_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
