package com.expensetracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.ui.components.AddEditExpenseSheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonViolet
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.categoryEmoji
import com.expensetracker.app.viewmodel.ExpenseViewModel
import java.time.LocalDate

/**
 * Manage recurring-expense templates ("Repeat every month" expenses) in one place — rent,
 * memberships, EMIs, and anything else the user marked to auto-copy monthly.
 *
 * This is a management view over the *existing* [ExpenseEntity.isRecurring] template
 * mechanism (see [com.expensetracker.app.data.ExpenseRepository.createRecurringExpensesForCurrentMonth])
 * — deliberately not a new parallel "subscription" data model. It is distinct from the
 * category-based "Subscription Cost" Dashboard card, which is a spend heuristic, not a
 * template list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val templates by viewModel.recurringTemplates.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val paymentAccounts by viewModel.paymentAccounts.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val locale = LocalConfiguration.current.locales[0]

    var showAddSheet by remember { mutableStateOf(false) }
    var editingTemplate by remember { mutableStateOf<ExpenseEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val monthlyTotal = remember(templates) { templates.sumOf { it.amount } }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBlobBackground(
            blobColors = listOf(NeonViolet, NeonCyan, AccentIndigo),
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.subscriptions_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { editingTemplate = null; showAddSheet = true },
                    containerColor = AccentIndigo
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add))
                }
            }
        ) { innerPadding ->
            if (templates.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    EmptySubscriptionsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 64.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        SubscriptionsSummaryHeader(
                            monthlyTotal = monthlyTotal,
                            count = templates.size,
                            currencySymbol = currencySymbol
                        )
                    }
                    items(templates, key = { it.id }) { template ->
                        val category = categories.firstOrNull { it.id == template.categoryId }
                        SubscriptionListItem(
                            template = template,
                            category = category,
                            currencySymbol = currencySymbol,
                            locale = locale,
                            onClick = { editingTemplate = template; showAddSheet = true },
                            onDelete = { pendingDelete = template }
                        )
                    }
                    item { Spacer(Modifier.height(96.dp)) }
                }
            }
        }
    }

    if (showAddSheet) {
        AddEditExpenseSheet(
            categories = categories.filter { it.nameKey != "cat_debt_payments" },
            existing = editingTemplate,
            defaultDate = DateUtils.todayIso(),
            defaultRecurring = true,
            accounts = paymentAccounts,
            onDismiss = { showAddSheet = false; editingTemplate = null },
            onSave = { id, catId, desc, amt, date, recurring, memberId, recurringDay, accountId ->
                viewModel.saveExpense(id, catId, desc, amt, date, recurring, recurringDay, memberId, accountId) {
                    showAddSheet = false
                    editingTemplate = null
                }
            }
        )
    }

    pendingDelete?.let { template ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.subscriptions_delete_title)) },
            text = { Text(stringResource(R.string.subscriptions_delete_confirm, template.description)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(template) { pendingDelete = null }
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun SubscriptionsSummaryHeader(
    monthlyTotal: Double,
    count: Int,
    currencySymbol: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.subscriptions_monthly_total_label),
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted
        )
        Text(
            text = Formatters.money(monthlyTotal, currencySymbol),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = stringResource(
                if (count == 1) R.string.subscriptions_count_singular else R.string.subscriptions_count_plural,
                count
            ),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
private fun SubscriptionListItem(
    template: ExpenseEntity,
    category: CategoryEntity?,
    currencySymbol: String,
    locale: java.util.Locale,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val nextRenewalIso = remember(template.recurringDayOfMonth) {
        nextRenewalDateIso(template.recurringDayOfMonth)
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(categoryEmoji(category?.nameKey), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.subscriptions_next_renewal,
                        DateUtils.formatExpenseDate(nextRenewalIso, locale)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = Formatters.money(template.amount, currencySymbol),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = DangerRed)
            }
        }
    }
}

@Composable
private fun EmptySubscriptionsState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.Repeat,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.subscriptions_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.subscriptions_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Next renewal date for a template with billing day [dayOfMonth] (null = day 1, matching
 * [com.expensetracker.app.data.ExpenseRepository.createRecurringExpensesForCurrentMonth]'s
 * own fallback). If that day hasn't happened yet this month, the renewal is this month;
 * otherwise it rolls to next month. Clamped to each month's actual last day.
 */
private fun nextRenewalDateIso(dayOfMonth: Int?): String {
    val today = LocalDate.now()
    val day = (dayOfMonth ?: 1).coerceIn(1, 31)
    val candidateThisMonth = today.withDayOfMonth(day.coerceAtMost(today.lengthOfMonth()))
    val next = if (!candidateThisMonth.isBefore(today)) {
        candidateThisMonth
    } else {
        val nextMonth = today.plusMonths(1)
        nextMonth.withDayOfMonth(day.coerceAtMost(nextMonth.lengthOfMonth()))
    }
    return next.toString()
}
