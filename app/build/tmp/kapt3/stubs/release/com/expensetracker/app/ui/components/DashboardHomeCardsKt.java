package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000^\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010$\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\u001a\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001as\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000f\u001a\u00020\b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a2\u0006\u0002\u0010\u0014\u001a2\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\b2\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001aM\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000f\u001a\u00020\b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a2\u0006\u0002\u0010\u001d\u001a,\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020 2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u000f\u001a\u00020\b2\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001aT\u0010!\u001a\u00020\u00012\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020 0#2\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\u00170%2\u0006\u0010\u000f\u001a\u00020\b2\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010(\u001a\u00020\n2\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001a:\u0010)\u001a\u00020\u00012\u0006\u0010*\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\b2\u0012\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010,2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00010\u0011H\u0007\u001aR\u0010.\u001a\u00020\u00012\u0012\u0010/\u001a\u000e\u0012\u0004\u0012\u00020&\u0012\u0004\u0012\u00020\r0%2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u00170#2\u0006\u0010\u0019\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\b2\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a8\u00062"}, d2 = {"BudgetProgressRing", "", "spentFraction", "", "modifier", "Landroidx/compose/ui/Modifier;", "BudgetRingCard", "currentMonthKey", "", "monthSlideDirection", "", "monthDisplayLabel", "totalSpent", "", "overallBudget", "currencySymbol", "onNavigatePrev", "Lkotlin/Function0;", "onNavigateNext", "onManageBudget", "(Ljava/lang/String;ILjava/lang/String;DLjava/lang/Double;Ljava/lang/String;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;)V", "CategorySpendRow", "category", "Lcom/expensetracker/app/data/CategoryEntity;", "amount", "totalThisMonth", "IncomeBudgetTiles", "monthlySalary", "onSetSalary", "(DLjava/lang/Double;Ljava/lang/String;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;)V", "RecentTransactionRow", "expense", "Lcom/expensetracker/app/data/ExpenseEntity;", "RecentTransactionsSection", "expenses", "", "categoryById", "", "", "onViewAll", "maxItems", "SalarySetDialog", "currentSalary", "onConfirm", "Lkotlin/Function1;", "onDismiss", "SpendingCategorySection", "categoryTotals", "categories", "onManageCategories", "app_release"})
public final class DashboardHomeCardsKt {
    
    /**
     * Circular arc ring showing budget consumption. Track is dark-green translucent;
     * arc is amber → coral when over budget. Centre shows the percentage consumed.
     */
    @androidx.compose.runtime.Composable()
    public static final void BudgetProgressRing(float spentFraction, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Deep forest-green gradient card matching the reference screenshot:
     * - Top row: "Month's Budget" label + month navigation arrows
     * - Left: Budget progress ring showing percentage
     * - Right: Spent (coral) and Remaining (amber) amounts stacked
     * - "No budget set" prompt when budget is null
     */
    @androidx.compose.runtime.Composable()
    public static final void BudgetRingCard(@org.jetbrains.annotations.NotNull()
    java.lang.String currentMonthKey, int monthSlideDirection, @org.jetbrains.annotations.NotNull()
    java.lang.String monthDisplayLabel, double totalSpent, @org.jetbrains.annotations.Nullable()
    java.lang.Double overallBudget, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigatePrev, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateNext, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onManageBudget, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Two tiles side by side matching the reference screenshot:
     * Left = bright green (Salary), Right = coral-red (Budget Cap).
     */
    @androidx.compose.runtime.Composable()
    public static final void IncomeBudgetTiles(double monthlySalary, @org.jetbrains.annotations.Nullable()
    java.lang.Double overallBudget, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSetSalary, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onManageBudget, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SalarySetDialog(double currentSalary, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Double, kotlin.Unit> onConfirm, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    /**
     * White card listing top-6 categories. Each row is itself a white rounded card
     * with a squircle emoji icon, name, spend/budget label, and coloured progress bar —
     * matching the reference design closely.
     */
    @androidx.compose.runtime.Composable()
    public static final void SpendingCategorySection(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.Long, java.lang.Double> categoryTotals, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.CategoryEntity> categories, double totalThisMonth, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onManageCategories, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * One category row: white rounded card, squircle emoji, name, amount, progress bar.
     */
    @androidx.compose.runtime.Composable()
    public static final void CategorySpendRow(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, double amount, double totalThisMonth, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RecentTransactionsSection(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.ExpenseEntity> expenses, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.Long, com.expensetracker.app.data.CategoryEntity> categoryById, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onViewAll, int maxItems, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RecentTransactionRow(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}