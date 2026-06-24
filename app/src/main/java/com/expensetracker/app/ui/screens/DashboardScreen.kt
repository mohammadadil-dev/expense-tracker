package com.expensetracker.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.data.PendingSmsExpense
import com.expensetracker.app.ui.components.AddEditExpenseSheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.BackgroundScrollSignal
import com.expensetracker.app.ui.components.BentoCard
import com.expensetracker.app.ui.components.BudgetManageSheet
import com.expensetracker.app.ui.components.CategoryManageSheet
import com.expensetracker.app.ui.components.CategorySlice
import com.expensetracker.app.ui.components.DonutChart
import com.expensetracker.app.ui.components.ExpenseGridCard
import com.expensetracker.app.ui.components.ExpensePrefill
import com.expensetracker.app.ui.components.ExpenseRowCard
import com.expensetracker.app.ui.components.ExpenseViewMode
import com.expensetracker.app.ui.components.ExpenseViewModeToggle
import com.expensetracker.app.ui.components.FinancialHealthRing
import com.expensetracker.app.ui.components.HealthSubScore
import com.expensetracker.app.ui.components.InsightFeedCard
import com.expensetracker.app.ui.components.MonthCalendarView
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.SmsReviewSheet
import com.expensetracker.app.ui.components.TrendBarChart
import com.expensetracker.app.ui.components.TrendPoint
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonViolet
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.WarningAmber
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.ExportRow
import com.expensetracker.app.util.FinancialInsights
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.PdfExporter
import com.expensetracker.app.util.categoryDisplayName
import com.expensetracker.app.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Locale
import kotlin.math.abs

