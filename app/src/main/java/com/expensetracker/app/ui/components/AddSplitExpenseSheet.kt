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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.ItemDraft
import com.expensetracker.app.data.SplitMemberEntity
import com.expensetracker.app.util.SplitMath
import kotlin.math.abs

/** How the total amount is divided among the selected members. */
private enum class SplitMode { EQUAL, EXACT, PERCENT, ITEMIZED }

/** One in-progress line item in ITEMIZED mode. [key] is a stable identity for Compose list
 *  diffing/removal — unrelated to any database id (nothing is persisted until Save). */
private data class ItemRow(
    val key: Int,
    val name: String = "",
    val amountText: String = "",
    val memberIds: List<Long> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSplitExpenseSheet(
    members: List<SplitMemberEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        description: String,
        amount: Double,
        paidByMemberId: Long,
        splitAmongIds: List<Long>,
        customShares: Map<Long, Double>?,
        items: List<ItemDraft>?
    ) -> Unit
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

    // ITEMIZED mode — a receipt broken into line items, each assigned to whoever had them.
    // Starts with one blank row so the "+ Add item" affordance is obvious immediately.
    var itemRows by remember { mutableStateOf(listOf(ItemRow(key = 0))) }
    var nextItemKey by remember { mutableStateOf(1) }

    // Clearing focus before an item row (with its own TextFields) is removed from composition
    // avoids a known Android/Compose platform race — a focused row's pending scroll-into-view
    // request can otherwise fire after the row's View is detached, throwing
    // "IllegalArgumentException: parameter must be a descendant of this view" during draw.
    val focusManager: FocusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()
    val memberMap = remember(members) { members.associateBy { it.id } }

    val itemsTotal = itemRows.sumOf { it.amountText.toDoubleOrNull() ?: 0.0 }
    val amount = if (splitMode == SplitMode.ITEMIZED) itemsTotal else (amountText.toDoubleOrNull() ?: 0.0)

    // Sums used for validation — recomputed on every recomposition (cheap, small lists).
    val exactSum = splitAmongIds.sumOf { exactAmounts[it]?.toDoubleOrNull() ?: 0.0 }
    val percentSum = splitAmongIds.sumOf { percentInputs[it]?.toDoubleOrNull() ?: 0.0 }
    // Per-member EQUAL-mode preview — must match SplitRepository.addExpense's actual save logic
    // (SplitMath.splitEvenly, not amount / size) or this preview lies about what gets charged:
    // plain division shows the same rounded amount for everyone even when it doesn't actually
    // add up to the bill (e.g. SAR 100 / 3 previewing as 33.33 × 3 = 99.99).
    val equalShareByMemberId: Map<Long, Double> = if (splitMode == SplitMode.EQUAL && splitAmongIds.isNotEmpty())
        splitAmongIds.zip(SplitMath.splitEvenly(amount, splitAmongIds.size)).toMap()
    else emptyMap()
    val exactMatches = amount > 0 && abs(exactSum - amount) < 0.01
    // Tolerance here must be in CURRENCY units, not raw percentage points — unlike EXACT mode
    // (whose shares are typed directly in currency, so its own < 0.01 epsilon already caps the
    // real error at 1 cent) and unlike EQUAL/itemized (protected by SplitMath.splitEvenly),
    // PERCENT-mode shares are used verbatim with no cent-rounding pass (see addExpense's doc
    // comment). A flat "< 0.01 percentage points" check let a large bill through with a real,
    // multi-unit currency gap — e.g. amount = 100,000 and percentSum = 100.005% (well within a
    // flat 0.01-point tolerance) actually leaves SAR 5 uncredited/undebited across the group,
    // which the settlement calculator would then surface as a stray balance nobody can resolve.
    val percentMatches = amount <= 0 || abs(amount * (percentSum - 100.0) / 100.0) < 0.01
    val itemizedMatches = itemRows.isNotEmpty() && itemRows.all { row ->
        row.name.isNotBlank() && (row.amountText.toDoubleOrNull() ?: 0.0) > 0.0 && row.memberIds.isNotEmpty()
    }

    val canSave = description.isNotBlank() && when (splitMode) {
        SplitMode.EQUAL     -> amount > 0 && splitAmongIds.isNotEmpty()
        SplitMode.EXACT     -> amount > 0 && splitAmongIds.isNotEmpty() && exactMatches
        SplitMode.PERCENT   -> amount > 0 && splitAmongIds.isNotEmpty() && percentMatches
        SplitMode.ITEMIZED  -> itemizedMatches
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

            // Amount — auto-calculated from items below in ITEMIZED mode, so it's shown
            // read-only there instead of asking the user to enter (and keep in sync) the
            // same number twice.
            if (splitMode == SplitMode.ITEMIZED) {
                OutlinedTextField(
                    value = "%.2f".format(itemsTotal),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.split_expense_amount)) },
                    supportingText = { Text(stringResource(R.string.split_itemized_amount_hint)) },
                    prefix = { CurrencyPrefix(currencySymbol) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
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
            }
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

            // Equal / Exact / Percentage / Itemized segmented selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    SplitMode.EQUAL    to R.string.split_mode_equal,
                    SplitMode.EXACT    to R.string.split_mode_exact,
                    SplitMode.PERCENT  to R.string.split_mode_percent,
                    SplitMode.ITEMIZED to R.string.split_mode_itemized
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
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            if (splitMode == SplitMode.ITEMIZED) {
                // ── Itemized line items ────────────────────────────────────────
                Text(
                    stringResource(R.string.split_itemized_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                itemRows.forEach { row ->
                    ItemRowEditor(
                        row = row,
                        members = members,
                        currencySymbol = currencySymbol,
                        canRemove = itemRows.size > 1,
                        onNameChange = { newName ->
                            itemRows = itemRows.map { if (it.key == row.key) it.copy(name = newName) else it }
                        },
                        onAmountChange = { newAmount ->
                            itemRows = itemRows.map { if (it.key == row.key) it.copy(amountText = newAmount) else it }
                        },
                        onToggleMember = { memberId ->
                            itemRows = itemRows.map {
                                if (it.key != row.key) return@map it
                                val newIds = if (it.memberIds.contains(memberId))
                                    it.memberIds - memberId
                                else
                                    it.memberIds + memberId
                                it.copy(memberIds = newIds)
                            }
                        },
                        onRemove = {
                            // Clear focus first — if a field inside this row currently has
                            // focus, removing the row out from under it can otherwise trigger
                            // the platform-level "must be a descendant of this view" crash
                            // (see focusManager comment above / installBenignRenderRaceGuard).
                            focusManager.clearFocus(force = true)
                            itemRows = itemRows.filter { it.key != row.key }
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }

                OutlinedButton(
                    onClick = {
                        itemRows = itemRows + ItemRow(key = nextItemKey, memberIds = splitAmongIds.toList())
                        nextItemKey++
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.split_add_item))
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.split_itemized_total, "$currencySymbol${"%.2f".format(itemsTotal)}"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
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
                                val share = if (checked) equalShareByMemberId[member.id] ?: 0.0 else 0.0
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
                            SplitMode.ITEMIZED -> Unit // handled in the branch above
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
                        SplitMode.ITEMIZED -> null
                    }
                    val items: List<ItemDraft>? = if (splitMode == SplitMode.ITEMIZED) {
                        itemRows.map { row ->
                            ItemDraft(
                                name = row.name.trim(),
                                amount = row.amountText.toDoubleOrNull() ?: 0.0,
                                memberIds = row.memberIds
                            )
                        }
                    } else null
                    // In ITEMIZED mode splitAmongIds (the whole-member EQUAL/EXACT/PERCENT
                    // selection) doesn't reflect per-item assignment, so pass the union of
                    // every item's assigned members instead — used only as a fallback list,
                    // since the real per-member amounts come from [items]/customShares.
                    val effectiveSplitAmongIds = if (splitMode == SplitMode.ITEMIZED)
                        itemRows.flatMap { it.memberIds }.distinct()
                    else
                        splitAmongIds.toList()
                    onSave(description.trim(), amount, paidByMemberId, effectiveSplitAmongIds, customShares, items)
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

/** One editable item row in ITEMIZED mode: name, amount, and a row of tappable member chips
 *  to assign who shared this specific item. */
@Composable
private fun ItemRowEditor(
    row: ItemRow,
    members: List<SplitMemberEntity>,
    currencySymbol: String,
    canRemove: Boolean,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onToggleMember: (Long) -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = row.name,
                onValueChange = onNameChange,
                placeholder = { Text(stringResource(R.string.split_item_name_hint)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = row.amountText,
                onValueChange = onAmountChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text(currencySymbol, style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.width(108.dp)
            )
            if (canRemove) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.split_remove_item), modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            members.forEach { member ->
                val selected = row.memberIds.contains(member.id)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (selected) 2.dp else 0.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onToggleMember(member.id) },
                    contentAlignment = Alignment.Center
                ) {
                    MemberAvatar(member = member, size = 26)
                }
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
