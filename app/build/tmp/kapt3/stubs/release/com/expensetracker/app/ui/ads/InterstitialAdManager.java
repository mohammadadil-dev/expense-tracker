package com.expensetracker.app.ui.ads;

/**
 * Singleton that keeps one interstitial loaded and ready to show.
 *
 * Usage:
 * 1. Call [preload] early (LaunchedEffect on screen entry) so the ad is ready.
 * 2. Call [showThen] at the trigger point (e.g. PDF export tap).
 *    - Ad ready → shows it, then calls [onAfterAd] when user dismisses it.
 *    - Ad not ready → calls [onAfterAd] immediately; user is never blocked.
 * 3. After each show or failure, [showThen] automatically reloads the next ad.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u001c\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\b0\u000fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/expensetracker/app/ui/ads/InterstitialAdManager;", "", "()V", "isLoading", "", "loadedAd", "Lcom/google/android/gms/ads/interstitial/InterstitialAd;", "preload", "", "context", "Landroid/content/Context;", "showThen", "activity", "Landroid/app/Activity;", "onAfterAd", "Lkotlin/Function0;", "app_release"})
public final class InterstitialAdManager {
    @org.jetbrains.annotations.Nullable()
    private static com.google.android.gms.ads.interstitial.InterstitialAd loadedAd;
    private static boolean isLoading = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.ui.ads.InterstitialAdManager INSTANCE = null;
    
    private InterstitialAdManager() {
        super();
    }
    
    /**
     * Starts loading in the background; no-op if one is already loaded or loading.
     */
    public final void preload(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Shows the interstitial if ready, then calls [onAfterAd] on dismiss.
     * If no ad is available, [onAfterAd] fires immediately so the user's action
     * still goes through without any delay.
     */
    public final void showThen(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAfterAd) {
    }
}