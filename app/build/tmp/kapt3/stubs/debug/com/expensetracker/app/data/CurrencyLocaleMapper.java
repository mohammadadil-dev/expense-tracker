package com.expensetracker.app.data;

/**
 * Picks a sensible *default* currency symbol from the phone's region setting, free and
 * completely offline (java.util.Locale only — no IP lookups, no paid geolocation APIs).
 *
 * This is a best-guess default, not a verified "nationality" — the user can always change
 * it later in Settings.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004J\u0006\u0010\f\u001a\u00020\u0004J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\u0004J\u000e\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/expensetracker/app/data/CurrencyLocaleMapper;", "", "()V", "SAUDI_RIYAL_SYMBOL", "", "arabicSpeakingCountries", "", "countryToCurrency", "", "eurozoneCountries", "currencyForCountry", "countryCode", "detectFromDevice", "isArabicSpeakingCountry", "", "isSaudiRiyalSymbol", "symbol", "app_debug"})
public final class CurrencyLocaleMapper {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> countryToCurrency = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> eurozoneCountries = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> arabicSpeakingCountries = null;
    
    /**
     * The textual Saudi Riyal abbreviation used as the stored currency symbol (unchanged from
     * before — custom currency, persistence, and the exchange-rate converter all keep working
     * with this plain string). UI call sites that want the new official symbol glyph instead
     * check [isSaudiRiyalSymbol] and swap in `R.drawable.ic_saudi_riyal` for just that one spot.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SAUDI_RIYAL_SYMBOL = "\u0631.\u0633";
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.data.CurrencyLocaleMapper INSTANCE = null;
    
    private CurrencyLocaleMapper() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String currencyForCountry(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    public final boolean isArabicSpeakingCountry(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return false;
    }
    
    /**
     * Reads the phone's current region setting and returns a default currency symbol.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String detectFromDevice() {
        return null;
    }
    
    public final boolean isSaudiRiyalSymbol(@org.jetbrains.annotations.NotNull()
    java.lang.String symbol) {
        return false;
    }
}