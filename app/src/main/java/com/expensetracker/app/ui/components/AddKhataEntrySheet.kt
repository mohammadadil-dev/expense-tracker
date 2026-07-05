package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKhataEntrySheet(
    initialType: String = KhataEntryEntity.TYPE_CREDIT,
    onSave: (amount: Double, note: String, date: String, type: String) -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    var amountText  by remember { mutableStateOf("") }
    var note        by remember { mutableStateOf("") }
    var date        by remember { mutableStateOf(DateUtils.todayIso()) }
    var entryType   by remember { mutableStateOf(initialType) }
    var amountError by remember { mutableStateOf(false) }
    var dateError   by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.khata_add_entry),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            // Credit / Payment toggle buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isCredit = entryType == KhataEntryEntity.TYPE_CREDIT
                Button(
                    onClick = { entryType = KhataEntryEntity.TYPE_CREDIT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCredit) DangerRed else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor   = if (isCredit) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.khata_credit_label)) }

                Button(
                    onClick = { entryType = KhataEntryEntity.TYPE_PAYMENT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isCredit) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor   = if (!isCredit) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.khata_payment_label)) }
            }

            Spacer(Modifier.height(16.dp))

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it; amountError = false },
                label = { Text(stringResource(R.string.khata_amount_hint)) },
                singleLine = true,
                isError = amountError,
                supportingText = if (amountError) {
                    { Text(stringResource(R.string.error_invalid_rate)) }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.khata_note_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Date
            OutlinedTextField(
                value = date,
                onValueChange = { date = it; dateError = false },
                label = { Text(stringResource(R.string.khata_entry_date)) },
                singleLine = true,
                isError = dateError,
                supportingText = if (dateError) {
                    { Text(stringResource(R.string.error_invalid_date)) }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0.0) { amountError = true; return@Button }
                        val parsedDate = try { java.time.LocalDate.parse(date); date } catch (e: Exception) { null }
                        if (parsedDate == null) { dateError = true; return@Button }
                        onSave(amount, note.trim(), parsedDate, entryType)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) { Text(stringResource(R.string.add)) }
            }
        }
    }
}
