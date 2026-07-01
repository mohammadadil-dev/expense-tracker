package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
 * Bottom sheet for setting/editing/clearing the single overall monthly budget. Saving a blank
 * field or 0 clears the budget instead of erroring — [onSave] is the single entry point for all
 * three outcomes (create, update, clear).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManageSheet(
    overallBudget: BudgetEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var overallText by remember(overallBudget) {
        mutableStateOf(overallBudget?.amount?.let { formatPlainAmount(it) } ?: "")
    }
    var overallError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(text = stringResource(R.string.budget_manage_sheet_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.budget_overall_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = overallText,
                    onValueChange = {
                        overallText = it
                        overallError = false
                    },
                    placeholder = { Text(stringResource(R.string.budget_overall_hint)) },
                    leadingIcon = {
                        // Saudi Riyal renders as the dedicated icon glyph, never the raw "ر.س"
                        // text abbreviation; every other currency keeps its plain symbol since
                        // those already have real, readable glyphs (€, $, ₹, ...).
                        if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
                            Icon(
                                painter = painterResource(R.drawable.ic_saudi_riyal),
                                contentDescription = null
                            )
                        } else {
                            Text(currencySymbol)
                        }
                    },
                    isError = overallError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    val trimmed = overallText.trim()
                    if (trimmed.isEmpty()) {
                        onSave(0.0)
                        onDismiss()
                        return@IconButton
                    }
                    val amount = trimmed.toDoubleOrNull()
                    if (amount == null || amount < 0.0) {
                        overallError = true
                        return@IconButton
                    }
                    onSave(amount)
                    onDismiss()
                }) {
                    Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
                }
            }
            if (overallError) {
                Text(
                    text = stringResource(R.string.error_invalid_budget_amount),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

/** Same plain-number formatting AddEditExpenseSheet uses for its amount field — whole numbers
 * show without a trailing ".0", everything else keeps up to 2 decimal places. */
private fun formatPlainAmount(value: Double): String {
    val rounded = Math.round(value * 100) / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
}
