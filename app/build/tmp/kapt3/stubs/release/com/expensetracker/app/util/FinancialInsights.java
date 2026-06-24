package com.expensetracker.app.util;

/**
 * On-device "AI" layer for the Home dashboard.
 *
 * Everything here is plain arithmetic over the expenses/budgets already loaded in memory —
 * no network call, no LLM, nothing leaves the device. The "AI insight" framing in the UI just
 * means these read like a short human note instead of a raw number; the computation itself is
 * deterministic rules + statistics over [ExpenseEntity]/[BudgetEntity] rows.
 *
 * This file stays Compose-free on purpose so it can return raw, structured results — category
 * names, formatted money strings, etc. are resolved by the caller (which already has the
 * @Composable categoryDisplayName()/Formatters helpers) rather than baked in here.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0013\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0006,-./01B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J`\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\b2\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u0014JM\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u00182\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001c\u001a\u00020\u0018H\u0002\u00a2\u0006\u0002\u0010\u001dJ\'\u0010\u001e\u001a\u00020\u001f2\b\u0010\u0019\u001a\u0004\u0018\u00010\u00182\u0006\u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u0018H\u0002\u00a2\u0006\u0002\u0010\"J\u0012\u0010#\u001a\u0004\u0018\u00010\u00142\u0006\u0010$\u001a\u00020\u0004H\u0002J\u0010\u0010%\u001a\u00020\u00042\u0006\u0010&\u001a\u00020\u0018H\u0002J(\u0010\'\u001a\u0004\u0018\u00010\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J&\u0010(\u001a\u00020\u00182\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010*\u001a\u00020\u00142\u0006\u0010+\u001a\u00020\u0014H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u00062"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights;", "", "()V", "SUBSCRIPTION_NAME_KEY", "", "compute", "Lcom/expensetracker/app/util/FinancialInsights$DashboardIntelligence;", "monthExpenses", "", "Lcom/expensetracker/app/data/ExpenseEntity;", "allExpenses", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "budgets", "Lcom/expensetracker/app/data/BudgetEntity;", "currentMonthKey", "currencySymbol", "locale", "Ljava/util/Locale;", "today", "Ljava/time/LocalDate;", "computeHealthScore", "Lcom/expensetracker/app/util/FinancialInsights$HealthScore;", "monthlySpend", "", "overallBudget", "last7", "prev7", "subscriptionSpend", "(DLjava/lang/Double;DDLjava/util/List;Ljava/time/LocalDate;D)Lcom/expensetracker/app/util/FinancialInsights$HealthScore;", "computeSavingsGoal", "Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;", "lastMonthSpend", "projectedTotal", "(Ljava/lang/Double;DD)Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;", "parseDateOrNull", "iso", "pct", "value", "strongestSavingDay", "sumInRange", "expenses", "from", "to", "DashboardIntelligence", "HealthScore", "Insight", "InsightKind", "InsightTemplate", "SavingsGoal", "app_release"})
public final class FinancialInsights {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String SUBSCRIPTION_NAME_KEY = "cat_subscriptions";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.FinancialInsights INSTANCE = null;
    
    private FinancialInsights() {
        super();
    }
    
    private final java.time.LocalDate parseDateOrNull(java.lang.String iso) {
        return null;
    }
    
    private final double sumInRange(java.util.List<com.expensetracker.app.data.ExpenseEntity> expenses, java.time.LocalDate from, java.time.LocalDate to) {
        return 0.0;
    }
    
    private final java.lang.String pct(double value) {
        return null;
    }
    
    /**
     * Single entry point: computes everything the Home dashboard needs in one pass over the
     * already-loaded expense/category/budget lists. Callers should wrap this in `remember(...)`
     * keyed on the input lists so it only re-runs when the underlying data actually changes.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.util.FinancialInsights.DashboardIntelligence compute(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.ExpenseEntity> monthExpenses, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.ExpenseEntity> allExpenses, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.CategoryEntity> categories, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.BudgetEntity> budgets, @org.jetbrains.annotations.NotNull()
    java.lang.String currentMonthKey, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol, @org.jetbrains.annotations.NotNull()
    java.util.Locale locale, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate today) {
        return null;
    }
    
    private final com.expensetracker.app.util.FinancialInsights.SavingsGoal computeSavingsGoal(java.lang.Double overallBudget, double lastMonthSpend, double projectedTotal) {
        return null;
    }
    
    private final com.expensetracker.app.util.FinancialInsights.HealthScore computeHealthScore(double monthlySpend, java.lang.Double overallBudget, double last7, double prev7, java.util.List<com.expensetracker.app.data.ExpenseEntity> allExpenses, java.time.LocalDate today, double subscriptionSpend) {
        return null;
    }
    
    /**
     * Looks at the last 4 weeks of dated expenses and finds the day of week with the lowest
     * average spend — framed to the user as their "strongest saving day". Returns null when
     * there isn't enough spread of data (fewer than 2 distinct weekdays represented) to make
     * the comparison meaningful rather than noise.
     */
    private final java.lang.String strongestSavingDay(java.util.List<com.expensetracker.app.data.ExpenseEntity> allExpenses, java.time.LocalDate today, java.util.Locale locale) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\b\u0010\n\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\bH\u00c6\u0003J\u0010\u0010 \u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J\u0010\u0010!\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\"\u001a\u00020\rH\u00c6\u0003J^\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001\u00a2\u0006\u0002\u0010$J\u0013\u0010%\u001a\u00020&2\b\u0010\'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020)H\u00d6\u0001J\t\u0010*\u001a\u00020+H\u00d6\u0001R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0015\u0010\n\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u0018\u0010\u0010R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0017\u00a8\u0006,"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights$DashboardIntelligence;", "", "healthScore", "Lcom/expensetracker/app/util/FinancialInsights$HealthScore;", "insights", "", "Lcom/expensetracker/app/util/FinancialInsights$Insight;", "monthlySpend", "", "subscriptionSpend", "overallBudget", "budgetRemaining", "savingsGoal", "Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;", "(Lcom/expensetracker/app/util/FinancialInsights$HealthScore;Ljava/util/List;DDLjava/lang/Double;Ljava/lang/Double;Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;)V", "getBudgetRemaining", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getHealthScore", "()Lcom/expensetracker/app/util/FinancialInsights$HealthScore;", "getInsights", "()Ljava/util/List;", "getMonthlySpend", "()D", "getOverallBudget", "getSavingsGoal", "()Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;", "getSubscriptionSpend", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(Lcom/expensetracker/app/util/FinancialInsights$HealthScore;Ljava/util/List;DDLjava/lang/Double;Ljava/lang/Double;Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;)Lcom/expensetracker/app/util/FinancialInsights$DashboardIntelligence;", "equals", "", "other", "hashCode", "", "toString", "", "app_release"})
    public static final class DashboardIntelligence {
        @org.jetbrains.annotations.NotNull()
        private final com.expensetracker.app.util.FinancialInsights.HealthScore healthScore = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.expensetracker.app.util.FinancialInsights.Insight> insights = null;
        private final double monthlySpend = 0.0;
        private final double subscriptionSpend = 0.0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Double overallBudget = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Double budgetRemaining = null;
        @org.jetbrains.annotations.NotNull()
        private final com.expensetracker.app.util.FinancialInsights.SavingsGoal savingsGoal = null;
        
        public DashboardIntelligence(@org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.HealthScore healthScore, @org.jetbrains.annotations.NotNull()
        java.util.List<com.expensetracker.app.util.FinancialInsights.Insight> insights, double monthlySpend, double subscriptionSpend, @org.jetbrains.annotations.Nullable()
        java.lang.Double overallBudget, @org.jetbrains.annotations.Nullable()
        java.lang.Double budgetRemaining, @org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.SavingsGoal savingsGoal) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.HealthScore getHealthScore() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.expensetracker.app.util.FinancialInsights.Insight> getInsights() {
            return null;
        }
        
        public final double getMonthlySpend() {
            return 0.0;
        }
        
        public final double getSubscriptionSpend() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Double getOverallBudget() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Double getBudgetRemaining() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.SavingsGoal getSavingsGoal() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.HealthScore component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.expensetracker.app.util.FinancialInsights.Insight> component2() {
            return null;
        }
        
        public final double component3() {
            return 0.0;
        }
        
        public final double component4() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Double component5() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Double component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.SavingsGoal component7() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.DashboardIntelligence copy(@org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.HealthScore healthScore, @org.jetbrains.annotations.NotNull()
        java.util.List<com.expensetracker.app.util.FinancialInsights.Insight> insights, double monthlySpend, double subscriptionSpend, @org.jetbrains.annotations.Nullable()
        java.lang.Double overallBudget, @org.jetbrains.annotations.Nullable()
        java.lang.Double budgetRemaining, @org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.SavingsGoal savingsGoal) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J;\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001b"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights$HealthScore;", "", "total", "", "budgetScore", "trendScore", "consistencyScore", "subscriptionScore", "(IIIII)V", "getBudgetScore", "()I", "getConsistencyScore", "getSubscriptionScore", "getTotal", "getTrendScore", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "app_release"})
    public static final class HealthScore {
        private final int total = 0;
        private final int budgetScore = 0;
        private final int trendScore = 0;
        private final int consistencyScore = 0;
        private final int subscriptionScore = 0;
        
        public HealthScore(int total, int budgetScore, int trendScore, int consistencyScore, int subscriptionScore) {
            super();
        }
        
        public final int getTotal() {
            return 0;
        }
        
        public final int getBudgetScore() {
            return 0;
        }
        
        public final int getTrendScore() {
            return 0;
        }
        
        public final int getConsistencyScore() {
            return 0;
        }
        
        public final int getSubscriptionScore() {
            return 0;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final int component5() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.HealthScore copy(int total, int budgetScore, int trendScore, int consistencyScore, int subscriptionScore) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    /**
     * One feed item. [categoryId] is set only for category-specific templates so the caller can
     * resolve the localized display name itself; [args] holds every *other* already-formatted
     * placeholder (percentages, money strings, weekday names) in template order, inserted after
     * the resolved category name if there is one.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00070\u000bH\u00c6\u0003JH\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u001eJ\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0007H\u00d6\u0001R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0015\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006%"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights$Insight;", "", "template", "Lcom/expensetracker/app/util/FinancialInsights$InsightTemplate;", "kind", "Lcom/expensetracker/app/util/FinancialInsights$InsightKind;", "emoji", "", "categoryId", "", "args", "", "(Lcom/expensetracker/app/util/FinancialInsights$InsightTemplate;Lcom/expensetracker/app/util/FinancialInsights$InsightKind;Ljava/lang/String;Ljava/lang/Long;Ljava/util/List;)V", "getArgs", "()Ljava/util/List;", "getCategoryId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getEmoji", "()Ljava/lang/String;", "getKind", "()Lcom/expensetracker/app/util/FinancialInsights$InsightKind;", "getTemplate", "()Lcom/expensetracker/app/util/FinancialInsights$InsightTemplate;", "component1", "component2", "component3", "component4", "component5", "copy", "(Lcom/expensetracker/app/util/FinancialInsights$InsightTemplate;Lcom/expensetracker/app/util/FinancialInsights$InsightKind;Ljava/lang/String;Ljava/lang/Long;Ljava/util/List;)Lcom/expensetracker/app/util/FinancialInsights$Insight;", "equals", "", "other", "hashCode", "", "toString", "app_release"})
    public static final class Insight {
        @org.jetbrains.annotations.NotNull()
        private final com.expensetracker.app.util.FinancialInsights.InsightTemplate template = null;
        @org.jetbrains.annotations.NotNull()
        private final com.expensetracker.app.util.FinancialInsights.InsightKind kind = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String emoji = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Long categoryId = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> args = null;
        
        public Insight(@org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.InsightTemplate template, @org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.InsightKind kind, @org.jetbrains.annotations.NotNull()
        java.lang.String emoji, @org.jetbrains.annotations.Nullable()
        java.lang.Long categoryId, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> args) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.InsightTemplate getTemplate() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.InsightKind getKind() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getEmoji() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long getCategoryId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getArgs() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.InsightTemplate component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.InsightKind component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Long component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.Insight copy(@org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.InsightTemplate template, @org.jetbrains.annotations.NotNull()
        com.expensetracker.app.util.FinancialInsights.InsightKind kind, @org.jetbrains.annotations.NotNull()
        java.lang.String emoji, @org.jetbrains.annotations.Nullable()
        java.lang.Long categoryId, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> args) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights$InsightKind;", "", "(Ljava/lang/String;I)V", "POSITIVE", "WARNING", "NEUTRAL", "app_release"})
    public static enum InsightKind {
        /*public static final*/ POSITIVE /* = new POSITIVE() */,
        /*public static final*/ WARNING /* = new WARNING() */,
        /*public static final*/ NEUTRAL /* = new NEUTRAL() */;
        
        InsightKind() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.expensetracker.app.util.FinancialInsights.InsightKind> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\r\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r\u00a8\u0006\u000e"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights$InsightTemplate;", "", "(Ljava/lang/String;I)V", "TREND_DOWN", "TREND_UP", "TREND_FLAT", "CATEGORY_INCREASE", "CATEGORY_DECREASE", "PROJECTED_SAVINGS", "PROJECTED_OVERSPEND", "STRONGEST_DAY", "SUBSCRIPTION_LOAD", "DAILY_AVERAGE", "NOT_ENOUGH_DATA", "app_release"})
    public static enum InsightTemplate {
        /*public static final*/ TREND_DOWN /* = new TREND_DOWN() */,
        /*public static final*/ TREND_UP /* = new TREND_UP() */,
        /*public static final*/ TREND_FLAT /* = new TREND_FLAT() */,
        /*public static final*/ CATEGORY_INCREASE /* = new CATEGORY_INCREASE() */,
        /*public static final*/ CATEGORY_DECREASE /* = new CATEGORY_DECREASE() */,
        /*public static final*/ PROJECTED_SAVINGS /* = new PROJECTED_SAVINGS() */,
        /*public static final*/ PROJECTED_OVERSPEND /* = new PROJECTED_OVERSPEND() */,
        /*public static final*/ STRONGEST_DAY /* = new STRONGEST_DAY() */,
        /*public static final*/ SUBSCRIPTION_LOAD /* = new SUBSCRIPTION_LOAD() */,
        /*public static final*/ DAILY_AVERAGE /* = new DAILY_AVERAGE() */,
        /*public static final*/ NOT_ENOUGH_DATA /* = new NOT_ENOUGH_DATA() */;
        
        InsightTemplate() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.expensetracker.app.util.FinancialInsights.InsightTemplate> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\b2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f\u00a8\u0006\u001c"}, d2 = {"Lcom/expensetracker/app/util/FinancialInsights$SavingsGoal;", "", "targetAmount", "", "projectedSavings", "progress", "", "hasEnoughData", "", "(DDFZ)V", "getHasEnoughData", "()Z", "getProgress", "()F", "getProjectedSavings", "()D", "getTargetAmount", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "app_release"})
    public static final class SavingsGoal {
        private final double targetAmount = 0.0;
        private final double projectedSavings = 0.0;
        private final float progress = 0.0F;
        private final boolean hasEnoughData = false;
        
        public SavingsGoal(double targetAmount, double projectedSavings, float progress, boolean hasEnoughData) {
            super();
        }
        
        public final double getTargetAmount() {
            return 0.0;
        }
        
        public final double getProjectedSavings() {
            return 0.0;
        }
        
        public final float getProgress() {
            return 0.0F;
        }
        
        public final boolean getHasEnoughData() {
            return false;
        }
        
        public final double component1() {
            return 0.0;
        }
        
        public final double component2() {
            return 0.0;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.FinancialInsights.SavingsGoal copy(double targetAmount, double projectedSavings, float progress, boolean hasEnoughData) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}