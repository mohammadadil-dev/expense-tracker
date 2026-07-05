package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\u001aL\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0003\u001ab\u0010\r\u001a\u00020\u00012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0006\u001a\u00020\u00032\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\f28\u0010\u000b\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u0012\u00a2\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\u0016\u00a2\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0017\u0012\u0004\u0012\u00020\u00010\u0011H\u0007\u001a\u0010\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u0016H\u0002\u00a8\u0006\u001a"}, d2 = {"BudgetInputRow", "", "text", "", "onTextChange", "Lkotlin/Function1;", "currencySymbol", "isError", "", "modifier", "Landroidx/compose/ui/Modifier;", "onSave", "Lkotlin/Function0;", "BudgetManageSheet", "overallBudget", "Lcom/expensetracker/app/data/BudgetEntity;", "onDismiss", "Lkotlin/Function2;", "", "Lkotlin/ParameterName;", "name", "categoryId", "", "amount", "formatPlainAmount", "value", "app_release"})
public final class BudgetManageSheetKt {
    
    /**
     * Bottom sheet for managing the overall monthly budget.
     *
     * The ✓ button saves the entered amount. Clearing the field and tapping ✓ removes the budget.
     * [onSave] is called with (categoryId = null, amount) for the overall budget.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void BudgetManageSheet(@org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.BudgetEntity overallBudget, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.Long, ? super java.lang.Double, kotlin.Unit> onSave) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BudgetInputRow(java.lang.String text, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTextChange, java.lang.String currencySymbol, boolean isError, androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> onSave) {
    }
    
    private static final java.lang.String formatPlainAmount(double value) {
        return null;
    }
}