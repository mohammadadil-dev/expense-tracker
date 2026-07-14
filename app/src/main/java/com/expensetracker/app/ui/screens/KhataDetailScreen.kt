package com.expensetracker.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.data.KhataPartyEntity
import com.expensetracker.app.ui.components.AddEditKhataPartySheet
import com.expensetracker.app.ui.components.AddKhataEntrySheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.RequestUpiPaymentSheet
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonPink
import com.expensetracker.app.ui.theme.NeonTeal
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.expensetracker.app.viewmodel.KhataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataDetailScreen(
    partyId: Long,
    khataViewModel: KhataViewModel,
    expenseViewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currencySymbol by expenseViewModel.currencySymbol.collectAsState()
    val displayName    by expenseViewModel.displayName.collectAsState()
    val myUpiId        by expenseViewModel.myUpiId.collectAsState()
    val allParties     by khataViewModel.allParties.collectAsState()
    val allEntries     by khataViewModel.allEntries.collectAsState()

    val party   = allParties.firstOrNull { it.id == partyId } ?: return
    val entries = remember(partyId) { khataViewModel.entriesForParty(partyId) }.collectAsState().value
    val balance = khataViewModel.balanceForParty(partyId, allEntries)
    val isIOwe  = party.direction == KhataPartyEntity.DIRECTION_I_OWE

    var showAddEntry   by remember { mutableStateOf(false) }
    var showEditParty  by remember { mutableStateOf(false) }
    var showDeleteParty by remember { mutableStateOf(false) }
    var pendingDelete  by remember { mutableStateOf<KhataEntryEntity?>(null) }
    var showUpiSheet   by remember { mutableStateOf(false) }

    // "Request via UPI" is only offered when: they owe the user money, the outstanding
    // balance clears the ₹1 floor, the currency is INR, and the user has set their own
    // UPI ID in Settings. See FEATURE_SPEC_KHATA_UPI_PAYMENTS.md §3b/§10.2.
    val showUpiButton = !isIOwe && balance >= 1.0 &&
        CurrencyLocaleMapper.isInrSymbol(currencySymbol) && myUpiId.isNotBlank()

    // WhatsApp message builder
    // For plain-text messages, "ر.س500.00" is unreadable. For SAR use "SAR 500.00" (ISO code
    // + space + amount). For other currencies keep the symbol but still add a space so the
    // message reads naturally: "$500.00" → "$ 500.00" in a sentence context.
    val plainAmount = run {
        val formatted = String.format(java.util.Locale.US, "%.2f", balance)
        if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) "SAR $formatted"
        else "$currencySymbol $formatted"
    }
    val whatsappNotInstalled = stringResource(R.string.khata_whatsapp_not_installed)
    val senderDefault = stringResource(R.string.khata_sender_name_default)
    val reminderMsgOwe     = stringResource(R.string.khata_reminder_msg_owe,
        party.name, plainAmount, displayName.ifBlank { senderDefault })
    val reminderMsgCredit  = stringResource(R.string.khata_reminder_msg_credit,
        party.name, plainAmount, displayName.ifBlank { senderDefault })
    // Play Store link appended in code (not in the translated string resources) since the URL
    // itself needs no localization — this reaches the recipient even if they don't have the app
    // yet, which is often the case for a Khata reminder sent to someone outside the user base.
    val reminderMsg = (if (isIOwe) reminderMsgCredit else reminderMsgOwe) +
        "\n\n📲 play.google.com/store/apps/details?id=${context.packageName}"

    // Shared by the plain reminder and the "Request via UPI" share — both hand off to
    // WhatsApp via Intent.ACTION_VIEW, never touching the message content or transport.
    fun sendWhatsAppMessage(message: String) {
        // WhatsApp API requires E.164 WITHOUT the leading '+' and WITHOUT spaces/dashes.
        // e.g. "+966 51 234 5678" → "96651234567"
        val phone = party.phone
            .replace("+", "")
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp") }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // WhatsApp Business fallback
            val intent2 = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp.w4b") }
            try {
                context.startActivity(intent2)
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(context, whatsappNotInstalled, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendWhatsApp() = sendWhatsAppMessage(reminderMsg)

    // "Share payment link" from the UPI sheet — reuses reminderMsg verbatim, just adds the
    // UPI deep link as one more line (FEATURE_SPEC_KHATA_UPI_PAYMENTS.md §3b/§5). No copy-link
    // fallback: sharing always goes through this same WhatsApp flow (§10.3).
    fun sendUpiPaymentLink(upiLink: String) {
        sendWhatsAppMessage(reminderMsg + "\n\n" + upiLink)
        showUpiSheet = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBlobBackground(
            blobColors = listOf(NeonCyan, NeonPink, NeonTeal),
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(party.name) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                        }
                    },
                    actions = {
                        IconButton(onClick = { showEditParty = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                        }
                        IconButton(onClick = { showDeleteParty = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = DangerRed)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddEntry = true },
                    containerColor = AccentIndigo
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.khata_add_entry))
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // ── Balance card ───────────────────────────────────────────────
                KhataBalanceCard(
                    partyName = party.name,
                    balance = balance,
                    currencySymbol = currencySymbol,
                    isIOwe = isIOwe,
                    hasPhone = party.phone.isNotBlank(),
                    onSendReminder = { sendWhatsApp() },
                    showUpiButton = showUpiButton,
                    onRequestUpi = { showUpiSheet = true }
                )

                Spacer(Modifier.height(16.dp))

                // ── Entry ledger ───────────────────────────────────────────────
                if (entries.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.khata_no_entries),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        // Quick-add buttons — full-width column so text never wraps
                        Column(
                            modifier = Modifier.fillMaxWidth(0.75f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showAddEntry = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(R.string.khata_credit_label))
                            }
                            OutlinedButton(
                                onClick = { showAddEntry = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(R.string.khata_payment_label))
                            }
                        }
                    }
                } else {
                    // Running balance from oldest to newest
                    val sorted = entries.sortedWith(compareBy({ it.date }, { it.id }))
                    var running = 0.0
                    val withRunning = sorted.map { entry ->
                        running += if (entry.type == KhataEntryEntity.TYPE_CREDIT) entry.amount else -entry.amount
                        entry to running.coerceAtLeast(0.0)
                    }.reversed() // show newest at top

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(withRunning, key = { it.first.id }) { (entry, runningBalance) ->
                            KhataEntryRow(
                                entry = entry,
                                runningBalance = runningBalance,
                                currencySymbol = currencySymbol,
                                onDelete = { pendingDelete = entry }
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Sheets & dialogs ────────────────────────────────────────────────────
    if (showAddEntry) {
        AddKhataEntrySheet(
            onSave = { amount, note, date, type ->
                khataViewModel.addEntry(partyId, amount, note, date, type)
                showAddEntry = false
            },
            onDismiss = { showAddEntry = false }
        )
    }

    if (showEditParty) {
        AddEditKhataPartySheet(
            initial = party,
            defaultDirection = party.direction,
            currencySymbol = currencySymbol,
            onSave = { id, name, phone, direction, _, _, upiId ->
                // Editing an existing party — initial amount fields are hidden, pass-through ignored
                khataViewModel.saveParty(id, name, phone, direction, upiId = upiId)
                showEditParty = false
            },
            onDismiss = { showEditParty = false }
        )
    }

    if (showUpiSheet) {
        RequestUpiPaymentSheet(
            myUpiId = myUpiId,
            payeeDisplayName = displayName.ifBlank { senderDefault },
            partyName = party.name,
            amount = balance,
            currencySymbol = currencySymbol,
            onShareLink = { upiLink -> sendUpiPaymentLink(upiLink) },
            onDismiss = { showUpiSheet = false }
        )
    }

    pendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.khata_delete_entry)) },
            text  = { Text(stringResource(R.string.delete_expense_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    khataViewModel.deleteEntry(entry)
                    pendingDelete = null
                }) { Text(stringResource(R.string.delete), color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showDeleteParty) {
        AlertDialog(
            onDismissRequest = { showDeleteParty = false },
            title = { Text(stringResource(R.string.khata_delete_party_title)) },
            text  = { Text(stringResource(R.string.khata_delete_party_confirm, party.name)) },
            confirmButton = {
                TextButton(onClick = {
                    khataViewModel.deleteParty(party) { onBack() }
                    showDeleteParty = false
                }) { Text(stringResource(R.string.delete), color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteParty = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

// ── Balance summary card ───────────────────────────────────────────────────────

@Composable
private fun KhataBalanceCard(
    partyName: String,
    balance: Double,
    currencySymbol: String,
    isIOwe: Boolean,
    hasPhone: Boolean,
    onSendReminder: () -> Unit,
    showUpiButton: Boolean = false,
    onRequestUpi: () -> Unit = {}
) {
    val isSettled = balance <= 0.0

    // Gradient colors: red for "I owe", green for "they owe me", teal-green for settled
    val gradientStart = when {
        isSettled -> SuccessGreen
        isIOwe    -> DangerRed
        else      -> SuccessGreen
    }
    val gradientEnd = when {
        isSettled -> NeonTeal
        isIOwe    -> Color(0xFFB71C1C)   // deep red
        else      -> NeonCyan
    }

    // Context-aware label so the user immediately understands direction
    val contextLabel = when {
        isSettled -> stringResource(R.string.khata_label_all_cleared, partyName)
        isIOwe    -> stringResource(R.string.khata_label_i_owe, partyName)
        else      -> stringResource(R.string.khata_label_they_owe, partyName)
    }

    // Party initials avatar
    val initials = partyName.trim().split(" ")
        .take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = gradientStart.copy(alpha = 0.3f),
                spotColor = gradientStart.copy(alpha = 0.4f)
            )
            .clip(shape)
            .drawBehind {
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(gradientStart, gradientEnd),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    )
                }
            }
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Initials avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.20f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))

            // Direction label
            Text(
                text = contextLabel,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.90f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            // Big balance amount
            MoneyText(
                formatted = Formatters.money(balance, currencySymbol),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            if (isSettled) {
                // Settled badge pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.20f)
                ) {
                    Text(
                        text = stringResource(R.string.khata_settled),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            } else if (hasPhone) {
                // WhatsApp reminder button — white outline on coloured background
                OutlinedButton(
                    onClick = onSendReminder,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.70f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.khata_whatsapp_reminder))
                }
            } else {
                // No phone — show outstanding pill instead
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.20f)
                ) {
                    Text(
                        text = stringResource(R.string.khata_outstanding),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // "Request via UPI" — independent of the WhatsApp/no-phone branch above since
            // the QR needs no phone number at all. INR-only, ₹1 floor (see call site).
            if (showUpiButton) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onRequestUpi,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.70f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.khata_request_via_upi))
                }
            }
        }
    }
}

// ── Single ledger row ─────────────────────────────────────────────────────────

@Composable
private fun KhataEntryRow(
    entry: KhataEntryEntity,
    runningBalance: Double,
    currencySymbol: String,
    onDelete: () -> Unit
) {
    val isCredit = entry.type == KhataEntryEntity.TYPE_CREDIT
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isCredit) DangerRed else SuccessGreen)
            )
            Spacer(Modifier.width(10.dp))

            // Note + date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCredit) stringResource(R.string.khata_credit_label)
                           else stringResource(R.string.khata_payment_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCredit) DangerRed else SuccessGreen
                )
                if (entry.note.isNotBlank()) {
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Amount + running balance
            Column(horizontalAlignment = Alignment.End) {
                val sign = if (isCredit) "+" else "−"
                MoneyText(
                    formatted = "$sign ${Formatters.money(entry.amount, currencySymbol)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isCredit) DangerRed else SuccessGreen
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.khata_balance_label) + " ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    MoneyText(
                        formatted = Formatters.money(runningBalance, currencySymbol),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.khata_delete_entry),
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
