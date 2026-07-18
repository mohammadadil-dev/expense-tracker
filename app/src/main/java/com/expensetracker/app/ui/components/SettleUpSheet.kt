package com.expensetracker.app.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.SplitExpenseEntity
import com.expensetracker.app.data.SplitMemberEntity
import com.expensetracker.app.util.Settlement
import com.expensetracker.app.util.SmsFallback
import com.expensetracker.app.util.UpiPaymentHelper
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpSheet(
    groupName: String,
    settlements: List<Settlement>,
    currencySymbol: String,
    // Extra data needed to build a full group summary in WhatsApp
    expenses: List<SplitExpenseEntity> = emptyList(),
    members: List<SplitMemberEntity> = emptyList(),
    netBalances: Map<Long, Double> = emptyMap(),
    // UPI close-the-loop (India-only, gated on currencySymbol) — mirrors the Khata "Pay via
    // UPI" / "Request via UPI" treatment. meMemberId is null if this group somehow has no
    // isMe member (shouldn't normally happen, but guards the UI cleanly either way).
    meMemberId: Long? = null,
    myUpiId: String = "",
    ownerDisplayName: String = "",
    onSetMemberUpiId: (SplitMemberEntity, String) -> Unit = { _, _ -> },
    onSetMemberPhone: (SplitMemberEntity, String) -> Unit = { _, _ -> },
    onOpenSettings: () -> Unit = {},
    onDismiss: () -> Unit,
    onMarkPaid: (Settlement) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val memberMap = remember(members) { members.associateBy { it.id } }
    val showUpiActions = CurrencyLocaleMapper.isInrSymbol(currencySymbol)
    val noUpiAppInstalled = stringResource(R.string.khata_no_upi_app)
    val whatsappNotInstalled = stringResource(R.string.split_whatsapp_not_installed)

    // Settlement currently showing the "Request via UPI" QR sheet (someone owes "me").
    var requestUpiSettlement by remember { mutableStateOf<Settlement?>(null) }
    // Member currently showing the inline "set their UPI ID" dialog (before I can pay them).
    var editingUpiForMember by remember { mutableStateOf<SplitMemberEntity?>(null) }
    // Member currently showing the inline "set their phone number" dialog (before I can
    // send them an individual settlement reminder).
    var editingPhoneForMember by remember { mutableStateOf<SplitMemberEntity?>(null) }
    // The settlement that triggered the phone-number prompt, so the reminder can be sent
    // immediately once a number is saved instead of making the user tap the icon twice.
    var pendingReminderSettlement by remember { mutableStateOf<Settlement?>(null) }
    // Settlements currently mid-flight through onMarkPaid — guards against double-tap.
    // markSettlementPaid is fired via a fire-and-forget viewModelScope.launch, and this sheet's
    // `settlements` list is only refreshed by a *separate* coroutine after that DB write lands
    // (see SplitGroupDetailScreen's onMarkPaid), so without this, two taps inside that window
    // insert two "Settlement" rows for the same debt — doubling the credit to the debtor and
    // the debit to the creditor. Settlement is a data class (value equality), so this correctly
    // keys off (fromMemberId, toMemberId, amount) rather than object identity.
    var markingPaidSettlements by remember { mutableStateOf(setOf<Settlement>()) }
    // Once the caller supplies a fresh `settlements` list (the post-write refresh in
    // SplitGroupDetailScreen), any settlement still sitting in the guard has either resolved
    // (it's gone from the new list — harmless leftover) or genuinely needs re-enabling — either
    // way, holding onto a stale guard entry forever would permanently disable a legitimate
    // future "Mark Paid" that happens to match the same (from, to, amount) by coincidence.
    LaunchedEffect(settlements) { markingPaidSettlements = emptySet() }

    fun payMemberViaUpi(toMember: SplitMemberEntity, amount: Double) {
        val vpa = toMember.upiId
        if (vpa.isNullOrBlank()) return
        val uri = UpiPaymentHelper.buildUpiUri(vpa, toMember.name, amount, "Split: $groupName")
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, noUpiAppInstalled, Toast.LENGTH_SHORT).show()
        }
    }

    // Builds and sends the individual reminder once we definitely have a phone number —
    // shared by the normal "already has a phone" path and the "just typed one into the
    // dialog" path below (which can't wait for a recomposition to see the saved member).
    fun sendReminderTo(fromName: String, phone: String, toName: String, amount: Double) {
        val amountText = run {
            val formatted = String.format(java.util.Locale.US, "%.2f", amount)
            if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) "SAR $formatted"
            else "$currencySymbol $formatted"
        }
        val message = context.getString(
            R.string.split_reminder_msg, fromName, groupName, amountText, toName
        ) + (if (ownerDisplayName.isNotBlank())
            "\n\n" + context.getString(R.string.split_reminder_signature, ownerDisplayName)
        else "") + "\n\n📲 play.google.com/store/apps/details?id=${context.packageName}"

        // WhatsApp API requires E.164 WITHOUT the leading '+' and WITHOUT spaces/dashes.
        val phoneDigits = phone
            .replace("+", "").replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneDigits&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp") }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent2 = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp.w4b") }
            try {
                context.startActivity(intent2)
            } catch (e2: ActivityNotFoundException) {
                if (!SmsFallback.send(context, phone, message)) {
                    Toast.makeText(context, whatsappNotInstalled, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Nudges the member who owes money on this one settlement — as opposed to the header's
    // "Share via WhatsApp" button, which dumps the whole group summary on whoever receives it.
    // Prompts for a phone number first (via editingPhoneForMember) if this member doesn't have
    // one on file yet, same "ask once, reuse after" flow as payMemberViaUpi's UPI-ID prompt.
    fun sendSettlementReminder(settlement: Settlement) {
        val fromMember = memberMap[settlement.fromMemberId] ?: return
        if (fromMember.phone.isNullOrBlank()) {
            pendingReminderSettlement = settlement
            editingPhoneForMember = fromMember
            return
        }
        sendReminderTo(fromMember.name, fromMember.phone!!, settlement.toMemberName, settlement.amount)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stringResource(R.string.split_settle_up),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        groupName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // WhatsApp share button — shares the full group expense summary
                IconButton(
                    onClick = {
                        val text = buildGroupSummaryText(
                            context        = context,
                            groupName      = groupName,
                            expenses       = expenses,
                            members        = members,
                            netBalances    = netBalances,
                            settlements    = settlements,
                            currencySymbol = currencySymbol
                        )
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                            setPackage("com.whatsapp")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            val fallback = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(fallback, null))
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = stringResource(R.string.split_share_whatsapp),
                        tint = Color(0xFF25D366)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (settlements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.split_all_settled),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            stringResource(R.string.split_all_settled_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Text(
                    stringResource(R.string.split_transfers_needed, settlements.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))

                settlements.forEach { settlement ->
                    val iOwe   = showUpiActions && meMemberId != null && settlement.fromMemberId == meMemberId
                    val owedToMe = showUpiActions && meMemberId != null && settlement.toMemberId == meMemberId
                    val toMember = memberMap[settlement.toMemberId]
                    // No point nudging myself — only offer the reminder for settlements where
                    // someone *other* than the device owner is the one who needs to pay.
                    val canRemind = meMemberId == null || settlement.fromMemberId != meMemberId

                    val isMarkingPaid = settlement in markingPaidSettlements

                    SettlementRow(
                        settlement = settlement,
                        currencySymbol = currencySymbol,
                        onMarkPaid = {
                            if (settlement !in markingPaidSettlements) {
                                markingPaidSettlements = markingPaidSettlements + settlement
                                onMarkPaid(settlement)
                            }
                        },
                        markPaidEnabled = !isMarkingPaid,
                        onRemind = if (canRemind) { { sendSettlementReminder(settlement) } } else null,
                        payUpiAction = when {
                            iOwe && !toMember?.upiId.isNullOrBlank() -> {
                                { payMemberViaUpi(toMember!!, settlement.amount) }
                            }
                            iOwe -> {
                                { editingUpiForMember = toMember }
                            }
                            owedToMe && myUpiId.isNotBlank() -> {
                                { requestUpiSettlement = settlement }
                            }
                            owedToMe -> {
                                { onOpenSettings() }
                            }
                            else -> null
                        },
                        payUpiLabel = when {
                            iOwe && !toMember?.upiId.isNullOrBlank() -> stringResource(R.string.split_pay_via_upi)
                            iOwe -> stringResource(R.string.split_add_their_upi)
                            owedToMe && myUpiId.isNotBlank() -> stringResource(R.string.split_request_via_upi)
                            owedToMe -> stringResource(R.string.split_set_your_upi)
                            else -> null
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    // ── Inline "set their UPI ID" dialog ─────────────────────────────────────
    editingUpiForMember?.let { member ->
        var upiInput by remember(member.id) { mutableStateOf(member.upiId ?: "") }
        val upiValid = upiInput.isBlank() || UpiPaymentHelper.isValidVpa(upiInput)
        AlertDialog(
            onDismissRequest = { editingUpiForMember = null },
            title = { Text(stringResource(R.string.split_add_their_upi)) },
            text = {
                OutlinedTextField(
                    value = upiInput,
                    onValueChange = { upiInput = it },
                    label = { Text(stringResource(R.string.upi_id_label)) },
                    placeholder = { Text(stringResource(R.string.upi_id_hint)) },
                    singleLine = true,
                    isError = !upiValid,
                    supportingText = if (!upiValid) {
                        { Text(stringResource(R.string.upi_id_invalid)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    enabled = upiInput.isNotBlank() && upiValid,
                    onClick = {
                        onSetMemberUpiId(member, upiInput.trim())
                        editingUpiForMember = null
                    }
                ) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { editingUpiForMember = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    // ── Inline "set their phone number" dialog ───────────────────────────────
    // Country code + local number (not a single free-text field) — without a code prefix the
    // stored number never resolves to a valid WhatsApp/E.164 number (see this file's top-level
    // doc comment in PhoneNumberField.kt for the "+51375230 is not a valid phone number" bug
    // this caused).
    editingPhoneForMember?.let { member ->
        val (initCode, initLocal) = remember(member.id) { splitPhone(member.phone ?: "") }
        var selectedCountry by remember(member.id) {
            mutableStateOf(COUNTRY_ENTRIES.firstOrNull { it.dial == initCode } ?: defaultCountryEntry())
        }
        var localPhone by remember(member.id) { mutableStateOf(initLocal) }
        var showCountryPicker by remember { mutableStateOf(false) }

        if (showCountryPicker) {
            CountryPickerDialog(
                selectedCountry = selectedCountry,
                onSelect = { selectedCountry = it; showCountryPicker = false },
                onDismiss = { showCountryPicker = false }
            )
        }

        AlertDialog(
            onDismissRequest = { editingPhoneForMember = null; pendingReminderSettlement = null },
            title = { Text(stringResource(R.string.split_add_their_phone)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.split_member_phone_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    CountryCodePhoneField(
                        selectedCountry = selectedCountry,
                        onCountryClick = { showCountryPicker = true },
                        localNumber = localPhone,
                        onLocalNumberChange = { localPhone = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = localPhone.isNotBlank(),
                    onClick = {
                        val fullPhone = buildFullPhone(selectedCountry, localPhone)
                        onSetMemberPhone(member, fullPhone)
                        // Fire the reminder immediately with the freshly-typed number, rather
                        // than waiting for the recomposition that would carry the saved member
                        // back in — the caller (SplitGroupDetailScreen) re-fetches members
                        // asynchronously, so `member` here would still show the old blank phone.
                        pendingReminderSettlement?.let { settlement ->
                            sendReminderTo(member.name, fullPhone, settlement.toMemberName, settlement.amount)
                        }
                        editingPhoneForMember = null
                        pendingReminderSettlement = null
                    }
                ) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { editingPhoneForMember = null; pendingReminderSettlement = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // ── "Request via UPI" QR sheet (someone owes "me") ───────────────────────
    requestUpiSettlement?.let { settlement ->
        val fromName = memberMap[settlement.fromMemberId]?.name ?: ""
        RequestUpiPaymentSheet(
            myUpiId = myUpiId,
            payeeDisplayName = ownerDisplayName,
            partyName = fromName,
            amount = settlement.amount,
            currencySymbol = currencySymbol,
            notePrefix = "Split",
            onShareQr = { qrUri ->
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, qrUri)
                    putExtra(Intent.EXTRA_TEXT, "Split: $groupName")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
                requestUpiSettlement = null
            },
            onDismiss = { requestUpiSettlement = null }
        )
    }
}

@Composable
private fun SettlementRow(
    settlement: Settlement,
    currencySymbol: String,
    onMarkPaid: () -> Unit,
    markPaidEnabled: Boolean = true,
    onRemind: (() -> Unit)? = null,
    payUpiAction: (() -> Unit)? = null,
    payUpiLabel: String? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            settlement.fromMemberName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            settlement.toMemberName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    MoneyText(
                        formatted = "$currencySymbol${"%.2f".format(settlement.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (onRemind != null) {
                    IconButton(onClick = onRemind, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Filled.NotificationsActive,
                            contentDescription = stringResource(R.string.split_remind_member),
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onMarkPaid,
                    enabled = markPaidEnabled,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(stringResource(R.string.split_mark_paid), style = MaterialTheme.typography.labelMedium)
                }
            }

            if (payUpiAction != null && payUpiLabel != null) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = payUpiAction,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Icon(
                        if (payUpiLabel == stringResource(R.string.split_request_via_upi)) Icons.Filled.QrCode
                        else Icons.Filled.AccountBalance,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(payUpiLabel, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ── WhatsApp message builder ───────────────────────────────────────────────────

/**
 * Builds a rich, WhatsApp-formatted expense summary for the group.
 *
 * Format (with WhatsApp bold/italic markdown):
 *
 *   💰 *Group Name — Expense Summary*
 *
 *   📋 *Expenses (3)*
 *   • Lunch — SAR 300.00  _(paid by Adil)_
 *   • Taxi  — SAR 120.00  _(paid by Roshni)_
 *   *Total: SAR 420.00*
 *
 *   👥 *Balances*
 *   • Adil:   +SAR 180.00 ✅
 *   • Roshni:  -SAR 40.00 💸
 *   • Me:     -SAR 140.00 💸
 *
 *   💸 *Settlements*
 *   • Me → Adil: SAR 140.00
 *   • Roshni → Adil: SAR 40.00
 *
 *   _Tracked with Baqaya_ 🧾
 *
 * Currency: SAR is written as "SAR" in plain text (the "ر.س" glyph is unreadable in
 * most WhatsApp notifications and chat bubbles on non-Arabic system fonts).
 * All other currencies use their symbol directly.
 */
private fun buildGroupSummaryText(
    context: android.content.Context,
    groupName: String,
    expenses: List<SplitExpenseEntity>,
    members: List<SplitMemberEntity>,
    netBalances: Map<Long, Double>,
    settlements: List<Settlement>,
    currencySymbol: String
): String {
    val memberMap = members.associateBy { it.id }
    val divider = "━━━━━━━━━━━━━━━━━"

    // For plain-text (WhatsApp): swap any Arabic-script symbol for U+20C1 (new SAR sign).
    val plainCurrency = if (currencySymbol.any { it.code in 0x0600..0x06FF }) "⃁" else currencySymbol
    fun fmtAmt(amount: Double): String = "$plainCurrency ${"%.2f".format(amount)}"

    // Localized labels — uses the language the user chose in Settings.
    val lHeader     = context.getString(R.string.split_wa_header)
    val lExpenses   = context.getString(R.string.split_wa_expenses)
    val lPaidBy     = context.getString(R.string.split_wa_paid_by)
    val lTotal      = context.getString(R.string.split_wa_total)
    val lBalances   = context.getString(R.string.split_wa_balances)
    val lGets       = context.getString(R.string.split_wa_gets)
    val lOwes       = context.getString(R.string.split_wa_owes)
    val lSettled    = context.getString(R.string.split_wa_member_settled)
    val lToPay      = context.getString(R.string.split_wa_to_pay)
    val lAllSettled = context.getString(R.string.split_wa_all_settled)
    val lFooter     = context.getString(R.string.split_wa_footer)

    val realExpenses = expenses.filter { !it.isSettlement }
    val total = realExpenses.sumOf { it.amount }

    val sb = StringBuilder()

    // ── Header ────────────────────────────────────────────────────────────────
    sb.appendLine("💰 *$groupName — $lHeader*")

    // ── Expenses ──────────────────────────────────────────────────────────────
    if (realExpenses.isNotEmpty()) {
        sb.appendLine(divider)
        sb.appendLine("📋 *${String.format(lExpenses, realExpenses.size)}*")
        realExpenses.sortedByDescending { it.date }.forEachIndexed { i, expense ->
            val paidByName = memberMap[expense.paidByMemberId]?.name ?: "?"
            sb.appendLine("${i + 1}. *${expense.description}* — *${fmtAmt(expense.amount)}*")
            sb.appendLine("   ${String.format(lPaidBy, paidByName)}")
        }
        sb.appendLine()
        sb.appendLine("*${String.format(lTotal, fmtAmt(total))}*")
    }

    // ── Balances ──────────────────────────────────────────────────────────────
    if (members.isNotEmpty()) {
        sb.appendLine(divider)
        sb.appendLine("📊 *$lBalances*")
        members.forEach { member ->
            val balance = netBalances[member.id] ?: 0.0
            val absBalance = abs(balance)
            val line = when {
                absBalance < 0.01 -> "✅ ${member.name} — $lSettled"
                balance > 0       -> "🟢 ${member.name} — $lGets *${fmtAmt(absBalance)}*"
                else              -> "🔴 ${member.name} — $lOwes *${fmtAmt(absBalance)}*"
            }
            sb.appendLine(line)
        }
    }

    // ── Settlements ───────────────────────────────────────────────────────────
    sb.appendLine(divider)
    if (settlements.isNotEmpty()) {
        sb.appendLine("💸 *$lToPay*")
        settlements.forEach { s ->
            sb.appendLine("${s.fromMemberName} ➡️ ${s.toMemberName} — *${fmtAmt(s.amount)}*")
        }
    } else {
        sb.appendLine("✅ *$lAllSettled*")
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    // Play Store link appended here in code, not in the translated split_wa_footer string —
    // the URL needs no localization, and this is often the only touchpoint a non-user group
    // member (someone who hasn't installed the app yet) has with it.
    sb.appendLine(divider)
    sb.appendLine("_${lFooter}_ 🧾")
    sb.append("play.google.com/store/apps/details?id=${context.packageName}")

    return sb.toString()
}
