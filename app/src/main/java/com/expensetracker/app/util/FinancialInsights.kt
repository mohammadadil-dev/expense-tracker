package com.expensetracker.app.util

import com.expensetracker.app.data.BudgetEntity
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * On-device "AI" layer for the Home dashboard.
 *
 * Everything here is plain arithmetic over the expenses/budgets already loaded in memory —
 * no network call, no LLM, nothing leaves the device. The "AI insight" framing in the UI just
 * means these read like a short human note instead of a raw number; the computation itself is
 * deterministic rules + statistics over [ExpenseEntity]/[BudgetEntity] rows.
 *
 * This file stays Compose-free on purpose so it can return raw, structured results — category
 * names, formatted money strings, etc. are resolved by the caller (which already has the
 * @Composable categoryDisplayName()/Formatters helpers) rather than baked in here.
 */
object FinancialInsights {

    enum class InsightKind { POSITIVE, WARNING, NEUTRAL }

    enum class InsightTemplate {
        TREND_DOWN,
        TREND_UP,
        TREND_FLAT,
        CATEGORY_INCREASE,
        CATEGORY_DECREASE,
        PROJECTED_SAVINGS,
        PROJECTED_OVERSPEND,
        STRONGEST_DAY,
        SUBSCRIPTION_LOAD,
        DAILY_AVERAGE,
        NOT_ENOUGH_DATA
    }

    /**
     * One feed item. [categoryId] is set only for category-specific templates so the caller can
     * resolve the localized display name itself; [args] holds every *other* already-formatted
     * placeholder (percentages, money strings, weekday names) in template order, inserted after
     * the resolved category name if there is one.
     */
    data class Insight(
        val template: InsightTemplate,
        val kind: InsightKind,
        val emoji: String,
        val categoryId: Long? = null,
        val args: List<String> = emptyList()
    )

    data class HealthScore(
        val total: Int,
        val budgetScore: Int,
        val trendScore: Int,
        val consistencyScore: Int,
        val subscriptionScore: Int
    )

    data class SavingsGoal(
        val targetAmount: Double,
        val projectedSavings: Double,
        val progress: Float,
        val hasEnoughData: Boolean
    )

    data class DashboardIntelligence(
        val healthScore: HealthScore,
        val insights: List<Insight>,
        val monthlySpend: Double,
        val subscriptionSpend: Double,
        val overallBudget: Double?,
        val budgetRemaining: Double?,
        val savingsGoal: SavingsGoal
    )

    private const val SUBSCRIPTION_NAME_KEY = "cat_subscriptions"

    private fun parseDateOrNull(iso: String): LocalDate? =
        try { LocalDate.parse(iso) } catch (e: Exception) { null }

    private fun sumInRange(expenses: List<ExpenseEntity>, from: LocalDate, to: LocalDate): Double =
        expenses.sumOf { exp ->
            val d = parseDateOrNull(exp.date)
            if (d != null && !d.isBefore(from) && !d.isAfter(to)) exp.amount else 0.0
        }

    private fun pct(value: Double): String = "${abs(value).roundToInt()}%"

