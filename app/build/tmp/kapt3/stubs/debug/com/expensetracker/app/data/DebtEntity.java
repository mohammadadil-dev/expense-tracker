package com.expensetracker.app.data;

/**
 * One debt or loan being tracked — either money the user owes someone else
 * ([direction] == [DIRECTION_OWE]) or money someone else owes the user
 * ([direction] == [DIRECTION_OWED]). [direction] is stored as a plain string rather than a
 * Kotlin enum + Room TypeConverter, matching this project's existing minimal-entity style
 * (see [CategoryEntity.colorHex]).
 *
 * [principal] is the original loan amount. The *current* outstanding balance is never stored
 * here — it's computed live as `principal - sum(DebtPaymentEntity.amount for this debt)`, the
 * same "standing target vs. computed total" split already used by [BudgetEntity] (persisted
 * target, computed spend).
 *
 * [interestRatePercent] is an annual rate (e.g. 12.0 for 12%/year) — 0 for interest-free debts,
 * which covers most informal IOUs between friends/family. [minimumPayment] is the EMI / minimum
 * monthly payment used to project a payoff date; 0 means "no fixed schedule", and payoff
 * projection is skipped for that debt.
 *
 * [isClosed] is set once the debt is fully paid off (automatically, when a recorded payment
 * brings the outstanding balance to zero) or manually settled by the user (e.g. forgiven, or
 * settled informally for a different amount than originally tracked).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u001d\n\u0002\u0010\b\n\u0002\b\u0003\b\u0087\b\u0018\u0000 .2\u00020\u0001:\u0001.Be\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\u0005\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0005H\u00c6\u0003J\t\u0010!\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\"\u001a\u00020\bH\u00c6\u0003J\t\u0010#\u001a\u00020\bH\u00c6\u0003J\t\u0010$\u001a\u00020\bH\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\'\u001a\u00020\u000eH\u00c6\u0003Jq\u0010(\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\u00052\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010)\u001a\u00020\u000e2\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\t\u0010-\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0017R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0012R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0012\u00a8\u0006/"}, d2 = {"Lcom/expensetracker/app/data/DebtEntity;", "", "id", "", "name", "", "direction", "principal", "", "interestRatePercent", "minimumPayment", "startDate", "notes", "isClosed", "", "loanType", "(JLjava/lang/String;Ljava/lang/String;DDDLjava/lang/String;Ljava/lang/String;ZLjava/lang/String;)V", "getDirection", "()Ljava/lang/String;", "getId", "()J", "getInterestRatePercent", "()D", "()Z", "getLoanType", "getMinimumPayment", "getName", "getNotes", "getPrincipal", "getStartDate", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "Companion", "app_debug"})
@androidx.room.Entity(tableName = "debts")
public final class DebtEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String direction = null;
    private final double principal = 0.0;
    private final double interestRatePercent = 0.0;
    private final double minimumPayment = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String startDate = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String notes = null;
    private final boolean isClosed = false;
    
    /**
     * Optional loan type tag — one of the TYPE_* constants below, or null for unclassified.
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String loanType = null;
    
    /**
     * Money the user owes someone else (a liability).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DIRECTION_OWE = "OWE";
    
    /**
     * Money someone else owes the user (a receivable).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DIRECTION_OWED = "OWED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_PERSONAL = "personal";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_HOME = "home";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_CAR = "car";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_EDUCATION = "education";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_GOLD = "gold";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_BUSINESS = "business";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_INFORMAL = "informal";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_OTHER = "other";
    
    /**
     * All types in display order for the picker.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> ALL_TYPES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.DebtEntity.Companion Companion = null;
    
    public DebtEntity(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, double principal, double interestRatePercent, double minimumPayment, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, boolean isClosed, @org.jetbrains.annotations.Nullable()
    java.lang.String loanType) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDirection() {
        return null;
    }
    
    public final double getPrincipal() {
        return 0.0;
    }
    
    public final double getInterestRatePercent() {
        return 0.0;
    }
    
    public final double getMinimumPayment() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStartDate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotes() {
        return null;
    }
    
    public final boolean isClosed() {
        return false;
    }
    
    /**
     * Optional loan type tag — one of the TYPE_* constants below, or null for unclassified.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLoanType() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
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
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.DebtEntity copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, double principal, double interestRatePercent, double minimumPayment, @org.jetbrains.annotations.NotNull()
    java.lang.String startDate, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, boolean isClosed, @org.jetbrains.annotations.Nullable()
    java.lang.String loanType) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u000f\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0012\u001a\u00020\u00052\b\u0010\u0013\u001a\u0004\u0018\u00010\u0005R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/expensetracker/app/data/DebtEntity$Companion;", "", "()V", "ALL_TYPES", "", "", "getALL_TYPES", "()Ljava/util/List;", "DIRECTION_OWE", "DIRECTION_OWED", "TYPE_BUSINESS", "TYPE_CAR", "TYPE_EDUCATION", "TYPE_GOLD", "TYPE_HOME", "TYPE_INFORMAL", "TYPE_OTHER", "TYPE_PERSONAL", "loanTypeEmoji", "type", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Emoji for each loan type — used in the list card and add/edit chips.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String loanTypeEmoji(@org.jetbrains.annotations.Nullable()
        java.lang.String type) {
            return null;
        }
        
        /**
         * All types in display order for the picker.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getALL_TYPES() {
            return null;
        }
    }
}