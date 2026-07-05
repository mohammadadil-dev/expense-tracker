package com.expensetracker.app;

/**
 * The only Activity in the app. Everything else is composables hosted by a single
 * NavHost — no other screens, no external intents, no network permissions needed.
 *
 * Extends AppCompatActivity (not plain ComponentActivity) so AppCompat's per-app
 * language backport can reliably recreate this activity when the user switches
 * language at runtime via LocaleHelper / AppCompatDelegate.setApplicationLocales().
 *
 * Also hosts the SMS User Consent listener: showing the system consent prompt requires an
 * Activity, so this is where the [ActivityResultLauncher] and [SmsConsentManager] live. The
 * `by viewModels()` delegate resolves to the same ExpenseViewModel instance Compose's
 * `viewModel()` call inside AppNav() gets (both are scoped to this Activity), so a consented
 * SMS's text can be forwarded straight into the existing data flow with no extra plumbing.
 *
 * Biometric lock: if the user has enabled the app lock in Settings, onResume shows a
 * fingerprint/device-credential prompt. The app content is set in onCreate so the NavHost
 * is always initialised — the lock just blocks the Activity window visually until auth passes.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0014\u001a\u00020\u0015H\u0002J\u0012\u0010\u0016\u001a\u00020\u00152\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0014J\b\u0010\u0019\u001a\u00020\u0015H\u0014J\b\u0010\u001a\u001a\u00020\u0015H\u0014J\b\u0010\u001b\u001a\u00020\u0015H\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000e\u001a\u00020\u000f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001d"}, d2 = {"Lcom/expensetracker/app/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "appUpdateManager", "Lcom/google/android/play/core/appupdate/AppUpdateManager;", "biometricPromptShowing", "", "smsConsentManager", "Lcom/expensetracker/app/util/SmsConsentManager;", "smsResultLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "updateResultLauncher", "Landroidx/activity/result/IntentSenderRequest;", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "getViewModel", "()Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "checkForAppUpdate", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "Companion", "app_release"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private com.expensetracker.app.util.SmsConsentManager smsConsentManager;
    private androidx.activity.result.ActivityResultLauncher<android.content.Intent> smsResultLauncher;
    private com.google.android.play.core.appupdate.AppUpdateManager appUpdateManager;
    private androidx.activity.result.ActivityResultLauncher<androidx.activity.result.IntentSenderRequest> updateResultLauncher;
    private boolean biometricPromptShowing = false;
    private static boolean sessionAuthenticated = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    private final com.expensetracker.app.viewmodel.ExpenseViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void checkForAppUpdate() {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    protected void onStart() {
    }
    
    @java.lang.Override()
    protected void onStop() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/expensetracker/app/MainActivity$Companion;", "", "()V", "sessionAuthenticated", "", "getSessionAuthenticated", "()Z", "setSessionAuthenticated", "(Z)V", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final boolean getSessionAuthenticated() {
            return false;
        }
        
        public final void setSessionAuthenticated(boolean p0) {
        }
    }
}