    /**
     * Single entry point: computes everything the Home dashboard needs in one pass over the
     * already-loaded expense/category/budget lists. Callers should wrap this in `remember(...)`
     * keyed on the input lists so it only re-runs when the underlying data actually changes.
     */
    fun compute(
        monthExpenses: List<ExpenseEntity>,
        allExpenses: List<ExpenseEntity>,
        categories: List<CategoryEntity>,
        budgets: List<BudgetEntity>,
        currentMonthKey: String,
        currencySymbol: String,
        locale: Locale,
        today: LocalDate = LocalDate.now()
    ): DashboardIntelligence {
        val overallBudget = budgets.firstOrNull { it.categoryId == null }?.amount
        val monthlySpend = monthExpenses.sumOf { it.amount }
        val subscriptionCategoryIds = categories.filter { it.nameKey == SUBSCRIPTION_NAME_KEY }.map { it.id }.toSet()
        val subscriptionSpend = monthExpenses.filter { it.categoryId in subscriptionCategoryIds }.sumOf { it.amount }

        val daysInMonthCount = DateUtils.daysInMonth(currentMonthKey)
        val daysElapsed = DateUtils.daysElapsedInMonth(currentMonthKey).coerceAtLeast(1)
        val dailyAverage = monthlySpend / daysElapsed
        val projectedTotal = dailyAverage * daysInMonthCount

        val previousMonthKey = DateUtils.previousMonthKey(currentMonthKey)
        val lastMonthSpend = allExpenses.filter { it.monthKey == previousMonthKey }.sumOf { it.amount }

        // Week-over-week trend: last 7 days vs the 7 days before that.
        val last7 = sumInRange(allExpenses, today.minusDays(6), today)
        val prev7 = sumInRange(allExpenses, today.minusDays(13), today.minusDays(7))

        val insights = mutableListOf<Insight>()

        // 1. Trend insight.
        if (prev7 > 0.0) {
            val changeRatio = (last7 - prev7) / prev7
            when {
                changeRatio <= -0.05 -> insights += Insight(
                    InsightTemplate.TREND_DOWN, InsightKind.POSITIVE, "📉",
                    args = listOf(pct(changeRatio * 100))
                )
                changeRatio >= 0.05 -> insights += Insight(
                    InsightTemplate.TREND_UP, InsightKind.WARNING, "📈",
                    args = listOf(pct(changeRatio * 100))
                )
                else -> insights += Insight(InsightTemplate.TREND_FLAT, InsightKind.NEUTRAL, "➖")
            }
        }

        // 2. Biggest category move vs last month (only if it's a meaningful swing).
        val thisMonthByCategory = monthExpenses.groupBy { it.categoryId }.mapValues { it.value.sumOf { e -> e.amount } }
        val lastMonthByCategory = allExpenses.filter { it.monthKey == previousMonthKey }
            .groupBy { it.categoryId }.mapValues { it.value.sumOf { e -> e.amount } }
        var biggestMoveCategoryId: Long? = null
        var biggestMoveRatio = 0.0
        for ((catId, thisAmt) in thisMonthByCategory) {
            val lastAmt = lastMonthByCategory[catId] ?: 0.0
            if (lastAmt < 1.0) continue // no meaningful baseline to compare against
            val ratio = (thisAmt - lastAmt) / lastAmt
            if (abs(ratio) > abs(biggestMoveRatio)) {
                biggestMoveRatio = ratio
                biggestMoveCategoryId = catId
            }
        }
        if (biggestMoveCategoryId != null && abs(biggestMoveRatio) >= 0.15) {
            insights += if (biggestMoveRatio > 0) {
                Insight(
                    InsightTemplate.CATEGORY_INCREASE, InsightKind.WARNING, "⚠️",
                    categoryId = biggestMoveCategoryId, args = listOf(pct(biggestMoveRatio * 100))
                )
            } else {
                Insight(
                    InsightTemplate.CATEGORY_DECREASE, InsightKind.POSITIVE, "✅",
                    categoryId = biggestMoveCategoryId, args = listOf(pct(biggestMoveRatio * 100))
                )
            }
        }

        // 3. Forward-looking savings projection vs an auto-suggested target.
        // Requires at least 7 days of data — projecting from 1–6 days amplifies noise badly
        // (e.g. one big purchase on day 1 inflates the whole-month projection wildly).
        val savingsGoal = computeSavingsGoal(overallBudget, lastMonthSpend, projectedTotal)
        if (savingsGoal.hasEnoughData && daysElapsed >= 7) {
            insights += if (savingsGoal.projectedSavings > 1.0) {
                Insight(
                    InsightTemplate.PROJECTED_SAVINGS, InsightKind.POSITIVE, "💰",
                    args = listOf(Formatters.money(savingsGoal.projectedSavings, currencySymbol))
                )
            } else {
                val baseline = if (overallBudget != null && overallBudget > 0) overallBudget else lastMonthSpend
                val overBy = projectedTotal - baseline
                if (overBy > 1.0) {
                    Insight(
                        InsightTemplate.PROJECTED_OVERSPEND, InsightKind.WARNING, "🚨",
                        args = listOf(Formatters.money(overBy, currencySymbol))
                    )
                } else {
                    Insight(InsightTemplate.DAILY_AVERAGE, InsightKind.NEUTRAL, "📊",
                        args = listOf(Formatters.money(dailyAverage, currencySymbol)))
                }
            }
        } else if (monthExpenses.isNotEmpty()) {
            // Too early in the month to project reliably — just show the daily average.
            insights += Insight(
                InsightTemplate.DAILY_AVERAGE, InsightKind.NEUTRAL, "📊",
                args = listOf(Formatters.money(dailyAverage, currencySymbol))
            )
        }

        // 4. Strongest (lowest-average-spend) day of week, over the last 4 weeks of history.
        strongestSavingDay(allExpenses, today, locale)?.let { dayLabel ->
            insights += Insight(InsightTemplate.STRONGEST_DAY, InsightKind.POSITIVE, "🌟", args = listOf(dayLabel))
        }

        // 5. Subscription load, only worth flagging when it's a real share of the month.
        if (monthlySpend > 0.0) {
            val share = subscriptionSpend / monthlySpend
            if (share >= 0.15) {
                insights += Insight(InsightTemplate.SUBSCRIPTION_LOAD, InsightKind.WARNING, "🔁", args = listOf(pct(share * 100)))
            }
        }

        // Always have at least one card — a generic daily-average note for brand-new users
        // who don't have enough history yet for the smarter insights above.
        if (insights.isEmpty()) {
            insights += if (monthExpenses.isEmpty()) {
                Insight(InsightTemplate.NOT_ENOUGH_DATA, InsightKind.NEUTRAL, "✨")
            } else {
                Insight(InsightTemplate.DAILY_AVERAGE, InsightKind.NEUTRAL, "📊",
                    args = listOf(Formatters.money(dailyAverage, currencySymbol)))
            }
        }

        val healthScore = computeHealthScore(
            monthlySpend = monthlySpend,
            overallBudget = overallBudget,
            last7 = last7,
            prev7 = prev7,
            allExpenses = allExpenses,
            today = today,
            subscriptionSpend = subscriptionSpend
        )

        return DashboardIntelligence(
            healthScore = healthScore,
            insights = insights,
            monthlySpend = monthlySpend,
            subscriptionSpend = subscriptionSpend,
            overallBudget = overallBudget,
            budgetRemaining = overallBudget?.let { it - monthlySpend },
            savingsGoal = savingsGoal
        )
    }

