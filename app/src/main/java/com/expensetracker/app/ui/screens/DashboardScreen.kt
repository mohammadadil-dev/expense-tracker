package com.expensetracker.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.ui.ads.BannerAdView
import com.expensetracker.app.ui.ads.InterstitialAdManager
import com.expensetracker.app.data.PendingSmsExpense
import com.expensetracker.app.ui.components.AddEditExpenseSheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.BackgroundScrollSignal
import com.expensetracker.app.ui.components.BentoCard
import com.expensetracker.app.ui.components.BudgetManageSheet
import com.expensetracker.app.ui.components.BudgetRingCard
import com.expensetracker.app.ui.components.CategoryManageSheet
import com.expensetracker.app.ui.components.ExpenseGridCard
import com.expensetracker.app.ui.components.ExpensePrefill
import com.expensetracker.app.ui.components.ExpenseRowCard
import com.expensetracker.app.ui.components.ExpenseViewMode
import com.expensetracker.app.ui.components.ExpenseViewModeToggle
import com.expensetracker.app.ui.components.IncomeBudgetTiles
import com.expensetracker.app.ui.components.InsightFeedCard
import com.expensetracker.app.ui.components.MonthCalendarView
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.RecentTransactionsSection
import com.expensetracker.app.ui.components.SalarySetDialog
import com.expensetracker.app.ui.components.SpendingCategorySection
import com.expensetracker.app.ui.components.SmsReviewSheet
import com.expensetracker.app.ui.components.TransactionActionSheet
import com.expensetracker.app.ui.components.TrendBarChart
import com.expensetracker.app.ui.components.TrendPoint
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonViolet
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.WarningAmber
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.DebtInsights
import com.expensetracker.app.util.ExportRow
import com.expensetracker.app.util.FinancialInsights
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.PdfExporter
import com.expensetracker.app.util.categoryDisplayName
import com.expensetracker.app.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onOpenSettings: () -> Unit,
    onOpenDebts: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val monthExpenses by viewModel.monthExpenses.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val currentMonthKey by viewModel.currentMonthKey.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val debtPayments by viewModel.debtPayments.collectAsState()
    val debtNetPosition = remember(debts, debtPayments) { DebtInsights.computeNetPosition(debts, debtPayments) }
    val pendingSmsExpenses by viewModel.pendingSmsExpenses.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val monthlySalary by viewModel.monthlySalary.collectAsState()
    val locale: Locale = LocalConfiguration.current.locales[0]

    val context = LocalContext.current
    var showAddSheet by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var expensePendingDelete by remember { mutableStateOf<ExpenseEntity?>(null) }
    // Expense tapped — shows action sheet (Edit / Delete)
    var actionExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showSmsReviewSheet by remember { mutableStateOf(false) }
    var smsItemBeingAccepted by remember { mutableStateOf<PendingSmsExpense?>(null) }
    var showBudgetSheet by remember { mutableStateOf(false) }
    var showSalaryDialog by remember { mutableStateOf(false) }
    // Tracks which way the user just navigated so the month label slides the right direction.
    var monthSlideDirection by remember { mutableStateOf(1) }
    var viewMode by remember { mutableStateOf(ExpenseViewMode.DAY) }
    var selectedCalendarDay by remember(currentMonthKey) { mutableStateOf<String?>(null) }
    // Drives entrance stagger — flips true once, right after first composition.
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    // AdMob: get the host Activity so the interstitial can show full-screen.
    val activity = context as? android.app.Activity
    // Pre-load the interstitial in the background so it's ready when the user taps Export PDF.
    LaunchedEffect(Unit) { activity?.let { InterstitialAdManager.preload(it) } }

    // Cat_debt_payments is auto-managed; hide it from the manual Add Expense picker so
    // users can't accidentally file an expense there by hand (debt payments create their
    // linked expense automatically when recorded from the Debts screen).
    val expensePickerCategories = remember(categories) {
        categories.filter { it.nameKey != "cat_debt_payments" }
    }

    val categoryById = remember(categories) { categories.associateBy { it.id } }

    val categoryTotals = remember(monthExpenses) {
        monthExpenses.groupBy { it.categoryId }.mapValues { (_, list) -> list.sumOf { it.amount } }
    }
    val totalThisMonth = remember(monthExpenses) { monthExpenses.sumOf { it.amount } }

    // Overall budget target (categoryId == null)
    val overallBudget = remember(budgets) { budgets.find { it.categoryId == null } }

    val previousKey = remember(currentMonthKey) { DateUtils.previousMonthKey(currentMonthKey) }
    val previousTotal = remember(allExpenses, previousKey) {
        allExpenses.filter { it.monthKey == previousKey }.sumOf { it.amount }
    }

    // On-device intelligence — no network call, no LLM.
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

    val expensesByDay = remember(monthExpenses) {
        monthExpenses.groupBy { it.date }.toList().sortedByDescending { it.first }
    }
    // Start by showing the 7 most-recent days; the user can tap "Load more" to see older days.
    // Reset to 7 whenever the month changes so switching months doesn't carry over a large count.
    var visibleDayCount by remember(currentMonthKey) { mutableStateOf(7) }
    val loadMoreLabel = stringResource(R.string.load_more)
    val todayIso = remember { DateUtils.todayIso() }
    val yesterdayIso = remember { DateUtils.yesterdayIso() }
    val todayLabel = stringResource(R.string.today)
    val yesterdayLabel = stringResource(R.string.yesterday)

    val expensesByCategory = remember(monthExpenses) {
        monthExpenses.groupBy { it.categoryId }
            .toList()
            .sortedByDescending { (_, list) -> list.sumOf { it.amount } }
    }
    val dailyTotals = remember(monthExpenses) {
        monthExpenses.groupBy { it.date }.mapValues { (_, list) -> list.sumOf { it.amount } }
    }
    val firstWeekdayIndex = remember(currentMonthKey) { DateUtils.firstWeekdayIndexOfMonth(currentMonthKey) }
    val daysInMonthCount = remember(currentMonthKey) { DateUtils.daysInMonth(currentMonthKey) }
    val weekdayLabels = remember(locale) { DateUtils.weekdayShortLabels(locale) }
    val selectedDayExpenses = remember(selectedCalendarDay, monthExpenses) {
        selectedCalendarDay?.let { day -> monthExpenses.filter { it.date == day } } ?: emptyList()
    }
    val gridRows = remember(monthExpenses) {
        monthExpenses.sortedByDescending { it.date }.chunked(2)
    }

    // PDF export strings resolved in composable scope so the exporter stays non-composable.
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

    // Scroll anchors for list navigation.
    // breakdownItemIndex = 3: SpendingCategorySection (tapping subscription tile scrolls here)
    // expenseListItemIndex = 7: "Expenses this month" header
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val breakdownItemIndex = 3
    val expenseListItemIndex = 7
    fun scrollToBreakdown() {
        coroutineScope.launch { listState.animateScrollToItem(breakdownItemIndex) }
    }
    fun scrollToExpenseList() {
        coroutineScope.launch { listState.animateScrollToItem(expenseListItemIndex) }
    }

    val dashboardScrollPx by remember {
        derivedStateOf { listState.firstVisibleItemIndex * 800f + listState.firstVisibleItemScrollOffset }
    }
    LaunchedEffect(dashboardScrollPx) {
        BackgroundScrollSignal.pixels.floatValue = dashboardScrollPx
    }

    LaunchedEffect(pendingSmsExpenses) {
        if (pendingSmsExpenses.isEmpty()) showSmsReviewSheet = false
    }

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
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

            // ── item 0 ─────────────────────────────────────────────────────────
            // Greeting header + dark indigo Budget Ring card (month nav embedded).
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = greetingText(displayName),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 12.dp)
                        )
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
                    Spacer(Modifier.height(14.dp))
                    BudgetRingCard(
                        currentMonthKey = currentMonthKey,
                        monthSlideDirection = monthSlideDirection,
                        monthDisplayLabel = DateUtils.monthLabelForDisplay(currentMonthKey, locale),
                        totalSpent = totalThisMonth,
                        overallBudget = overallBudget?.amount,
                        currencySymbol = currencySymbol,
                        onNavigatePrev = {
                            monthSlideDirection = -1
                            viewModel.navigateMonth(-1)
                        },
                        onNavigateNext = {
                            monthSlideDirection = 1
                            viewModel.navigateMonth(1)
                        },
                        onManageBudget = { showBudgetSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── item 1 ─────────────────────────────────────────────────────────
            // Salary (green) + Budget Cap (amber-orange) tiles side by side.
            item {
                IncomeBudgetTiles(
                    monthlySalary = monthlySalary,
                    overallBudget = overallBudget?.amount,
                    currencySymbol = currencySymbol,
                    onSetSalary = { showSalaryDialog = true },
                    onManageBudget = { showBudgetSheet = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── item 2 ─────────────────────────────────────────────────────────
            // Compact side-by-side tiles: Debts net position + Subscription spend.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BentoCard(
                        title = stringResource(R.string.bento_debts_title),
                        value = Formatters.money(abs(debtNetPosition.net), currencySymbol),
                        subtitle = stringResource(
                            when {
                                debtNetPosition.net > 0.0 -> R.string.debts_net_owed_to_you
                                debtNetPosition.net < 0.0 -> R.string.debts_net_you_owe
                                else -> R.string.debts_net_settled
                            }
                        ),
                        icon = Icons.Filled.AccountBalance,
                        gradientColors = if (debtNetPosition.net < 0.0) {
                            listOf(DangerRed, WarningAmber)
                        } else {
                            listOf(SuccessGreen, NeonCyan)
                        },
                        onClick = onOpenDebts,
                        visible = contentVisible,
                        entranceDelayMillis = 0,
                        modifier = Modifier.weight(1f).heightIn(min = 110.dp)
                    )
                    BentoCard(
                        title = stringResource(R.string.bento_subscription_cost),
                        value = Formatters.money(intelligence.subscriptionSpend, currencySymbol),
                        subtitle = subscriptionSharePct?.let { stringResource(R.string.bento_subscription_subtitle, it) },
                        icon = Icons.Filled.Receipt,
                        gradientColors = listOf(WarningAmber, NeonViolet),
                        onClick = { scrollToBreakdown() },
                        visible = contentVisible,
                        entranceDelayMillis = 60,
                        modifier = Modifier.weight(1f).heightIn(min = 110.dp)
                    )
                }
            }

            // ── item 3 ─────────────────────────────────────────────────────────
            // Spending by Category — emoji squircle icons + coloured progress bars.
            // Index 3 is intentional: breakdownItemIndex = 3 and scrollToBreakdown() rely on it.
            item {
                SpendingCategorySection(
                    categoryTotals = categoryTotals,
                    categories = categories,
                    totalThisMonth = totalThisMonth,
                    currencySymbol = currencySymbol,
                    onManageCategories = { showCategorySheet = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── item 4 ─────────────────────────────────────────────────────────
            // Recent Transactions — last 5 expenses from this month.
            item {
                RecentTransactionsSection(
                    expenses = monthExpenses,
                    categoryById = categoryById,
                    currencySymbol = currencySymbol,
                    onViewAll = { scrollToExpenseList() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── item 5 ─────────────────────────────────────────────────────────
            // AI Insights Feed — on-device rule-based intelligence cards.
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

            // ── item 6 ─────────────────────────────────────────────────────────
            // Monthly Trend bar chart — 12-month rolling view.
            item {
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
                        .drawBehind {
                            // Guard against zero-size: Android's native LinearGradient crashes
                            // when start == end (both collapse to 0,0 at zero measured size).
                            if (size.width > 0f && size.height > 0f) {
                                drawRect(
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFFE9FBF3), Color(0xFFE2F5FF)),
                                        start = Offset.Zero,
                                        end = Offset(size.width, size.height)
                                    )
                                )
                            }
                        }
                        .border(width = 1.dp, color = BorderLight, shape = trendShape)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.trend_title), style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        TrendBarChart(points = trendPoints, modifier = Modifier.fillMaxWidth().height(140.dp))
                    }
                }
            }

            // ── item 7 ─────────────────────────────────────────────────────────
            // Full expense list header (title + PDF export chip).
            // expenseListItemIndex = 7 — "View All" in RecentTransactionsSection scrolls here.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.expenses_this_month),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    ExportPdfChip(
                        enabled = monthExpenses.isNotEmpty(),
                        onClick = {
                            // Show an interstitial before export (high CPM, natural break
                            // point). If no ad is ready yet, exportMonthAsPdf() fires
                            // immediately so the user is never kept waiting.
                            if (activity != null) {
                                InterstitialAdManager.showThen(activity) { exportMonthAsPdf() }
                            } else {
                                exportMonthAsPdf()
                            }
                        }
                    )
                }
            }
            // ── item 8 ─────────────────────────────────────────────────────────
            item {
                ExpenseViewModeToggle(current = viewMode, onSelect = { viewMode = it })
            }

            // ── items 9+ ───────────────────────────────────────────────────────
            // Expense rows for the selected view mode.
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
                        expensesByDay.take(visibleDayCount).forEach { (dayIso, dayExpenses) ->
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
                                    onClick = { actionExpense = expense },
                                    colorFromHex = ::colorFromHex
                                )
                            }
                        }
                        // "Load more" button — only shown when there are older day-groups not yet visible
                        if (expensesByDay.size > visibleDayCount) {
                            item(key = "day_load_more") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(onClick = { visibleDayCount += 7 }) {
                                        Text(
                                            loadMoreLabel,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = AccentIndigo
                                        )
                                    }
                                }
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
                                    onClick = { actionExpense = expense },
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
                                        onClick = { actionExpense = expense },
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
                                onClick = { actionExpense = expense },
                                colorFromHex = ::colorFromHex
                            )
                        }
                    }
                }
            }
            } // close LazyColumn

            // Anchored banner ad pinned below the list.
            BannerAdView()
        } // close Column
    }
    } // close Box

    // ── Modal sheets ──────────────────────────────────────────────────────────

    // Transaction action sheet — shown when the user taps any expense row/grid card
    actionExpense?.let { expense ->
        TransactionActionSheet(
            expense = expense,
            category = categoryById[expense.categoryId],
            currencySymbol = currencySymbol,
            onEdit = {
                editingExpense = expense
                showAddSheet = true
            },
            onDelete = { expensePendingDelete = expense },
            onDismiss = { actionExpense = null }
        )
    }

    if (showAddSheet) {
        AddEditExpenseSheet(
            // Deliberately excludes cat_debt_payments — that category is auto-managed by the
            // Debts screen and should never be used for hand-entered expenses.
            categories = expensePickerCategories,
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

    if (showSalaryDialog) {
        SalarySetDialog(
            currentSalary = monthlySalary,
            currencySymbol = currencySymbol,
            onConfirm = { amount ->
                viewModel.setMonthlySalary(amount)
                showSalaryDialog = false
            },
            onDismiss = { showSalaryDialog = false }
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

    // Accepting a queued SMS pre-fills the standard expense sheet for a final review.
    smsItemBeingAccepted?.let { item ->
        AddEditExpenseSheet(
            categories = expensePickerCategories,
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

// ── Private helper composables ────────────────────────────────────────────────

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

    // Icon-only chip — no text label — so it never overflows the row regardless of how long
    // the section title is in any language (e.g. Tagalog "Mga Gastos Ngayong Buwan" already
    // fills most of the available width; adding a translated text label would push this chip
    // off-screen). The PDF icon is universally recognised.
    Box(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.PictureAsPdf,
            contentDescription = stringResource(R.string.export_pdf),
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

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
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
    }
}

private fun colorFromHex(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(TextMuted)

@Composable
private fun greetingText(name: String): String =
    if (name.isBlank()) stringResource(R.string.greeting_hello)
    else stringResource(R.string.greeting_hello_named, name)

private fun insightAccentColor(kind: FinancialInsights.InsightKind): Color = when (kind) {
    FinancialInsights.InsightKind.POSITIVE -> SuccessGreen
    FinancialInsights.InsightKind.WARNING -> WarningAmber
    FinancialInsights.InsightKind.NEUTRAL -> NeonCyan
}

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
