package com.expensetracker.app.util;

/**
 * Exports monthly expenses to a CSV file, saved to the app's private cache dir and
 * shared via FileProvider — no storage permission required.
 *
 * Format: Date, Category, Description, Amount
 * First row is a header. Values containing commas or quotes are RFC 4180 quoted.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J!\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0006\"\u00020\u0004H\u0002\u00a2\u0006\u0002\u0010\u0007JD\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u00042\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012J\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u0004\u00a8\u0006\u0018"}, d2 = {"Lcom/expensetracker/app/util/CsvExporter;", "", "()V", "csvRow", "", "values", "", "([Ljava/lang/String;)Ljava/lang/String;", "export", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "monthLabel", "colDate", "colCategory", "colDescription", "colAmount", "rows", "", "Lcom/expensetracker/app/util/ExportRow;", "shareOrSave", "", "uri", "chooserTitle", "app_debug"})
public final class CsvExporter {
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.CsvExporter INSTANCE = null;
    
    private CsvExporter() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.net.Uri export(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String monthLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String colDate, @org.jetbrains.annotations.NotNull()
    java.lang.String colCategory, @org.jetbrains.annotations.NotNull()
    java.lang.String colDescription, @org.jetbrains.annotations.NotNull()
    java.lang.String colAmount, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.util.ExportRow> rows) {
        return null;
    }
    
    public final void shareOrSave(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String chooserTitle) {
    }
    
    /**
     * RFC 4180: wrap in double-quotes if value contains comma, quote, or newline.
     */
    private final java.lang.String csvRow(java.lang.String... values) {
        return null;
    }
}