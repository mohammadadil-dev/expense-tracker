package com.expensetracker.app.data;

/**
 * A single income entry (salary, freelance payment, bonus, etc.) recorded by the user.
 * Income entries are scoped to a monthKey so they show up correctly on the dashboard.
 *
 * When [isRecurring] is true this row is a *template* — it auto-generates a copy at the start
 * of each new month (analogous to how recurring expenses work). The template itself is stored
 * with the month it was first created in; generated copies share [source]/[note]/[amount] but
 * get a fresh [date] and [monthKey].
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\fH\u00c6\u0003JO\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010 \u001a\u00020\f2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0014R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011\u00a8\u0006%"}, d2 = {"Lcom/expensetracker/app/data/IncomeEntity;", "", "id", "", "amount", "", "source", "", "note", "date", "monthKey", "isRecurring", "", "(JDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V", "getAmount", "()D", "getDate", "()Ljava/lang/String;", "getId", "()J", "()Z", "getMonthKey", "getNote", "getSource", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "app_release"})
@androidx.room.Entity(tableName = "income_entries", indices = {@androidx.room.Index(value = {"monthKey"})})
public final class IncomeEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    
    /**
     * Positive amount in the user's currency.
     */
    private final double amount = 0.0;
    
    /**
     * Free-text label: "Salary", "Freelance", "Bonus", etc.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String source = null;
    
    /**
     * Optional extra note.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String note = null;
    
    /**
     * ISO date YYYY-MM-DD.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String date = null;
    
    /**
     * YYYY-MM derived from [date].
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String monthKey = null;
    
    /**
     * True if this is a recurring income template that should auto-generate a copy at the
     * start of every new month. Default false (one-time entry).
     */
    private final boolean isRecurring = false;
    
    public IncomeEntity(long id, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String source, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, boolean isRecurring) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    /**
     * Positive amount in the user's currency.
     */
    public final double getAmount() {
        return 0.0;
    }
    
    /**
     * Free-text label: "Salary", "Freelance", "Bonus", etc.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSource() {
        return null;
    }
    
    /**
     * Optional extra note.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNote() {
        return null;
    }
    
    /**
     * ISO date YYYY-MM-DD.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDate() {
        return null;
    }
    
    /**
     * YYYY-MM derived from [date].
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMonthKey() {
        return null;
    }
    
    /**
     * True if this is a recurring income template that should auto-generate a copy at the
     * start of every new month. Default false (one-time entry).
     */
    public final boolean isRecurring() {
        return false;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final double component2() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.IncomeEntity copy(long id, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String source, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String monthKey, boolean isRecurring) {
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