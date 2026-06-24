package com.expensetracker.app.util;

/**
 * Renders a simple monthly expense report straight to PDF using the platform's own
 * [android.graphics.pdf.PdfDocument] — no third-party library, no network call, no
 * recurring cost. The finished file is written to the app's private cache dir and handed
 * out via [androidx.core.content.FileProvider] so it can be saved/shared through the
 * system chooser without any storage permission.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J0\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J|\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020 2\u0006\u0010#\u001a\u00020 2\u0006\u0010$\u001a\u00020 2\u0006\u0010%\u001a\u00020 2\u0006\u0010&\u001a\u00020 2\u0006\u0010\'\u001a\u00020 2\u0006\u0010(\u001a\u00020 2\u0006\u0010)\u001a\u00020 2\u0006\u0010*\u001a\u00020 2\u0006\u0010+\u001a\u00020 2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020.0-J\u001e\u0010/\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u00100\u001a\u00020\u001c2\u0006\u00101\u001a\u00020 R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u001b\u0010\f\u001a\u00020\r8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u000e\u0010\u000f\u00a8\u00062"}, d2 = {"Lcom/expensetracker/app/util/PdfExporter;", "", "()V", "COL_AMOUNT_RIGHT_X", "", "COL_CATEGORY_X", "COL_DATE_X", "COL_DESCRIPTION_X", "MARGIN", "PAGE_HEIGHT", "PAGE_WIDTH", "ROW_HEIGHT", "riyalPath", "Landroid/graphics/Path;", "getRiyalPath", "()Landroid/graphics/Path;", "riyalPath$delegate", "Lkotlin/Lazy;", "drawRiyalIcon", "", "c", "Landroid/graphics/Canvas;", "x", "baselineY", "iconHeight", "paint", "Landroid/graphics/Paint;", "export", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "appName", "", "reportTitle", "monthLabel", "customerIdLabel", "colDate", "colCategory", "colDescription", "colAmount", "totalLabel", "totalValue", "footerText", "emptyLabel", "rows", "", "Lcom/expensetracker/app/util/ExportRow;", "shareOrSave", "uri", "chooserTitle", "app_debug"})
public final class PdfExporter {
    private static final float PAGE_WIDTH = 595.0F;
    private static final float PAGE_HEIGHT = 842.0F;
    private static final float MARGIN = 36.0F;
    private static final float ROW_HEIGHT = 20.0F;
    private static final float COL_DATE_X = 36.0F;
    private static final float COL_CATEGORY_X = 116.0F;
    private static final float COL_DESCRIPTION_X = 246.0F;
    private static final float COL_AMOUNT_RIGHT_X = 559.0F;
    
    /**
     * Same traced outline as `res/drawable/ic_saudi_riyal.xml` (685x765 viewport), reproduced
     * here as a plain [Path] since this exporter draws straight to [Canvas] with no Compose/
     * vector-drawable inflation available. Built once and reused for every export.
     */
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy riyalPath$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.util.PdfExporter INSTANCE = null;
    
    private PdfExporter() {
        super();
    }
    
    /**
     * Same traced outline as `res/drawable/ic_saudi_riyal.xml` (685x765 viewport), reproduced
     * here as a plain [Path] since this exporter draws straight to [Canvas] with no Compose/
     * vector-drawable inflation available. Built once and reused for every export.
     */
    private final android.graphics.Path getRiyalPath() {
        return null;
    }
    
    /**
     * Draws the riyal glyph so its bottom rests on [baselineY], like a capital letter.
     */
    private final void drawRiyalIcon(android.graphics.Canvas c, float x, float baselineY, float iconHeight, android.graphics.Paint paint) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.net.Uri export(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String appName, @org.jetbrains.annotations.NotNull()
    java.lang.String reportTitle, @org.jetbrains.annotations.NotNull()
    java.lang.String monthLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String customerIdLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String colDate, @org.jetbrains.annotations.NotNull()
    java.lang.String colCategory, @org.jetbrains.annotations.NotNull()
    java.lang.String colDescription, @org.jetbrains.annotations.NotNull()
    java.lang.String colAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String totalLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String totalValue, @org.jetbrains.annotations.NotNull()
    java.lang.String footerText, @org.jetbrains.annotations.NotNull()
    java.lang.String emptyLabel, @org.jetbrains.annotations.NotNull()
    java.util.List<com.expensetracker.app.util.ExportRow> rows) {
        return null;
    }
    
    /**
     * Opens the system chooser so the user can save the report (Files/Drive/etc.) or share it.
     */
    public final void shareOrSave(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String chooserTitle) {
    }
}