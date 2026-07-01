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
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.util.DateUtils

/**
 * Bottom sheet for recording a single payment against [debt] — a repayment if the user owes
 * it, a collection if it's owed to the user. Mirrors [BudgetManageSheet]'s compact
 * field-plus-Check-icon save pattern, with an extra date field and optional note.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentSheet(
    debt: DebtEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, date: String, note: String?) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var dateIso by remember { mutableStateOf(DateUtils.todayIso()) }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]
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
            Text(
                text = stringResource(R.string.debt_record_payment),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = debt.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.amount_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    leadingIcon = {
                        if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
                            Icon(painter = painterResource(R.drawable.ic_saudi_riyal), contentDescription = null)
                        } else {
                            Text(currencySymbol)
                        }
                    },
                    isError = amountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    val amount = amountText.replace(",", "").toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        amountError = true
                        return@IconButton
                    }
                    onSave(amount, dateIso, note.trim().ifEmpty { null })
                }) {
                    Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
                }
            }
            if (amountError) {
                Text(
                    text = stringResource(R.string.error_invalid_amount),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.date_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = DateUtils.formatExpenseDate(dateIso, locale),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = stringResource(R.string.select_date))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.payment_note_label)) },
                placeholder = { Text(stringResource(R.string.payment_note_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.close))
            }
        }
    }

    if (showDatePicker) {
        CalendarDatePickerDialog(
            initialDateIso = dateIso,
            locale = locale,
            onDismiss = { showDatePicker = false },
            onConfirm = { newDateIso ->
                dateIso = newDateIso
                showDatePicker = false
            }
        )
    }
}
