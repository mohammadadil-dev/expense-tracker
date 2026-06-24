package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\u001a<\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\nH\u0002\u00a8\u0006\r"}, d2 = {"BudgetManageSheet", "", "overallBudget", "Lcom/expensetracker/app/data/BudgetEntity;", "currencySymbol", "", "onDismiss", "Lkotlin/Function0;", "onSave", "Lkotlin/Function1;", "", "formatPlainAmount", "value", "app_release"})
public final class BudgetManageSheetKt {
    
    /**
     * Bottom sheet for setting/editing/clearing the single overall monthly budget. Saving a blank
     * field or 0 clears the budget instead of erroring — [onSave] is the single entry point for all
     * three outcomes (create, update, clear).
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void BudgetManageSheet(@org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.BudgetEntity overallBudget, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Double, kotlin.Unit> onSave) {
    }
    
    /**
     * Same plain-number formatting AddEditExpenseSheet uses for its amount field — whole numbers
     * show without a trailing ".0", everything else keeps up to 2 decimal places.
     */
    private static final java.lang.String formatPlainAmount(double value) {
        return null;
    }
}