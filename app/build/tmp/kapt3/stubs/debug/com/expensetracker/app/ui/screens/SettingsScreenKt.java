package com.expensetracker.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001a+\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0011\u0010\t\u001a\r\u0012\u0004\u0012\u00020\u00040\n\u00a2\u0006\u0002\b\u000bH\u0003\u001a\u001e\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\nH\u0003\u001a&\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000e2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\nH\u0003\u001a\b\u0010\u0015\u001a\u00020\u0004H\u0003\u001a\u001e\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\nH\u0007\u001a\u0018\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u000eH\u0003\u001a\u001a\u0010\u001c\u001a\u00020\u001d*\u00020\u001d2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\nH\u0002\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"allCurrencies", "", "Lcom/expensetracker/app/ui/screens/CurrencyOption;", "AnimatedSection", "", "visible", "", "delayMillis", "", "content", "Lkotlin/Function0;", "Landroidx/compose/runtime/Composable;", "CustomerIdRow", "customerId", "", "onCopy", "SettingsActionRow", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "onClick", "SettingsFooter", "SettingsScreen", "viewModel", "Lcom/expensetracker/app/viewmodel/ExpenseViewModel;", "onBack", "SettingsSectionHeader", "title", "clickableNoRipple", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class SettingsScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.expensetracker.app.ui.screens.CurrencyOption> allCurrencies = null;
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull()
    com.expensetracker.app.viewmodel.ExpenseViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    /**
     * Wraps a settings block in a gentle staggered fade + slide-up entrance.
     */
    @androidx.compose.runtime.Composable()
    private static final void AnimatedSection(boolean visible, int delayMillis, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
    
    /**
     * Small icon badge + title used to head up each settings block.
     */
    @androidx.compose.runtime.Composable()
    private static final void SettingsSectionHeader(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title) {
    }
    
    private static final androidx.compose.ui.Modifier clickableNoRipple(androidx.compose.ui.Modifier $this$clickableNoRipple, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
        return null;
    }
    
    /**
     * Customer's locally-generated, stable ID — shown here and stamped into every PDF export.
     */
    @androidx.compose.runtime.Composable()
    private static final void CustomerIdRow(java.lang.String customerId, kotlin.jvm.functions.Function0<kotlin.Unit> onCopy) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsActionRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * Bottom-of-screen brand footer — small, muted, out of the way.
     */
    @androidx.compose.runtime.Composable()
    private static final void SettingsFooter() {
    }
}