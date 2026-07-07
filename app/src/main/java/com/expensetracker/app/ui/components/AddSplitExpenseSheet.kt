package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.SplitMemberEntity
import com.expensetracker.app.data.SplitRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSplitExpenseSheet(
    members: List<SplitMemberEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (description: String, amount: Double, paidByMemberId: Long, splitAmongIds: List<Long>) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paidByMemberId by remember { mutableStateOf(members.firstOrNull { it.isMe }?.id ?: members.firstOrNull()?.id ?: 0L) }
    var paidByDropdownExpanded by remember { mutableStateOf(false) }

    // Split-among: all members selected by default
    val splitAmongIds = remember { mutableStateListOf<Long>().apply { addAll(members.map { it.id }) } }

    val scrollState = rememberScrollState()
    val memberMap = remember(members) { members.associateBy { it.id } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
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
                stringResource(R.string.split_add_expense),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.split_expense_description)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(stringResource(R.string.split_expense_amount)) },
                prefix = { CurrencyPrefix(currencySymbol) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(16.dp))

            // Paid by
            Text(stringResource(R.string.split_paid_by), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = paidByDropdownExpanded,
                onExpandedChange = { paidByDropdownExpanded = !paidByDropdownExpanded }
            ) {
                val paidByMember = memberMap[paidByMemberId]
                OutlinedTextField(
                    value = paidByMember?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Filled.ExpandMore, contentDescription = null)
                    },
                    leadingIcon = {
                        if (paidByMember != null) {
                            MemberAvatar(member = paidByMember, size = 24)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = paidByDropdownExpanded,
                    onDismissRequest = { paidByDropdownExpanded = false }
                ) {
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    MemberAvatar(member = member, size = 28)
                                    Text(member.name)
                                }
                            },
                            onClick = {
                                paidByMemberId = member.id
                                paidByDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Split among
            Text(stringResource(R.string.split_split_among), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.split_split_among_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            members.forEach { member ->
                val checked = splitAmongIds.contains(member.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (checked) {
                                // Don't allow unchecking the last member
                                if (splitAmongIds.size > 1) splitAmongIds.remove(member.id)
                            } else {
                                splitAmongIds.add(member.id)
                            }
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = null // handled by Row click
                    )
                    MemberAvatar(member = member, size = 32)
                    Text(member.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))

                    // Preview share amount
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val share = if (splitAmongIds.size > 0 && checked) amount / splitAmongIds.size else 0.0
                    if (share > 0) {
                        MoneyText(
                            formatted = "$currencySymbol${"%.2f".format(share)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            val amount = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = {
                    if (description.isNotBlank() && amount > 0 && splitAmongIds.isNotEmpty()) {
                        onSave(description.trim(), amount, paidByMemberId, splitAmongIds.toList())
                    }
                },
                enabled = description.isNotBlank() && amount > 0 && splitAmongIds.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.split_save_expense))
            }
        }
    }
}

/**
 * Renders the currency prefix inside a TextField. For SAR ("ر.س") it shows the official
 * riyal vector icon so the user never sees the raw Arabic abbreviation in the input field.
 * All other currencies render as plain text as before.
 */
@Composable
private fun CurrencyPrefix(currencySymbol: String) {
    if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) {
        Icon(
            painter = painterResource(R.drawable.ic_saudi_riyal),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Text(currencySymbol, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MemberAvatar(member: SplitMemberEntity, size: Int) {
    val color = try { Color(android.graphics.Color.parseColor(member.colorHex)) }
    catch (_: Exception) { Color(0xFF4CAF50) }
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            member.name.take(1).uppercase(),
            color = Color.White,
            style = if (size <= 24) MaterialTheme.typography.labelSmall
                    else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
