package com.expensetracker.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AppWidget broadcast receiver that Glance uses to host [ExpenseWidget].
 *
 * The system delivers ACTION_APPWIDGET_UPDATE here; we override [onUpdate] to refresh the
 * widget's data from Room so freshly pinned widgets show real numbers immediately.
 */
class ExpenseWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = ExpenseWidget()

    /** Called by the system when widget instances are added or need an update. */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Refresh widget data from Room in the background.
        CoroutineScope(Dispatchers.IO).launch {
            ExpenseWidget.refresh(context)
        }
    }
}
