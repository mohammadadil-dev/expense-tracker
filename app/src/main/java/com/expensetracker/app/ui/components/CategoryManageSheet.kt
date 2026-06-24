package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.DeleteCategoryResult
import com.expensetracker.app.ui.theme.CategoryPalette
import com.expensetracker.app.util.categoryDisplayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManageSheet(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onRename: (CategoryEntity, String) -> Unit,
    onRecolor: (CategoryEntity, String) -> Unit,
    onAddCategory: (String, String) -> Unit,
    onDeleteCategory: (CategoryEntity, (DeleteCategoryResult) -> Unit) -> Unit
) {
    var editingId by remember { mutableStateOf<Long?>(null) }
    var editingName by remember { mutableStateOf("") }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryNameError by remember { mutableStateOf(false) }
    var categoryPendingDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    var showMustKeepOneWarning by remember { mutableStateOf(false) }
    val displayNames = categories.associate { it.id to categoryDisplayName(it) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Text(text = stringResource(R.string.manage_categories), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                items(categories, key = { it.id }) { category ->
                    val isEditing = editingId == category.id
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            if (isEditing) {
                                OutlinedTextField(
                                    value = editingName,
                                    onValueChange = { editingName = it },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    if (editingName.isNotBlank()) {
                                        onRename(category, editingName.trim())
                                    }
                                    editingId = null
                                }) {
                                    Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            color = runCatching { Color(android.graphics.Color.parseColor(category.colorHex)) }
                                                .getOrDefault(MaterialTheme.colorScheme.primary),
                                            shape = CircleShape
                                        )
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = displayNames[category.id] ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    editingId = category.id
                                    editingName = displayNames[category.id] ?: ""
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                                }
                                IconButton(onClick = { categoryPendingDelete = category }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                                }
                            }
                        }
                        if (isEditing) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CategoryPalette.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(color = color, shape = CircleShape)
                                            .clickable { onRecolor(category, color.toHexString()) }
                                    )
                                }
                            }
                        }
                        Divider()
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = {
                        newCategoryName = it
                        newCategoryNameError = false
                    },
                    placeholder = { Text(stringResource(R.string.new_category_hint)) },
                    isError = newCategoryNameError,
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    if (newCategoryName.isBlank()) {
                        newCategoryNameError = true
                        return@IconButton
                    }
                    val color = CategoryPalette[categories.size % CategoryPalette.size]
                    onAddCategory(newCategoryName.trim(), color.toHexString())
                    newCategoryName = ""
                }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_category))
                }
            }
            if (newCategoryNameError) {
                Text(
                    text = stringResource(R.string.enter_category_name),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.close))
            }
        }
    }

    categoryPendingDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryPendingDelete = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_category_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCategory(category) { result ->
                        if (result is DeleteCategoryResult.MustKeepOne) {
                            showMustKeepOneWarning = true
                        }
                    }
                    categoryPendingDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { categoryPendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showMustKeepOneWarning) {
        AlertDialog(
            onDismissRequest = { showMustKeepOneWarning = false },
            title = { Text(stringResource(R.string.manage_categories)) },
            text = { Text(stringResource(R.string.keep_one_category_warning)) },
            confirmButton = {
                TextButton(onClick = { showMustKeepOneWarning = false }) { Text(stringResource(R.string.ok)) }
            }
        )
    }
}

private fun Color.toHexString(): String {
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)
    return String.format("#%02X%02X%02X", r, g, b)
}
