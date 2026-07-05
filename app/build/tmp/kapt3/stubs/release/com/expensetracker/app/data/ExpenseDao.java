package com.expensetracker.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0006\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0012\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00100\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00170\u001aH\'J\u001c\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00170\u001a2\u0006\u0010\n\u001a\u00020\u000bH\'J\u001e\u0010\u001c\u001a\u00020\u000e2\u0006\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00100\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010!\u001a\u00020\u000e2\u0006\u0010\"\u001a\u00020#H\u00a7@\u00a2\u0006\u0002\u0010$J\u0016\u0010%\u001a\u00020#2\u0006\u0010&\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\'J\u0016\u0010(\u001a\u00020#2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\'J\u0016\u0010)\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011\u00a8\u0006*"}, d2 = {"Lcom/expensetracker/app/data/ExpenseDao;", "", "categoryInUse", "", "categoryId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "countRecurringInstance", "", "sourceId", "monthKey", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "", "expense", "Lcom/expensetracker/app/data/ExpenseEntity;", "(Lcom/expensetracker/app/data/ExpenseEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteById", "id", "getAllOnce", "", "insert", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "observeByMonth", "reassignCategory", "oldCategoryId", "newCategoryId", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "recurringTemplatesOnce", "scaleAllAmounts", "rate", "", "(DLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sumForDay", "date", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sumForMonth", "update", "app_release"})
@androidx.room.Dao()
public abstract interface ExpenseDao {
    
    @androidx.room.Query(value = "SELECT * FROM expenses ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> observeAll();
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE monthKey = :monthKey ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.ExpenseEntity>> observeByMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseEntity expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Deletes by id directly — used to clean up an auto-generated expense linked to a debt
     * payment without needing to load the full row first.
     */
    @androidx.room.Query(value = "DELETE FROM expenses WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT EXISTS(SELECT 1 FROM expenses WHERE categoryId = :categoryId)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object categoryInUse(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM expenses ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.ExpenseEntity>> $completion);
    
    @androidx.room.Query(value = "UPDATE expenses SET categoryId = :newCategoryId WHERE categoryId = :oldCategoryId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object reassignCategory(long oldCategoryId, long newCategoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE expenses SET amount = amount * :rate")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scaleAllAmounts(double rate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM expenses")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Total spend for a single ISO date (YYYY-MM-DD) — used by the home-screen widget.
     */
    @androidx.room.Query(value = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE date = :date AND isRecurring = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sumForDay(@org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion);
    
    /**
     * Total spend for a month (YYYY-MM) — used by the home-screen widget.
     */
    @androidx.room.Query(value = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE monthKey = :monthKey AND isRecurring = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sumForMonth(@org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion);
    
    /**
     * All template rows — expenses the user has marked to repeat every month.
     */
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE isRecurring = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object recurringTemplatesOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.ExpenseEntity>> $completion);
    
    /**
     * How many auto-generated copies of [sourceId] already exist for [monthKey].
     * Used by the scheduler to avoid duplicates on successive app launches.
     */
    @androidx.room.Query(value = "SELECT COUNT(*) FROM expenses WHERE recurringSourceId = :sourceId AND monthKey = :monthKey")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countRecurringInstance(long sourceId, @org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}