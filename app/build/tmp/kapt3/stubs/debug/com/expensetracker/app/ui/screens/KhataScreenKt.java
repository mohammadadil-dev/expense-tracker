package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000^\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tH\u0003\u001aJ\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000fH\u0003\u001a,\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00010\u0018H\u0007\u001aD\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\t2\u0006\u0010\u001c\u001a\u00020\u00032\u0006\u0010\u001d\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010 \u001a\u00020!H\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b\"\u0010#\u001a>\u0010$\u001a\u00020\u00012\u0006\u0010%\u001a\u00020&2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0012\u0010\'\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\u00010\u00182\b\b\u0002\u0010 \u001a\u00020!H\u0003\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006("}, d2 = {"KhataHeroHeader", "", "totalIOwe", "", "totalTheyOwe", "iOweCount", "", "theyOweCount", "currencySymbol", "", "KhataPartyCard", "party", "Lcom/expensetracker/app/data/KhataPartyEntity;", "balance", "onClick", "Lkotlin/Function0;", "onEdit", "onDelete", "KhataScreen", "khataViewModel", "Lcom/expensetracker/app/viewmodel/KhataViewModel;", "expenseViewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onOpenDetail", "Lkotlin/Function1;", "", "KhataStatCard", "label", "amount", "count", "amountColor", "Landroidx/compose/ui/graphics/Color;", "modifier", "Landroidx/compose/ui/Modifier;", "KhataStatCard-jzV_Hc0", "(Ljava/lang/String;DILjava/lang/String;JLandroidx/compose/ui/Modifier;)V", "KhataToggle", "showIOwe", "", "onToggle", "app_debug"})
public final class KhataScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void KhataScreen(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.KhataViewModel khataViewModel, @org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.ExpenseViewModel expenseViewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onOpenDetail) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void KhataHeroHeader(double totalIOwe, double totalTheyOwe, int iOweCount, int theyOweCount, java.lang.String currencySymbol) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void KhataToggle(boolean showIOwe, int iOweCount, int theyOweCount, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onToggle, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void KhataPartyCard(com.expensetracker.app.data.KhataPartyEntity party, double balance, java.lang.String currencySymbol, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
}