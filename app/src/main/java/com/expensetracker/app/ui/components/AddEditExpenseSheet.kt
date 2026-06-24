package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.categoryDisplayName

/**
 * Optional starting values for a brand-new (not-yet-existing) expense — used by the SMS review
 * queue to open this sheet pre-populated with the parser's best guess, so the user only has to
 * confirm or fix a couple of fields instead of typing the whole thing from scratch. Ignored
 * whenever [AddEditExpenseSheet]'s `existing` parameter is non-null.
 */
data class ExpensePrefill(
    val description: String = "",
    val amount: Double? = null,
    val categoryId: Long? = null,
    val date: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseSheet(
    categories: List<CategoryEntity>,
    existing: ExpenseEntity?,
    defaultDate: String,
    prefill: ExpensePrefill? = null,
    onDismiss: () -> Unit,
    onSave: (id: Long?, categoryId: Long, description: String, amount: Double, date: String) -> Unit
) {
    var selectedCategory by remember {
        val wantedId = existing?.categoryId ?: prefill?.categoryId
        mutableStateOf(categories.firstOrNull { it.id == wantedId } ?: categories.firstOrNull())
    }
    var description by remember { mutableStateOf(existing?.description ?: prefill?.description ?: "") }
    var amountText by remember {
        mutableStateOf(
            existing?.amount?.let { formatPlain(it) } ?: prefill?.amount?.let { formatPlain(it) } ?: ""
        )
    }
    var dateIso by remember { mutableStateOf(existing?.date ?: prefill?.date ?: defaultDate) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResourceCompat(if (existing == null) R.string.add_expense else R.string.edit_expense),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            Text(stringResourceCompat(R.string.category_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { categoryMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.let { categoryDisplayName(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    color = selectedCategory?.colorHex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
                                        ?: MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    categoryDisplayName(category),
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = runCatching { Color(android.graphics.Color.parseColor(category.colorHex)) }
                                                .getOrDefault(MaterialTheme.colorScheme.primary),
                                            shape = CircleShape
                                        )
                                )
                            },
                            onClick = {
                                selectedCategory = category
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResourceCompat(R.string.description_label)) },
                placeholder = { Text(stringResourceCompat(R.string.description_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    amountError = false
                },
                label = { Text(stringResourceCompat(R.string.amount_label)) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError,
                supportingText = if (amountError) {
                    { Text(stringResourceCompat(R.string.error_invalid_amount)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            Text(stringResourceCompat(R.string.date_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = DateUtils.formatExpenseDate(dateIso, locale),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = stringResourceCompat(R.string.select_date))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text(stringResourceCompat(R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val amount = amountText.replace(",", "").toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        amountError = true
                        return@Button
                    }
                    val categoryId = selectedCategory?.id
                    if (categoryId != null) {
                        onSave(existing?.id, categoryId, description.trim(), amount, dateIso)
                    }
                }) {
                    Text(stringResourceCompat(if (existing == null) R.string.add else R.string.update))
                }
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

private fun formatPlain(value: Double): String {
    val rounded = Math.round(value * 100) / 100.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
}

@Composable
private fun stringResourceCompat(id: Int) = androidx.compose.ui.res.stringResource(id)