@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onOpenSettings: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val monthExpenses by viewModel.monthExpenses.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val currentMonthKey by viewModel.currentMonthKey.collectAsState()
    val pendingSmsExpenses by viewModel.pendingSmsExpenses.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val locale: Locale = LocalConfiguration.current.locales[0]

    val context = LocalContext.current
    var showAddSheet by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var expensePendingDelete by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showSmsReviewSheet by remember { mutableStateOf(false) }
    var smsItemBeingAccepted by remember { mutableStateOf<PendingSmsExpense?>(null) }
    var showBudgetSheet by remember { mutableStateOf(false) }
    // Tracks which way the user just navigated so the month label slides the right direction.
    var monthSlideDirection by remember { mutableStateOf(1) }
    var viewMode by remember { mutableStateOf(ExpenseViewMode.DAY) }
    var selectedCalendarDay by remember(currentMonthKey) { mutableStateOf<String?>(null) }
    // Drives the Bento Grid / AI Insights Feed entrance stagger — flips true once, right after
    // first composition, the same "fade everything in a beat after landing" pattern Settings uses.
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    val categoryById = remember(categories) { categories.associateBy { it.id } }

    val categoryTotals = remember(monthExpenses) {
        monthExpenses.groupBy { it.categoryId }.mapValues { (_, list) -> list.sumOf { it.amount } }
    }
    val totalThisMonth = remember(monthExpenses) { monthExpenses.sumOf { it.amount } }

    // The single overall budget target (categoryId == null) — still needed by BudgetManageSheet
    // below even though the old standalone BudgetOverviewCard display is gone, folded into the
    // Budget Remaining bento tile above.
    val overallBudget = remember(budgets) { budgets.find { it.categoryId == null } }

    val previousKey = remember(currentMonthKey) { DateUtils.previousMonthKey(currentMonthKey) }
    val previousTotal = remember(allExpenses, previousKey) {
        allExpenses.filter { it.monthKey == previousKey }.sumOf { it.amount }
    }
    // The Home "Financial Command Center" header's health ring + AI insight feed — one pass of
    // on-device rules/statistics over the data already loaded above. No network call, no LLM.
    val intelligence = remember(monthExpenses, allExpenses, categories, budgets, currentMonthKey, currencySymbol, locale) {
        FinancialInsights.compute(
            monthExpenses = monthExpenses,
            allExpenses = allExpenses,
            categories = categories,
            budgets = budgets,
            currentMonthKey = currentMonthKey,
            currencySymbol = currencySymbol,
            locale = locale
        )
    }
    // Plain (non-remember) derived value — cheap enough to recompute every recomposition, and
    // only feeds the Subscription Cost bento tile's subtitle below.
    val subscriptionSharePct: String? = if (intelligence.monthlySpend > 0) {
        String.format(Locale.US, "%.0f%%", (intelligence.subscriptionSpend / intelligence.monthlySpend) * 100)
    } else null

    val trendPoints = remember(allExpenses, currentMonthKey, locale) {
        val keys = DateUtils.last12MonthKeysEndingAt(currentMonthKey)
        val totalsByMonth = allExpenses.groupBy { it.monthKey }.mapValues { (_, list) -> list.sumOf { it.amount } }
        keys.map { key ->
            TrendPoint(
                label = DateUtils.shortMonthLabel(key, locale),
                value = totalsByMonth[key] ?: 0.0,
                highlighted = key == currentMonthKey
            )
        }
    }

    // Groups this month's expenses by exact day (newest day first) so the list can show
    // "Today" / "Yesterday" / date headers with a per-day subtotal instead of one long flat
    // list — order within a day follows monthExpenses' own existing order.
    val expensesByDay = remember(monthExpenses) {
        monthExpenses.groupBy { it.date }.toList().sortedByDescending { it.first }
    }
    val todayIso = remember { DateUtils.todayIso() }
    val yesterdayIso = remember { DateUtils.yesterdayIso() }
    val todayLabel = stringResource(R.string.today)
    val yesterdayLabel = stringResource(R.string.yesterday)

    // Same expenses, grouped by category instead of by day — backs the "By Category" layout.
    val expensesByCategory = remember(monthExpenses) {
        monthExpenses.groupBy { it.categoryId }
            .toList()
            .sortedByDescending { (_, list) -> list.sumOf { it.amount } }
    }
    // Backs the Calendar layout: which days have spending, and how much.
    val dailyTotals = remember(monthExpenses) {
        monthExpenses.groupBy { it.date }.mapValues { (_, list) -> list.sumOf { it.amount } }
    }
    val firstWeekdayIndex = remember(currentMonthKey) { DateUtils.firstWeekdayIndexOfMonth(currentMonthKey) }
    val daysInMonthCount = remember(currentMonthKey) { DateUtils.daysInMonth(currentMonthKey) }
    val weekdayLabels = remember(locale) { DateUtils.weekdayShortLabels(locale) }
    val selectedDayExpenses = remember(selectedCalendarDay, monthExpenses) {
        selectedCalendarDay?.let { day -> monthExpenses.filter { it.date == day } } ?: emptyList()
    }
    // Backs the Grid layout — two expenses per row, newest first.
    val gridRows = remember(monthExpenses) {
        monthExpenses.sortedByDescending { it.date }.chunked(2)
    }

    // PDF export — every label is resolved here (composable scope) so the actual export
    // function below stays a plain, non-composable click handler.
    val exportReportTitle = stringResource(R.string.export_report_title)
    val exportTotalLabel = stringResource(R.string.export_total_label)
    val exportColDate = stringResource(R.string.date_label)
    val exportColCategory = stringResource(R.string.category_label)
    val exportColDescription = stringResource(R.string.description_label)
    val exportColAmount = stringResource(R.string.amount_label)
    val poweredByFooter = stringResource(R.string.powered_by_footer)
    val exportChooserTitle = stringResource(R.string.export_pdf_chooser_title)
    val exportStartedLabel = stringResource(R.string.export_started)
    val appNameStr = stringResource(R.string.app_name)
    val noExpensesLabel = stringResource(R.string.no_expenses_this_month)
    val exportCustomerIdLabel = stringResource(R.string.export_customer_id_label, viewModel.customerId)
    val monthLabelForExport = DateUtils.monthLabel(currentMonthKey, locale)
    // categoryDisplayName() is itself @Composable, so it can't be called inside remember{}'s
    // calculation lambda (Compose disallows composable calls there). Resolve every category's
    // display name here, in plain composable scope, before exportRows' remember block needs it.
    val categoryNameById = categories.associate { it.id to categoryDisplayName(it) }
    val exportRows = remember(monthExpenses, categoryNameById, locale, currencySymbol) {
        monthExpenses.sortedByDescending { it.date }.map { e ->
            ExportRow(
                dateLabel = DateUtils.formatExpenseDate(e.date, locale),
                categoryLabel = categoryNameById[e.categoryId] ?: "",
                description = e.description,
                amountLabel = Formatters.money(e.amount, currencySymbol)
            )
        }
    }

    fun exportMonthAsPdf() {
        // Nothing to put in a report — don't generate an empty PDF, just tell the user why.
        if (exportRows.isEmpty()) {
            Toast.makeText(context, noExpensesLabel, Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, exportStartedLabel, Toast.LENGTH_SHORT).show()
        val uri = PdfExporter.export(
            context = context,
            appName = appNameStr,
            reportTitle = exportReportTitle,
            monthLabel = monthLabelForExport,
            customerIdLabel = exportCustomerIdLabel,
            colDate = exportColDate,
            colCategory = exportColCategory,
            colDescription = exportColDescription,
            colAmount = exportColAmount,
            totalLabel = exportTotalLabel,
            totalValue = Formatters.money(totalThisMonth, currencySymbol),
            footerText = poweredByFooter,
            emptyLabel = noExpensesLabel,
            rows = exportRows
        )
        PdfExporter.shareOrSave(context, uri, exportChooserTitle)
    }

    val legendEntries = remember(categoryTotals, categories, totalThisMonth) {
        categories.mapNotNull { cat ->
            val amt = categoryTotals[cat.id]
            if (amt != null && amt > 0.0) Triple(cat, amt, if (totalThisMonth > 0) amt / totalThisMonth * 100 else 0.0) else null
        }.sortedByDescending { it.second }
    }
    val slices = legendEntries.map { (cat, amt, _) -> CategorySlice(colorFromHex(cat.colorHex), amt) }

    // "vs last month" comparison — computed directly in the composable body (not inside
    // remember) since it needs to call stringResource().
    val deltaSubLabel: String
    val deltaColor: Color
    when {
        previousTotal <= 0.0 && totalThisMonth <= 0.0 -> {
            deltaSubLabel = stringResource(R.string.stat_no_prior_data)
            deltaColor = TextMuted
        }
        previousTotal <= 0.0 -> {
            // Unicode triangles (not directional arrow icons) on purpose — they read the same
            // in both LTR and RTL layouts, so no mirroring logic is needed for Arabic.
            deltaSubLabel = "▲ " + stringResource(R.string.stat_up_from_zero, Formatters.money(totalThisMonth, currencySymbol))
            deltaColor = DangerRed
        }
        else -> {
            val percent = ((totalThisMonth - previousTotal) / previousTotal) * 100
            if (abs(percent) < 0.05) {
                deltaSubLabel = stringResource(R.string.stat_same_as_last_month)
                deltaColor = TextMuted
            } else {
                val sign = if (percent > 0) "+" else ""
                val arrow = if (percent > 0) "▲" else "▼"
                deltaSubLabel = "$arrow $sign${String.format(Locale.US, "%.1f", percent)}% " + stringResource(R.string.stat_vs_last_month)
                deltaColor = if (percent > 0) DangerRed else SuccessGreen
            }
        }
    }

    // The Monthly Spending bento tile counts up/down toward the new month's number instead of
    // snapping instantly — a cheap, reliable way to make swiping between months feel alive.
    val animatedTotal by animateFloatAsState(
        targetValue = totalThisMonth.toFloat(),
        animationSpec = tween(450),
        label = "totalSpentCountUp"
    )

    // Lets tapping a bento tile smooth-scroll down to the category breakdown card. Index 3 is
    // positional (header=0, month selector=1, bento grid=2, breakdown=3) — if an item{} block is
    // added/removed above the breakdown card in the LazyColumn below, update this constant to match.
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val breakdownItemIndex = 3
    fun scrollToBreakdown() {
        coroutineScope.launch { listState.animateScrollToItem(breakdownItemIndex) }
    }

    // Feeds the shared animated background a rough "how far scrolled" cue so its blobs drift
    // in subtle parallax with this list. Doesn't need to be pixel-exact — it's a decorative
    // background signal, not a layout calculation.
    val dashboardScrollPx by remember {
        derivedStateOf { listState.firstVisibleItemIndex * 800f + listState.firstVisibleItemScrollOffset }
    }
    LaunchedEffect(dashboardScrollPx) {
        BackgroundScrollSignal.pixels.floatValue = dashboardScrollPx
    }

    // If the queue empties out (last item accepted or dismissed) while the review sheet is
    // open, close it automatically instead of leaving an empty sheet on screen.
    LaunchedEffect(pendingSmsExpenses) {
        if (pendingSmsExpenses.isEmpty()) showSmsReviewSheet = false
    }

    // Dashboard gets its own touch-reactive background instance — a distinct violet/indigo/green
    // trio so it reads as "this place" rather than reusing whatever color another screen has.
    Box(modifier = Modifier.fillMaxSize()) {
    AnimatedBlobBackground(
        blobColors = listOf(AccentIndigo, NeonViolet, SuccessGreen),
        modifier = Modifier.fillMaxSize()
    )
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingExpense = null
                showAddSheet = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_expense))
            }
        }
    ) { innerPadding ->
        // The pending-SMS banner lives in this outer Column, above the LazyColumn — not as a
        // LazyColumn item{} — so it pushes the list down without touching any item index below
        // (see breakdownItemIndex above, which a banner item{} would have silently broken).
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AnimatedVisibility(
                visible = pendingSmsExpenses.isNotEmpty(),
                enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { -it },
                exit = fadeOut(tween(220)) + slideOutVertically(tween(220)) { -it }
            ) {
                PendingSmsBanner(
                    count = pendingSmsExpenses.size,
                    onClick = { showSmsReviewSheet = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp)
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                // Scaffold's innerPadding only reserves space for app/bottom bars, not the FAB —
                // the FAB floats over content by design. Without extra bottom padding here, the
                // last row's edit/delete icons end up sitting underneath it (the bug reported
                // against this exact list). 96dp clears the 56dp FAB plus its 16dp margin with
                // a little breathing room.
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // weight(1f) reserves the badge's 44dp first, then gives the greeting
                        // only what's left — so a long display name truncates with an ellipsis
                        // instead of shrinking/squeezing the settings badge off the row.
                        Text(
                            text = greetingText(displayName),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 12.dp)
                        )
                        // Solid, fully-opaque badge (not a pale tint on a pale backdrop) so it
                        // reads clearly as a tappable button against the app's light lavender
                        // background regardless of scroll position or blob overlay underneath.
                        // Fixed size, no weight — it never shrinks regardless of name length.
                        IconButton(onClick = onOpenSettings, modifier = Modifier.size(44.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .shadow(elevation = 4.dp, shape = CircleShape, spotColor = AccentIndigo.copy(alpha = 0.5f))
                                    .background(AccentIndigo, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = stringResource(R.string.nav_settings),
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    FinancialHealthRing(
                        score = intelligence.healthScore.total,
                        bandLabel = healthBandLabel(intelligence.healthScore.total),
                        subScores = listOf(
                            HealthSubScore(stringResource(R.string.health_sub_budget), intelligence.healthScore.budgetScore, 40),
                            HealthSubScore(stringResource(R.string.health_sub_trend), intelligence.healthScore.trendScore, 25),
                            HealthSubScore(stringResource(R.string.health_sub_consistency), intelligence.healthScore.consistencyScore, 20),
                            HealthSubScore(stringResource(R.string.health_sub_subscriptions), intelligence.healthScore.subscriptionScore, 15)
                        )
                    )
                    // The first insight is rendered once, below, inside the "AI Insights Feed"
                    // list — it used to also be duplicated here as a standalone "hero" card,
                    // showing the same sentence twice on screen.
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            monthSlideDirection = -1
                            viewModel.navigateMonth(-1)
                        }) {
                            Icon(Icons.Filled.ChevronLeft, contentDescription = null)
                        }
                        AnimatedContent(
                            targetState = currentMonthKey,
                            transitionSpec = {
                                if (monthSlideDirection >= 0) {
                                    (slideInHorizontally(tween(220)) { it / 3 } + fadeIn(tween(220))) togetherWith
                                        (slideOutHorizontally(tween(220)) { -it / 3 } + fadeOut(tween(220)))
                                } else {
                                    (slideInHorizontally(tween(220)) { -it / 3 } + fadeIn(tween(220))) togetherWith
                                        (slideOutHorizontally(tween(220)) { it / 3 } + fadeOut(tween(220)))
                                }
                            },
                            label = "monthLabel"
                        ) { key ->
                            Text(
                                text = DateUtils.monthLabelForDisplay(key, locale),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        IconButton(onClick = {
                            monthSlideDirection = 1
                            viewModel.navigateMonth(1)
                        }) {
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BentoCard(
                        title = stringResource(R.string.bento_monthly_spending),
                        value = Formatters.money(animatedTotal.toDouble(), currencySymbol),
                        subtitle = deltaSubLabel,
                        icon = Icons.Filled.AccountBalanceWallet,
                        gradientColors = listOf(AccentIndigo, NeonViolet),
                        onClick = { scrollToBreakdown() },
                        visible = contentVisible,
                        entranceDelayMillis = 0,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 132.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BentoCard(
                            title = stringResource(R.string.bento_budget_remaining),
                            value = intelligence.budgetRemaining?.let { Formatters.money(it, currencySymbol) }
                                ?: stringResource(R.string.bento_budget_no_target),
                            subtitle = intelligence.overallBudget?.let {
                                stringResource(R.string.bento_budget_remaining_subtitle, Formatters.money(it, currencySymbol))
                            },
                            icon = Icons.Filled.Tune,
                            gradientColors = listOf(NeonCyan, AccentIndigo),
                            progress = intelligence.overallBudget?.let { budget ->
                                if (budget > 0) (intelligence.monthlySpend / budget).toFloat().coerceIn(0f, 1f) else null
                            },
                            onClick = { showBudgetSheet = true },
                            visible = contentVisible,
                            entranceDelayMillis = 60,
                            modifier = Modifier.weight(1f).heightIn(min = 132.dp)
                        )
                        BentoCard(
                            title = stringResource(R.string.bento_savings_goal),
                            value = Formatters.money(intelligence.savingsGoal.projectedSavings, currencySymbol),
                            subtitle = if (intelligence.savingsGoal.hasEnoughData) {
                                stringResource(
                                    R.string.bento_savings_goal_subtitle,
                                    Formatters.money(intelligence.savingsGoal.targetAmount, currencySymbol)
                                )
                            } else {
                                stringResource(R.string.bento_savings_goal_no_data)
                            },
                            icon = Icons.Filled.EmojiEvents,
                            gradientColors = listOf(SuccessGreen, NeonCyan),
                            progress = if (intelligence.savingsGoal.hasEnoughData) intelligence.savingsGoal.progress else null,
                            onClick = { showBudgetSheet = true },
                            visible = contentVisible,
                            entranceDelayMillis = 90,
                            modifier = Modifier.weight(1f).heightIn(min = 132.dp)
                        )
                    }
                    BentoCard(
                        title = stringResource(R.string.bento_subscription_cost),
                        value = Formatters.money(intelligence.subscriptionSpend, currencySymbol),
                        subtitle = subscriptionSharePct?.let { stringResource(R.string.bento_subscription_subtitle, it) },
                        icon = Icons.Filled.Receipt,
                        gradientColors = listOf(WarningAmber, NeonViolet),
                        onClick = { scrollToBreakdown() },
                        visible = contentVisible,
                        entranceDelayMillis = 120,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp)
                    )
                }
            }

            item {
                // A soft pastel-violet tile (not a flat white card) — same rounded "glass" shape
                // and shadow language as the Bento tiles above, but diluted to a pale gradient so
                // the donut chart and legend text underneath stay easy to read. The hue echoes the
                // Monthly Spending tile's indigo/violet pair, since tapping that tile scrolls down
                // to this card.
                val breakdownShape = RoundedCornerShape(22.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = breakdownShape,
                            ambientColor = AccentIndigo.copy(alpha = 0.18f),
                            spotColor = NeonViolet.copy(alpha = 0.18f)
                        )
                        .clip(breakdownShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFF3EFFF), Color(0xFFE8E0FF))))
                        .border(width = 1.dp, color = BorderLight, shape = breakdownShape)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.breakdown_title),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.width(8.dp))
                            ManageCategoriesButton(onClick = { showCategorySheet = true })
                        }
                        Spacer(Modifier.height(12.dp))
                        if (slices.isEmpty()) {
                            Text(
                                stringResource(R.string.no_expenses_breakdown),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DonutChart(slices = slices, modifier = Modifier.size(120.dp))
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    legendEntries.forEach { (cat, amt, pct) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(colorFromHex(cat.colorHex), shape = CircleShape)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            // Category name gets its own full-width line and is
                                            // capped at one line with an ellipsis fallback — this
                                            // is what stops long translated names (e.g. Arabic
                                            // category phrases) from being squeezed into a
                                            // character-per-line vertical wrap.
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = categoryDisplayName(cat),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 1,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${pct.toInt()}%",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            MoneyText(
                                                formatted = Formatters.money(amt, currencySymbol),
                                                style = MaterialTheme.typography.labelMedium,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // The AI Insights Feed — social-media-style cards, one per on-device insight computed
            // above. Inserted right after the breakdown card (which stays at breakdownItemIndex =
            // 3 above); appending here instead of before it means that index doesn't shift.
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.ai_insights_feed_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    intelligence.insights.forEachIndexed { index, insight ->
                        InsightFeedCard(
                            emoji = insight.emoji,
                            text = insightText(insight, categoryNameById),
                            accentColor = insightAccentColor(insight.kind),
                            visible = contentVisible,
                            entranceDelayMillis = index * 40
                        )
                    }
                }
            }

            item {
                // Same soft-tile treatment as the breakdown card above, tuned to a teal/sky pastel
                // — a "movement/trend" hue family — so the two content tiles read as siblings
                // without being identical.
                val trendShape = RoundedCornerShape(22.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = trendShape,
                            ambientColor = SuccessGreen.copy(alpha = 0.18f),
                            spotColor = NeonCyan.copy(alpha = 0.18f)
                        )
                        .clip(trendShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFE9FBF3), Color(0xFFE2F5FF))))
                        .border(width = 1.dp, color = BorderLight, shape = trendShape)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.trend_title), style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        TrendBarChart(points = trendPoints, modifier = Modifier.fillMaxWidth().height(140.dp))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.expenses_this_month), style = MaterialTheme.typography.titleMedium)
                    ExportPdfChip(
                        enabled = monthExpenses.isNotEmpty(),
                        onClick = { exportMonthAsPdf() }
                    )
                }
            }
            item {
                ExpenseViewModeToggle(current = viewMode, onSelect = { viewMode = it })
            }

            when (viewMode) {
                ExpenseViewMode.DAY -> {
                    if (expensesByDay.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_expenses_this_month),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        expensesByDay.forEach { (dayIso, dayExpenses) ->
                            item(key = "day_header_$dayIso") {
                                val headerLabel = when (dayIso) {
                                    todayIso -> todayLabel
                                    yesterdayIso -> yesterdayLabel
                                    else -> DateUtils.formatExpenseDate(dayIso, locale)
                                }
                                val dayTotal = dayExpenses.sumOf { it.amount }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(headerLabel, style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                                    MoneyText(
                                        formatted = Formatters.money(dayTotal, currencySymbol),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextSecondary
                                    )
                                }
                            }
                            items(dayExpenses, key = { "day_${it.id}" }) { expense ->
                                ExpenseRowCard(
                                    expense = expense,
                                    category = categoryById[expense.categoryId],
                                    currencySymbol = currencySymbol,
                                    dateText = DateUtils.formatExpenseDate(expense.date, locale),
                                    onClick = { editingExpense = expense; showAddSheet = true },
                                    onEdit = { editingExpense = expense; showAddSheet = true },
                                    onDelete = { expensePendingDelete = expense },
                                    colorFromHex = ::colorFromHex
                                )
                            }
                        }
                    }
                }

                ExpenseViewMode.CATEGORY -> {
                    if (expensesByCategory.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_expenses_this_month),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        expensesByCategory.forEach { (categoryId, catExpenses) ->
                            val category = categoryById[categoryId]
                            item(key = "cat_header_$categoryId") {
                                val catTotal = catExpenses.sumOf { it.amount }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(
                                                    color = category?.let { colorFromHex(it.colorHex) } ?: TextMuted,
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = category?.let { categoryDisplayName(it) } ?: "",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = TextSecondary
                                        )
                                    }
                                    MoneyText(
                                        formatted = Formatters.money(catTotal, currencySymbol),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextSecondary
                                    )
                                }
                            }
                            items(catExpenses, key = { "cat_${it.id}" }) { expense ->
                                ExpenseRowCard(
                                    expense = expense,
                                    category = category,
                                    currencySymbol = currencySymbol,
                                    dateText = DateUtils.formatExpenseDate(expense.date, locale),
                                    onClick = { editingExpense = expense; showAddSheet = true },
                                    onEdit = { editingExpense = expense; showAddSheet = true },
                                    onDelete = { expensePendingDelete = expense },
                                    colorFromHex = ::colorFromHex
                                )
                            }
                        }
                    }
                }

                ExpenseViewMode.GRID -> {
                    if (monthExpenses.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_expenses_this_month),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(gridRows, key = { pair -> "grid_${pair.first().id}" }) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                pair.forEach { expense ->
                                    ExpenseGridCard(
                                        expense = expense,
                                        category = categoryById[expense.categoryId],
                                        currencySymbol = currencySymbol,
                                        dateText = DateUtils.formatExpenseDate(expense.date, locale),
                                        onClick = { editingExpense = expense; showAddSheet = true },
                                        modifier = Modifier.weight(1f),
                                        colorFromHex = ::colorFromHex
                                    )
                                }
                                if (pair.size == 1) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                ExpenseViewMode.CALENDAR -> {
                    item {
                        MonthCalendarView(
                            monthKey = currentMonthKey,
                            firstWeekdayIndex = firstWeekdayIndex,
                            daysInMonth = daysInMonthCount,
                            dailyTotals = dailyTotals,
                            selectedDay = selectedCalendarDay,
                            todayIso = todayIso,
                            weekdayLabels = weekdayLabels,
                            onDaySelected = { day ->
                                selectedCalendarDay = if (selectedCalendarDay == day) null else day
                            }
                        )
                    }
                    if (selectedCalendarDay == null) {
                        item {
                            Text(
                                stringResource(R.string.calendar_tap_hint),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else if (selectedDayExpenses.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_expenses_this_day),
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(selectedDayExpenses, key = { "cal_${it.id}" }) { expense ->
                            ExpenseRowCard(
                                expense = expense,
                                category = categoryById[expense.categoryId],
                                currencySymbol = currencySymbol,
                                dateText = DateUtils.formatExpenseDate(expense.date, locale),
                                onClick = { editingExpense = expense; showAddSheet = true },
                                onEdit = { editingExpense = expense; showAddSheet = true },
                                onDelete = { expensePendingDelete = expense },
                                colorFromHex = ::colorFromHex
                            )
                        }
                    }
                }
            }
            } // close LazyColumn
        } // close outer Column wrapping the SMS banner + LazyColumn
    }
    } // close Box wrapping AnimatedBlobBackground + Scaffold

    if (showAddSheet) {
        AddEditExpenseSheet(
            categories = categories,
            existing = editingExpense,
            defaultDate = DateUtils.todayIso(),
            onDismiss = { showAddSheet = false },
            onSave = { id, catId, desc, amt, date ->
                viewModel.saveExpense(id, catId, desc, amt, date) { showAddSheet = false }
            }
        )
    }

    if (showCategorySheet) {
        CategoryManageSheet(
            categories = categories,
            onDismiss = { showCategorySheet = false },
            onRename = { cat, name -> viewModel.renameCategory(cat, name) },
            onRecolor = { cat, hex -> viewModel.recolorCategory(cat, hex) },
            onAddCategory = { name, hex -> viewModel.addCategory(name, hex) {} },
            onDeleteCategory = { cat, onResult -> viewModel.deleteCategory(cat, onResult) }
        )
    }

    if (showBudgetSheet) {
        BudgetManageSheet(
            overallBudget = overallBudget,
            currencySymbol = currencySymbol,
            onDismiss = { showBudgetSheet = false },
            onSave = { amount -> viewModel.setBudget(null, amount) }
        )
    }

    expensePendingDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { expensePendingDelete = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_expense_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(expense) { expensePendingDelete = null }
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { expensePendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showSmsReviewSheet) {
        SmsReviewSheet(
            items = pendingSmsExpenses,
            categories = categories,
            currencySymbol = currencySymbol,
            locale = locale,
            onDismiss = { showSmsReviewSheet = false },
            onAccept = { item ->
                showSmsReviewSheet = false
                smsItemBeingAccepted = item
            },
            onDismissItem = { item -> viewModel.dismissPendingSms(item) }
        )
    }

    // Accepting a queued SMS opens the normal add/edit sheet pre-filled with the parser's
    // guess (existing = null, so it always inserts a new expense) instead of saving silently —
    // the user gets one last chance to fix the amount/category/description before it's real.
    smsItemBeingAccepted?.let { item ->
        AddEditExpenseSheet(
            categories = categories,
            existing = null,
            defaultDate = item.date,
            prefill = ExpensePrefill(
                description = item.description,
                amount = item.amount,
                categoryId = item.suggestedCategoryId,
                date = item.date
            ),
            onDismiss = { smsItemBeingAccepted = null },
            onSave = { _, catId, desc, amt, date ->
                viewModel.acceptPendingSms(item, catId, desc, amt, date) {
                    smsItemBeingAccepted = null
                }
            }
        )
    }
}

