package com.expensetracker.app.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0013\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J@\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\u001b2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00150\u001fJ\u001c\u0010 \u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u00172\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J\u001e\u0010\"\u001a\u00020\u00152\u0006\u0010#\u001a\u00020\b2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00150\u001fJ\u001e\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020\f2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00150\u001fJ\u001a\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u0010\u0016\u001a\u00020\u0017JQ\u0010\'\u001a\u00020\u00152\b\u0010(\u001a\u0004\u0018\u00010\u00172\u0006\u0010)\u001a\u00020\u001b2\u0006\u0010*\u001a\u00020\u001b2\u0006\u0010+\u001a\u00020\u001b2\b\b\u0002\u0010,\u001a\u00020\u00192\b\b\u0002\u0010-\u001a\u00020\u001b2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00150\u001f\u00a2\u0006\u0002\u0010.J\"\u0010/\u001a\u00020\u00192\f\u00100\u001a\b\u0012\u0004\u0012\u00020\f0\u00072\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J\"\u00101\u001a\u00020\u00192\f\u00100\u001a\b\u0012\u0004\u0012\u00020\f0\u00072\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\b0\u0007R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\nR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\n\u00a8\u00062"}, d2 = {"Lcom/expensetracker/app/viewmodel/KhataViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "allEntries", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/expensetracker/app/data/KhataEntryEntity;", "getAllEntries", "()Lkotlinx/coroutines/flow/StateFlow;", "allParties", "Lcom/expensetracker/app/data/KhataPartyEntity;", "getAllParties", "iOweParties", "getIOweParties", "repo", "Lcom/expensetracker/app/data/KhataRepository;", "theyOweParties", "getTheyOweParties", "addEntry", "", "partyId", "", "amount", "", "note", "", "date", "type", "onDone", "Lkotlin/Function0;", "balanceForParty", "entries", "deleteEntry", "entry", "deleteParty", "party", "entriesForParty", "saveParty", "id", "name", "phone", "direction", "initialAmount", "initialNote", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/lang/String;Lkotlin/jvm/functions/Function0;)V", "totalIOwe", "parties", "totalTheyOwe", "app_release"})
public final class KhataViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.expensetracker.app.data.KhataRepository repo = null;
    
    /**
     * All parties, all directions — used for summary totals on dashboard.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> allParties = null;
    
    /**
     * All entries across all parties — used to compute per-party balances reactively.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> allEntries = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> iOweParties = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> theyOweParties = null;
    
    public KhataViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    /**
     * All parties, all directions — used for summary totals on dashboard.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> getAllParties() {
        return null;
    }
    
    /**
     * All entries across all parties — used to compute per-party balances reactively.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> getAllEntries() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> getIOweParties() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataPartyEntity>> getTheyOweParties() {
        return null;
    }
    
    /**
     * Outstanding balance for [partyId] derived from the already-loaded [allEntries] snapshot.
     * Credits add to the balance; payments reduce it. Clamp to 0 — a negative balance just
     * means overpaid, which is surfaced as SAR 0.00 rather than a confusing negative number.
     */
    public final double balanceForParty(long partyId, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.KhataEntryEntity> entries) {
        return 0.0;
    }
    
    /**
     * Total outstanding balance across all I-OWE parties.
     */
    public final double totalIOwe(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.KhataPartyEntity> parties, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.KhataEntryEntity> entries) {
        return 0.0;
    }
    
    /**
     * Total outstanding balance across all THEY-OWE parties.
     */
    public final double totalTheyOwe(@org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.KhataPartyEntity> parties, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.data.KhataEntryEntity> entries) {
        return 0.0;
    }
    
    public final void saveParty(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, double initialAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String initialNote, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteParty(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataPartyEntity party, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void addEntry(long partyId, double amount, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    java.lang.String date, @org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    public final void deleteEntry(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.data.KhataEntryEntity entry, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
    
    /**
     * Returns a hot StateFlow of entries for a specific party. Each call creates a new
     * collector; callers should hoist this inside a [remember] keyed on [partyId].
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.expensetracker.app.data.KhataEntryEntity>> entriesForParty(long partyId) {
        return null;
    }
}