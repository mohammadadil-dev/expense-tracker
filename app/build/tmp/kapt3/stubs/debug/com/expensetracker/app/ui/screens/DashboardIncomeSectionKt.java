package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001ap\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0001\u00a8\u0006\u0014"}, d2 = {"DashboardIncomeSection", "", "monthIncomeTotal", "", "monthIncomeEntries", "", "Lcom/expensetracker/app/data/IncomeEntity;", "currencySymbol", "", "locale", "Ljava/util/Locale;", "expanded", "", "onExpandToggle", "Lkotlin/Function0;", "onAddIncome", "onDeleteIncome", "Lkotlin/Function1;", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class DashboardIncomeSectionKt {
    
    /**
     * Expandable income-tracking card for the current month.
     *
     * Shows the total logged income and — when expanded — lists individual income
     * entries with source, date, note and a delete button. An "Add income" button
     * is always visible to the right of the header row.
     */
    @androidx.compose.runtime.Composable()
    public static final void DashboardIncomeSection(double monthIncomeTotal, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.IncomeEntity> monthIncomeEntries, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale, boolean expanded, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onExpandToggle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddIncome, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.IncomeEntity, kotlin.Unit> onDeleteIncome, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}