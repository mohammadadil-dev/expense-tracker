package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000P\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\t\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u001e\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0016\u0010\n\u001a\u00020\u00012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a(\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0003\u001a\u0015\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002\u00a2\u0006\u0002\u0010\u0014\u001a\u0010\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0013H\u0003\u001a\u0010\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\rH\u0003\u001a\u0015\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u001bH\u0002\u00a2\u0006\u0002\u0010\u001c\u001a$\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\u001f2\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00130!H\u0003\u00a8\u0006#"}, d2 = {"DashboardScreen", "", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onOpenSettings", "Lkotlin/Function0;", "ExportPdfChip", "enabled", "", "onClick", "ManageCategoriesButton", "PendingSmsBanner", "count", "", "modifier", "Landroidx/compose/ui/Modifier;", "colorFromHex", "Landroidx/compose/ui/graphics/Color;", "hex", "", "(Ljava/lang/String;)J", "greetingText", "name", "healthBandLabel", "score", "insightAccentColor", "kind", "Lcom/expensetracker/app/util/FinancialInsights$InsightKind;", "(Lcom/expensetracker/app/util/FinancialInsights$InsightKind;)J", "insightText", "insight", "Lcom/expensetracker/app/util/FinancialInsights$Insight;", "categoryNameById", "", "", "app_release"})
public final class DashboardScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardScreen(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.ExpenseViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings) {
    }
    
    /**
     * Tappable "Export PDF" pill next to the expenses list header. Replaces the old bare icon
     * button with something more interactive: a labeled chip that dips on press, and visibly
     * dims (and won't try to generate an empty report) when there's nothing for the current
     * month to export.
     */
    @androidx.compose.runtime.Composable()
    private static final void ExportPdfChip(boolean enabled, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * "Manage Categories" link next to the breakdown header. Pulled out into its own composable
     * (was inline) so it can carry a press-scale animation, and given maxLines/ellipsis so a long
     * translated label shrinks instead of wrapping character-by-character when the sibling title
     * Text claims most of the row's width.
     */
    @androidx.compose.runtime.Composable()
    private static final void ManageCategoriesButton(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * Banner shown above the expenses list whenever the SMS parser has queued one or more
     * transactions for review. Tapping it opens [SmsReviewSheet] — nothing here has been saved
     * as a real expense yet.
     */
    @androidx.compose.runtime.Composable()
    private static final void PendingSmsBanner(int count, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    private static final long colorFromHex(java.lang.String hex) {
        return 0L;
    }
    
    /**
     * Time-of-day greeting for the Command Center header — "Good morning" / "Good afternoon" /
     * "Good evening", personalized with [name] when the user has set one in Settings, falling back
     * to a name-less phrasing when they haven't.
     */
    @androidx.compose.runtime.Composable()
    private static final java.lang.String greetingText(java.lang.String name) {
        return null;
    }
    
    /**
     * Localized band label shown next to the Financial Health ring.
     */
    @androidx.compose.runtime.Composable()
    private static final java.lang.String healthBandLabel(int score) {
        return null;
    }
    
    /**
     * Maps an insight's positive/warning/neutral kind to the accent color its feed card is drawn in.
     */
    private static final long insightAccentColor(com.expensetracker.app.util.FinancialInsights.InsightKind kind) {
        return 0L;
    }
    
    /**
     * Resolves one [FinancialInsights.Insight] to its localized sentence. [categoryNameById] resolves
     * [FinancialInsights.Insight.categoryId] to a display name, which (when present) is always the
     * first format argument, followed by the engine's own already-formatted [FinancialInsights.Insight.args].
     */
    @androidx.compose.runtime.Composable()
    private static final java.lang.String insightText(com.expensetracker.app.util.FinancialInsights.Insight insight, java.util.Map<java.lang.Long, java.lang.String> categoryNameById) {
        return null;
    }
}