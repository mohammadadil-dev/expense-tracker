package com.expensetracker.app.data;

/**
 * The entire app's storage is this single on-device SQLite database via Room.
 * There is no network/server component — everything lives in this file on the phone,
 * which is what keeps this app at zero ongoing infrastructure cost.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\u000eH&J\b\u0010\u000f\u001a\u00020\u0010H&J\b\u0010\u0011\u001a\u00020\u0012H&J\b\u0010\u0013\u001a\u00020\u0014H&\u00a8\u0006\u0016"}, d2 = {"Lcom/expensetracker/app/data/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "budgetDao", "Lcom/expensetracker/app/data/BudgetDao;", "categoryDao", "Lcom/expensetracker/app/data/CategoryDao;", "debtDao", "Lcom/expensetracker/app/data/DebtDao;", "debtPaymentDao", "Lcom/expensetracker/app/data/DebtPaymentDao;", "expenseDao", "Lcom/expensetracker/app/data/ExpenseDao;", "goalDao", "Lcom/expensetracker/app/data/GoalDao;", "incomeDao", "Lcom/expensetracker/app/data/IncomeDao;", "khataDao", "Lcom/expensetracker/app/data/KhataDao;", "pendingSmsExpenseDao", "Lcom/expensetracker/app/data/PendingSmsExpenseDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.expensetracker.app.data.CategoryEntity.class, com.expensetracker.app.data.ExpenseEntity.class, com.expensetracker.app.data.PendingSmsExpense.class, com.expensetracker.app.data.BudgetEntity.class, com.expensetracker.app.data.DebtEntity.class, com.expensetracker.app.data.DebtPaymentEntity.class, com.expensetracker.app.data.KhataPartyEntity.class, com.expensetracker.app.data.KhataEntryEntity.class, com.expensetracker.app.data.IncomeEntity.class, com.expensetracker.app.data.GoalEntity.class}, version = 12, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.expensetracker.app.data.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_2_3 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_3_4 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_4_5 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_5_6 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_6_7 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_7_8 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_8_9 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_9_10 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_10_11 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_11_12 = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.CategoryDao categoryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.ExpenseDao expenseDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.PendingSmsExpenseDao pendingSmsExpenseDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.BudgetDao budgetDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.DebtDao debtDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.DebtPaymentDao debtPaymentDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.KhataDao khataDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.IncomeDao incomeDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.expensetracker.app.data.GoalDao goalDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0013R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/expensetracker/app/data/AppDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/expensetracker/app/data/AppDatabase;", "MIGRATION_10_11", "Landroidx/room/migration/Migration;", "MIGRATION_11_12", "MIGRATION_1_2", "MIGRATION_2_3", "MIGRATION_3_4", "MIGRATION_4_5", "MIGRATION_5_6", "MIGRATION_6_7", "MIGRATION_7_8", "MIGRATION_8_9", "MIGRATION_9_10", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.data.AppDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}