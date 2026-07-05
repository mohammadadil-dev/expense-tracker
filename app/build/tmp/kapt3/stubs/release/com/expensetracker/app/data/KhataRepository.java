package com.expensetracker.app.data;

/**
 * Single access point for all Khata (local shop credit ledger) data.
 *
 * Takes the full [AppDatabase] so it can also write to the expenses table:
 * every CREDIT entry on an I-OWE party auto-generates an [ExpenseEntity] row
 * (filed under the "Khata" category) so the amount appears in the dashboard's
 * monthly totals, budget tracker, and category breakdown — exactly the same
 * pattern used by debt/EMI payments.
 *
 * Deleting an entry or its parent party cleans up the linked expense so no
 * phantom spend lingers in the dashboard.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u0002\n\u0002\b\u000b\u0018\u0000 +2\u00020\u0001:\u0001+B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J6\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0019J0\u0010\u001a\u001a\u00020\u00112\b\u0010\u001b\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u001fJ\u0016\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010#J\u0016\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010&J\u000e\u0010\'\u001a\u00020\u0011H\u0082@\u00a2\u0006\u0002\u0010(J\u001a\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u0010\u0012\u001a\u00020\u0011J\u001a\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u00062\u0006\u0010\u001e\u001a\u00020\u0016R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/expensetracker/app/data/KhataRepository;", "", "db", "Lcom/expensetracker/app/data/AppDatabase;", "(Lcom/expensetracker/app/data/AppDatabase;)V", "allEntries", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/expensetracker/app/data/KhataEntryEntity;", "getAllEntries", "()Lkotlinx/coroutines/flow/Flow;", "allParties", "Lcom/expensetracker/app/data/KhataPartyEntity;", "getAllParties", "dao", "Lcom/expensetracker/app/data/KhataDao;", "addEntry", "", "partyId", "amount", "", "note", "", "date", "type", "(JDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addOrUpdateParty", "id", "name", "phone", "direction", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteEntry", "", "entry", "(Lcom/expensetracker/app/data/KhataEntryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteParty", "party", "(Lcom/expensetracker/app/data/KhataPartyEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ensureKhataCategoryId", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "entriesForParty", "partiesByDirection", "Companion", "app_release"})
public final class KhataRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KHATA_CATEGORY_KEY = "cat_khata";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KHATA_CATEGORY_COLOR = "#7C3AED";
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.KhataDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> allParties = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> allEntries = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.KhataRepository.Companion Companion = null;
    
    public KhataRepository(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.AppDatabase db) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> getAllParties() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> partiesByDirection(@org.jetbrains.annotations.NotNull()
    java.lang.String direction) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> entriesForParty(long partyId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> getAllEntries() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addOrUpdateParty(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteParty(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataPartyEntity party, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Adds a new Khata entry. For CREDIT entries on I-OWE parties the amount is
     * real outgoing spend, so an [ExpenseEntity] is created and the link stored.
     * PAYMENT entries (settling the debt) and THEY-OWE entries (incoming money)
     * produce no expense row.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addEntry(long partyId, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteEntry(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataEntryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Returns (or creates) the Khata category and gives back its DB id.
     */
    private final java.lang.Object ensureKhataCategoryId(kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/expensetracker/app/data/KhataRepository$Companion;", "", "()V", "KHATA_CATEGORY_COLOR", "", "KHATA_CATEGORY_KEY", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}