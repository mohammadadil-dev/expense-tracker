package com.expensetracker.app.data;

/**
 * One line in a Khata party's ledger — either a credit (goods/services taken on tab,
 * increasing the outstanding balance) or a payment (cash settled, decreasing the balance).
 *
 * Outstanding balance for a party = Σ credit amounts − Σ payment amounts.
 * This is always computed live; no balance column is stored.
 *
 * [type] is [TYPE_CREDIT] or [TYPE_PAYMENT].
 * [date] is an ISO-8601 date string ("yyyy-MM-dd"), matching the rest of the app.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0087\b\u0018\u0000 (2\u00020\u0001:\u0001(BE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\bH\u00c6\u0003J\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014JV\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010!J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020&H\u00d6\u0001J\t\u0010\'\u001a\u00020\bH\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010\u00a8\u0006)"}, d2 = {"Lcom/expensetracker/app/data/KhataEntryEntity;", "", "id", "", "partyId", "amount", "", "note", "", "date", "type", "linkedExpenseId", "(JJDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;)V", "getAmount", "()D", "getDate", "()Ljava/lang/String;", "getId", "()J", "getLinkedExpenseId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getNote", "getPartyId", "getType", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(JJDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;)Lcom/expensetracker/app/data/KhataEntryEntity;", "equals", "", "other", "hashCode", "", "toString", "Companion", "app_debug"})
@androidx.room.Entity(tableName = "khata_entries", foreignKeys = {@androidx.room.ForeignKey(entity = com.expensetracker.app.data.KhataPartyEntity.class, parentColumns = {"id"}, childColumns = {"partyId"}, onDelete = 5)}, indices = {@androidx.room.Index(value = {"partyId"})})
public final class KhataEntryEntity {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    private final long partyId = 0L;
    private final double amount = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String note = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String date = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String type = null;
    
    /**
     * Non-null for I-OWE CREDIT entries — points at the auto-generated ExpenseEntity row so
     * deleting this entry also removes the linked expense from the dashboard.
     */
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long linkedExpenseId = null;
    
    /**
     * Goods/services taken on credit — increases the outstanding balance.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_CREDIT = "CREDIT";
    
    /**
     * Cash or in-kind payment — decreases the outstanding balance.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TYPE_PAYMENT = "PAYMENT";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.KhataEntryEntity.Companion Companion = null;
    
    public KhataEntryEntity(long id, long partyId, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.Nullable()
    java.lang.Long linkedExpenseId) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    public final long getPartyId() {
        return 0L;
    }
    
    public final double getAmount() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNote() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getType() {
        return null;
    }
    
    /**
     * Non-null for I-OWE CREDIT entries — points at the auto-generated ExpenseEntity row so
     * deleting this entry also removes the linked expense from the dashboard.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLinkedExpenseId() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final double component3() {
        return 0.0;
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.expensetracker.app.data.KhataEntryEntity copy(long id, long partyId, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.Nullable()
    java.lang.Long linkedExpenseId) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/expensetracker/app/data/KhataEntryEntity$Companion;", "", "()V", "TYPE_CREDIT", "", "TYPE_PAYMENT", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}