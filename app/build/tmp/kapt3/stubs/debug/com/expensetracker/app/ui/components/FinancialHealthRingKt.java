package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a2\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u001a\u0015\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000e"}, d2 = {"FinancialHealthRing", "", "score", "", "bandLabel", "", "subScores", "", "Lcom/expensetracker/app/ui/components/HealthSubScore;", "modifier", "Landroidx/compose/ui/Modifier;", "healthBandColor", "Landroidx/compose/ui/graphics/Color;", "(I)J", "app_debug"})
public final class FinancialHealthRingKt {
    
    private static final long healthBandColor(int score) {
        return 0L;
    }
    
    /**
     * The animated circular "Financial Health Score" ring at the top of the Home dashboard.
     * The arc sweeps in from 0 to [score] (0-100) and the number counts up alongside it; tapping
     * the row expands a small breakdown of [subScores] (budget / trend / consistency /
     * subscriptions, each already-localized by the caller).
     */
    @androidx.compose.runtime.Composable()
    public static final void FinancialHealthRing(int score, @org.jetbrains.annotations.NotNull()
    java.lang.String bandLabel, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.ui.components.HealthSubScore> subScores, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}