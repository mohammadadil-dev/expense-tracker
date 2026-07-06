package com.expensetracker.app.data;

/**
 * A savings goal the user wants to reach — "Vacation fund", "New phone", "Eid gifts", etc.
 *
 * [savedAmount] is manually updated by the user (or auto-credited from surplus budget) rather
 * than derived from expenses, keeping goals independent of the spending tracker so users can
 * track goals for money kept in a separate envelope / jar.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0018\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BQ\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\rH\u00c6\u0003JY\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010#\u001a\u00020\r2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020&H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\n\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0012\u00a8\u0006("}, d2 = {"Lcom/expensetracker/app/data/GoalEntity;", "", "id", "", "name", "", "emoji", "targetAmount", "", "savedAmount", "targetDate", "createdAt", "isCompleted", "", "(JLjava/lang/String;Ljava/lang/String;DDLjava/lang/String;JZ)V", "getCreatedAt", "()J", "getEmoji", "()Ljava/lang/String;", "getId", "()Z", "getName", "getSavedAmount", "()D", "getTargetAmount", "getTargetDate", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "savings_goals")
public final class GoalEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    
    /**
     * User-chosen label, e.g. "New Phone" or "Eid gifts".
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    
    /**
     * Single emoji chosen from the picker, e.g. "🏖️". Empty string = use default 🎯.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String emoji = null;
    
    /**
     * Target amount the user wants to save.
     */
    private final double targetAmount = 0.0;
    
    /**
     * Amount already saved / contributed toward this goal.
     */
    private final double savedAmount = 0.0;
    
    /**
     * Optional ISO date string (yyyy-MM-dd) by which the user wants to reach the goal. Empty = no deadline.
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String targetDate = null;
    
    /**
     * Epoch millis when this goal was created — used for default sort order.
     */
    private final long createdAt = 0L;
    
    /**
     * True once the user manually marks the goal as reached.
     */
    private final boolean isCompleted = false;
    
    public GoalEntity(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String emoji, double targetAmount, double savedAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String targetDate, long createdAt, boolean isCompleted) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    /**
     * User-chosen label, e.g. "New Phone" or "Eid gifts".
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    /**
     * Single emoji chosen from the picker, e.g. "🏖️". Empty string = use default 🎯.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getEmoji() {
        return null;
    }
    
    /**
     * Target amount the user wants to save.
     */
    public final double getTargetAmount() {
        return 0.0;
    }
    
    /**
     * Amount already saved / contributed toward this goal.
     */
    public final double getSavedAmount() {
        return 0.0;
    }
    
    /**
     * Optional ISO date string (yyyy-MM-dd) by which the user wants to reach the goal. Empty = no deadline.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTargetDate() {
        return null;
    }
    
    /**
     * Epoch millis when this goal was created — used for default sort order.
     */
    public final long getCreatedAt() {
        return 0L;
    }
    
    /**
     * True once the user manually marks the goal as reached.
     */
    public final boolean isCompleted() {
        return false;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.GoalEntity copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String emoji, double targetAmount, double savedAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String targetDate, long createdAt, boolean isCompleted) {
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