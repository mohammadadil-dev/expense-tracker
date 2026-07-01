package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u00e4\u0001\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u00b9\u0001\u0010\b\u001a\u00b4\u0001\u0012\u0015\u0012\u0013\u0018\u00010\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0010\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0012\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0013\u0012\u0015\u0012\u0013\u0018\u00010\u0005\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u0014\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0010\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u0005H\u0003\u001a0\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0003\u001a\u0010\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\u000fH\u0002\u00a8\u0006\u001f"}, d2 = {"AddEditDebtSheet", "", "existing", "Lcom/expensetracker/app/data/DebtEntity;", "currencySymbol", "", "onDismiss", "Lkotlin/Function0;", "onSave", "Lkotlin/Function8;", "", "Lkotlin/ParameterName;", "name", "id", "direction", "", "principal", "interestRatePercent", "minimumPayment", "startDate", "notes", "CurrencyLeadingIcon", "DirectionChip", "label", "selected", "", "onClick", "modifier", "Landroidx/compose/ui/Modifier;", "formatPlainDebtAmount", "value", "app_release"})
public final class AddEditDebtSheetKt {
    
    /**
     * Bottom sheet for creating or editing a single debt/loan, in either direction. Mirrors
     * [AddEditExpenseSheet]'s field-by-field OutlinedTextField + [CalendarDatePickerDialog] pattern.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AddEditDebtSheet(@org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.DebtEntity existing, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function8<? super java.lang.Long, ? super java.lang.String, ? super java.lang.String, ? super java.lang.Double, ? super java.lang.Double, ? super java.lang.Double, ? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CurrencyLeadingIcon(java.lang.String currencySymbol) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DirectionChip(java.lang.String label, boolean selected, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Same plain-number formatting as the other Add/Edit sheets — whole numbers show without a
     * trailing ".0", everything else keeps up to 2 decimal places.
     */
    private static final java.lang.String formatPlainDebtAmount(double value) {
        return null;
    }
}