package com.expensetracker.app.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0019\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J<\u00103\u001a\u0002042\u0006\u00105\u001a\u00020+2\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\u00072\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020\u00072\f\u0010<\u001a\b\u0012\u0004\u0012\u0002040=J$\u0010>\u001a\u0002042\u0006\u0010?\u001a\u00020\u00072\u0006\u0010@\u001a\u00020\u00072\f\u0010<\u001a\b\u0012\u0004\u0012\u0002040=J$\u0010A\u001a\u0002042\u0006\u0010B\u001a\u00020\u00072\u0006\u0010C\u001a\u00020:2\f\u0010<\u001a\b\u0012\u0004\u0012\u0002040=J\"\u0010D\u001a\u0002042\u0006\u0010E\u001a\u00020\u00192\u0012\u0010F\u001a\u000e\u0012\u0004\u0012\u00020H\u0012\u0004\u0012\u0002040GJ\u001c\u0010I\u001a\u0002042\u0006\u0010J\u001a\u00020\u00102\f\u0010<\u001a\b\u0012\u0004\u0012\u0002040=J\u000e\u0010K\u001a\u0002042\u0006\u00105\u001a\u00020+J\u000e\u0010L\u001a\u0002042\u0006\u0010M\u001a\u00020\u0007J\"\u0010N\u001a\u0002042\u0006\u00106\u001a\u0002072\u0012\u0010F\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u0002040GJ\u000e\u0010O\u001a\u0002042\u0006\u0010P\u001a\u000207J\u0016\u0010Q\u001a\u0002042\u0006\u0010E\u001a\u00020\u00192\u0006\u0010@\u001a\u00020\u0007J\u0016\u0010R\u001a\u0002042\u0006\u0010E\u001a\u00020\u00192\u0006\u0010S\u001a\u00020\u0007J\u0014\u0010T\u001a\u0002042\f\u0010<\u001a\b\u0012\u0004\u0012\u0002040=JC\u0010U\u001a\u0002042\b\u0010V\u001a\u0004\u0018\u0001072\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\u00072\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020\u00072\f\u0010<\u001a\b\u0012\u0004\u0012\u0002040=\u00a2\u0006\u0002\u0010WJ\u001d\u0010X\u001a\u0002042\b\u00106\u001a\u0004\u0018\u0001072\u0006\u00109\u001a\u00020:\u00a2\u0006\u0002\u0010YJ\u000e\u0010Z\u001a\u0002042\u0006\u0010[\u001a\u00020\u0007J\u000e\u0010\\\u001a\u0002042\u0006\u0010?\u001a\u00020\u0007J\u000e\u0010]\u001a\u0002042\u0006\u0010^\u001a\u00020\u0007J\u000e\u0010_\u001a\u0002042\u0006\u0010`\u001a\u00020\fR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u001d\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0012R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012R\u0011\u0010\u001f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0012R\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0012R#\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u000e\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\'\u0010(\u001a\u0004\b)\u0010\u0012R\u001d\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020+0\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u0012R\u000e\u0010-\u001a\u00020.X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u000200X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u00101\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u0012\u00a8\u0006a"}, d2 = {"Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_currencySymbol", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_currentMonthKey", "_displayName", "_languagePref", "_smsDetectionEnabled", "", "allExpenses", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/expensetracker/app/data/ExpenseEntity;", "getAllExpenses", "()Lkotlinx/coroutines/flow/StateFlow;", "app", "Lcom/expensetracker/app/ExpenseApp;", "budgets", "Lcom/expensetracker/app/data/BudgetEntity;", "getBudgets", "categories", "Lcom/expensetracker/app/data/CategoryEntity;", "getCategories", "currencySymbol", "getCurrencySymbol", "currentMonthKey", "getCurrentMonthKey", "customerId", "getCustomerId", "()Ljava/lang/String;", "displayName", "getDisplayName", "languagePref", "getLanguagePref", "monthExpenses", "getMonthExpenses$annotations", "()V", "getMonthExpenses", "pendingSmsExpenses", "Lcom/expensetracker/app/data/PendingSmsExpense;", "getPendingSmsExpenses", "repository", "Lcom/expensetracker/app/data/ExpenseRepository;", "settings", "Lcom/expensetracker/app/data/SettingsRepository;", "smsDetectionEnabled", "getSmsDetectionEnabled", "acceptPendingSms", "", "item", "categoryId", "", "description", "amount", "", "date", "onDone", "Lkotlin/Function0;", "addCategory", "name", "colorHex", "convertCurrency", "newSymbol", "rate", "deleteCategory", "category", "onResult", "Lkotlin/Function1;", "Lcom/expensetracker/app/data/DeleteCategoryResult;", "deleteExpense", "expense", "dismissPendingSms", "handleIncomingSms", "message", "isCategoryInUse", "navigateMonth", "delta", "recolorCategory", "renameCategory", "newName", "resetAllData", "saveExpense", "id", "(Ljava/lang/Long;JLjava/lang/String;DLjava/lang/String;Lkotlin/jvm/functions/Function0;)V", "setBudget", "(Ljava/lang/Long;D)V", "setCurrencySymbol", "symbol", "setDisplayName", "setLanguagePref", "pref", "setSmsDetectionEnabled", "enabled", "app_debug"})
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
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> pendingSmsExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.BudgetEntity>> budgets = null;
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
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.PendingSmsExpense>> getPendingSmsExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.BudgetEntity>> getBudgets() {
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
}