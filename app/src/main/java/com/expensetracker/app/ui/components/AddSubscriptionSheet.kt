package com.expensetracker.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.RecurringPeriod
import com.expensetracker.app.util.SubscriptionPresets

/**
 * Purpose-built sheet for adding/editing a subscription. Unlike the generic expense sheet (which
 * exposes the full category list — most of which have nothing to do with subscriptions), this
 * leads with a grid of popular services: tap one to fill the name + icon, then set the price and
 * renewal day. Everything is still saved as a recurring expense under the "Subscriptions"
 * category by the caller, so the dashboard's subscription-cost card is unaffected.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSubscriptionSheet(
    existing: ExpenseEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (id: Long?, name: String, amount: Double, billingDay: Int, period: String) -> Unit
) {
    var name by remember { mutableStateOf(existing?.description ?: "") }
    var amountText by remember { mutableStateOf(existing?.amount?.let { formatPlainAmount(it) } ?: "") }
    var amountError by remember { mutableStateOf(false) }
    val todayDay = remember { DateUtils.todayIso().takeLast(2).toIntOrNull()?.coerceIn(1, 31) ?: 1 }
    var billingDay by remember { mutableStateOf(existing?.recurringDayOfMonth ?: todayDay) }
    var period by remember { mutableStateOf(existing?.recurringPeriod ?: RecurringPeriod.MONTHLY) }
    val scrollState = rememberScrollState()

    val parsedAmount = amountText.replace(",", "").trim().toDoubleOrNull()
    val canSave = name.isNotBlank() && parsedAmount != null && parsedAmount > 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(
                    if (existing == null) R.string.subscription_add_title else R.string.subscription_edit_title
                ),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            // ── Service preset grid ──────────────────────────────────────────
            Text(stringResource(R.string.subscription_choose_service), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubscriptionPresets.ALL.forEach { preset ->
                    PresetChip(
                        emoji = preset.emoji,
                        label = preset.name,
                        selected = name.trim().equals(preset.name, ignoreCase = true),
                        onClick = { name = preset.name }
                    )
                }
                // "Custom" clears the name so the user can type their own service.
                PresetChip(
                    emoji = "✏️",
                    label = stringResource(R.string.subscription_custom),
                    selected = false,
                    onClick = { name = "" }
                )
            }
            Spacer(Modifier.height(16.dp))

            // ── Name ─────────────────────────────────────────────────────────
            Text(stringResource(R.string.subscription_name_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(stringResource(R.string.subscription_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ── Amount ───────────────────────────────────────────────────────
            Text(stringResource(R.string.amount_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it; amountError = false },
                leadingIcon = {
                    // SAR renders as the official Riyal glyph icon (matching the rest of the app),
                    // not the "ر.س" text; every other currency shows its plain symbol.
                    if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
                        Icon(
                            painter = painterResource(R.drawable.ic_saudi_riyal),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(currencySymbol, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = amountError,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ── Billing cycle ────────────────────────────────────────────────
            Text(stringResource(R.string.subscription_billing_cycle), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RecurringPeriod.ALL.forEach { p ->
                    FilterChip(
                        selected = period == p,
                        onClick = { period = p },
                        label = { Text(stringResource(cycleLabelRes(p))) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            // ── Renewal day of month ─────────────────────────────────────────
            Text(stringResource(R.string.subscription_billing_day_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (billingDay > 1) billingDay-- }) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                }
                Text(
                    text = billingDay.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(48.dp)
                )
                IconButton(onClick = { if (billingDay < 31) billingDay++ }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = parsedAmount
                    if (amt == null || amt <= 0.0) { amountError = true; return@Button }
                    onSave(existing?.id, name.trim(), amt, billingDay, period)
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun PresetChip(emoji: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) AccentIndigo.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (selected) BorderStroke(1.5.dp, AccentIndigo) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) AccentIndigo else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatPlainAmount(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()

private fun cycleLabelRes(period: String): Int = when (period) {
    RecurringPeriod.QUARTERLY -> R.string.subscription_cycle_quarterly
    RecurringPeriod.HALF_YEARLY -> R.string.subscription_cycle_halfyearly
    RecurringPeriod.YEARLY -> R.string.subscription_cycle_yearly
    else -> R.string.subscription_cycle_monthly
}
