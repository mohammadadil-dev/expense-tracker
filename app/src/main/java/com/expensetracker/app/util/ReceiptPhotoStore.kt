package com.expensetracker.app.util

import android.content.Context
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
 * Nothing here ever leaves the device: no upload, no network call. A photo is either
 * captured fresh via the camera (written directly to a file this class hands out a
 * FileProvider [Uri] for) or copied byte-for-byte from a gallery pick.
 */
object ReceiptPhotoStore {

    private const val DIR_NAME = "receipts"

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
     * picker) into a new permanent file under `filesDir/receipts/`. A straight copy — rather
     * than just storing [sourceUri] — is needed because the photo picker only grants
     * temporary read access; without copying, the image would become unreadable the next
     * time the app restarts. Returns the new file's absolute path, or null if the copy failed.
     */
    fun copyFromGallery(context: Context, sourceUri: Uri): String? {
        return try {
            val destFile = newPhotoFile(context)
            val input = context.contentResolver.openInputStream(sourceUri) ?: return null
            input.use { inStream ->
                FileOutputStream(destFile).use { outStream -> inStream.copyTo(outStream) }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

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
