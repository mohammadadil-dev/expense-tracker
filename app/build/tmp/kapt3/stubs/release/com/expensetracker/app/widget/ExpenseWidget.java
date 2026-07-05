package com.expensetracker.app.widget;

/**
 * Home-screen widget that shows today's spend, the monthly budget progress bar, and a
 * tap-to-open shortcut. Data is pushed via [WidgetUpdater.refresh] whenever expenses change.
 *
 * Layout: 2×1 cells minimum (180×110 dp).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0003J\u001e\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eR\u0018\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/expensetracker/app/widget/ExpenseWidget;", "Landroidx/glance/appwidget/GlanceAppWidget;", "()V", "stateDefinition", "Landroidx/glance/state/GlanceStateDefinition;", "getStateDefinition", "()Landroidx/glance/state/GlanceStateDefinition;", "WidgetContent", "", "provideGlance", "context", "Landroid/content/Context;", "id", "Landroidx/glance/GlanceId;", "(Landroid/content/Context;Landroidx/glance/GlanceId;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_release"})
public final class ExpenseWidget extends androidx.glance.appwidget.GlanceAppWidget {
    @org.jetbrains.annotations.NotNull()
    private final androidx.glance.state.GlanceStateDefinition<?> stateDefinition = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Float> KEY_TODAY_SPEND = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Float> KEY_MONTH_SPEND = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Float> KEY_BUDGET = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> KEY_CURRENCY = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.expensetracker.app.widget.ExpenseWidget.Companion Companion = null;
    
    public ExpenseWidget() {
        super(0);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.glance.state.GlanceStateDefinition<?> getStateDefinition() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object provideGlance(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.glance.GlanceId id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private final void WidgetContent() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\tH\u0002J\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0007R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0007R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0007\u00a8\u0006\u0018"}, d2 = {"Lcom/expensetracker/app/widget/ExpenseWidget$Companion;", "", "()V", "KEY_BUDGET", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getKEY_BUDGET", "()Landroidx/datastore/preferences/core/Preferences$Key;", "KEY_CURRENCY", "", "getKEY_CURRENCY", "KEY_MONTH_SPEND", "getKEY_MONTH_SPEND", "KEY_TODAY_SPEND", "getKEY_TODAY_SPEND", "formatMoney", "amount", "", "currencySymbol", "refresh", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Float> getKEY_TODAY_SPEND() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Float> getKEY_MONTH_SPEND() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Float> getKEY_BUDGET() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getKEY_CURRENCY() {
            return null;
        }
        
        private final java.lang.String formatMoney(double amount, java.lang.String currencySymbol) {
            return null;
        }
        
        /**
         * Reads fresh data from Room + SettingsRepository and pushes it to all live widget
         * instances via Glance's state store. Call this whenever expenses or budgets change.
         */
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Object refresh(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}