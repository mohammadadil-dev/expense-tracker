package com.expensetracker.app.util;

/**
 * On-device JSON backup and restore.
 *
 * Export: serialises categories, expenses, budgets, Khata parties/entries, and debts/payments
 * to a single JSON file and hands it out via FileProvider so the user can save to Downloads
 * or share to Drive.
 *
 * Import: reads a previously exported JSON file and re-inserts all records, building ID
 * remaps so foreign-key relationships are correctly re-wired on the new device.
 *
 * Version history:
 *  v1 — categories, expenses, budgets only
 *  v2 — adds khataParties, khataEntries, debts, debtPayments
 *  v3 — adds income entries
 *
 * v1 and v2 backups import cleanly into v3 (missing sections default to empty arrays).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u00010B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0082@\u00a2\u0006\u0002\u0010\u0014J\u001e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u001e\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0019J&\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u001e\u0010\u001f\u001a\u00020\u001c2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010 \u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010!J\u001e\u0010\"\u001a\u00020\u001c2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u0006H\u0082@\u00a2\u0006\u0002\u0010!J\u001e\u0010$\u001a\u00020%2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010&\u001a\u00020\u0006J\f\u0010\'\u001a\u00020\u0011*\u00020(H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020)H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020*H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020+H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020,H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020-H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020.H\u0002J\f\u0010\'\u001a\u00020\u0011*\u00020/H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcom/expensetracker/app/util/BackupManager;", "", "()V", "BACKUP_VERSION", "", "KEY_BUDGETS", "", "KEY_CATEGORIES", "KEY_DEBTS", "KEY_DEBT_PAYMENTS", "KEY_EXPENSES", "KEY_EXPORTED_AT", "KEY_INCOME", "KEY_KHATA_ENTRIES", "KEY_KHATA_PARTIES", "KEY_VERSION", "buildJson", "Lorg/json/JSONObject;", "db", "Lcom/expensetracker/app/data/AppDatabase;", "(Lcom/expensetracker/app/data/AppDatabase;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "export", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "(Landroid/content/Context;Lcom/expensetracker/app/data/AppDatabase;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exportJson", "import", "Lcom/expensetracker/app/util/BackupManager$ImportResult;", "uri", "(Landroid/content/Context;Lcom/expensetracker/app/data/AppDatabase;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "importFromJson", "json", "(Lcom/expensetracker/app/data/AppDatabase;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "importJson", "text", "shareExport", "", "chooserTitle", "toJson", "Lcom/expensetracker/app/data/BudgetEntity;", "Lcom/expensetracker/app/data/CategoryEntity;", "Lcom/expensetracker/app/data/DebtEntity;", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "Lcom/expensetracker/app/data/ExpenseEntity;", "Lcom/expensetracker/app/data/IncomeEntity;", "Lcom/expensetracker/app/data/KhataEntryEntity;", "Lcom/expensetracker/app/data/KhataPartyEntity;", "ImportResult", "app_debug"})
public final class BackupManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_VERSION = "version";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_EXPORTED_AT = "exportedAt";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CATEGORIES = "categories";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_EXPENSES = "expenses";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BUDGETS = "budgets";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_KHATA_PARTIES = "khataParties";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_KHATA_ENTRIES = "khataEntries";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_DEBTS = "debts";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_DEBT_PAYMENTS = "debtPayments";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_INCOME = "income";
    private static final int BACKUP_VERSION = 3;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.BackupManager INSTANCE = null;
    
    private BackupManager() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object export(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.AppDatabase db, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super android.net.Uri> $completion) {
        return null;
    }
    
    /**
     * Same serialisation as [export] but returns the raw JSON string directly —
     * used by the Drive upload path to avoid an unnecessary FileProvider round-trip.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object exportJson(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.AppDatabase db, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.Object buildJson(com.expensetracker.app.data.AppDatabase db, kotlin.coroutines.Continuation<? super org.json.JSONObject> $completion) {
        return null;
    }
    
    public final void shareExport(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String chooserTitle) {
    }
    
    /**
     * Overload used by the Drive restore path, which already has the JSON string.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object importFromJson(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.AppDatabase db, @org.jetbrains.annotations.NotNull()
    java.lang.String json, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.expensetracker.app.util.BackupManager.ImportResult> $completion) {
        return null;
    }
    
    private final java.lang.Object importJson(com.expensetracker.app.data.AppDatabase db, java.lang.String text, kotlin.coroutines.Continuation<? super com.expensetracker.app.util.BackupManager.ImportResult> $completion) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.CategoryEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.ExpenseEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.BudgetEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.KhataPartyEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.KhataEntryEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.DebtEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.DebtPaymentEntity $this$toJson) {
        return null;
    }
    
    private final org.json.JSONObject toJson(com.expensetracker.app.data.IncomeEntity $this$toJson) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003JY\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\"\u001a\u00020#H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\r\u00a8\u0006$"}, d2 = {"Lcom/expensetracker/app/util/BackupManager$ImportResult;", "", "categories", "", "expenses", "budgets", "khataParties", "khataEntries", "debts", "debtPayments", "income", "(IIIIIIII)V", "getBudgets", "()I", "getCategories", "getDebtPayments", "getDebts", "getExpenses", "getIncome", "getKhataEntries", "getKhataParties", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    public static final class ImportResult {
        private final int categories = 0;
        private final int expenses = 0;
        private final int budgets = 0;
        private final int khataParties = 0;
        private final int khataEntries = 0;
        private final int debts = 0;
        private final int debtPayments = 0;
        private final int income = 0;
        
        public ImportResult(int categories, int expenses, int budgets, int khataParties, int khataEntries, int debts, int debtPayments, int income) {
            super();
        }
        
        public final int getCategories() {
            return 0;
        }
        
        public final int getExpenses() {
            return 0;
        }
        
        public final int getBudgets() {
            return 0;
        }
        
        public final int getKhataParties() {
            return 0;
        }
        
        public final int getKhataEntries() {
            return 0;
        }
        
        public final int getDebts() {
            return 0;
        }
        
        public final int getDebtPayments() {
            return 0;
        }
        
        public final int getIncome() {
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
        
        public final int component6() {
            return 0;
        }
        
        public final int component7() {
            return 0;
        }
        
        public final int component8() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.BackupManager.ImportResult copy(int categories, int expenses, int budgets, int khataParties, int khataEntries, int debts, int debtPayments, int income) {
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