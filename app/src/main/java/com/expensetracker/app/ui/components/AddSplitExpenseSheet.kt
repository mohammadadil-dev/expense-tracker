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
import kotlin.math.abs

/** How the total amount is divided among the selected members. */
private enum class SplitMode { EQUAL, EXACT, PERCENT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSplitExpenseSheet(
    members: List<SplitMemberEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (description: String, amount: Double, paidByMemberId: Long, splitAmongIds: List<Long>, customShares: Map<Long, Double>?) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paidByMemberId by remember { mutableStateOf(members.firstOrNull { it.isMe }?.id ?: members.firstOrNull()?.id ?: 0L) }
    var paidByDropdownExpanded by remember { mutableStateOf(false) }

    // Split-among: all members selected by default
    val splitAmongIds = remember { mutableStateListOf<Long>().apply { addAll(members.map { it.id }) } }

    var splitMode by remember { mutableStateOf(SplitMode.EQUAL) }
    // Per-member free-text inputs for EXACT (currency amount) and PERCENT (0-100) modes —
    // kept as raw strings (like the main amount field) so users can type/clear freely
    // without a parse error mid-keystroke.
    val exactAmounts  = remember { mutableStateMapOf<Long, String>() }
    val percentInputs = remember { mutableStateMapOf<Long, String>() }

    val scrollState = rememberScrollState()
    val memberMap = remember(members) { members.associateBy { it.id } }
    val amount = amountText.toDoubleOrNull() ?: 0.0

    // Sums used for validation — recomputed on every recomposition (cheap, small lists).
    val exactSum = splitAmongIds.sumOf { exactAmounts[it]?.toDoubleOrNull() ?: 0.0 }
    val percentSum = splitAmongIds.sumOf { percentInputs[it]?.toDoubleOrNull() ?: 0.0 }
    val exactMatches = amount > 0 && abs(exactSum - amount) < 0.01
    val percentMatches = abs(percentSum - 100.0) < 0.01

    val canSave = description.isNotBlank() && amount > 0 && splitAmongIds.isNotEmpty() && when (splitMode) {
        SplitMode.EQUAL   -> true
        SplitMode.EXACT   -> exactMatches
        SplitMode.PERCENT -> percentMatches
    }

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

            // Split among + mode selector
            Text(stringResource(R.string.split_split_among), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            // Equal / Exact / Percentage segmented selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    SplitMode.EQUAL   to R.string.split_mode_equal,
                    SplitMode.EXACT   to R.string.split_mode_exact,
                    SplitMode.PERCENT to R.string.split_mode_percent
                ).forEach { (mode, labelRes) ->
                    val selected = splitMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { splitMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(labelRes),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            if (splitMode == SplitMode.EQUAL) {
                Text(
                    stringResource(R.string.split_split_among_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }

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

                    when (splitMode) {
                        SplitMode.EQUAL -> {
                            val share = if (splitAmongIds.size > 0 && checked) amount / splitAmongIds.size else 0.0
                            if (share > 0) {
                                MoneyText(
                                    formatted = "$currencySymbol${"%.2f".format(share)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        SplitMode.EXACT -> if (checked) {
                            OutlinedTextField(
                                value = exactAmounts[member.id] ?: "",
                                onValueChange = { exactAmounts[member.id] = it },
                                modifier = Modifier.width(96.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                prefix = { Text(currencySymbol, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                        SplitMode.PERCENT -> if (checked) {
                            OutlinedTextField(
                                value = percentInputs[member.id] ?: "",
                                onValueChange = { percentInputs[member.id] = it },
                                modifier = Modifier.width(72.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                suffix = { Text("%", style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
            }

            // Running-total validation hint for Exact/Percentage modes
            if (splitMode == SplitMode.EXACT) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.split_exact_sum_hint,
                        "$currencySymbol${"%.2f".format(exactSum)}",
                        "$currencySymbol${"%.2f".format(amount)}"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (exactMatches) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            } else if (splitMode == SplitMode.PERCENT) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.split_percent_sum_hint, "%.1f".format(percentSum)),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (percentMatches) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!canSave) return@Button
                    val customShares: Map<Long, Double>? = when (splitMode) {
                        SplitMode.EQUAL -> null
                        SplitMode.EXACT -> splitAmongIds.associateWith { exactAmounts[it]?.toDoubleOrNull() ?: 0.0 }
                        SplitMode.PERCENT -> splitAmongIds.associateWith { memberId ->
                            val pct = percentInputs[memberId]?.toDoubleOrNull() ?: 0.0
                            amount * pct / 100.0
                        }
                    }
                    onSave(description.trim(), amount, paidByMemberId, splitAmongIds.toList(), customShares)
                },
                enabled = canSave,
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
