package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.BudgetEntity
import com.expensetracker.app.data.CurrencyLocaleMapper

/**
 * Bottom sheet for managing the overall monthly budget.
 *
 * The ✓ button saves the entered amount. Clearing the field and tapping ✓ removes the budget.
 * [onSave] is called with (categoryId = null, amount) for the overall budget.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManageSheet(
    overallBudget: BudgetEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (categoryId: Long?, amount: Double) -> Unit
) {
    var overallText by remember(overallBudget) {
        mutableStateOf(overallBudget?.amount?.let { formatPlainAmount(it) } ?: "")
    }
    var overallError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.budget_manage_sheet_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.budget_set_prompt),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(Modifier.height(20.dp))

            Text(
                stringResource(R.string.budget_overall_label),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(6.dp))
            BudgetInputRow(
                text           = overallText,
                onTextChange   = { overallText = it; overallError = false },
                currencySymbol = currencySymbol,
                isError        = overallError,
                onSave         = {
                    val trimmed = overallText.trim()
                    if (trimmed.isEmpty()) { onSave(null, 0.0); onDismiss(); return@BudgetInputRow }
                    val amount = trimmed.toDoubleOrNull()
                    if (amount == null || amount < 0.0) { overallError = true; return@BudgetInputRow }
                    onSave(null, amount)
                    onDismiss()
                }
            )
            if (overallError) {
                Text(
                    text = stringResource(R.string.error_invalid_budget_amount),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

// ─── Shared input row ─────────────────────────────────────────────────────────

@Composable
private fun BudgetInputRow(
    text: String,
    onTextChange: (String) -> Unit,
    currencySymbol: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onSave: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value         = text,
            onValueChange = onTextChange,
            placeholder   = { Text(stringResource(R.string.budget_overall_hint)) },
            leadingIcon   = {
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
            isError         = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier        = Modifier.weight(1f),
            singleLine      = true
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onSave) {
            Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
        }
    }
}

private fun formatPlainAmount(value: Double): String {
    val rounded = Math.round(value * 100) / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
}
