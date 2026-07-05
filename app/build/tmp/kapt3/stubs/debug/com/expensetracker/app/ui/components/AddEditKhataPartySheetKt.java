package com.expensetracker.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u00b8\u0001\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\u00042\u008d\u0001\u0010\n\u001a\u0088\u0001\u0012\u0015\u0012\u0013\u0018\u00010\f\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0010\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0013\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u0014\u0012\u0004\u0012\u00020\u00060\u000b2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00060\u0016H\u0007\u001a\b\u0010\u0017\u001a\u00020\u0002H\u0002\u001a\u001c\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u00192\u0006\u0010\u001a\u001a\u00020\u0004H\u0002\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00040\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"COUNTRY_ENTRIES", "", "Lcom/expensetracker/app/ui/components/CountryEntry;", "KNOWN_CODES", "", "AddEditKhataPartySheet", "", "initial", "Lcom/expensetracker/app/data/KhataPartyEntity;", "defaultDirection", "onSave", "Lkotlin/Function6;", "", "Lkotlin/ParameterName;", "name", "id", "phone", "direction", "", "initialAmount", "initialNote", "onDismiss", "Lkotlin/Function0;", "defaultCountryEntry", "splitPhone", "Lkotlin/Pair;", "stored", "app_debug"})
public final class AddEditKhataPartySheetKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.expensetracker.app.ui.components.CountryEntry> COUNTRY_ENTRIES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> KNOWN_CODES = null;
    
    /**
     * Auto-detect country from device locale; falls back to Saudi Arabia.
     */
    private static final com.expensetracker.app.ui.components.CountryEntry defaultCountryEntry() {
        return null;
    }
    
    /**
     * Split a stored phone string back into (countryCode, localNumber).
     */
    private static final kotlin.Pair<java.lang.String, java.lang.String> splitPhone(java.lang.String stored) {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AddEditKhataPartySheet(@org.jetbrains.annotations.Nullable()
    com.expensetracker.app.data.KhataPartyEntity initial, @org.jetbrains.annotations.NotNull()
    java.lang.String defaultDirection, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function6<? super java.lang.Long, ? super java.lang.String, ? super java.lang.String, ? super java.lang.String, ? super java.lang.Double, ? super java.lang.String, kotlin.Unit> onSave, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}