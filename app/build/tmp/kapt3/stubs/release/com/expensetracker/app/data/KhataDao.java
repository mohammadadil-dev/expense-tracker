package com.expensetracker.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00040\u0003H\'J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0010\u001a\u00020\u0011H\'J\u001c\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0017\u001a\u00020\u00112\u0006\u0010\n\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\u0018\u001a\u00020\u00112\u0006\u0010\r\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00040\u00032\u0006\u0010\u001a\u001a\u00020\u001bH\'J\u0018\u0010\u001c\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u001d\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010 \u001a\u00020\u001f2\u0006\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010!\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u000e\u00a8\u0006\""}, d2 = {"Lcom/expensetracker/app/data/KhataDao;", "", "allEntries", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/expensetracker/app/data/KhataEntryEntity;", "allParties", "Lcom/expensetracker/app/data/KhataPartyEntity;", "deleteEntry", "", "entry", "(Lcom/expensetracker/app/data/KhataEntryEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteParty", "party", "(Lcom/expensetracker/app/data/KhataPartyEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "entriesForParty", "partyId", "", "entriesForPartyOnce", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllEntriesOnce", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllPartiesOnce", "insertEntry", "insertParty", "partiesByDirection", "direction", "", "partyById", "id", "totalCreditForParty", "", "totalPaymentForParty", "updateParty", "app_release"})
@androidx.room.Dao()
public abstract interface KhataDao {
    
    @androidx.room.Query(value = "SELECT * FROM khata_parties WHERE direction = :direction ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> partiesByDirection(@org.jetbrains.annotations.NotNull()
    java.lang.String direction);
    
    @androidx.room.Query(value = "SELECT * FROM khata_parties ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> allParties();
    
    @androidx.room.Query(value = "SELECT * FROM khata_parties ORDER BY name ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllPartiesOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.KhataPartyEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM khata_parties WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object partyById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.expensetracker.app.data.KhataPartyEntity> $completion);
    
    @androidx.room.Insert(onConflict = 3)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertParty(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataPartyEntity party, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    /**
     * Use for edits — avoids the DELETE+INSERT that REPLACE does, which would
     * cascade-delete all entries for the party.
     */
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateParty(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataPartyEntity party, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteParty(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataPartyEntity party, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM khata_entries WHERE partyId = :partyId ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> entriesForParty(long partyId);
    
    @androidx.room.Query(value = "SELECT * FROM khata_entries WHERE partyId = :partyId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object entriesForPartyOnce(long partyId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.KhataEntryEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM khata_entries ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> allEntries();
    
    @androidx.room.Query(value = "SELECT * FROM khata_entries ORDER BY date DESC, id DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllEntriesOnce(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.expensetracker.app.data.KhataEntryEntity>> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertEntry(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataEntryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteEntry(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataEntryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Sum of all CREDIT entries for a party (the gross tab).
     */
    @androidx.room.Query(value = "SELECT COALESCE(SUM(amount),0) FROM khata_entries WHERE partyId = :partyId AND type = \'CREDIT\'")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object totalCreditForParty(long partyId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion);
    
    /**
     * Sum of all PAYMENT entries for a party (what has been settled).
     */
    @androidx.room.Query(value = "SELECT COALESCE(SUM(amount),0) FROM khata_entries WHERE partyId = :partyId AND type = \'PAYMENT\'")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object totalPaymentForParty(long partyId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Double> $completion);
}