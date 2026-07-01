package com.expensetracker.app.ui.ads;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"BANNER_UNIT_ID", "", "INTERSTITIAL_UNIT_ID", "BannerAdView", "", "modifier", "Landroidx/compose/ui/Modifier;", "app_release"})
public final class AdComponentsKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BANNER_UNIT_ID = "ca-app-pub-8890346685665889/8936478861";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String INTERSTITIAL_UNIT_ID = "ca-app-pub-8890346685665889/4726982565";
    
    /**
     * Standard 320×50 banner ad embedded via [AndroidView].
     *
     * [AdView] is a legacy View that must be paused/resumed/destroyed in step with the
     * Activity lifecycle — skipping these calls is the #1 cause of banner ad crashes on
     * Compose screens. We attach a [DefaultLifecycleObserver] via [DisposableEffect] to
     * forward every relevant event, then destroy the view when the composable leaves.
     */
    @androidx.compose.runtime.Composable()
    public static final void BannerAdView(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}