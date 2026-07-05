package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\u001au\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072M\u0010\b\u001aI\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\u0015\u0012\u0013\u0018\u00010\u0005\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000f\u0012\u0004\u0012\u00020\u00010\tH\u0007\u00a8\u0006\u0010"}, d2 = {"RecordPaymentSheet", "", "debt", "Lcom/expensetracker/app/data/DebtEntity;", "currencySymbol", "", "onDismiss", "Lkotlin/Function0;", "onSave", "Lkotlin/Function3;", "", "Lkotlin/ParameterName;", "name", "amount", "date", "note", "app_release"})
public final class RecordPaymentSheetKt {
    
    /**
     * Bottom sheet for recording a single payment against [debt]. Always expands fully so the
     * Add button is never hidden. Save action is a full Button at the bottom, not an inline icon.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void RecordPaymentSheet(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.Double, ? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave) {
    }
}