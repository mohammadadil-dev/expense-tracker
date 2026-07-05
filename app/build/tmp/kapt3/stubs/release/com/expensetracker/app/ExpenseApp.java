package com.expensetracker.app;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u000b\u001a\u00020\fX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u0012X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001c\u00a8\u0006\u001f"}, d2 = {"Lcom/expensetracker/app/ExpenseApp;", "Landroid/app/Application;", "()V", "appScope", "Lkotlinx/coroutines/CoroutineScope;", "database", "Lcom/expensetracker/app/data/AppDatabase;", "getDatabase", "()Lcom/expensetracker/app/data/AppDatabase;", "setDatabase", "(Lcom/expensetracker/app/data/AppDatabase;)V", "khataRepository", "Lcom/expensetracker/app/data/KhataRepository;", "getKhataRepository", "()Lcom/expensetracker/app/data/KhataRepository;", "setKhataRepository", "(Lcom/expensetracker/app/data/KhataRepository;)V", "repository", "Lcom/expensetracker/app/data/ExpenseRepository;", "getRepository", "()Lcom/expensetracker/app/data/ExpenseRepository;", "setRepository", "(Lcom/expensetracker/app/data/ExpenseRepository;)V", "settings", "Lcom/expensetracker/app/data/SettingsRepository;", "getSettings", "()Lcom/expensetracker/app/data/SettingsRepository;", "setSettings", "(Lcom/expensetracker/app/data/SettingsRepository;)V", "onCreate", "", "app_release"})
public final class ExpenseApp extends android.app.Application {
    public com.expensetracker.app.data.AppDatabase database;
    public com.expensetracker.app.data.ExpenseRepository repository;
    public com.expensetracker.app.data.KhataRepository khataRepository;
    public com.expensetracker.app.data.SettingsRepository settings;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope appScope = null;
    
    public ExpenseApp() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.AppDatabase getDatabase() {
        return null;
    }
    
    public final void setDatabase(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.AppDatabase p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.ExpenseRepository getRepository() {
        return null;
    }
    
    public final void setRepository(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.ExpenseRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.KhataRepository getKhataRepository() {
        return null;
    }
    
    public final void setKhataRepository(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.SettingsRepository getSettings() {
        return null;
    }
    
    public final void setSettings(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.SettingsRepository p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
}