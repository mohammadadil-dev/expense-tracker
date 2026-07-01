package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000P\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\t\n\u0000\u001a,\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u001e\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a(\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0003\u001a\u0015\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002\u00a2\u0006\u0002\u0010\u0014\u001a\u0010\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0013H\u0003\u001a\u0015\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u0019H\u0002\u00a2\u0006\u0002\u0010\u001a\u001a$\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u001d2\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020 \u0012\u0004\u0012\u00020\u00130\u001fH\u0003\u00a8\u0006!"}, d2 = {"DashboardScreen", "", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onOpenSettings", "Lkotlin/Function0;", "onOpenDebts", "ExportPdfChip", "enabled", "", "onClick", "PendingSmsBanner", "count", "", "modifier", "Landroidx/compose/ui/Modifier;", "colorFromHex", "Landroidx/compose/ui/graphics/Color;", "hex", "", "(Ljava/lang/String;)J", "greetingText", "name", "insightAccentColor", "kind", "Lcom/expensetracker/app/util/FinancialInsights$InsightKind;", "(Lcom/expensetracker/app/util/FinancialInsights$InsightKind;)J", "insightText", "insight", "Lcom/expensetracker/app/util/FinancialInsights$Insight;", "categoryNameById", "", "", "app_release"})
public final class DashboardScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardScreen(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.ExpenseViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenDebts) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ExportPdfChip(boolean enabled, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PendingSmsBanner(int count, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    private static final long colorFromHex(java.lang.String hex) {
        return 0L;
    }
    
    @androidx.compose.runtime.Composable()
    private static final java.lang.String greetingText(java.lang.String name) {
        return null;
    }
    
    private static final long insightAccentColor(com.expensetracker.app.util.FinancialInsights.InsightKind kind) {
        return 0L;
    }
    
    @androidx.compose.runtime.Composable()
    private static final java.lang.String insightText(com.expensetracker.app.util.FinancialInsights.Insight insight, java.util.Map<java.lang.Long, java.lang.String> categoryNameById) {
        return null;
    }
}