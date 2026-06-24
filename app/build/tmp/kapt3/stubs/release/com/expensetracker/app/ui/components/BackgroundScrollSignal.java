package com.expensetracker.app.ui.components;

/**
 * Continuous "how far has the user scrolled" signal. Each screen with its own scrollable
 * content (Dashboard's LazyColumn, Settings' Column) writes its current scroll position here
 * from a tiny side effect; the background reads it directly inside its Canvas draw block to
 * nudge the blobs, layering a subtle scroll parallax on top of the touch reaction below.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/expensetracker/app/ui/components/BackgroundScrollSignal;", "", "()V", "pixels", "Landroidx/compose/runtime/MutableFloatState;", "getPixels", "()Landroidx/compose/runtime/MutableFloatState;", "app_release"})
public final class BackgroundScrollSignal {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.runtime.MutableFloatState pixels = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.ui.components.BackgroundScrollSignal INSTANCE = null;
    
    private BackgroundScrollSignal() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.runtime.MutableFloatState getPixels() {
        return null;
    }
}