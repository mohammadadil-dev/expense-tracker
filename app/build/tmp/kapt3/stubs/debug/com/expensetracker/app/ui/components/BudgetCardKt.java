package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u001a8\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0007\u001a,\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0013\u0010\u0014\u001a\u0016\u0010\u0015\u001a\u00020\u00012\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u001a\u0016\u0010\u0017\u001a\u00020\u00012\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u001a\u0013\u0010\u0018\u001a\u00020\u00102\u0006\u0010\r\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u0019\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001a"}, d2 = {"BudgetOverviewCard", "", "overallBudget", "Lcom/expensetracker/app/data/BudgetEntity;", "totalSpend", "", "currencySymbol", "", "daysRemaining", "", "onManageClick", "Lkotlin/Function0;", "BudgetProgressBar", "fraction", "", "color", "Landroidx/compose/ui/graphics/Color;", "modifier", "Landroidx/compose/ui/Modifier;", "BudgetProgressBar-bw27NRU", "(FJLandroidx/compose/ui/Modifier;)V", "ManageBudgetButton", "onClick", "SetBudgetChip", "budgetStatusColor", "(F)J", "app_debug"})
public final class BudgetCardKt {
    
    /**
     * Green while comfortably under budget, amber once close, red once over.
     */
    public static final long budgetStatusColor(float fraction) {
        return 0L;
    }
    
    /**
     * Dashboard card summarizing this month's single overall budget against actual spend so far.
     * Shows a prompt + CTA when no budget has been set yet; otherwise an animated progress bar,
     * a left/over-budget line, and a pacing hint ("₹X/day for the next Y days" — only for the live
     * current month, since "days remaining" is meaningless while browsing a past month).
     */
    @androidx.compose.runtime.Composable()
    public static final void BudgetOverviewCard(@org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.BudgetEntity overallBudget, double totalSpend, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, int daysRemaining, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onManageClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ManageBudgetButton(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SetBudgetChip(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}