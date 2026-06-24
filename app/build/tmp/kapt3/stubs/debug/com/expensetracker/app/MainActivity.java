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
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0014J\b\u0010\u0012\u001a\u00020\u000fH\u0014J\b\u0010\u0013\u001a\u00020\u000fH\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0014"}, d2 = {"Lcom/expensetracker/app/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "smsConsentManager", "Lcom/expensetracker/app/util/SmsConsentManager;", "smsResultLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "getViewModel", "()Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onStart", "onStop", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private com.expensetracker.app.util.SmsConsentManager smsConsentManager;
    private androidx.activity.result.ActivityResultLauncher<android.content.Intent> smsResultLauncher;
    
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
    
    @java.lang.Override()
    protected void onStart() {
    }
    
    @java.lang.Override()
    protected void onStop() {
    }
}