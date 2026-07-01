package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0012\u0010\u0006\u001a\u00020\u00012\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0003\u001a\"\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0003\u001a\u0010\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\rH\u0003\u00a8\u0006\u0010"}, d2 = {"DebtsScreen", "", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onBack", "Lkotlin/Function0;", "EmptyDebtsState", "modifier", "Landroidx/compose/ui/Modifier;", "NetPositionHeader", "netPosition", "Lcom/expensetracker/app/util/DebtInsights$NetPosition;", "currencySymbol", "", "SectionHeader", "title", "app_release"})
public final class DebtsScreenKt {
    
    /**
     * Debts & Loans screen — both directions (money the user owes, and money owed to the user)
     * live in one list, split into three sections: active "you owe", active "owed to you", and
     * settled. A gradient header card shows the net position across everything at a glance.
     * Mirrors Settings' TopAppBar-with-back-button shell and Dashboard's FAB-to-add pattern.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DebtsScreen(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.ExpenseViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void NetPositionHeader(com.expensetracker.app.util.DebtInsights.NetPosition netPosition, java.lang.String currencySymbol, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SectionHeader(java.lang.String title) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyDebtsState(androidx.compose.ui.Modifier modifier) {
    }
}