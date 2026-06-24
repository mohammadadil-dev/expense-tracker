package com.expensetracker.app.data;

/**
 * Single source of truth for all expense/category data. Backed entirely by the local
 * Room database — no network calls anywhere in this class.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\u0019J8\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00172\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010\"J\u0016\u0010#\u001a\u00020\u00152\u0006\u0010$\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010%J\u0016\u0010&\u001a\u00020\u001b2\u0006\u0010\'\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010(J\u000e\u0010)\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007H\u0002J\u0016\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010-J\u0016\u0010.\u001a\u00020\u001b2\u0006\u0010/\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u00100J\u0016\u00101\u001a\u00020\u001b2\u0006\u0010$\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010%J\u001a\u00102\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u00103\u001a\u00020\u0017J\u0016\u00104\u001a\u0002052\u0006\u0010\u001d\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u00106J\u001e\u00107\u001a\u00020\u001b2\u0006\u0010,\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u00108J\u001e\u00109\u001a\u00020\u001b2\u0006\u0010,\u001a\u00020\u000f2\u0006\u0010:\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u00108J\u000e\u0010;\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010<J\u000e\u0010=\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010<J \u0010>\u001a\u00020\u001b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u001f\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010?R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\n\u00a8\u0006@"}, d2 = {"Lcom/expensetracker/app/data/ExpenseRepository;", "", "db", "Lcom/expensetracker/app/data/AppDatabase;", "(Lcom/expensetracker/app/data/AppDatabase;)V", "allExpenses", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/expensetracker/app/data/ExpenseEntity;", "getAllExpenses", "()Lkotlinx/coroutines/flow/Flow;", "budgets", "Lcom/expensetracker/app/data/BudgetEntity;", "getBudgets", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "getCategories", "pendingSmsExpenses", "Lcom/expensetracker/app/data/PendingSmsExpense;", "getPendingSmsExpenses", "addCategory", "", "name", "", "colorHex", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addOrUpdateExpense", "", "id", "categoryId", "description", "amount", "", "date", "(Ljava/lang/Long;JLjava/lang/String;DLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addPendingSmsExpense", "item", "(Lcom/expensetracker/app/data/PendingSmsExpense;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "convertAllAmounts", "rate", "(DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "defaultCategorySeed", "deleteCategory", "Lcom/expensetracker/app/data/DeleteCategoryResult;", "category", "(Lcom/expensetracker/app/data/CategoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpense", "expense", "(Lcom/expensetracker/app/data/ExpenseEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dismissPendingSmsExpense", "expensesForMonth", "monthKey", "isCategoryInUse", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "recolorCategory", "(Lcom/expensetracker/app/data/CategoryEntity;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "renameCategory", "newName", "resetAllData", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "seedDefaultCategoriesIfNeeded", "setBudget", "(Ljava/lang/Long;DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class ExpenseRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.CategoryEntity>> categories = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> allExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> pendingSmsExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.BudgetEntity>> budgets = null;
    
    public ExpenseRepository(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.AppDatabase db) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.CategoryEntity>> getCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> getAllExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> getPendingSmsExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.BudgetEntity>> getBudgets() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> expensesForMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object seedDefaultCategoriesIfNeeded(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.util.List<com.expensetracker.app.data.CategoryEntity> defaultCategorySeed() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addOrUpdateExpense(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, long categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteExpense(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Queues a parser-detected SMS transaction for the user to review before it becomes a
     * real expense.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addPendingSmsExpense(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.PendingSmsExpense item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Removes a pending SMS item — used both when the user dismisses it outright and when
     * it's been accepted (turned into a real expense) and no longer needs to sit in the queue.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object dismissPendingSmsExpense(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.PendingSmsExpense item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Multiplies every stored expense amount by [rate] — used when the user switches
     * currency and supplies a manual exchange rate. Purely local arithmetic, no network call.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object convertAllAmounts(double rate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String colorHex, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object renameCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String newName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object recolorCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String colorHex, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isCategoryInUse(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.expensetracker.app.data.DeleteCategoryResult> $completion) {
        return null;
    }
    
    /**
     * Creates, updates, or clears the standing budget target for [categoryId] (null = overall).
     * Upserting is done here at the app layer — rather than a DB-level unique index — because
     * SQLite treats every NULL in a unique index as distinct, so it wouldn't actually stop
     * duplicate "overall" rows from piling up. An [amount] of 0 or less clears the budget
     * instead of saving a zero target.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setBudget(@org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, double amount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object resetAllData(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}