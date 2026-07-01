package com.expensetracker.app.util;

/**
 * On-device "AI" layer for debt/loan tracking — same philosophy as [FinancialInsights]: plain
 * arithmetic over [DebtEntity]/[DebtPaymentEntity] rows already loaded in memory, no network
 * call. Stays Compose-free so money/date formatting is left to the caller.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002\r\u000eB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\"\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006J\u001c\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/expensetracker/app/util/DebtInsights;", "", "()V", "computeNetPosition", "Lcom/expensetracker/app/util/DebtInsights$NetPosition;", "debts", "", "Lcom/expensetracker/app/data/DebtEntity;", "payments", "Lcom/expensetracker/app/data/DebtPaymentEntity;", "computeProgress", "Lcom/expensetracker/app/util/DebtInsights$DebtProgress;", "debt", "DebtProgress", "NetPosition", "app_debug"})
public final class DebtInsights {
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.DebtInsights INSTANCE = null;
    
    private DebtInsights() {
        super();
    }
    
    /**
     * Computes [DebtProgress] for one debt from its full payment history.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.util.DebtInsights.DebtProgress computeProgress(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.DebtEntity debt, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.DebtPaymentEntity> payments) {
        return null;
    }
    
    /**
     * Net position across every open debt: positive = others owe the user more than they owe out.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.util.DebtInsights.NetPosition computeNetPosition(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.DebtEntity> debts, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.DebtPaymentEntity> payments) {
        return null;
    }
    
    /**
     * Everything the UI needs to render one debt's card: its live outstanding balance (never
     * stored — always [DebtEntity.principal] minus payments so far), payoff projection, and the
     * "pay off sooner" accelerated-payoff insight.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\u0006\u0010\r\u001a\u00020\u0005\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\n\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0005H\u00c6\u0003J\t\u0010!\u001a\u00020\bH\u00c6\u0003J\u0010\u0010\"\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\u000b\u0010#\u001a\u0004\u0018\u00010\fH\u00c6\u0003J\t\u0010$\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010%\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013Jd\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u00052\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\nH\u00c6\u0001\u00a2\u0006\u0002\u0010\'J\u0013\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020\nH\u00d6\u0001J\t\u0010,\u001a\u00020-H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0015\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0015\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\r\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0017\u00a8\u0006."}, d2 = {"Lcom/expensetracker/app/util/DebtInsights$DebtProgress;", "", "debt", "Lcom/expensetracker/app/data/DebtEntity;", "totalPaid", "", "outstandingBalance", "paidFraction", "", "monthsToPayoff", "", "payoffDate", "Ljava/time/LocalDate;", "suggestedExtraPayment", "monthsSavedWithExtra", "(Lcom/expensetracker/app/data/DebtEntity;DDFLjava/lang/Integer;Ljava/time/LocalDate;DLjava/lang/Integer;)V", "getDebt", "()Lcom/expensetracker/app/data/DebtEntity;", "getMonthsSavedWithExtra", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getMonthsToPayoff", "getOutstandingBalance", "()D", "getPaidFraction", "()F", "getPayoffDate", "()Ljava/time/LocalDate;", "getSuggestedExtraPayment", "getTotalPaid", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Lcom/expensetracker/app/data/DebtEntity;DDFLjava/lang/Integer;Ljava/time/LocalDate;DLjava/lang/Integer;)Lcom/expensetracker/app/util/DebtInsights$DebtProgress;", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    public static final class DebtProgress {
        @org.jetbrains.annotations.NotNull()
        private final com.expensetracker.app.data.DebtEntity debt = null;
        private final double totalPaid = 0.0;
        private final double outstandingBalance = 0.0;
        private final float paidFraction = 0.0F;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer monthsToPayoff = null;
        @org.jetbrains.annotations.Nullable()
        private final java.time.LocalDate payoffDate = null;
        private final double suggestedExtraPayment = 0.0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Integer monthsSavedWithExtra = null;
        
        public DebtProgress(@org.jetbrains.annotations.NotNull()
        com.expensetracker.app.data.DebtEntity debt, double totalPaid, double outstandingBalance, float paidFraction, @org.jetbrains.annotations.Nullable()
        java.lang.Integer monthsToPayoff, @org.jetbrains.annotations.Nullable()
        java.time.LocalDate payoffDate, double suggestedExtraPayment, @org.jetbrains.annotations.Nullable()
        java.lang.Integer monthsSavedWithExtra) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.data.DebtEntity getDebt() {
            return null;
        }
        
        public final double getTotalPaid() {
            return 0.0;
        }
        
        public final double getOutstandingBalance() {
            return 0.0;
        }
        
        public final float getPaidFraction() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getMonthsToPayoff() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.time.LocalDate getPayoffDate() {
            return null;
        }
        
        public final double getSuggestedExtraPayment() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer getMonthsSavedWithExtra() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.data.DebtEntity component1() {
            return null;
        }
        
        public final double component2() {
            return 0.0;
        }
        
        public final double component3() {
            return 0.0;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component5() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.time.LocalDate component6() {
            return null;
        }
        
        public final double component7() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Integer component8() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.DebtInsights.DebtProgress copy(@org.jetbrains.annotations.NotNull()
        com.expensetracker.app.data.DebtEntity debt, double totalPaid, double outstandingBalance, float paidFraction, @org.jetbrains.annotations.Nullable()
        java.lang.Integer monthsToPayoff, @org.jetbrains.annotations.Nullable()
        java.time.LocalDate payoffDate, double suggestedExtraPayment, @org.jetbrains.annotations.Nullable()
        java.lang.Integer monthsSavedWithExtra) {
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
    
    /**
     * Aggregate across every open (not closed) debt — powers the dashboard tile + screen header.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lcom/expensetracker/app/util/DebtInsights$NetPosition;", "", "totalOwedByUser", "", "totalOwedToUser", "net", "(DDD)V", "getNet", "()D", "getTotalOwedByUser", "getTotalOwedToUser", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class NetPosition {
        private final double totalOwedByUser = 0.0;
        private final double totalOwedToUser = 0.0;
        private final double net = 0.0;
        
        public NetPosition(double totalOwedByUser, double totalOwedToUser, double net) {
            super();
        }
        
        public final double getTotalOwedByUser() {
            return 0.0;
        }
        
        public final double getTotalOwedToUser() {
            return 0.0;
        }
        
        public final double getNet() {
            return 0.0;
        }
        
        public final double component1() {
            return 0.0;
        }
        
        public final double component2() {
            return 0.0;
        }
        
        public final double component3() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.expensetracker.app.util.DebtInsights.NetPosition copy(double totalOwedByUser, double totalOwedToUser, double net) {
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