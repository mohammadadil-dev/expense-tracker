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
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.expensetracker.app.data.PaymentAccountEntity
import com.expensetracker.app.ui.theme.CategoryPalette
import com.expensetracker.app.util.accountDisplayName
import com.expensetracker.app.util.accountEmoji

/**
 * Manage payment accounts (Cash / Bank / Card / user-added) — mirrors [CategoryManageSheet]
 * closely on purpose, same interaction model users already learned there. Deleting an account
 * never deletes any expense; it only clears that expense's account tag (see
 * [com.expensetracker.app.data.ExpenseRepository.deleteAccount]), so there's no "must keep
 * one" restriction the way categories have.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentAccountManageSheet(
    accounts: List<PaymentAccountEntity>,
    onDismiss: () -> Unit,
    onRename: (PaymentAccountEntity, String) -> Unit,
    onRecolor: (PaymentAccountEntity, String) -> Unit,
    onAddAccount: (String, String) -> Unit,
    onDeleteAccount: (PaymentAccountEntity) -> Unit
) {
    var editingId by remember { mutableStateOf<Long?>(null) }
    var editingName by remember { mutableStateOf("") }
    var newAccountName by remember { mutableStateOf("") }
    var newAccountNameError by remember { mutableStateOf(false) }
    var accountPendingDelete by remember { mutableStateOf<PaymentAccountEntity?>(null) }
    val displayNames = accounts.associate { it.id to accountDisplayName(it) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Text(text = stringResource(R.string.manage_payment_accounts), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.payment_accounts_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                items(accounts, key = { it.id }) { account ->
                    val isEditing = editingId == account.id
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
                                        onRename(account, editingName.trim())
                                    }
                                    editingId = null
                                }) {
                                    Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
                                }
                            } else {
                                Text(accountEmoji(account.type), style = MaterialTheme.typography.bodyLarge)
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            color = runCatching { Color(android.graphics.Color.parseColor(account.colorHex)) }
                                                .getOrDefault(MaterialTheme.colorScheme.primary),
                                            shape = CircleShape
                                        )
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = displayNames[account.id] ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    editingId = account.id
                                    editingName = displayNames[account.id] ?: ""
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                                }
                                IconButton(onClick = { accountPendingDelete = account }) {
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
                                            .clickable { onRecolor(account, color.toAccountHexString()) }
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
                    value = newAccountName,
                    onValueChange = {
                        newAccountName = it
                        newAccountNameError = false
                    },
                    placeholder = { Text(stringResource(R.string.new_account_name_hint)) },
                    isError = newAccountNameError,
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    if (newAccountName.isBlank()) {
                        newAccountNameError = true
                        return@IconButton
                    }
                    val color = CategoryPalette[accounts.size % CategoryPalette.size]
                    onAddAccount(newAccountName.trim(), color.toAccountHexString())
                    newAccountName = ""
                }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_payment_account))
                }
            }
            if (newAccountNameError) {
                Text(
                    text = stringResource(R.string.enter_account_name),
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

    accountPendingDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { accountPendingDelete = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_payment_account_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteAccount(account)
                    accountPendingDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { accountPendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

private fun Color.toAccountHexString(): String {
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)
    return String.format("#%02X%02X%02X", r, g, b)
}
