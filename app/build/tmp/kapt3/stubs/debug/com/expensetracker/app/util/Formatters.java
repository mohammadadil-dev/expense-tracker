package com.expensetracker.app.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u0016\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0006J\u000e\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\f"}, d2 = {"Lcom/expensetracker/app/util/Formatters;", "", "()V", "isSaudiRiyalAmount", "", "formatted", "", "money", "amount", "", "currencySymbol", "stripSaudiRiyalSymbol", "app_debug"})
public final class Formatters {
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.Formatters INSTANCE = null;
    
    private Formatters() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String money(double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String currencySymbol) {
        return null;
    }
    
    /**
     * True if [formatted] (the output of [money]) was built with the Saudi Riyal symbol.
     */
    public final boolean isSaudiRiyalAmount(@org.jetbrains.annotations.NotNull()
    java.lang.String formatted) {
        return false;
    }
    
    /**
     * Strips the "ر.س" prefix from [formatted], keeping any leading "-" sign intact.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String stripSaudiRiyalSymbol(@org.jetbrains.annotations.NotNull()
    java.lang.String formatted) {
        return null;
    }
}