/**
 * Tappable "Export PDF" pill next to the expenses list header. Replaces the old bare icon
 * button with something more interactive: a labeled chip that dips on press, and visibly
 * dims (and won't try to generate an empty report) when there's nothing for the current
 * month to export.
 */
@Composable
private fun ExportPdfChip(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = tween(120),
        label = "exportChipPress"
    )
    val tint = if (enabled) AccentIndigo else TextMuted
    val bg = if (enabled) AccentIndigo.copy(alpha = 0.1f) else TextMuted.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.export_pdf),
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
    }
}

/**
 * "Manage Categories" link next to the breakdown header. Pulled out into its own composable
 * (was inline) so it can carry a press-scale animation, and given maxLines/ellipsis so a long
 * translated label shrinks instead of wrapping character-by-character when the sibling title
 * Text claims most of the row's width.
 */
@Composable
private fun ManageCategoriesButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(120),
        label = "manageCategoriesPress"
    )

    Row(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentIndigo)
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.manage_categories),
            style = MaterialTheme.typography.labelMedium,
            color = AccentIndigo,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

/**
 * Banner shown above the expenses list whenever the SMS parser has queued one or more
 * transactions for review. Tapping it opens [SmsReviewSheet] — nothing here has been saved
 * as a real expense yet.
 */
