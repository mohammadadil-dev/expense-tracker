package com.expensetracker.app.util;

/**
 * Google Drive cloud backup for Expense Tracker.
 *
 * Uses the DRIVE_APPDATA scope so the backup lives in a private, app-specific
 * folder that is NOT visible in the user's Drive UI — it won't clutter their
 * Drive root and can only be read by this app.
 *
 * SETUP REQUIRED (one-time, in Google Cloud Console):
 * 1. Create or open a project at console.cloud.google.com
 * 2. Enable the "Google Drive API"
 * 3. Go to APIs & Services → Credentials → Create Credentials → OAuth 2.0 Client ID
 * 4. Application type: Android
 * 5. Package name: com.agtech.expensetracker
 * 6. SHA-1 fingerprint: run `keytool -list -v -keystore <your-release-keystore>`
 *    and paste the SHA-1 shown for the release key.
 * 7. No google-services.json needed — Android OAuth for Drive works without it.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0012\u001a\u00020\fJ\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u00102\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u001a"}, d2 = {"Lcom/expensetracker/app/util/DriveBackupManager;", "", "()V", "APP_DATA_SPACE", "", "BACKUP_FILENAME", "BACKUP_MIME_TYPE", "DRIVE_SCOPE", "Lcom/google/android/gms/common/api/Scope;", "getDRIVE_SCOPE", "()Lcom/google/android/gms/common/api/Scope;", "buildDriveService", "Lcom/google/api/services/drive/Drive;", "context", "Landroid/content/Context;", "account", "Lcom/google/android/gms/auth/api/signin/GoogleSignInAccount;", "download", "drive", "getAuthorizedAccount", "getSignInOptions", "Lcom/google/android/gms/auth/api/signin/GoogleSignInOptions;", "signOut", "", "upload", "jsonContent", "app_debug"})
public final class DriveBackupManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BACKUP_FILENAME = "expense_tracker_backup.json";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BACKUP_MIME_TYPE = "application/json";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String APP_DATA_SPACE = "appDataFolder";
    
    /**
     * The Drive scope used for sign-in and permission checks.
     */
    @org.jetbrains.annotations.NotNull()
    private static final com.google.android.gms.common.api.Scope DRIVE_SCOPE = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.DriveBackupManager INSTANCE = null;
    
    private DriveBackupManager() {
        super();
    }
    
    /**
     * The Drive scope used for sign-in and permission checks.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.common.api.Scope getDRIVE_SCOPE() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.auth.api.signin.GoogleSignInOptions getSignInOptions() {
        return null;
    }
    
    /**
     * Returns the cached signed-in account if it already has Drive permission, else null.
     * Call on the Main thread; does no network I/O.
     */
    @org.jetbrains.annotations.Nullable()
    public final com.google.android.gms.auth.api.signin.GoogleSignInAccount getAuthorizedAccount(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void signOut(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Builds a Drive REST client authenticated with the given account.
     * Must be called from a background thread (network transport is created here).
     */
    @org.jetbrains.annotations.NotNull()
    public final com.google.api.services.drive.Drive buildDriveService(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.google.android.gms.auth.api.signin.GoogleSignInAccount account) {
        return null;
    }
    
    /**
     * Uploads [jsonContent] as the app's backup file in Drive appDataFolder.
     * If a backup file already exists it is overwritten in-place (single slot),
     * so the user's Drive never accumulates stale backup copies.
     *
     * Returns the Drive file ID of the saved backup.
     * Must be called from a background thread.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String upload(@org.jetbrains.annotations.NotNull()
    com.google.api.services.drive.Drive drive, @org.jetbrains.annotations.NotNull()
    java.lang.String jsonContent) {
        return null;
    }
    
    /**
     * Downloads the backup JSON from Drive and returns it as a String,
     * or null if no backup has been saved yet.
     * Must be called from a background thread.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String download(@org.jetbrains.annotations.NotNull()
    com.google.api.services.drive.Drive drive) {
        return null;
    }
}