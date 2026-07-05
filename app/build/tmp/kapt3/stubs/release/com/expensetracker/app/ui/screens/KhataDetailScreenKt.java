package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a>\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u001a.\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0007\u001a.\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00032\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u00a8\u0006\u0019"}, d2 = {"KhataBalanceCard", "", "partyName", "", "balance", "", "currencySymbol", "isIOwe", "", "hasPhone", "onSendReminder", "Lkotlin/Function0;", "KhataDetailScreen", "partyId", "", "khataViewModel", "Lcom/expensetracker/app/viewmodel/KhataViewModel;", "expenseViewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onBack", "KhataEntryRow", "entry", "Lcom/expensetracker/app/data/KhataEntryEntity;", "runningBalance", "onDelete", "app_release"})
public final class KhataDetailScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void KhataDetailScreen(long partyId, @org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.KhataViewModel khataViewModel, @org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.ExpenseViewModel expenseViewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void KhataBalanceCard(java.lang.String partyName, double balance, java.lang.String currencySymbol, boolean isIOwe, boolean hasPhone, kotlin.jvm.functions.Function0<kotlin.Unit> onSendReminder) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void KhataEntryRow(com.expensetracker.app.data.KhataEntryEntity entry, double runningBalance, java.lang.String currencySymbol, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
}