@Composable
private fun PendingSmsBanner(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "smsBannerPress"
    )

    Row(
        modifier = modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(14.dp))
            .background(AccentIndigo.copy(alpha = 0.12f))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Sms, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.sms_pending_banner, count),
            style = MaterialTheme.typography.bodyMedium,
            color = AccentIndigo,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
    }
}

private fun colorFromHex(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(TextMuted)

/**
 * Time-of-day greeting for the Command Center header — "Good morning" / "Good afternoon" /
 * "Good evening", personalized with [name] when the user has set one in Settings, falling back
 * to a name-less phrasing when they haven't.
 */
@Composable
private fun greetingText(name: String): String {
    val hour = remember { LocalTime.now().hour }
    return if (name.isBlank()) {
        when {
            hour < 12 -> stringResource(R.string.greeting_morning)
            hour < 17 -> stringResource(R.string.greeting_afternoon)
            else -> stringResource(R.string.greeting_evening)
        }
    } else {
        when {
            hour < 12 -> stringResource(R.string.greeting_morning_named, name)
            hour < 17 -> stringResource(R.string.greeting_afternoon_named, name)
            else -> stringResource(R.string.greeting_evening_named, name)
        }
    }
}

/** Localized band label shown next to the Financial Health ring. */
@Composable
private fun healthBandLabel(score: Int): String = when {
    score >= 80 -> stringResource(R.string.health_band_excellent)
    score >= 60 -> stringResource(R.string.health_band_good)
    score >= 40 -> stringResource(R.string.health_band_fair)
    else -> stringResource(R.string.health_band_needs_attention)
}

/** Maps an insight's positive/warning/neutral kind to the accent color its feed card is drawn in. */
private fun insightAccentColor(kind: FinancialInsights.InsightKind): Color = when (kind) {
    FinancialInsights.InsightKind.POSITIVE -> SuccessGreen
    FinancialInsights.InsightKind.WARNING -> WarningAmber
    FinancialInsights.InsightKind.NEUTRAL -> NeonCyan
}

/**
 * Resolves one [FinancialInsights.Insight] to its localized sentence. [categoryNameById] resolves
 * [FinancialInsights.Insight.categoryId] to a display name, which (when present) is always the
 * first format argument, followed by the engine's own already-formatted [FinancialInsights.Insight.args].
 */
@Composable
private fun insightText(insight: FinancialInsights.Insight, categoryNameById: Map<Long, String>): String {
    val resId = when (insight.template) {
        FinancialInsights.InsightTemplate.TREND_DOWN -> R.string.insight_trend_down
        FinancialInsights.InsightTemplate.TREND_UP -> R.string.insight_trend_up
        FinancialInsights.InsightTemplate.TREND_FLAT -> R.string.insight_trend_flat
        FinancialInsights.InsightTemplate.CATEGORY_INCREASE -> R.string.insight_category_increase
        FinancialInsights.InsightTemplate.CATEGORY_DECREASE -> R.string.insight_category_decrease
        FinancialInsights.InsightTemplate.PROJECTED_SAVINGS -> R.string.insight_projected_savings
        FinancialInsights.InsightTemplate.PROJECTED_OVERSPEND -> R.string.insight_projected_overspend
        FinancialInsights.InsightTemplate.STRONGEST_DAY -> R.string.insight_strongest_day
        FinancialInsights.InsightTemplate.SUBSCRIPTION_LOAD -> R.string.insight_subscription_load
        FinancialInsights.InsightTemplate.DAILY_AVERAGE -> R.string.insight_daily_average
        FinancialInsights.InsightTemplate.NOT_ENOUGH_DATA -> R.string.insight_not_enough_data
    }
    val args: List<String> = buildList {
        insight.categoryId?.let { id -> add(categoryNameById[id] ?: "") }
        addAll(insight.args)
    }
    return if (args.isEmpty()) stringResource(resId) else stringResource(resId, *args.toTypedArray())
}
