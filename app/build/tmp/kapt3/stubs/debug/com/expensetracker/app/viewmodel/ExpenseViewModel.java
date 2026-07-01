package com.expensetracker.app.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a2\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b.\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J<\u0010F\u001a\u00020G2\u0006\u0010H\u001a\u00020:2\u0006\u0010I\u001a\u00020J2\u0006\u0010K\u001a\u00020\u00072\u0006\u0010L\u001a\u00020\f2\u0006\u0010M\u001a\u00020\u00072\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ$\u0010P\u001a\u00020G2\u0006\u0010Q\u001a\u00020\u00072\u0006\u0010R\u001a\u00020\u00072\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ$\u0010S\u001a\u00020G2\u0006\u0010T\u001a\u00020\u00072\u0006\u0010U\u001a\u00020\f2\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ\"\u0010V\u001a\u00020G2\u0006\u0010W\u001a\u00020\u001e2\u0012\u0010X\u001a\u000e\u0012\u0004\u0012\u00020Z\u0012\u0004\u0012\u00020G0YJ\u001c\u0010[\u001a\u00020G2\u0006\u0010\\\u001a\u00020+2\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ\u000e\u0010]\u001a\u00020G2\u0006\u0010^\u001a\u00020(J\u001c\u0010_\u001a\u00020G2\u0006\u0010`\u001a\u00020\u00152\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ\u000e\u0010a\u001a\u00020G2\u0006\u0010H\u001a\u00020:J\u000e\u0010b\u001a\u00020G2\u0006\u0010c\u001a\u00020\u0007J\"\u0010d\u001a\u00020G2\u0006\u0010I\u001a\u00020J2\u0012\u0010X\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020G0YJ\u0006\u0010e\u001a\u00020GJ\u000e\u0010f\u001a\u00020G2\u0006\u0010g\u001a\u00020JJ\u0016\u0010h\u001a\u00020G2\u0006\u0010W\u001a\u00020\u001e2\u0006\u0010R\u001a\u00020\u0007J6\u0010i\u001a\u00020G2\u0006\u0010\\\u001a\u00020+2\u0006\u0010L\u001a\u00020\f2\u0006\u0010M\u001a\u00020\u00072\b\u0010j\u001a\u0004\u0018\u00010\u00072\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ\u0016\u0010k\u001a\u00020G2\u0006\u0010W\u001a\u00020\u001e2\u0006\u0010l\u001a\u00020\u0007J\u0014\u0010m\u001a\u00020G2\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0OJ]\u0010n\u001a\u00020G2\b\u0010o\u001a\u0004\u0018\u00010J2\u0006\u0010Q\u001a\u00020\u00072\u0006\u0010p\u001a\u00020\u00072\u0006\u0010q\u001a\u00020\f2\u0006\u0010r\u001a\u00020\f2\u0006\u0010s\u001a\u00020\f2\u0006\u0010t\u001a\u00020\u00072\b\u0010u\u001a\u0004\u0018\u00010\u00072\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0O\u00a2\u0006\u0002\u0010vJC\u0010w\u001a\u00020G2\b\u0010o\u001a\u0004\u0018\u00010J2\u0006\u0010I\u001a\u00020J2\u0006\u0010K\u001a\u00020\u00072\u0006\u0010L\u001a\u00020\f2\u0006\u0010M\u001a\u00020\u00072\f\u0010N\u001a\b\u0012\u0004\u0012\u00020G0O\u00a2\u0006\u0002\u0010xJ\u001d\u0010y\u001a\u00020G2\b\u0010I\u001a\u0004\u0018\u00010J2\u0006\u0010L\u001a\u00020\f\u00a2\u0006\u0002\u0010zJ\u000e\u0010{\u001a\u00020G2\u0006\u0010|\u001a\u00020\u0007J\u0016\u0010}\u001a\u00020G2\u0006\u0010\\\u001a\u00020+2\u0006\u0010~\u001a\u00020\u000eJ\u000e\u0010\u007f\u001a\u00020G2\u0006\u0010Q\u001a\u00020\u0007J\u0010\u0010\u0080\u0001\u001a\u00020G2\u0007\u0010\u0081\u0001\u001a\u00020\u0007J\u000f\u0010\u0082\u0001\u001a\u00020G2\u0006\u0010L\u001a\u00020\fJ\u0010\u0010\u0083\u0001\u001a\u00020G2\u0007\u0010\u0084\u0001\u001a\u00020\u000eJ\u0010\u0010\u0085\u0001\u001a\u00020G2\u0007\u0010\u0086\u0001\u001a\u00020\u0010J\u0010\u0010\u0087\u0001\u001a\u00020G2\u0007\u0010\u0084\u0001\u001a\u00020\u000eR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0017R\u001d\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0017R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0017R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0017R\u0011\u0010$\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u001d\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0017R\u001d\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020+0\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u0017R\u0017\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0017R\u0011\u0010/\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b/\u00100R\u0017\u00101\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u0017R#\u00103\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00140\u0013\u00a2\u0006\u000e\n\u0000\u0012\u0004\b4\u00105\u001a\u0004\b6\u0010\u0017R\u0017\u00107\u001a\b\u0012\u0004\u0012\u00020\f0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010\u0017R\u001d\u00109\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020:0\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010\u0017R\u0017\u0010<\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010\u0017R\u0017\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00100\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010\u0017R\u000e\u0010@\u001a\u00020AX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010B\u001a\u00020CX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010D\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010\u0017\u00a8\u0006\u0088\u0001"}, d2 = {"Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_currencySymbol", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_currentMonthKey", "_displayName", "_languagePref", "_monthlySalary", "", "_reminderEnabled", "", "_reminderHour", "", "_smsDetectionEnabled", "allExpenses", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/expensetracker/app/data/ExpenseEntity;", "getAllExpenses", "()Lkotlinx/coroutines/flow/StateFlow;", "app", "Lcom/expensetracker/app/ExpenseApp;", "budgets", "Lcom/expensetracker/app/data/BudgetEntity;", "getBudgets", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "getCategories", "currencySymbol", "getCurrencySymbol", "currentMonthKey", "getCurrentMonthKey", "customerId", "getCustomerId", "()Ljava/lang/String;", "debtPayments", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "getDebtPayments", "debts", "Lcom/expensetracker/app/data/DebtEntity;", "getDebts", "displayName", "getDisplayName", "isCurrencySetupDone", "()Z", "languagePref", "getLanguagePref", "monthExpenses", "getMonthExpenses$annotations", "()V", "getMonthExpenses", "monthlySalary", "getMonthlySalary", "pendingSmsExpenses", "Lcom/expensetracker/app/data/PendingSmsExpense;", "getPendingSmsExpenses", "reminderEnabled", "getReminderEnabled", "reminderHour", "getReminderHour", "repository", "Lcom/expensetracker/app/data/ExpenseRepository;", "settings", "Lcom/expensetracker/app/data/SettingsRepository;", "smsDetectionEnabled", "getSmsDetectionEnabled", "acceptPendingSms", "", "item", "categoryId", "", "description", "amount", "date", "onDone", "Lkotlin/Function0;", "addCategory", "name", "colorHex", "convertCurrency", "newSymbol", "rate", "deleteCategory", "category", "onResult", "Lkotlin/Function1;", "Lcom/expensetracker/app/data/DeleteCategoryResult;", "deleteDebt", "debt", "deleteDebtPayment", "payment", "deleteExpense", "expense", "dismissPendingSms", "handleIncomingSms", "message", "isCategoryInUse", "markCurrencySetupDone", "navigateMonth", "delta", "recolorCategory", "recordDebtPayment", "note", "renameCategory", "newName", "resetAllData", "saveDebt", "id", "direction", "principal", "interestRatePercent", "minimumPayment", "startDate", "notes", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;DDDLjava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V", "saveExpense", "(Ljava/lang/Long;JLjava/lang/String;DLjava/lang/String;Lkotlin/jvm/functions/Function0;)V", "setBudget", "(Ljava/lang/Long;D)V", "setCurrencySymbol", "symbol", "setDebtClosed", "isClosed", "setDisplayName", "setLanguagePref", "pref", "setMonthlySalary", "setReminderEnabled", "enabled", "setReminderHour", "hour", "setSmsDetectionEnabled", "app_debug"})
public final class ExpenseViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.ExpenseApp app = null;
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.ExpenseRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.SettingsRepository settings = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currentMonthKey = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentMonthKey = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.CategoryEntity>> categories = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> allExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> monthExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currencySymbol = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currencySymbol = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _languagePref = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> languagePref = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _smsDetectionEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> smsDetectionEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _reminderEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> reminderEnabled = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _reminderHour = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> reminderHour = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Double> _monthlySalary = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> monthlySalary = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> pendingSmsExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.BudgetEntity>> budgets = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtEntity>> debts = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> debtPayments = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String customerId = null;
    
    public ExpenseViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentMonthKey() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.CategoryEntity>> getCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> getAllExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> getMonthExpenses() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getMonthExpenses$annotations() {
    }
    
    public final boolean isCurrencySetupDone() {
        return false;
    }
    
    public final void markCurrencySetupDone() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrencySymbol() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getLanguagePref() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getSmsDetectionEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getReminderEnabled() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getReminderHour() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getMonthlySalary() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> getPendingSmsExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.BudgetEntity>> getBudgets() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtEntity>> getDebts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> getDebtPayments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCustomerId() {
        return null;
    }
    
    public final void navigateMonth(long delta) {
    }
    
    public final void saveExpense(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, long categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteExpense(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void addCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String colorHex, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void renameCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String newName) {
    }
    
    public final void recolorCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    java.lang.String colorHex) {
    }
    
    public final void isCategoryInUse(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onResult) {
    }
    
    public final void deleteCategory(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.CategoryEntity category, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.expensetracker.app.data.DeleteCategoryResult, kotlin.Unit> onResult) {
    }
    
    /**
     * Sets, updates, or (passing 0 or less) clears the standing budget target for
     * [categoryId] — null means the overall monthly budget rather than a per-category one.
     */
    public final void setBudget(@org.jetbrains.annotations.Nullable()
    java.lang.Long categoryId, double amount) {
    }
    
    public final void setLanguagePref(@org.jetbrains.annotations.NotNull()
    java.lang.String pref) {
    }
    
    public final void setDisplayName(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void setCurrencySymbol(@org.jetbrains.annotations.NotNull()
    java.lang.String symbol) {
    }
    
    /**
     * Switches currency and rescales every stored expense by [rate] (1 old-currency unit
     * = [rate] new-currency units). All arithmetic is local — no network call.
     */
    public final void convertCurrency(@org.jetbrains.annotations.NotNull()
    java.lang.String newSymbol, double rate, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void resetAllData(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void setSmsDetectionEnabled(boolean enabled) {
    }
    
    public final void setReminderEnabled(boolean enabled) {
    }
    
    public final void setReminderHour(int hour) {
    }
    
    public final void setMonthlySalary(double amount) {
    }
    
    /**
     * Called with the raw text of an SMS the user just approved via the system consent prompt.
     * Silently does nothing if the message doesn't look like a debit transaction, or if SMS
     * detection has since been turned off — nothing is ever saved as an expense here, it only
     * lands in the review queue.
     */
    public final void handleIncomingSms(@org.jetbrains.annotations.NotNull()
    java.lang.String message) {
    }
    
    public final void dismissPendingSms(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.PendingSmsExpense item) {
    }
    
    /**
     * User reviewed (and possibly edited) a pending SMS item and confirmed it as a real expense.
     */
    public final void acceptPendingSms(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.PendingSmsExpense item, long categoryId, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void saveDebt(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, double principal, double interestRatePercent, double minimumPayment, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteDebt(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void setDebtClosed(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, boolean isClosed) {
    }
    
    public final void recordDebtPayment(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.Nullable()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteDebtPayment(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtPaymentEntity payment) {
    }
}