package com.expensetracker.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.expensetracker.app.ui.components.BottomNavVisibility
import com.expensetracker.app.ui.components.DebtListItem
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.RecordPaymentSheet
import com.expensetracker.app.ui.components.rememberIsScrollingUp
import androidx.compose.ui.text.font.FontWeight
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.ui.theme.TextSecondary
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

    var heroVisible     by remember { mutableStateOf(false) }
    var expandedDebtId by remember { mutableStateOf<Long?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var editingDebt by remember { mutableStateOf<DebtEntity?>(null) }
    var paymentSheetDebt by remember { mutableStateOf<DebtEntity?>(null) }
    var debtPendingDelete by remember { mutableStateOf<DebtEntity?>(null) }

    LaunchedEffect(Unit) { heroVisible = true }

    val scrollState = rememberScrollState()
    // Same parallax-feed convention as Dashboard/Settings — a rough scroll cue for the shared
    // animated background, not a layout calculation.
    LaunchedEffect(scrollState.value) {
        BackgroundScrollSignal.pixels.floatValue = scrollState.value.toFloat()
    }

    // Bottom nav bar + this screen's own FAB hide together while scrolling down, and come back
    // on scroll-up/stop — same cross-screen pattern as Dashboard/Khata/Splits.
    val isScrollingUp by scrollState.rememberIsScrollingUp()
    LaunchedEffect(isScrollingUp) {
        BottomNavVisibility.visible = isScrollingUp
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
            // ── No TopAppBar — title lives inline with the content ──
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isScrollingUp,
                    enter = slideInVertically(tween(220)) { it } + fadeIn(tween(220)),
                    exit = slideOutVertically(tween(180)) { it } + fadeOut(tween(150))
                ) {
                FloatingActionButton(onClick = {
                    editingDebt = null
                    showAddSheet = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.debt_add_title))
                }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                // ── Animated hero header ───────────────────────────────────────
                AnimatedVisibility(
                    visible = heroVisible,
                    enter = slideInVertically(tween(500, easing = FastOutSlowInEasing)) { -it / 2 } +
                            fadeIn(tween(500))
                ) {
                    DebtsHeroHeader(
                        netPosition    = netPosition,
                        owedByUserCount = owedByUser.size,
                        owedToUserCount = owedToUser.size,
                        currencySymbol = currencySymbol
                    )
                }

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
            onSave = { id, name, direction, principal, rate, payment, startDate, notes, loanType ->
                viewModel.saveDebt(id, name, direction, principal, rate, payment, startDate, notes, loanType) {
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

// ── Debts hero header (animated, mirrors KhataHeroHeader style) ────────────────

@Composable
private fun DebtsHeroHeader(
    netPosition: DebtInsights.NetPosition,
    owedByUserCount: Int,
    owedToUserCount: Int,
    currencySymbol: String
) {
    val allSettled    = netPosition.net == 0.0 &&
                        netPosition.totalOwedByUser == 0.0 &&
                        netPosition.totalOwedToUser == 0.0
    val netIsPositive = netPosition.net >= 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 12.dp)
    ) {
        Text(
            text  = stringResource(R.string.debts_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text  = stringResource(R.string.debts_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(Modifier.height(16.dp))

        // Two stat cards side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DebtStatCard(
                label          = stringResource(R.string.debts_section_you_owe),
                amount         = netPosition.totalOwedByUser,
                count          = owedByUserCount,
                currencySymbol = currencySymbol,
                amountColor    = DangerRed,
                modifier       = Modifier.weight(1f)
            )
            DebtStatCard(
                label          = stringResource(R.string.debts_section_owed_to_you),
                amount         = netPosition.totalOwedToUser,
                count          = owedToUserCount,
                currencySymbol = currencySymbol,
                amountColor    = SuccessGreen,
                modifier       = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Net position badge pill
        Surface(
            shape = RoundedCornerShape(50),
            color = when {
                allSettled    -> SuccessGreen.copy(alpha = 0.10f)
                netIsPositive -> SuccessGreen.copy(alpha = 0.10f)
                else          -> DangerRed.copy(alpha = 0.10f)
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (allSettled || netIsPositive) Icons.Filled.TrendingUp
                                  else Icons.Filled.TrendingDown,
                    contentDescription = null,
                    tint     = if (allSettled || netIsPositive) SuccessGreen else DangerRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                MoneyText(
                    formatted = when {
                        allSettled    -> stringResource(R.string.net_all_settled)
                        netIsPositive -> stringResource(R.string.net_in_favour, Formatters.money(abs(netPosition.net), currencySymbol))
                        else          -> stringResource(R.string.net_you_owe_net, Formatters.money(abs(netPosition.net), currencySymbol))
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (allSettled || netIsPositive) SuccessGreen else DangerRed
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Gradient net-position card (kept from original)
        NetPositionHeader(
            netPosition    = netPosition,
            currencySymbol = currencySymbol,
            modifier       = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DebtStatCard(
    label: String,
    amount: Double,
    count: Int,
    currencySymbol: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier  = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            MoneyText(
                formatted = Formatters.money(amount, currencySymbol),
                style     = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color     = amountColor
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = amountColor.copy(alpha = 0.10f)
            ) {
                Text(
                    text  = "$count ${if (count == 1) stringResource(R.string.label_debt_singular) else stringResource(R.string.label_debt_plural)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = amountColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ── Net position gradient card ─────────────────────────────────────────────────

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
