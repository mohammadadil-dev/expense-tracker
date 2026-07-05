package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aD\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b\r\u0010\u000e\u001a(\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0003H\u0003\u001a\u001e\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u0018H\u0007\u001a\u0012\u0010\u0019\u001a\u00020\u00012\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003\u001a\"\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0003\u001a\u0010\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u0003H\u0003\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001d"}, d2 = {"DebtStatCard", "", "label", "", "amount", "", "count", "", "currencySymbol", "amountColor", "Landroidx/compose/ui/graphics/Color;", "modifier", "Landroidx/compose/ui/Modifier;", "DebtStatCard-jzV_Hc0", "(Ljava/lang/String;DILjava/lang/String;JLandroidx/compose/ui/Modifier;)V", "DebtsHeroHeader", "netPosition", "Lcom/expensetracker/app/util/DebtInsights$NetPosition;", "owedByUserCount", "owedToUserCount", "DebtsScreen", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onBack", "Lkotlin/Function0;", "EmptyDebtsState", "NetPositionHeader", "SectionHeader", "title", "app_release"})
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
    private static final void DebtsHeroHeader(com.expensetracker.app.util.DebtInsights.NetPosition netPosition, int owedByUserCount, int owedToUserCount, java.lang.String currencySymbol) {
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