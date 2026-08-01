package com.expensetracker.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.SharedBillEntity
import com.expensetracker.app.data.SharedBillGroupMemberEntity
import com.expensetracker.app.data.SharedBillMemberEntity
import com.expensetracker.app.ui.components.AddSharedBillSheet
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.SaudiBanks
import com.expensetracker.app.util.SharedBillMath
import com.expensetracker.app.viewmodel.SharedBillViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedBillGroupDetailScreen(
    groupId: Long,
    groupName: String,
    groupEmoji: String,
    splitMode: Int,
    viewModel: SharedBillViewModel,
    currencySymbol: String,
    ownerName: String,
    myIban: String = "",
    myStcPay: String = "",
    myUpiId: String = "",
    onBack: () -> Unit
) {
    LaunchedEffect(groupId) { viewModel.selectGroup(groupId) }
    val bills by viewModel.selectedGroupBills.collectAsState()
    val members by viewModel.selectedBillMembers.collectAsState()
    val groupMembers by viewModel.selectedGroupMembers.collectAsState()
    val locale = LocalConfiguration.current.locales[0]
    val flatMode = splitMode == 1

    var showCreate by remember { mutableStateOf(false) }
    var selectedBillId by remember { mutableStateOf<Long?>(null) }
    var pendingDelete by remember { mutableStateOf<SharedBillEntity?>(null) }
    var newMemberName by remember { mutableStateOf("") }
    var memberDupError by remember { mutableStateOf(false) }

    LaunchedEffect(selectedBillId) { selectedBillId?.let { viewModel.selectBill(it) } }
    val selectedBill = bills.firstOrNull { it.id == selectedBillId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(groupName.ifBlank { stringResource(R.string.shared_bills_title) }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.shared_bill_add_title))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Group header ─────────────────────────────────────────────────
            item(key = "group_hero") {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) { Text(groupEmoji, style = MaterialTheme.typography.headlineSmall) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(groupName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            stringResource(R.string.shared_bill_people_count, groupMembers.size) + " · " +
                                stringResource(if (flatMode) R.string.shared_bill_mode_flat else R.string.shared_bill_mode_person),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Members (group template — editable) ──────────────────────────
            item(key = "members_header") {
                Text(
                    stringResource(if (flatMode) R.string.shared_bill_flats else R.string.shared_bill_people),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item(key = "members_card") {
                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
                        groupMembers.forEach { gm ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 5.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        gm.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    if (gm.isMe) "${gm.name} (${stringResource(R.string.family_me_label)})" else gm.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (flatMode) {
                                    IconButton(onClick = { if (gm.personCount > 1) viewModel.updateGroupMember(gm.copy(personCount = gm.personCount - 1)) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Text(gm.personCount.toString(), style = MaterialTheme.typography.bodyMedium)
                                    IconButton(onClick = { viewModel.updateGroupMember(gm.copy(personCount = gm.personCount + 1)) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                                if (!gm.isMe) {
                                    IconButton(onClick = { viewModel.deleteGroupMember(gm) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            OutlinedTextField(
                                value = newMemberName,
                                onValueChange = { newMemberName = it; memberDupError = false },
                                label = { Text(stringResource(if (flatMode) R.string.shared_bill_flat_hint else R.string.split_add_member_hint)) },
                                singleLine = true,
                                isError = memberDupError,
                                supportingText = if (memberDupError) {
                                    { Text(stringResource(R.string.split_member_duplicate), color = MaterialTheme.colorScheme.error) }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    val n = newMemberName.trim()
                                    when {
                                        n.isBlank() -> {}
                                        groupMembers.any { it.name.equals(n, ignoreCase = true) } -> memberDupError = true
                                        else -> {
                                            viewModel.addGroupMember(groupId, n, 1)
                                            newMemberName = ""
                                            memberDupError = false
                                        }
                                    }
                                },
                                modifier = Modifier.size(48.dp)
                            ) { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.split_add_member)) }
                        }
                    }
                }
            }

            item(key = "bills_header") {
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.shared_bill_bills_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (bills.isEmpty()) {
                item(key = "bills_empty") {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.shared_bills_empty_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(bills, key = { it.id }) { bill ->
                    Card(
                        onClick = { selectedBillId = bill.id },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    bill.label + if (bill.note.isNotBlank()) " · ${bill.note}" else "",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    "${DateUtils.formatExpenseDate(bill.periodStart, locale)} → ${DateUtils.formatExpenseDate(bill.periodEnd, locale)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                money(bill.totalAmount, currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(96.dp)) }
        }
    }

    if (showCreate) {
        AddSharedBillSheet(
            currencySymbol = currencySymbol,
            splitMode = splitMode,
            groupMembers = groupMembers,
            locale = locale,
            onDismiss = { showCreate = false },
            onCreate = { label, billNote, start, end, total, drafts ->
                viewModel.createBill(groupId, label, billNote, start, end, total, drafts) { newId ->
                    showCreate = false
                    selectedBillId = newId
                }
            }
        )
    }

    if (selectedBill != null) {
        SharedBillDetailSheet(
            bill = selectedBill,
            members = members,
            currencySymbol = currencySymbol,
            myIban = myIban,
            myStcPay = myStcPay,
            myUpiId = myUpiId,
            onSetPaid = { memberId, paid -> viewModel.setPaid(memberId, paid) },
            onLogMyShare = { amount -> viewModel.logMyShare(selectedBill, amount) },
            onDelete = { pendingDelete = selectedBill },
            onDismiss = { selectedBillId = null }
        )
    }

    pendingDelete?.let { bill ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.shared_bill_delete_title)) },
            text = { Text(stringResource(R.string.shared_bill_delete_confirm, bill.label)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBill(bill) { selectedBillId = null }
                    pendingDelete = null
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharedBillDetailSheet(
    bill: SharedBillEntity,
    members: List<SharedBillMemberEntity>,
    currencySymbol: String,
    myIban: String,
    myStcPay: String,
    myUpiId: String,
    onSetPaid: (Long, Boolean) -> Unit,
    onLogMyShare: (Double) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val periodDays = SharedBillMath.periodDays(bill.periodStart, bill.periodEnd)
    val shares = SharedBillMath.shares(bill.totalAmount, members, periodDays)
    val myIndex = members.indexOfFirst { it.isMe }
    val myShare = if (myIndex >= 0 && myIndex < shares.size) shares[myIndex] else null

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
            Text(bill.label, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                "${bill.periodStart} → ${bill.periodEnd} · ${money(bill.totalAmount, currencySymbol)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            members.forEachIndexed { i, m ->
                val present = SharedBillMath.presentDays(m, periodDays)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = m.paid, onCheckedChange = { onSetPaid(m.id, it) })
                    Spacer(Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (m.isMe) "${m.name} (${stringResource(R.string.family_me_label)})" else m.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        val presentText = stringResource(R.string.shared_bill_present_days, present, periodDays)
                        Text(
                            text = if (m.personCount > 1)
                                "${stringResource(R.string.shared_bill_people_count, m.personCount)} · $presentText"
                            else presentText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        money(shares.getOrElse(i) { 0.0 }, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val text = buildShareText(context, bill, members, shares, periodDays, currencySymbol, myIban, myStcPay, myUpiId)
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(send, null))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.shared_bill_share))
            }

            if (myShare != null) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onLogMyShare(myShare) },
                    enabled = bill.linkedExpenseId == null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (bill.linkedExpenseId == null)
                            stringResource(R.string.shared_bill_log_my_share, money(myShare, currencySymbol))
                        else
                            stringResource(R.string.shared_bill_my_share_logged)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun money(amount: Double, currencySymbol: String): String {
    val n = String.format(Locale.US, "%.2f", amount)
    return if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) "SAR $n" else "$currencySymbol $n"
}

private fun buildShareText(
    context: android.content.Context,
    bill: SharedBillEntity,
    members: List<SharedBillMemberEntity>,
    shares: List<Double>,
    periodDays: Int,
    currencySymbol: String,
    myIban: String,
    myStcPay: String,
    myUpiId: String
): String {
    val sb = StringBuilder()
    sb.appendLine("🏠 ${bill.label}")
    sb.appendLine("📅 ${bill.periodStart} → ${bill.periodEnd} (${periodDays})")
    sb.appendLine("💡 ${context.getString(R.string.shared_bill_total)}: ${money(bill.totalAmount, currencySymbol)}")
    sb.appendLine()
    sb.appendLine("*${context.getString(R.string.shared_bill_share_heading)}*")
    members.forEachIndexed { i, m ->
        val present = SharedBillMath.presentDays(m, periodDays)
        val mark = if (m.paid) " ✅" else ""
        val countTag = if (m.personCount > 1) " (${m.personCount}👤)" else ""
        sb.appendLine("• ${m.name}$countTag — ${present}/${periodDays} — *${money(shares.getOrElse(i) { 0.0 }, currencySymbol)}*$mark")
    }
    if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) {
        if (myIban.isNotBlank()) {
            sb.appendLine()
            sb.append(context.getString(R.string.iban_pay_to, "*${SaudiBanks.formatGrouped(myIban)}*"))
        }
        if (myStcPay.isNotBlank()) { sb.appendLine(); sb.append(context.getString(R.string.stc_pay_to, myStcPay)) }
    } else if (CurrencyLocaleMapper.isInrSymbol(currencySymbol) && myUpiId.isNotBlank()) {
        sb.appendLine(); sb.append("UPI: $myUpiId")
    }
    sb.appendLine()
    sb.appendLine("━━━━━━━━━━")
    sb.appendLine(context.getString(R.string.app_install_pitch))
    sb.append("👉 https://play.google.com/store/apps/details?id=${context.packageName}")
    return sb.toString()
}
