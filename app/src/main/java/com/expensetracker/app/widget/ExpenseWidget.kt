package com.expensetracker.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.expensetracker.app.MainActivity
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.SettingsRepository
import com.expensetracker.app.util.DateUtils
import kotlin.math.roundToInt

/**
 * Home-screen widget that shows today's spend, the monthly budget progress bar, and a
 * tap-to-open shortcut. Data is pushed via [WidgetUpdater.refresh] whenever expenses change.
 *
 * Layout: 2×1 cells minimum (180×110 dp).
 */
class ExpenseWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent() }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<Preferences>()
        val todaySpend   = (prefs[KEY_TODAY_SPEND]  ?: 0f).toDouble()
        val monthSpend   = (prefs[KEY_MONTH_SPEND]  ?: 0f).toDouble()
        val budget       = (prefs[KEY_BUDGET]        ?: 0f).toDouble()
        val currency     = prefs[KEY_CURRENCY]        ?: "$"

        val progress   = if (budget > 0) (monthSpend / budget).coerceIn(0.0, 1.0).toFloat() else 0f
        val budgetPct  = if (budget > 0) "${(progress * 100).roundToInt()}%" else "—"
        val overBudget = budget > 0 && monthSpend > budget

        val bgColor    = ColorProvider(Color(0xFF1B2A22))   // dark forest green, same as app theme
        val textPrime  = ColorProvider(Color.White)
        val textMuted  = ColorProvider(Color(0xFFAAAAAA))
        val progressOk = ColorProvider(Color(0xFF4CAF50))   // SuccessGreen
        val progressBad = ColorProvider(Color(0xFFEF5350))  // DangerRed

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .clickable(actionStartActivity<MainActivity>())
                .padding(12.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {

                // ── Header row: app name ─────────────────────────────────────
                Text(
                    text = "Baqaya",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFF59E0B)), // AccentAmber
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(GlanceModifier.height(6.dp))

                // ── Today's spend ────────────────────────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Today  ",
                        style = TextStyle(color = textMuted, fontSize = 12.sp)
                    )
                    Text(
                        text = formatMoney(todaySpend, currency),
                        style = TextStyle(
                            color = textPrime,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(GlanceModifier.height(8.dp))

                // ── Monthly budget bar ────────────────────────────────────────
                if (budget > 0) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = GlanceModifier.fillMaxWidth().height(4.dp),
                        color = if (overBudget) progressBad else progressOk,
                        backgroundColor = ColorProvider(Color.White.copy(alpha = 0.15f))
                    )
                    Spacer(GlanceModifier.height(4.dp))
                    Row {
                        Text(
                            text = "${formatMoney(monthSpend, currency)} / ${formatMoney(budget, currency)}",
                            style = TextStyle(color = textMuted, fontSize = 10.sp)
                        )
                        Spacer(GlanceModifier.width(4.dp))
                        Text(
                            text = "($budgetPct)",
                            style = TextStyle(
                                color = if (overBudget) progressBad else progressOk,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Text(
                        text = "This month  ${formatMoney(monthSpend, currency)}",
                        style = TextStyle(color = textMuted, fontSize = 11.sp)
                    )
                }
            }
        }
    }

    // ── Companion: state keys and helper ─────────────────────────────────────

    companion object {
        val KEY_TODAY_SPEND  = floatPreferencesKey("today_spend")
        val KEY_MONTH_SPEND  = floatPreferencesKey("month_spend")
        val KEY_BUDGET       = floatPreferencesKey("budget")
        val KEY_CURRENCY     = stringPreferencesKey("currency")

        private fun formatMoney(amount: Double, currencySymbol: String): String {
            return if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) {
                // Riyal glyph can't render in Glance; show SAR prefix instead.
                "SAR ${"%,.0f".format(amount)}"
            } else {
                "$currencySymbol${"%,.0f".format(amount)}"
            }
        }

        /**
         * Reads fresh data from Room + SettingsRepository and pushes it to all live widget
         * instances via Glance's state store. Call this whenever expenses or budgets change.
         */
        suspend fun refresh(context: Context) {
            val db       = AppDatabase.getInstance(context)
            val settings = SettingsRepository(context)
            val today    = DateUtils.todayIso()
            val month    = DateUtils.currentMonthKey()

            val todaySpend  = db.expenseDao().sumForDay(today)
            val monthSpend  = db.expenseDao().sumForMonth(month)
            val budget      = db.budgetDao().getAllOnce()
                .firstOrNull { it.categoryId == null }?.amount ?: 0.0
            val currency    = settings.currencySymbol

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(ExpenseWidget::class.java)
            for (glanceId in ids) {
                // Use the MutablePreferences overload — lambda returns Unit, not Preferences.
                updateAppWidgetState(context, glanceId) { mutablePrefs ->
                    mutablePrefs[KEY_TODAY_SPEND] = todaySpend.toFloat()
                    mutablePrefs[KEY_MONTH_SPEND] = monthSpend.toFloat()
                    mutablePrefs[KEY_BUDGET]      = budget.toFloat()
                    mutablePrefs[KEY_CURRENCY]    = currency
                }
                ExpenseWidget().update(context, glanceId)
            }
        }
    }
}
