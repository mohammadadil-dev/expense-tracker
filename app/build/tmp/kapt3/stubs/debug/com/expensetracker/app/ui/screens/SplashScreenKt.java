package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"SPLASH_HOLD_MS", "", "SplashScreen", "", "onFinished", "Lkotlin/Function0;", "app_debug"})
public final class SplashScreenKt {
    private static final long SPLASH_HOLD_MS = 1700L;
    
    /**
     * A short, self-animated brand moment shown once on cold start: the same wallet+coin motif
     * as the launcher icon fades/scales in, then a coin "drops" into the wallet with a small
     * bounce, before auto-advancing to the dashboard. Pure Compose — no extra splash-screen
     * library/dependency needed, and no network or assets involved (just drawn shapes), so it
     * costs nothing and stays fully offline like the rest of the app.
     */
    @androidx.compose.runtime.Composable()
    public static final void SplashScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFinished) {
    }
}