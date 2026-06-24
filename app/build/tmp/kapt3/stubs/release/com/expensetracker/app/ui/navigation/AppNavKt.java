package com.expensetracker.app.ui.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\b\u0010\u0002\u001a\u00020\u0003H\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0004"}, d2 = {"TRANSITION_MS", "", "AppNav", "", "app_release"})
public final class AppNavKt {
    private static final int TRANSITION_MS = 260;
    
    /**
     * Splash, then two screens, one shared ViewModel — everything is backed by the local Room
     * database, so there's no server/account state to keep in sync across screens.
     *
     * The splash screen is removed from the back stack as soon as it hands off to the
     * dashboard (`popUpTo(SPLASH) { inclusive = true }`), so the back button never returns to it.
     *
     * Settings slides in from the trailing edge and slides back out on the way home,
     * instead of the default instant cut, for a more tactile feel.
     */
    @androidx.compose.runtime.Composable()
    public static final void AppNav() {
    }
}