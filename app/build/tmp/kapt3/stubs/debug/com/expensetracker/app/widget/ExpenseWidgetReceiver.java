package com.expensetracker.app.widget;

/**
 * AppWidget broadcast receiver that Glance uses to host [ExpenseWidget].
 *
 * The system delivers ACTION_APPWIDGET_UPDATE here; we override [onUpdate] to refresh the
 * widget's data from Room so freshly pinned widgets show real numbers immediately.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J \u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/expensetracker/app/widget/ExpenseWidgetReceiver;", "Landroidx/glance/appwidget/GlanceAppWidgetReceiver;", "()V", "glanceAppWidget", "Landroidx/glance/appwidget/GlanceAppWidget;", "getGlanceAppWidget", "()Landroidx/glance/appwidget/GlanceAppWidget;", "onUpdate", "", "context", "Landroid/content/Context;", "appWidgetManager", "Landroid/appwidget/AppWidgetManager;", "appWidgetIds", "", "app_debug"})
public final class ExpenseWidgetReceiver extends androidx.glance.appwidget.GlanceAppWidgetReceiver {
    @org.jetbrains.annotations.NotNull()
    private final androidx.glance.appwidget.GlanceAppWidget glanceAppWidget = null;
    
    public ExpenseWidgetReceiver() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.glance.appwidget.GlanceAppWidget getGlanceAppWidget() {
        return null;
    }
    
    /**
     * Called by the system when widget instances are added or need an update.
     */
    @java.lang.Override()
    public void onUpdate(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.appwidget.AppWidgetManager appWidgetManager, @org.jetbrains.annotations.NotNull()
    int[] appWidgetIds) {
    }
}