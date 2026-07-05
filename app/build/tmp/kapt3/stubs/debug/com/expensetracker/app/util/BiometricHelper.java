package com.expensetracker.app.util;

/**
 * Thin wrapper around AndroidX BiometricPrompt.
 *
 * Supports BIOMETRIC_STRONG (fingerprint, face, iris) with DEVICE_CREDENTIAL (PIN/pattern/password)
 * as a fallback — so even users without enrolled biometrics can still use app lock via their screen
 * lock.
 *
 * [authenticate] shows the prompt. [onSuccess] is called on the main thread after a successful
 * authentication. [onError] is called with a human-readable message if the user cancels or the
 * device can't authenticate.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JB\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u000b2\u0014\b\u0002\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00040\rJ\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/expensetracker/app/util/BiometricHelper;", "", "()V", "authenticate", "", "activity", "Landroidx/fragment/app/FragmentActivity;", "title", "", "subtitle", "onSuccess", "Lkotlin/Function0;", "onError", "Lkotlin/Function1;", "isAvailable", "", "app_debug"})
public final class BiometricHelper {
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.BiometricHelper INSTANCE = null;
    
    private BiometricHelper() {
        super();
    }
    
    /**
     * Returns true if the device can authenticate with biometrics or device credential.
     */
    public final boolean isAvailable(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.FragmentActivity activity) {
        return false;
    }
    
    public final void authenticate(@org.jetbrains.annotations.NotNull()
    androidx.fragment.app.FragmentActivity activity, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String subtitle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onError) {
    }
}