package com.expensetracker.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001c\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\u0011H\'J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\f\u00a8\u0006\u0014"}, d2 = {"Lcom/expensetracker/app/data/DebtPaymentDao;", "", "delete", "", "payment", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "(Lcom/expensetracker/app/data/DebtPaymentEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteForDebt", "debtId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getForDebt", "", "insert", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "totalPaidFor", "", "app_debug"})
@androidx.room.Dao()
public abstract interface DebtPaymentDao {
    
    @androidx.room.Query(value = "SELECT * FROM debt_payments ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> observeAll();
    
    @androidx.room.Query(value = "SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getForDebt(long debtId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.DebtPaymentEntity>> $completion);
    
    /**
     * Sum of all payments recorded against [debtId] — used to derive the outstanding balance.
     */
    @androidx.room.Query(value = "SELECT COALESCE(SUM(amount), 0.0) FROM debt_payments WHERE debtId = :debtId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object totalPaidFor(long debtId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtPaymentEntity payment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtPaymentEntity payment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM debt_payments WHERE debtId = :debtId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteForDebt(long debtId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM debt_payments")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}