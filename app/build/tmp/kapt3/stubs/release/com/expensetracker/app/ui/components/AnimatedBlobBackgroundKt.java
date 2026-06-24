package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\"\u0010\u0000\u001a\u00020\u00012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0007\u001a\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0002\u00a8\u0006\t"}, d2 = {"AnimatedBlobBackground", "", "blobColors", "", "Landroidx/compose/ui/graphics/Color;", "modifier", "Landroidx/compose/ui/Modifier;", "layoutFor", "Lcom/expensetracker/app/ui/components/Blob;", "app_release"})
public final class AnimatedBlobBackgroundKt {
    
    private static final java.util.List<com.expensetracker.app.ui.components.Blob> layoutFor(java.util.List<androidx.compose.ui.graphics.Color> blobColors) {
        return null;
    }
    
    /**
     * Soft, slowly-drifting, gently "breathing" blobs painted behind a screen's content — and,
     * unlike a static gradient, genuinely touch-reactive: dragging a finger across the screen
     * pulls nearby blobs toward it (springing back once released), and every touch-down sends out
     * a soft expanding glow from that point. Each screen mounts its own instance with its own
     * [blobColors] trio so Dashboard, Settings, etc. each get a distinct color identity instead of
     * sharing one global background.
     *
     * Important: the gesture loop below only *observes* pointer positions — it never calls
     * `consume()` on anything — so it stays purely decorative and never steals touches from real
     * UI underneath (buttons, list item taps, scrolling all keep working exactly as before).
     */
    @androidx.compose.runtime.Composable()
    public static final void AnimatedBlobBackground(@org.jetbrains.annotations.NotNull()
    java.util.List<androidx.compose.ui.graphics.Color> blobColors, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}