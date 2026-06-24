package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a{\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u0010\b\u0002\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u0014H\u0007\u00a2\u0006\u0002\u0010\u0015\u00a8\u0006\u0016"}, d2 = {"BentoCard", "", "title", "", "value", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "gradientColors", "", "Landroidx/compose/ui/graphics/Color;", "subtitle", "progress", "", "visible", "", "entranceDelayMillis", "", "onClick", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/graphics/vector/ImageVector;Ljava/util/List;Ljava/lang/String;Ljava/lang/Float;ZILkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;)V", "app_debug"})
public final class BentoCardKt {
    
    /**
     * One gradient "glass" tile in the Home dashboard's Bento Grid (Monthly Spending, Budget
     * Remaining, Savings Goal, Subscription Cost). Each tile owns its own [gradientColors] for a
     * distinct identity, a soft top-start highlight + hairline border standing in for a glass
     * surface (true backdrop blur via `Modifier.blur()` only takes effect on API 31+, so this
     * gradient-based approximation is used everywhere so every device looks the same), a
     * press-dip, and a fade/slide/scale-in entrance staggered by [entranceDelayMillis].
     */
    @androidx.compose.runtime.Composable()
    public static final void BentoCard(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    java.util.List<androidx.compose.ui.graphics.Color> gradientColors, @org.jetbrains.annotations.Nullable()
    java.lang.String subtitle, @org.jetbrains.annotations.Nullable()
    java.lang.Float progress, boolean visible, int entranceDelayMillis, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}