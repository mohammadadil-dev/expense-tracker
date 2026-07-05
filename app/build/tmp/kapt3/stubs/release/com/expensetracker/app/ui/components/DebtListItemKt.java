package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u009a\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u0007\u001a\u0016\u0010\u0017\u001a\u00020\u00012\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0003\u001a\u0010\u0010\u0019\u001a\u00020\b2\u0006\u0010\u001a\u001a\u00020\bH\u0003\u00a8\u0006\u001b"}, d2 = {"DebtListItem", "", "progress", "Lcom/expensetracker/app/util/DebtInsights$DebtProgress;", "payments", "", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "currencySymbol", "", "locale", "Ljava/util/Locale;", "expanded", "", "onToggleExpand", "Lkotlin/Function0;", "onRecordPayment", "onEdit", "onDelete", "onToggleClosed", "onDeletePayment", "Lkotlin/Function1;", "modifier", "Landroidx/compose/ui/Modifier;", "RecordPaymentChip", "onClick", "loanTypeLabel", "type", "app_release"})
public final class DebtListItemKt {
    
    /**
     * One card in the Debts & Loans screen: direction badge + name + live outstanding balance,
     * an animated paid-off progress bar, an optional payoff projection + "pay off sooner" AI
     * insight (skipped when the debt has no monthly payment to project from), and an expandable
     * payment history list. Mirrors [BudgetOverviewCard]'s shadow/status-color styling and
     * [InsightFeedCard]'s emoji-badge insight row.
     */
    @androidx.compose.runtime.Composable()
    public static final void DebtListItem(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.util.DebtInsights.DebtProgress progress, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.DebtPaymentEntity> payments, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale, boolean expanded, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleExpand, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRecordPayment, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDelete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToggleClosed, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.DebtPaymentEntity, kotlin.Unit> onDeletePayment, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RecordPaymentChip(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * Localised label for a loan type — reads from string resources so all 7 locales are covered.
     */
    @androidx.compose.runtime.Composable()
    private static final java.lang.String loanTypeLabel(java.lang.String type) {
        return null;
    }
}