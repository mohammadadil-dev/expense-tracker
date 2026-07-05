package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.categoryDisplayName
import com.expensetracker.app.util.categoryEmoji

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
    onSave: (id: Long?, categoryId: Long, description: String, amount: Double, date: String, isRecurring: Boolean) -> Unit,
    onAddCategory: ((name: String, colorHex: String) -> Unit)? = null
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
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    val quickColors = listOf("#43A047","#E53935","#1E88E5","#FB8C00","#8E24AA","#00ACC1","#D81B60","#6D4C41")
    var newCategoryColor by remember { mutableStateOf(quickColors[0]) }
    // Recurring: pre-populate from existing record (templates have isRecurring=true; auto-copies
    // have isRecurring=false, so editing a copy keeps the toggle off by default — correct).
    var isRecurring by remember { mutableStateOf(existing?.isRecurring ?: false) }
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
                        Text(
                            text = categoryEmoji(selectedCategory?.nameKey),
                            style = MaterialTheme.typography.bodyLarge
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
                                Text(
                                    text = categoryEmoji(category.nameKey),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = {
                                selectedCategory = category
                                categoryMenuExpanded = false
                            }
                        )
                    }
                    if (onAddCategory != null) {
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResourceCompat(R.string.add_category),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                categoryMenuExpanded = false
                                newCategoryName = ""
                                newCategoryColor = quickColors[0]
                                showAddCategoryDialog = true
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

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f))
            Spacer(Modifier.height(12.dp))

            // Recurring toggle — only shown when adding/editing a normal expense (not an
            // auto-generated copy, which has recurringSourceId set and isRecurring=false).
            val isAutoCopy = existing?.recurringSourceId != null
            if (!isAutoCopy) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isRecurring) Modifier else Modifier
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Repeat,
                            contentDescription = null,
                            tint = if (isRecurring) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                stringResourceCompat(R.string.repeat_monthly_label),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (isRecurring) {
                                Text(
                                    stringResourceCompat(R.string.repeat_monthly_hint),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                    Switch(checked = isRecurring, onCheckedChange = { isRecurring = it })
                }
                Spacer(Modifier.height(12.dp))
            }

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
                        onSave(existing?.id, categoryId, description.trim(), amount, dateIso, isRecurring)
                    }
                }) {
                    Text(stringResourceCompat(if (existing == null) R.string.add else R.string.update))
                }
            }
        }
    }

    if (showAddCategoryDialog && onAddCategory != null) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text(stringResourceCompat(R.string.add_category)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text(stringResourceCompat(R.string.new_category_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        quickColors.forEach { hex ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (e: Exception) { Color.Gray }
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(color, CircleShape)
                                    .then(
                                        if (hex == newCategoryColor)
                                            Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { newCategoryColor = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newCategoryName.trim()
                        if (name.isNotBlank()) {
                            onAddCategory(name, newCategoryColor)
                            showAddCategoryDialog = false
                        }
                    }
                ) { Text(stringResourceCompat(R.string.add)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text(stringResourceCompat(R.string.cancel))
                }
            }
        )
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
