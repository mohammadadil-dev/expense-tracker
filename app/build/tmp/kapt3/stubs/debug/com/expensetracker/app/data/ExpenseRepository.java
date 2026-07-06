package com.expensetracker.app.data;

/**
 * Single source of truth for all expense/category data. Backed entirely by the local
 * Room database — no network calls anywhere in this class.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0088\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u001f\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u001c\u0018\u0000 {2\u00020\u0001:\u0001{B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010\"J.\u0010#\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010$\u001a\u00020 2\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010(J8\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020&2\u0006\u0010,\u001a\u00020 2\u0006\u0010-\u001a\u00020 2\u0006\u0010.\u001a\u00020 2\b\b\u0002\u0010/\u001a\u000200H\u0086@\u00a2\u0006\u0002\u00101J\\\u00102\u001a\u00020*2\b\u00103\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u00104\u001a\u00020 2\u0006\u00105\u001a\u00020&2\u0006\u00106\u001a\u00020&2\u0006\u00107\u001a\u00020&2\u0006\u00108\u001a\u00020 2\b\u00109\u001a\u0004\u0018\u00010 2\b\u0010:\u001a\u0004\u0018\u00010 H\u0086@\u00a2\u0006\u0002\u0010;JN\u0010<\u001a\u00020*2\b\u00103\u001a\u0004\u0018\u00010\u001e2\u0006\u0010=\u001a\u00020\u001e2\u0006\u0010>\u001a\u00020 2\u0006\u0010+\u001a\u00020&2\u0006\u0010.\u001a\u00020 2\b\b\u0002\u0010/\u001a\u0002002\n\b\u0002\u0010?\u001a\u0004\u0018\u00010 H\u0086@\u00a2\u0006\u0002\u0010@J\u0016\u0010A\u001a\u00020\u001e2\u0006\u0010B\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010CJ\u001e\u0010D\u001a\u00020*2\u0006\u0010E\u001a\u00020\u001e2\u0006\u0010F\u001a\u00020&H\u0086@\u00a2\u0006\u0002\u0010GJ\u0016\u0010H\u001a\u00020*2\u0006\u0010I\u001a\u00020&H\u0086@\u00a2\u0006\u0002\u0010JJ\u000e\u0010K\u001a\u00020*H\u0086@\u00a2\u0006\u0002\u0010LJ\u000e\u0010M\u001a\u00020*H\u0086@\u00a2\u0006\u0002\u0010LJ\u000e\u0010N\u001a\b\u0012\u0004\u0012\u00020\u00120\u0007H\u0002J\u0016\u0010O\u001a\u00020P2\u0006\u0010Q\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010RJ\u0016\u0010S\u001a\u00020*2\u0006\u0010T\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010UJ\u0016\u0010V\u001a\u00020*2\u0006\u0010W\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010XJ\u0016\u0010Y\u001a\u00020*2\u0006\u0010Z\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010[J\u0016\u0010\\\u001a\u00020*2\u0006\u0010E\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u0010]J\u0016\u0010^\u001a\u00020*2\u0006\u0010_\u001a\u00020`H\u0086@\u00a2\u0006\u0002\u0010aJ\u0016\u0010b\u001a\u00020*2\u0006\u0010B\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010CJ\u000e\u0010c\u001a\u00020\u001eH\u0082@\u00a2\u0006\u0002\u0010LJ\u000e\u0010d\u001a\u00020*H\u0086@\u00a2\u0006\u0002\u0010LJ\u001a\u0010e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u00062\u0006\u0010f\u001a\u00020 J\u001a\u0010g\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020`0\u00070\u00062\u0006\u0010f\u001a\u00020 J\u0016\u0010h\u001a\u0002002\u0006\u0010=\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u0010]J\u0016\u0010i\u001a\u00020*2\u0006\u0010E\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u0010]J\u0014\u0010j\u001a\b\u0012\u0004\u0012\u00020&0\u00062\u0006\u0010f\u001a\u00020 J\u001e\u0010k\u001a\u00020*2\u0006\u0010Q\u001a\u00020\u00122\u0006\u0010!\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010lJ0\u0010m\u001a\u00020*2\u0006\u0010T\u001a\u00020\u00182\u0006\u0010+\u001a\u00020&2\u0006\u0010.\u001a\u00020 2\b\u0010-\u001a\u0004\u0018\u00010 H\u0086@\u00a2\u0006\u0002\u0010nJ\u001e\u0010o\u001a\u00020*2\u0006\u0010Q\u001a\u00020\u00122\u0006\u0010p\u001a\u00020 H\u0086@\u00a2\u0006\u0002\u0010lJ\u000e\u0010q\u001a\u00020*H\u0086@\u00a2\u0006\u0002\u0010LJ\u000e\u0010r\u001a\u00020*H\u0086@\u00a2\u0006\u0002\u0010LJ \u0010s\u001a\u00020*2\b\u0010=\u001a\u0004\u0018\u00010\u001e2\u0006\u0010+\u001a\u00020&H\u0086@\u00a2\u0006\u0002\u0010tJ\u001e\u0010u\u001a\u00020*2\u0006\u0010T\u001a\u00020\u00182\u0006\u0010v\u001a\u000200H\u0086@\u00a2\u0006\u0002\u0010wJ\u0016\u0010x\u001a\u00020*2\u0006\u0010y\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010zR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\nR\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\nR\u001d\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\nR\u001d\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\n\u00a8\u0006|"}, d2 = {"Lcom/expensetracker/app/data/ExpenseRepository;", "", "db", "Lcom/expensetracker/app/data/AppDatabase;", "(Lcom/expensetracker/app/data/AppDatabase;)V", "activeGoals", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/expensetracker/app/data/GoalEntity;", "getActiveGoals", "()Lkotlinx/coroutines/flow/Flow;", "allExpenses", "Lcom/expensetracker/app/data/ExpenseEntity;", "getAllExpenses", "budgets", "Lcom/expensetracker/app/data/BudgetEntity;", "getBudgets", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "getCategories", "debtPayments", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "getDebtPayments", "debts", "Lcom/expensetracker/app/data/DebtEntity;", "getDebts", "pendingSmsExpenses", "Lcom/expensetracker/app/data/PendingSmsExpense;", "getPendingSmsExpenses", "addCategory", "", "name", "", "colorHex", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addGoal", "emoji", "targetAmount", "", "targetDate", "(Ljava/lang/String;Ljava/lang/String;DLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addIncome", "", "amount", "source", "note", "date", "isRecurring", "", "(DLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addOrUpdateDebt", "id", "direction", "principal", "interestRatePercent", "minimumPayment", "startDate", "notes", "loanType", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;DDDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addOrUpdateExpense", "categoryId", "description", "recurringPeriod", "(Ljava/lang/Long;JLjava/lang/String;DLjava/lang/String;ZLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addPendingSmsExpense", "item", "(Lcom/expensetracker/app/data/PendingSmsExpense;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "contributeToGoal", "goalId", "additionalAmount", "(JDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "convertAllAmounts", "rate", "(DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createRecurringExpensesForCurrentMonth", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createRecurringIncomeForCurrentMonth", "defaultCategorySeed", "deleteCategory", "Lcom/expensetracker/app/data/DeleteCategoryResult;", "category", "(Lcom/expensetracker/app/data/CategoryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDebt", "debt", "(Lcom/expensetracker/app/data/DebtEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDebtPayment", "payment", "(Lcom/expensetracker/app/data/DebtPaymentEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpense", "expense", "(Lcom/expensetracker/app/data/ExpenseEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteGoal", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteIncome", "income", "Lcom/expensetracker/app/data/IncomeEntity;", "(Lcom/expensetracker/app/data/IncomeEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dismissPendingSmsExpense", "ensureDebtPaymentCategory", "ensureNewBuiltinCategories", "expensesForMonth", "monthKey", "incomeForMonth", "isCategoryInUse", "markGoalCompleted", "monthlyIncomeTotal", "recolorCategory", "(Lcom/expensetracker/app/data/CategoryEntity;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "recordDebtPayment", "(Lcom/expensetracker/app/data/DebtEntity;DLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "renameCategory", "newName", "resetAllData", "seedDefaultCategoriesIfNeeded", "setBudget", "(Ljava/lang/Long;DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setDebtClosed", "isClosed", "(Lcom/expensetracker/app/data/DebtEntity;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateGoal", "goal", "(Lcom/expensetracker/app/data/GoalEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public final class ExpenseRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.AppDatabase db = null;
    
    /**
     * Built-in category every auto-generated debt/EMI repayment expense files under —
     * kept as constants so [defaultCategorySeed], [ensureDebtPaymentCategory], and
     * `MIGRATION_4_5`'s backfill all agree on the exact same key/color.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEBT_PAYMENT_CATEGORY_KEY = "cat_debt_payments";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DEBT_PAYMENT_CATEGORY_COLOR = "#F97316";
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.CategoryEntity>> categories = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> allExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> pendingSmsExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.BudgetEntity>> budgets = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.DebtEntity>> debts = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> debtPayments = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.GoalEntity>> activeGoals = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.ExpenseRepository.Companion Companion = null;
    
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
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.DebtEntity>> getDebts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> getDebtPayments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.GoalEntity>> getActiveGoals() {
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
    
    /**
     * Inserts any built-in categories that are missing from the DB — safe to call on every
     * app launch. New categories added in a later release appear automatically for existing
     * users without wiping their data.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object ensureNewBuiltinCategories(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addOrUpdateExpense(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, long categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, boolean isRecurring, @org.jetbrains.annotations.Nullable()
    java.lang.String recurringPeriod, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Auto-creates expense entries for the current month for every recurring-expense template
     * that doesn't already have one. Called once per app startup (in a background coroutine) —
     * idempotent, so running it multiple times is safe.
     *
     * Backfills any missed months: if the app hasn't been opened since e.g. March and today is
     * June, it inserts April, May, and June all in one pass, so the user never silently loses
     * recurring entries for months they were away.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createRecurringExpensesForCurrentMonth(@org.jetbrains.annotations.NotNull()
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addGoal(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String emoji, double targetAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String targetDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateGoal(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.GoalEntity goal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object contributeToGoal(long goalId, double additionalAmount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteGoal(long goalId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markGoalCompleted(long goalId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addOrUpdateDebt(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, double principal, double interestRatePercent, double minimumPayment, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    java.lang.String loanType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteDebt(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Manual close/reopen — for forgiving a debt, or settling it for less than full principal.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setDebtClosed(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, boolean isClosed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Finds the built-in "Debt & Loan Payments" category, recreating it if the user has since
     * deleted it (categories can always be deleted, see [deleteCategory]) — so a repayment can
     * never be left with nowhere to file its auto-generated expense.
     */
    private final java.lang.Object ensureDebtPaymentCategory(kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Records a payment against [debt] and auto-closes it once cumulative payments reach the
     * principal, so most users never have to remember to manually mark a debt settled. Manual
     * close/reopen (e.g. forgiving a debt, or settling for less than the full principal) stays
     * available separately via [setDebtClosed].
     *
     * EMI/debt repayments are real money leaving the user, so they impact the monthly budget
     * exactly like any other expense: when [debt] is [DebtEntity.DIRECTION_OWE] (money owed
     * *by* the user), this also creates a linked [ExpenseEntity] under the dedicated debt
     * category, which is what makes the payment show up in Total Spent, Budget Remaining, and
     * the Breakdown-by-Category chart. Collections on [DebtEntity.DIRECTION_OWED] debts are
     * money coming *in*, not spend, so no expense is created for those.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object recordDebtPayment(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.Nullable()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteDebtPayment(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtPaymentEntity payment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.IncomeEntity>> incomeForMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Double> monthlyIncomeTotal(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addIncome(double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String source, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, boolean isRecurring, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteIncome(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.IncomeEntity income, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Auto-generates non-recurring copies of every recurring income template for the current
     * month — mirroring [createRecurringExpensesForCurrentMonth] for income.
     *
     * Guards against duplicates: if an entry with the same source+amount already exists for
     * the current month (non-recurring), it is skipped. Called once per app launch from
     * [ExpenseApp] so users always see their monthly salary pre-filled.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createRecurringIncomeForCurrentMonth(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/expensetracker/app/data/ExpenseRepository$Companion;", "", "()V", "DEBT_PAYMENT_CATEGORY_COLOR", "", "DEBT_PAYMENT_CATEGORY_KEY", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}