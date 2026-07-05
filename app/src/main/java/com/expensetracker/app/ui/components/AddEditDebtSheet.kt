package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.DateUtils

/**
 * Bottom sheet for creating or editing a single debt/loan, in either direction. Mirrors
 * [AddEditExpenseSheet]'s field-by-field OutlinedTextField + [CalendarDatePickerDialog] pattern.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDebtSheet(
    existing: DebtEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        id: Long?,
        name: String,
        direction: String,
        principal: Double,
        interestRatePercent: Double,
        minimumPayment: Double,
        startDate: String,
        notes: String?,
        loanType: String?
    ) -> Unit
) {
    var direction by remember { mutableStateOf(existing?.direction ?: DebtEntity.DIRECTION_OWE) }
    var loanType by remember {
        mutableStateOf(existing?.loanType?.let { if (it in DebtEntity.ALL_TYPES) it else DebtEntity.TYPE_OTHER })
    }
    var customLoanTypeName by remember {
        mutableStateOf(existing?.loanType?.takeIf { it !in DebtEntity.ALL_TYPES } ?: "")
    }
    var loanTypeExpanded by remember { mutableStateOf(false) }
    var principalText by remember { mutableStateOf(existing?.principal?.let { formatPlainDebtAmount(it) } ?: "") }
    var rateText by remember {
        mutableStateOf(existing?.interestRatePercent?.let { if (it == 0.0) "" else formatPlainDebtAmount(it) } ?: "")
    }
    var paymentText by remember { mutableStateOf(existing?.minimumPayment?.let { formatPlainDebtAmount(it) } ?: "") }
    var startDateIso by remember { mutableStateOf(existing?.startDate ?: DateUtils.todayIso()) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    var loanTypeError by remember { mutableStateOf(false) }
    var principalError by remember { mutableStateOf(false) }
    var rateError by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]
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
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(if (existing == null) R.string.debt_add_title else R.string.debt_edit_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.debt_direction_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DirectionChip(
                    label = stringResource(R.string.debt_direction_owe),
                    selected = direction == DebtEntity.DIRECTION_OWE,
                    onClick = { direction = DebtEntity.DIRECTION_OWE },
                    modifier = Modifier.weight(1f)
                )
                DirectionChip(
                    label = stringResource(R.string.debt_direction_owed),
                    selected = direction == DebtEntity.DIRECTION_OWED,
                    onClick = { direction = DebtEntity.DIRECTION_OWED },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = loanTypeExpanded,
                onExpandedChange = { loanTypeExpanded = it; if (it) loanTypeError = false }
            ) {
                val loanTypeDisplay = loanType?.let { type ->
                    "${DebtEntity.loanTypeEmoji(type)} ${stringResource(loanTypeStringRes(type))}"
                } ?: ""
                OutlinedTextField(
                    value = loanTypeDisplay,
                    onValueChange = {},
                    readOnly = true,
                    isError = loanTypeError,
                    label = { Text(stringResource(R.string.debt_loan_type_label)) },
                    placeholder = { Text(stringResource(R.string.loan_type_select_hint)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = loanTypeExpanded) },
                    supportingText = if (loanTypeError) {
                        { Text(stringResource(R.string.error_loan_type_required)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = loanTypeExpanded,
                    onDismissRequest = { loanTypeExpanded = false }
                ) {
                    DebtEntity.ALL_TYPES.forEach { type ->
                        DropdownMenuItem(
                            text = { Text("${DebtEntity.loanTypeEmoji(type)} ${stringResource(loanTypeStringRes(type))}") },
                            onClick = { loanType = type; loanTypeExpanded = false; loanTypeError = false },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            if (loanType == DebtEntity.TYPE_OTHER) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customLoanTypeName,
                    onValueChange = { customLoanTypeName = it },
                    label = { Text(stringResource(R.string.loan_type_other_name_label)) },
                    placeholder = { Text(stringResource(R.string.loan_type_other_name_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = principalText,
                onValueChange = { principalText = it; principalError = false },
                label = { Text(stringResource(R.string.debt_principal_label)) },
                placeholder = { Text(stringResource(R.string.debt_principal_hint)) },
                leadingIcon = { CurrencyLeadingIcon(currencySymbol) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = principalError,
                supportingText = if (principalError) {
                    { Text(stringResource(R.string.error_invalid_amount)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = rateText,
                onValueChange = { rateText = it; rateError = false },
                label = { Text(stringResource(R.string.debt_rate_label)) },
                placeholder = { Text(stringResource(R.string.debt_rate_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = rateError,
                supportingText = if (rateError) {
                    { Text(stringResource(R.string.error_debt_rate_invalid)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = paymentText,
                onValueChange = { paymentText = it; paymentError = false },
                label = { Text(stringResource(R.string.debt_payment_label)) },
                placeholder = { Text(stringResource(R.string.debt_payment_hint)) },
                leadingIcon = { CurrencyLeadingIcon(currencySymbol) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = paymentError,
                supportingText = if (paymentError) {
                    { Text(stringResource(R.string.error_debt_payment_invalid)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.debt_start_date_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = DateUtils.formatExpenseDate(startDateIso, locale),
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
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.debt_notes_label)) },
                placeholder = { Text(stringResource(R.string.debt_notes_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    var hasError = false
                    if (loanType == null) {
                        loanTypeError = true
                        hasError = true
                    }
                    val principal = principalText.replace(",", "").toDoubleOrNull()
                    if (principal == null || principal <= 0.0) {
                        principalError = true
                        hasError = true
                    }
                    val rate = rateText.trim().ifEmpty { "0" }.replace(",", "").toDoubleOrNull()
                    if (rate == null || rate < 0.0) {
                        rateError = true
                        hasError = true
                    }
                    val payment = paymentText.replace(",", "").toDoubleOrNull()
                    if (payment == null || payment < 0.0) {
                        paymentError = true
                        hasError = true
                    }
                    if (hasError || principal == null || rate == null || payment == null) return@Button
                    val finalLoanType = if (loanType == DebtEntity.TYPE_OTHER && customLoanTypeName.isNotBlank())
                        customLoanTypeName.trim()
                    else
                        loanType
                    val derivedName = when (loanType) {
                        DebtEntity.TYPE_PERSONAL  -> "Personal Loan"
                        DebtEntity.TYPE_HOME      -> "Home Loan"
                        DebtEntity.TYPE_CAR       -> "Car Loan"
                        DebtEntity.TYPE_EDUCATION -> "Education Loan"
                        DebtEntity.TYPE_GOLD      -> "Gold Loan"
                        DebtEntity.TYPE_BUSINESS  -> "Business Loan"
                        DebtEntity.TYPE_INFORMAL  -> "Borrowed from Person"
                        DebtEntity.TYPE_OTHER     -> customLoanTypeName.trim().ifEmpty { "Other Loan" }
                        else                      -> finalLoanType ?: "Loan"
                    }
                    onSave(
                        existing?.id,
                        derivedName,
                        direction,
                        principal,
                        rate,
                        payment,
                        startDateIso,
                        notes.trim().ifEmpty { null },
                        finalLoanType
                    )
                }) {
                    Text(stringResource(if (existing == null) R.string.add else R.string.update))
                }
            }
        }
    }

    if (showDatePicker) {
        CalendarDatePickerDialog(
            initialDateIso = startDateIso,
            locale = locale,
            onDismiss = { showDatePicker = false },
            onConfirm = { newDateIso ->
                startDateIso = newDateIso
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun CurrencyLeadingIcon(currencySymbol: String) {
    if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
        Icon(painter = painterResource(R.drawable.ic_saudi_riyal), contentDescription = null)
    } else {
        Text(currencySymbol)
    }
}

@Composable
private fun DirectionChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) AccentIndigo else AccentIndigoLight)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) OnAccent else TextSecondary
        )
    }
}

/** Maps a loan type constant to its string resource ID. */
private fun loanTypeStringRes(type: String): Int = when (type) {
    DebtEntity.TYPE_PERSONAL  -> R.string.loan_type_personal
    DebtEntity.TYPE_HOME      -> R.string.loan_type_home
    DebtEntity.TYPE_CAR       -> R.string.loan_type_car
    DebtEntity.TYPE_EDUCATION -> R.string.loan_type_education
    DebtEntity.TYPE_GOLD      -> R.string.loan_type_gold
    DebtEntity.TYPE_BUSINESS  -> R.string.loan_type_business
    DebtEntity.TYPE_INFORMAL  -> R.string.loan_type_informal
    else                      -> R.string.loan_type_other
}

/** Same plain-number formatting as the other Add/Edit sheets — whole numbers show without a
 * trailing ".0", everything else keeps up to 2 decimal places. */
private fun formatPlainDebtAmount(value: Double): String {
    val rounded = Math.round(value * 100) / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
}