    private fun computeSavingsGoal(
        overallBudget: Double?,
        lastMonthSpend: Double,
        projectedTotal: Double
    ): SavingsGoal {
        val baseline = when {
            overallBudget != null && overallBudget > 0 -> overallBudget
            lastMonthSpend > 0 -> lastMonthSpend
            else -> return SavingsGoal(0.0, 0.0, 0f, hasEnoughData = false)
        }
        val target = baseline * 0.10
        val projectedSavings = (baseline - projectedTotal).coerceAtLeast(0.0)
        val progress = if (target > 0) (projectedSavings / target).toFloat().coerceIn(0f, 1f) else 0f
        return SavingsGoal(target, projectedSavings, progress, hasEnoughData = true)
    }

    private fun computeHealthScore(
        monthlySpend: Double,
        overallBudget: Double?,
        last7: Double,
        prev7: Double,
        allExpenses: List<ExpenseEntity>,
        today: LocalDate,
        subscriptionSpend: Double
    ): HealthScore {
        val budgetScore = when {
            overallBudget == null || overallBudget <= 0 -> 30
            monthlySpend <= overallBudget * 0.7 -> 40
            monthlySpend <= overallBudget -> 30
            monthlySpend <= overallBudget * 1.15 -> 15
            else -> 5
        }

        val trendScore = when {
            prev7 <= 0.0 -> 18
            last7 <= prev7 * 0.85 -> 25
            last7 <= prev7 -> 20
            last7 <= prev7 * 1.15 -> 12
            else -> 5
        }

        val datesWithLogs = allExpenses.mapNotNull { parseDateOrNull(it.date) }.toSet()
        val daysWithLogs = (0..13).count { offset -> today.minusDays(offset.toLong()) in datesWithLogs }
        val consistencyScore = ((daysWithLogs / 14.0) * 20).roundToInt().coerceIn(0, 20)

        val subscriptionShare = if (monthlySpend > 0) subscriptionSpend / monthlySpend else 0.0
        val subscriptionScore = when {
            subscriptionShare <= 0.05 -> 15
            subscriptionShare <= 0.15 -> 11
            subscriptionShare <= 0.30 -> 6
            else -> 2
        }

        val total = (budgetScore + trendScore + consistencyScore + subscriptionScore).coerceIn(0, 100)
        return HealthScore(total, budgetScore, trendScore, consistencyScore, subscriptionScore)
    }

    /**
     * Looks at the last 4 weeks of dated expenses and finds the day of week with the lowest
     * average spend — framed to the user as their "strongest saving day". Returns null when
     * there isn't enough spread of data (fewer than 2 distinct weekdays represented) to make
     * the comparison meaningful rather than noise.
     */
    private fun strongestSavingDay(allExpenses: List<ExpenseEntity>, today: LocalDate, locale: Locale): String? {
        val windowStart = today.minusDays(27)
        val dated = allExpenses.mapNotNull { exp ->
            val d = parseDateOrNull(exp.date) ?: return@mapNotNull null
            if (d.isBefore(windowStart) || d.isAfter(today)) null else d to exp.amount
        }
        if (dated.isEmpty()) return null

        val totalsByDow = DayOfWeek.entries.associateWith { 0.0 }.toMutableMap()
        val occurrencesByDow = DayOfWeek.entries.associateWith { 0 }.toMutableMap()
        var cursor = windowStart
        while (!cursor.isAfter(today)) {
            occurrencesByDow[cursor.dayOfWeek] = (occurrencesByDow[cursor.dayOfWeek] ?: 0) + 1
            cursor = cursor.plusDays(1)
        }
        for ((date, amount) in dated) {
            totalsByDow[date.dayOfWeek] = (totalsByDow[date.dayOfWeek] ?: 0.0) + amount
        }

        val averages = DayOfWeek.entries.mapNotNull { dow ->
            val occurrences = occurrencesByDow[dow] ?: 0
            if (occurrences == 0) null else dow to (totalsByDow[dow] ?: 0.0) / occurrences
        }
        if (averages.size < 2) return null

        val strongest = averages.minByOrNull { it.second } ?: return null
        return strongest.first.getDisplayName(TextStyle.FULL, locale)
    }
}
