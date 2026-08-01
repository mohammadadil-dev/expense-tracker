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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.SharedBillGroupMemberEntity
import com.expensetracker.app.data.SharedBillMemberDraft
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.SharedBillMath
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

private data class BillMemberUi(
    val name: String,
    val isMe: Boolean,
    val personCount: Int,
    val included: Boolean = true,
    val awayFrom: String? = null,
    val awayTo: String? = null
)

private data class DatePickTarget(val kind: Int, val index: Int)  // 0=start 1=end 2=awayFrom 3=awayTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSharedBillSheet(
    currencySymbol: String,
    splitMode: Int,
    groupMembers: List<SharedBillGroupMemberEntity>,
    locale: Locale,
    onDismiss: () -> Unit,
    onCreate: (label: String, note: String, periodStart: String, periodEnd: String, total: Double, members: List<SharedBillMemberDraft>) -> Unit
) {
    val flatMode = splitMode == 1
    var billTypeIndex by remember { mutableStateOf(0) }
    var customLabel by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var periodStart by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1).toString()) }
    var periodEnd by remember { mutableStateOf(DateUtils.todayIso()) }
    var amountText by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var datePicker by remember { mutableStateOf<DatePickTarget?>(null) }
    // Snapshot of the group's members for THIS bill — include flags + away dates are per-bill.
    val members = remember(groupMembers) {
        mutableStateListOf(*groupMembers.map {
            BillMemberUi(name = it.name, isMe = it.isMe, personCount = it.personCount)
        }.toTypedArray())
    }
    val scrollState = rememberScrollState()

    val billTypeRes = listOf(
        R.string.bill_type_electricity, R.string.bill_type_water, R.string.bill_type_gas,
        R.string.bill_type_internet, R.string.bill_type_rent, R.string.bill_type_maintenance,
        R.string.bill_type_other
    )
    val typeLabels = billTypeRes.map { stringResource(it) }
    val isOtherType = billTypeIndex == billTypeRes.lastIndex

    val periodDays = SharedBillMath.periodDays(periodStart, periodEnd)
    fun awayDays(m: BillMemberUi) = awayDaysInPeriod(m.awayFrom, m.awayTo, periodStart, periodEnd)

    val parsedAmount = amountText.replace(",", "").trim().toDoubleOrNull()
    val canSave = parsedAmount != null && parsedAmount > 0.0 && members.isNotEmpty() &&
        members.any { it.included } && (!isOtherType || customLabel.isNotBlank())

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
            Text(stringResource(R.string.shared_bill_add_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            // ── Bill type ─────────────────────────────────────────────────────
            Text(stringResource(R.string.shared_bill_bill_type), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = { typeMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = typeLabels[billTypeIndex],
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { typeMenuExpanded = false }
                ) {
                    typeLabels.forEachIndexed { i, name ->
                        DropdownMenuItem(text = { Text(name) }, onClick = { billTypeIndex = i; typeMenuExpanded = false })
                    }
                }
            }
            if (isOtherType) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    label = { Text(stringResource(R.string.shared_bill_label_hint)) },
                    placeholder = { Text(stringResource(R.string.shared_bill_label_example)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.shared_bill_note_hint)) },
                placeholder = { Text(stringResource(R.string.shared_bill_note_example)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ── Billing period ────────────────────────────────────────────────
            Text(stringResource(R.string.shared_bill_period), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { datePicker = DatePickTarget(0, -1) }, modifier = Modifier.weight(1f)) {
                    Text(DateUtils.formatExpenseDate(periodStart, locale))
                }
                Text("→", modifier = Modifier.align(Alignment.CenterVertically))
                OutlinedButton(onClick = { datePicker = DatePickTarget(1, -1) }, modifier = Modifier.weight(1f)) {
                    Text(DateUtils.formatExpenseDate(periodEnd, locale))
                }
            }
            Text(
                stringResource(R.string.shared_bill_period_days, periodDays),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(Modifier.height(16.dp))

            // ── Total ─────────────────────────────────────────────────────────
            Text(stringResource(R.string.shared_bill_total), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it; amountError = false },
                leadingIcon = {
                    if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
                        Icon(painterResource(R.drawable.ic_saudi_riyal), contentDescription = null, modifier = Modifier.size(20.dp))
                    } else {
                        Text(currencySymbol, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = amountError,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ── Members (from the group) ──────────────────────────────────────
            Text(
                stringResource(if (flatMode) R.string.shared_bill_flats else R.string.shared_bill_people),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            members.forEachIndexed { index, m ->
                val away = awayDays(m)
                val present = (periodDays - away).coerceIn(0, periodDays)
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = m.included, onCheckedChange = { members[index] = m.copy(included = it) })
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (m.isMe) "${m.name} (${stringResource(R.string.family_me_label)})" else m.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (m.included) {
                                val countTag = if (flatMode) "${stringResource(R.string.shared_bill_people_count, m.personCount)} · " else ""
                                Text(
                                    countTag + stringResource(R.string.shared_bill_present_days, present, periodDays),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    stringResource(R.string.shared_bill_excluded),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if (m.included) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 40.dp)) {
                            OutlinedButton(onClick = { datePicker = DatePickTarget(2, index) }, modifier = Modifier.weight(1f)) {
                                Text(m.awayFrom?.let { DateUtils.formatExpenseDate(it, locale) } ?: stringResource(R.string.shared_bill_away_from))
                            }
                            OutlinedButton(onClick = { datePicker = DatePickTarget(3, index) }, modifier = Modifier.weight(1f)) {
                                Text(m.awayTo?.let { DateUtils.formatExpenseDate(it, locale) } ?: stringResource(R.string.shared_bill_away_to))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val amt = parsedAmount ?: run { amountError = true; return@Button }
                    if (amt <= 0.0) { amountError = true; return@Button }
                    val finalLabel = if (isOtherType) customLabel.trim() else typeLabels[billTypeIndex]
                    val drafts = members.map {
                        SharedBillMemberDraft(
                            name = it.name.trim(),
                            isMe = it.isMe,
                            daysAbsent = awayDays(it),
                            personCount = if (flatMode) it.personCount else 1,
                            included = it.included
                        )
                    }
                    onCreate(finalLabel, note.trim(), periodStart, periodEnd, amt, drafts)
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.shared_bill_compute))
            }
        }
    }

    datePicker?.let { target ->
        val initial = when (target.kind) {
            0 -> periodStart
            1 -> periodEnd
            2 -> members.getOrNull(target.index)?.awayFrom ?: periodStart
            else -> members.getOrNull(target.index)?.awayTo ?: periodEnd
        }
        CalendarDatePickerDialog(
            initialDateIso = initial,
            locale = locale,
            onDismiss = { datePicker = null },
            onConfirm = { iso ->
                when (target.kind) {
                    0 -> periodStart = iso
                    1 -> periodEnd = iso
                    2 -> members.getOrNull(target.index)?.let { members[target.index] = it.copy(awayFrom = iso) }
                    3 -> members.getOrNull(target.index)?.let { members[target.index] = it.copy(awayTo = iso) }
                }
                datePicker = null
            }
        )
    }
}

/** Inclusive overlap (in days) of [fromIso]..[toIso] with the billing period. */
private fun awayDaysInPeriod(fromIso: String?, toIso: String?, pStart: String, pEnd: String): Int {
    if (fromIso == null || toIso == null) return 0
    return try {
        val from = LocalDate.parse(fromIso)
        val to = LocalDate.parse(toIso)
        val ps = LocalDate.parse(pStart)
        val pe = LocalDate.parse(pEnd)
        val s = if (from.isAfter(ps)) from else ps
        val e = if (to.isBefore(pe)) to else pe
        if (e.isBefore(s)) 0 else (ChronoUnit.DAYS.between(s, e).toInt() + 1)
    } catch (_: Exception) {
        0
    }
}
