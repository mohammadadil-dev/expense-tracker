package com.expensetracker.app.util;

/**
 * Wraps Google's SMS User Consent API so the app can read the text of an incoming transaction
 * SMS without ever declaring the READ_SMS/RECEIVE_SMS permissions — Play Store restricts those
 * to apps whose core purpose is SMS handling, which a general expense tracker isn't.
 *
 * Every matching message still requires the user to tap "Allow" on a system-drawn bottom sheet;
 * there is no silent background reading. It also can't see SMS history — only messages that
 * arrive while [startListening] is active, which in practice means while the app is open, since
 * showing the consent prompt requires a foreground Activity.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/expensetracker/app/util/SmsConsentManager;", "", "context", "Landroid/content/Context;", "launcher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "(Landroid/content/Context;Landroidx/activity/result/ActivityResultLauncher;)V", "isReceiverRegistered", "", "receiver", "Landroid/content/BroadcastReceiver;", "startListening", "", "stopListening", "app_release"})
public final class SmsConsentManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> launcher = null;
    private boolean isReceiverRegistered = false;
    @org.jetbrains.annotations.NotNull()
    private final android.content.BroadcastReceiver receiver = null;
    
    public SmsConsentManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.activity.result.ActivityResultLauncher<android.content.Intent> launcher) {
        super();
    }
    
    /**
     * Starts (or restarts) the listening window for the next incoming SMS.
     */
    public final void startListening() {
    }
    
    public final void stopListening() {
    }
}