package com.expensetracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.ui.components.AddEditDebtSheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.BackgroundScrollSignal
import com.expensetracker.app.ui.components.DebtListItem
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.RecordPaymentSheet
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.util.DebtInsights
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.viewmodel.ExpenseViewModel
import kotlin.math.abs

/**
 * Debts & Loans screen — both directions (money the user owes, and money owed to the user)
 * live in one list, split into three sections: active "you owe", active "owed to you", and
 * settled. A gradient header card shows the net position across everything at a glance.
 * Mirrors Settings' TopAppBar-with-back-button shell and Dashboard's FAB-to-add pattern.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtsScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val debts by viewModel.debts.collectAsState()
    val debtPayments by viewModel.debtPayments.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val locale = LocalConfiguration.current.locales[0]

    val paymentsByDebt = remember(debtPayments) { debtPayments.groupBy { it.debtId } }
    val progressList = remember(debts, debtPayments) {
        debts.map { debt -> DebtInsights.computeProgress(debt, paymentsByDebt[debt.id].orEmpty()) }
    }
    val netPosition = remember(debts, debtPayments) { DebtInsights.computeNetPosition(debts, debtPayments) }

    val owedByUser = progressList.filter { it.debt.direction == DebtEntity.DIRECTION_OWE && !it.debt.isClosed }
    val owedToUser = progressList.filter { it.debt.direction == DebtEntity.DIRECTION_OWED && !it.debt.isClosed }
    val settled = progressList.filter { it.debt.isClosed }

    var expandedDebtId by remember { mutableStateOf<Long?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var editingDebt by remember { mutableStateOf<DebtEntity?>(null) }
    var paymentSheetDebt by remember { mutableStateOf<DebtEntity?>(null) }
    var debtPendingDelete by remember { mutableStateOf<DebtEntity?>(null) }

    val scrollState = rememberScrollState()
    // Same parallax-feed convention as Dashboard/Settings — a rough scroll cue for the shared
    // animated background, not a layout calculation.
    LaunchedEffect(scrollState.value) {
        BackgroundScrollSignal.pixels.floatValue = scrollState.value.toFloat()
    }

    // Debts gets its own touch-reactive background trio — red/green (the two directions) plus
    // indigo, distinct from Dashboard's violet/indigo/green and Settings' cyan/pink/teal.
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBlobBackground(
            blobColors = listOf(DangerRed, SuccessGreen, AccentIndigo),
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.debts_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    editingDebt = null
                    showAddSheet = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.debt_add_title))
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                NetPositionHeader(
                    netPosition = netPosition,
                    currencySymbol = currencySymbol,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                if (debts.isEmpty()) {
                    EmptyDebtsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 48.dp)
                    )
                } else {
                    if (owedByUser.isNotEmpty()) {
                        SectionHeader(stringResource(R.string.debts_section_you_owe))
                        owedByUser.forEach { progress ->
                            DebtListItem(
                                progress = progress,
                                payments = paymentsByDebt[progress.debt.id].orEmpty(),
                                currencySymbol = currencySymbol,
                                locale = locale,
                                expanded = expandedDebtId == progress.debt.id,
                                onToggleExpand = {
                                    expandedDebtId = if (expandedDebtId == progress.debt.id) null else progress.debt.id
                                },
                                onRecordPayment = { paymentSheetDebt = progress.debt },
                                onEdit = { editingDebt = progress.debt; showAddSheet = true },
                                onDelete = { debtPendingDelete = progress.debt },
                                onToggleClosed = { viewModel.setDebtClosed(progress.debt, !progress.debt.isClosed) },
                                onDeletePayment = { payment -> viewModel.deleteDebtPayment(payment) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                    if (owedToUser.isNotEmpty()) {
                        SectionHeader(stringResource(R.string.debts_section_owed_to_you))
                        owedToUser.forEach { progress ->
                            DebtListItem(
                                progress = progress,
                                payments = paymentsByDebt[progress.debt.id].orEmpty(),
                                currencySymbol = currencySymbol,
                                locale = locale,
                                expanded = expandedDebtId == progress.debt.id,
                                onToggleExpand = {
                                    expandedDebtId = if (expandedDebtId == progress.debt.id) null else progress.debt.id
                                },
                                onRecordPayment = { paymentSheetDebt = progress.debt },
                                onEdit = { editingDebt = progress.debt; showAddSheet = true },
                                onDelete = { debtPendingDelete = progress.debt },
                                onToggleClosed = { viewModel.setDebtClosed(progress.debt, !progress.debt.isClosed) },
                                onDeletePayment = { payment -> viewModel.deleteDebtPayment(payment) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                    if (settled.isNotEmpty()) {
                        SectionHeader(stringResource(R.string.debts_section_settled))
                        settled.forEach { progress ->
                            DebtListItem(
                                progress = progress,
                                payments = paymentsByDebt[progress.debt.id].orEmpty(),
                                currencySymbol = currencySymbol,
                                locale = locale,
                                expanded = expandedDebtId == progress.debt.id,
                                onToggleExpand = {
                                    expandedDebtId = if (expandedDebtId == progress.debt.id) null else progress.debt.id
                                },
                                onRecordPayment = { paymentSheetDebt = progress.debt },
                                onEdit = { editingDebt = progress.debt; showAddSheet = true },
                                onDelete = { debtPendingDelete = progress.debt },
                                onToggleClosed = { viewModel.setDebtClosed(progress.debt, !progress.debt.isClosed) },
                                onDeletePayment = { payment -> viewModel.deleteDebtPayment(payment) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Scaffold's innerPadding doesn't reserve space for the floating FAB — without
                // this the last card's action row ends up underneath it (same fix as Dashboard).
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }

    if (showAddSheet) {
        AddEditDebtSheet(
            existing = editingDebt,
            currencySymbol = currencySymbol,
            onDismiss = { showAddSheet = false },
            onSave = { id, name, direction, principal, rate, payment, startDate, notes ->
                viewModel.saveDebt(id, name, direction, principal, rate, payment, startDate, notes) {
                    showAddSheet = false
                }
            }
        )
    }

    paymentSheetDebt?.let { debt ->
        RecordPaymentSheet(
            debt = debt,
            currencySymbol = currencySymbol,
            onDismiss = { paymentSheetDebt = null },
            onSave = { amount, date, note ->
                viewModel.recordDebtPayment(debt, amount, date, note) { paymentSheetDebt = null }
            }
        )
    }

    debtPendingDelete?.let { debt ->
        AlertDialog(
            onDismissRequest = { debtPendingDelete = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_debt_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDebt(debt) { debtPendingDelete = null }
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { debtPendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun NetPositionHeader(
    netPosition: DebtInsights.NetPosition,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val accentColor = when {
        netPosition.net > 0.0 -> SuccessGreen
        netPosition.net < 0.0 -> DangerRed
        else -> AccentIndigo
    }
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.25f),
                spotColor = accentColor.copy(alpha = 0.35f)
            )
            .clip(shape)
            .drawBehind {
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.7f)),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    )
                }
            }
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.debts_net_title),
                style = MaterialTheme.typography.labelLarge,
                color = OnAccent.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            MoneyText(
                formatted = Formatters.money(abs(netPosition.net), currencySymbol),
                style = MaterialTheme.typography.headlineMedium,
                color = OnAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    when {
                        netPosition.net > 0.0 -> R.string.debts_net_owed_to_you
                        netPosition.net < 0.0 -> R.string.debts_net_you_owe
                        else -> R.string.debts_net_settled
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = OnAccent.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun EmptyDebtsState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.AccountBalance,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.debts_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.debts_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}
