package com.expensetracker.app.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import java.io.ByteArrayOutputStream

/**
 * Google Drive cloud backup for Expense Tracker.
 *
 * Uses the DRIVE_APPDATA scope so the backup lives in a private, app-specific
 * folder that is NOT visible in the user's Drive UI — it won't clutter their
 * Drive root and can only be read by this app.
 *
 * SETUP REQUIRED (one-time, in Google Cloud Console):
 *  1. Create or open a project at console.cloud.google.com
 *  2. Enable the "Google Drive API"
 *  3. Go to APIs & Services → Credentials → Create Credentials → OAuth 2.0 Client ID
 *  4. Application type: Android
 *  5. Package name: com.agtech.expensetracker
 *  6. SHA-1 fingerprint: run `keytool -list -v -keystore <your-release-keystore>`
 *     and paste the SHA-1 shown for the release key.
 *  7. No google-services.json needed — Android OAuth for Drive works without it.
 */
object DriveBackupManager {

    private const val BACKUP_FILENAME  = "expense_tracker_backup.json"
    private const val BACKUP_MIME_TYPE = "application/json"
    private const val APP_DATA_SPACE   = "appDataFolder"

    /** The Drive scope used for sign-in and permission checks. */
    val DRIVE_SCOPE = Scope(DriveScopes.DRIVE_APPDATA)

    // ── Sign-in ───────────────────────────────────────────────────────────────

    fun getSignInOptions(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(DRIVE_SCOPE)
            .build()

    /**
     * Returns the cached signed-in account if it already has Drive permission, else null.
     * Call on the Main thread; does no network I/O.
     */
    fun getAuthorizedAccount(context: Context): GoogleSignInAccount? {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return null
        return if (GoogleSignIn.hasPermissions(account, DRIVE_SCOPE)) account else null
    }

    fun signOut(context: Context) {
        GoogleSignIn.getClient(context, getSignInOptions()).signOut()
    }

    // ── Drive service factory ─────────────────────────────────────────────────

    /**
     * Builds a Drive REST client authenticated with the given account.
     * Must be called from a background thread (network transport is created here).
     */
    fun buildDriveService(context: Context, account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential
            .usingOAuth2(context, listOf(DriveScopes.DRIVE_APPDATA))
            .apply { selectedAccount = account.account }
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("Expense Tracker").build()
    }

    // ── Upload ────────────────────────────────────────────────────────────────

    /**
     * Uploads [jsonContent] as the app's backup file in Drive appDataFolder.
     * If a backup file already exists it is overwritten in-place (single slot),
     * so the user's Drive never accumulates stale backup copies.
     *
     * Returns the Drive file ID of the saved backup.
     * Must be called from a background thread.
     */
    fun upload(drive: Drive, jsonContent: String): String {
        val content = ByteArrayContent(BACKUP_MIME_TYPE, jsonContent.toByteArray(Charsets.UTF_8))

        val existing = drive.files().list()
            .setSpaces(APP_DATA_SPACE)
            .setQ("name = '$BACKUP_FILENAME'")
            .setFields("files(id)")
            .execute()
            .files

        return if (existing.isNotEmpty()) {
            // Overwrite the existing backup file
            drive.files()
                .update(existing[0].id, null, content)
                .setFields("id")
                .execute().id
        } else {
            // First backup: create the file in appDataFolder
            val metadata = File().apply {
                name    = BACKUP_FILENAME
                parents = listOf(APP_DATA_SPACE)
            }
            drive.files()
                .create(metadata, content)
                .setFields("id")
                .execute().id
        }
    }

    // ── Download ──────────────────────────────────────────────────────────────

    /**
     * Downloads the backup JSON from Drive and returns it as a String,
     * or null if no backup has been saved yet.
     * Must be called from a background thread.
     */
    fun download(drive: Drive): String? {
        val files = drive.files().list()
            .setSpaces(APP_DATA_SPACE)
            .setQ("name = '$BACKUP_FILENAME'")
            .setFields("files(id)")
            .execute()
            .files

        if (files.isEmpty()) return null

        val out = ByteArrayOutputStream()
        drive.files().get(files[0].id).executeMediaAndDownloadTo(out)
        return out.toString(Charsets.UTF_8.name())
    }
}
