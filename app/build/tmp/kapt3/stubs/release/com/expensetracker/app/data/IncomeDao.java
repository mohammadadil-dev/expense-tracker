package com.expensetracker.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J&\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\r0\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00120\u00172\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\b0\u00172\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u0019"}, d2 = {"Lcom/expensetracker/app/data/IncomeDao;", "", "countMonthlyInstance", "", "monthKey", "", "source", "amount", "", "(Ljava/lang/String;Ljava/lang/String;DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "", "income", "Lcom/expensetracker/app/data/IncomeEntity;", "(Lcom/expensetracker/app/data/IncomeEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllOnce", "", "getRecurringTemplates", "insert", "", "observeForMonth", "Lkotlinx/coroutines/flow/Flow;", "totalForMonth", "app_release"})
@androidx.room.Dao()
public abstract interface IncomeDao {
    
    @androidx.room.Query(value = "SELECT * FROM income_entries WHERE monthKey = :monthKey ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.IncomeEntity>> observeForMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey);
    
    @androidx.room.Query(value = "SELECT COALESCE(SUM(amount), 0.0) FROM income_entries WHERE monthKey = :monthKey")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> totalForMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey);
    
    @androidx.room.Query(value = "SELECT * FROM income_entries ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.IncomeEntity>> $completion);
    
    /**
     * All recurring templates (isRecurring = 1) regardless of month.
     */
    @androidx.room.Query(value = "SELECT * FROM income_entries WHERE isRecurring = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecurringTemplates(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.IncomeEntity>> $completion);
    
    /**
     * Count how many income entries with the given [source] and [amount] already exist for
     * [monthKey]. Used to avoid generating duplicate recurring copies when the app opens
     * multiple times in the same month.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM income_entries WHERE monthKey = :monthKey AND source = :source AND amount = :amount AND isRecurring = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countMonthlyInstance(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, @org.jetbrains.annotations.NotNull()
    java.lang.String source, double amount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.IncomeEntity income, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.IncomeEntity income, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM income_entries")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}