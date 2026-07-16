package com.expensetracker.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Manages permanent, on-device storage of receipt/bill photos attached to Khata ledger
 * entries — saved under `filesDir/receipts/` (see `res/xml/file_paths.xml`'s `receipts`
 * files-path), which — unlike `cacheDir` — is never cleared by the OS to free space, since
 * these photos are meant to persist as long as the entry does.
 *
 * Nothing here ever leaves the device: no upload, no network call. Every photo — whether
 * captured fresh via the camera or picked from the gallery — is downscaled and re-encoded as
 * a JPEG (see [MAX_DIMENSION]/[JPEG_QUALITY]) rather than stored at full camera resolution.
 * A receipt only ever needs to be legible when reviewed later or shared over WhatsApp/SMS;
 * full-res photos (often 12MP+, several MB each) waste real storage on the budget devices this
 * app's users are likely to be on, for no visible benefit at the sizes it's ever displayed.
 * Compression is best-effort — any failure falls back to keeping the original file untouched,
 * since losing a bill photo entirely is a worse outcome than one that's a bit large.
 */
object ReceiptPhotoStore {

    private const val DIR_NAME = "receipts"

    /** Longest-side cap a stored receipt photo is downscaled to, in pixels. */
    private const val MAX_DIMENSION = 1600

    /** JPEG re-encode quality (0-100) applied after downscaling. */
    private const val JPEG_QUALITY = 80

    private fun photosDir(context: Context): File =
        File(context.filesDir, DIR_NAME).also { it.mkdirs() }

    private fun newPhotoFile(context: Context): File =
        File(photosDir(context), "receipt_${System.currentTimeMillis()}.jpg")

    /**
     * Creates a new empty file under `filesDir/receipts/` and returns it alongside a
     * FileProvider [Uri] pointing at it — pass the Uri to
     * [androidx.activity.result.contract.ActivityResultContracts.TakePicture], which writes
     * the captured photo directly into that file. The file's absolute path is what gets
     * stored on the [com.expensetracker.app.data.KhataEntryEntity].
     */
    fun newPhotoUriForCamera(context: Context): Pair<File, Uri> {
        val file = newPhotoFile(context)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return file to uri
    }

    /**
     * Copies the image at [sourceUri] (typically a `content://` Uri from the system photo
     * picker) into a new permanent file under `filesDir/receipts/`, downscaling/re-encoding it
     * along the way (see class doc) — needed regardless of compression because the photo
     * picker only grants temporary read access; without copying, the image would become
     * unreadable the next time the app restarts. Returns the new file's absolute path, or null
     * if the copy failed.
     */
    fun copyFromGallery(context: Context, sourceUri: Uri): String? {
        return try {
            val destFile = newPhotoFile(context)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(sourceUri)?.use {
                BitmapFactory.decodeStream(it, null, bounds)
            } ?: return null
            val sampleSize = calculateInSampleSize(bounds, MAX_DIMENSION)
            val bitmap = context.contentResolver.openInputStream(sourceUri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sampleSize })
            } ?: return null
            writeDownscaled(bitmap, destFile)
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Downscales/re-encodes a just-captured camera photo at [path] in place (see class doc).
     * Call this right after [androidx.activity.result.contract.ActivityResultContracts.TakePicture]
     * reports success — the camera app writes full (often very large) resolution directly to
     * the file [newPhotoUriForCamera] handed it, so there's no in-process callback with the raw
     * bytes to intercept before it hits disk; this runs as a quick follow-up pass instead.
     * Safe no-op on any failure — keeps the original, uncompressed photo rather than risk
     * losing it over a storage optimization.
     */
    fun compressCapturedPhoto(path: String) {
        try {
            val file = File(path)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || maxOf(bounds.outWidth, bounds.outHeight) <= MAX_DIMENSION) return
            val sampleSize = calculateInSampleSize(bounds, MAX_DIMENSION)
            val bitmap = BitmapFactory.decodeFile(
                file.absolutePath,
                BitmapFactory.Options().apply { inSampleSize = sampleSize }
            ) ?: return
            writeDownscaled(bitmap, file)
        } catch (e: Exception) {
            // Best-effort — the original capture is still on disk and usable.
        }
    }

    /** Scales [bitmap] down to [MAX_DIMENSION] on its longest side (if it isn't already
     *  smaller) and writes it as a JPEG to [destFile], recycling bitmaps along the way. */
    private fun writeDownscaled(bitmap: Bitmap, destFile: File) {
        val longest = maxOf(bitmap.width, bitmap.height)
        val scale = MAX_DIMENSION.toFloat() / longest
        val finalBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
        } else bitmap
        FileOutputStream(destFile).use { out -> finalBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out) }
        if (finalBitmap !== bitmap) bitmap.recycle()
        finalBitmap.recycle()
    }

    /** Largest power-of-two `inSampleSize` that still leaves the longest side >= [targetSize]
     *  — the standard Android downsample-during-decode pattern, so a 12MP source is never
     *  fully decoded into memory just to immediately scale it back down. */
    private fun calculateInSampleSize(options: BitmapFactory.Options, targetSize: Int): Int {
        var inSampleSize = 1
        val longest = maxOf(options.outWidth, options.outHeight)
        while (longest / (inSampleSize * 2) >= targetSize) inSampleSize *= 2
        return inSampleSize
    }

    /**
     * FileProvider [Uri] for an already-stored receipt photo at [path] (as saved on
     * [com.expensetracker.app.data.KhataEntryEntity.photoPath]) — for handing an existing
     * photo to another app (e.g. attaching it to a WhatsApp share), as opposed to
     * [newPhotoUriForCamera] which is for writing a brand-new one.
     */
    fun uriFor(context: Context, path: String): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))

    /** Deletes the photo file at [path], if any — safe to call with a null/blank/missing path. */
    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        try {
            File(path).delete()
        } catch (_: Exception) {
            // Best-effort cleanup — a leftover orphaned file costs a few KB of storage, not
            // worth surfacing an error to the user over.
        }
    }
}
