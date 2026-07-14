package com.expensetracker.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.data.KhataPartyEntity
import com.expensetracker.app.ui.components.AddEditKhataPartySheet
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
import com.expensetracker.app.util.UpiPaymentHelper
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.expensetracker.app.viewmodel.KhataViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataScreen(
    khataViewModel: KhataViewModel,
    expenseViewModel: ExpenseViewModel,
    onOpenDetail: (Long) -> Unit
) {
    val context = LocalContext.current
    val currencySymbol  by expenseViewModel.currencySymbol.collectAsState()
    val displayName     by expenseViewModel.displayName.collectAsState()
    val myUpiId         by expenseViewModel.myUpiId.collectAsState()
    val allParties      by khataViewModel.allParties.collectAsState()
    val iOweParties     by khataViewModel.iOweParties.collectAsState()
    val theyOweParties  by khataViewModel.theyOweParties.collectAsState()
    val allEntries      by khataViewModel.allEntries.collectAsState()

    var showIOwe     by remember { mutableStateOf(true) }
    var showAddParty by remember { mutableStateOf(false) }
    var editingParty by remember { mutableStateOf<KhataPartyEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<KhataPartyEntity?>(null) }
    var heroVisible  by remember { mutableStateOf(false) }
    // Party currently showing the "Request via UPI" sheet, opened from a compact icon
    // button on its list row (see FEATURE_SPEC_KHATA_UPI_PAYMENTS.md §3b/§10.1).
    var upiRequestParty by remember { mutableStateOf<KhataPartyEntity?>(null) }
    // Party pending a "Mark as Paid" confirm dialog.
    var markPaidParty by remember { mutableStateOf<KhataPartyEntity?>(null) }
    val whatsappNotInstalled = stringResource(R.string.khata_whatsapp_not_installed)
    val senderDefault = stringResource(R.string.khata_sender_name_default)
    val noUpiAppInstalled = stringResource(R.string.khata_no_upi_app)
    val markedAsPaidNote = stringResource(R.string.khata_marked_as_paid_note)

    // "Pay via UPI" — app-initiated ACTION_VIEW on the party's own UPI ID (I_OWE parties
    // only). Not shared via WhatsApp, so the upi:// scheme resolves directly to an installed
    // UPI app without the WhatsApp-linkify problem that affects the "Request via UPI" share.
    fun payViaUpi(party: KhataPartyEntity, balance: Double) {
        val vpa = party.upiId ?: return
        val uri = UpiPaymentHelper.buildUpiUri(vpa, party.name, balance, "Khata: ${party.name}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, noUpiAppInstalled, Toast.LENGTH_SHORT).show()
        }
    }

    val totalIOwe    = khataViewModel.totalIOwe(allParties, allEntries)
    val totalTheyOwe = khataViewModel.totalTheyOwe(allParties, allEntries)

    LaunchedEffect(Unit) { heroVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBlobBackground(
            blobColors = listOf(NeonCyan, NeonPink, NeonTeal),
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            // ── No TopAppBar — hero header is inline ──
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { editingParty = null; showAddParty = true },
                    containerColor = AccentIndigo
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.khata_add_party))
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ── Animated hero header ───────────────────────────────────────
                AnimatedVisibility(
                    visible = heroVisible,
                    enter = slideInVertically(tween(500, easing = FastOutSlowInEasing)) { -it / 2 } +
                            fadeIn(tween(500))
                ) {
                    KhataHeroHeader(
                        totalIOwe    = totalIOwe,
                        totalTheyOwe = totalTheyOwe,
                        iOweCount    = iOweParties.size,
                        theyOweCount = theyOweParties.size,
                        currencySymbol = currencySymbol
                    )
                }

                // ── Direction toggle with badges ───────────────────────────────
                KhataToggle(
                    showIOwe      = showIOwe,
                    iOweCount     = iOweParties.size,
                    theyOweCount  = theyOweParties.size,
                    onToggle      = { showIOwe = it },
                    modifier      = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(12.dp))

                // ── Party list ─────────────────────────────────────────────────
                val displayedParties = if (showIOwe) iOweParties else theyOweParties
                val emptyMsg = if (showIOwe)
                    stringResource(R.string.khata_no_parties_i_owe)
                else
                    stringResource(R.string.khata_no_parties_they_owe)

                AnimatedContent(
                    targetState = displayedParties,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label = "khataList"
                ) { parties ->
                    if (parties.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emptyMsg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(parties, key = { it.id }) { party ->
                                val balance = khataViewModel.balanceForParty(party.id, allEntries)
                                val showUpiButton = party.direction == KhataPartyEntity.DIRECTION_THEY_OWE &&
                                    balance >= 1.0 && CurrencyLocaleMapper.isInrSymbol(currencySymbol) &&
                                    myUpiId.isNotBlank()
                                // Reverse direction — app user owes the party, using the party's
                                // own UPI ID. App-initiated open, not shared via WhatsApp.
                                val showPayUpiButton = party.direction == KhataPartyEntity.DIRECTION_I_OWE &&
                                    balance >= 1.0 && CurrencyLocaleMapper.isInrSymbol(currencySymbol) &&
                                    !party.upiId.isNullOrBlank()
                                // General quick-settle — any direction, any currency.
                                val showMarkPaidButton = balance > 0.0
                                KhataPartyCard(
                                    party          = party,
                                    balance        = balance,
                                    currencySymbol = currencySymbol,
                                    onClick        = { onOpenDetail(party.id) },
                                    onEdit         = { editingParty = party; showAddParty = true },
                                    onDelete       = { pendingDelete = party },
                                    showUpiButton  = showUpiButton,
                                    onRequestUpi   = { upiRequestParty = party },
                                    showPayUpiButton   = showPayUpiButton,
                                    onPayUpi           = { payViaUpi(party, balance) },
                                    showMarkPaidButton = showMarkPaidButton,
                                    onMarkPaid         = { markPaidParty = party }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add/Edit sheet ──────────────────────────────────────────────────────
    if (showAddParty) {
        AddEditKhataPartySheet(
            initial          = editingParty,
            defaultDirection = if (showIOwe) KhataPartyEntity.DIRECTION_I_OWE
                               else KhataPartyEntity.DIRECTION_THEY_OWE,
            currencySymbol   = currencySymbol,
            myUpiId          = myUpiId,
            onSave    = { id, name, phone, direction, initialAmount, initialNote, upiId ->
                khataViewModel.saveParty(id, name, phone, direction, initialAmount, initialNote, upiId)
                showAddParty = false; editingParty = null
            },
            onDismiss = { showAddParty = false; editingParty = null }
        )
    }

    upiRequestParty?.let { party ->
        val balance = khataViewModel.balanceForParty(party.id, allEntries)
        // Same message construction as KhataDetailScreen's reminder — this button only ever
        // shows for THEY_OWE parties, so the "credit" wording always applies here.
        val plainAmount = run {
            val formatted = String.format(java.util.Locale.US, "%.2f", balance)
            if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) "SAR $formatted"
            else "$currencySymbol $formatted"
        }
        val reminderMsg = stringResource(
            R.string.khata_reminder_msg_owe, party.name, plainAmount, displayName.ifBlank { senderDefault }
        ) + "\n\n📲 play.google.com/store/apps/details?id=${context.packageName}"

        RequestUpiPaymentSheet(
            myUpiId = myUpiId,
            // Not falling back to "Me" here — that's fine as a signed reminder line (below),
            // but wrong to embed as the payee name shown on a stranger's UPI app. buildUpiUri
            // omits the `pn` param entirely when this is blank.
            payeeDisplayName = displayName,
            partyName = party.name,
            amount = balance,
            currencySymbol = currencySymbol,
            // Sends the actual QR *image*, not the raw upi:// link — WhatsApp only auto-linkifies
            // http(s) URLs, so that link shows up as inert, ugly percent-encoded plain text with
            // no functional benefit once the scannable QR is attached, so it's left out of the
            // caption. The image goes out via a generic ACTION_SEND (no phone-number prefill
            // possible for image shares), so WhatsApp opens its own contact picker here.
            onShareQr = { qrUri ->
                val caption = reminderMsg
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, qrUri)
                    putExtra(Intent.EXTRA_TEXT, caption)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setPackage("com.whatsapp")
                }
                try {
                    context.startActivity(sendIntent)
                } catch (e: ActivityNotFoundException) {
                    val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, qrUri)
                        putExtra(Intent.EXTRA_TEXT, caption)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setPackage("com.whatsapp.w4b")
                    }
                    try {
                        context.startActivity(fallbackIntent)
                    } catch (e2: ActivityNotFoundException) {
                        Toast.makeText(context, whatsappNotInstalled, Toast.LENGTH_SHORT).show()
                    }
                }
                upiRequestParty = null
            },
            onDismiss = { upiRequestParty = null }
        )
    }

    markPaidParty?.let { party ->
        val balance = khataViewModel.balanceForParty(party.id, allEntries)
        AlertDialog(
            onDismissRequest = { markPaidParty = null },
            title = { Text(stringResource(R.string.khata_mark_as_paid)) },
            text = { Text(stringResource(R.string.khata_mark_as_paid_confirm, Formatters.money(balance, currencySymbol))) },
            confirmButton = {
                TextButton(onClick = {
                    khataViewModel.addEntry(
                        partyId = party.id,
                        amount = balance,
                        note = markedAsPaidNote,
                        type = KhataEntryEntity.TYPE_PAYMENT
                    )
                    markPaidParty = null
                }) { Text(stringResource(R.string.khata_mark_as_paid)) }
            },
            dismissButton = {
                TextButton(onClick = { markPaidParty = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    pendingDelete?.let { party ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title   = { Text(stringResource(R.string.khata_delete_party)) },
            text    = { Text(stringResource(R.string.khata_delete_party_confirm, party.name)) },
            confirmButton = {
                TextButton(onClick = { khataViewModel.deleteParty(party); pendingDelete = null }) {
                    Text(stringResource(R.string.delete), color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

// ── Hero header ────────────────────────────────────────────────────────────────

@Composable
private fun KhataHeroHeader(
    totalIOwe: Double,
    totalTheyOwe: Double,
    iOweCount: Int,
    theyOweCount: Int,
    currencySymbol: String
) {
    val netAmount      = abs(totalTheyOwe - totalIOwe)
    val netIsPositive  = totalTheyOwe >= totalIOwe
    val allSettled     = totalIOwe == 0.0 && totalTheyOwe == 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 12.dp)
    ) {
        Text(
            text  = stringResource(R.string.khata_screen_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text  = stringResource(R.string.khata_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(Modifier.height(16.dp))

        // Two stat cards side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KhataStatCard(
                label          = stringResource(R.string.khata_total_i_owe),
                amount         = totalIOwe,
                count          = iOweCount,
                currencySymbol = currencySymbol,
                amountColor    = DangerRed,
                modifier       = Modifier.weight(1f)
            )
            KhataStatCard(
                label          = stringResource(R.string.khata_total_they_owe),
                amount         = totalTheyOwe,
                count          = theyOweCount,
                currencySymbol = currencySymbol,
                amountColor    = SuccessGreen,
                modifier       = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Net position badge
        Surface(
            shape = RoundedCornerShape(50),
            color = when {
                allSettled    -> SuccessGreen.copy(alpha = 0.10f)
                netIsPositive -> SuccessGreen.copy(alpha = 0.10f)
                else          -> DangerRed.copy(alpha = 0.10f)
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (allSettled || netIsPositive) Icons.Filled.TrendingUp
                                  else Icons.Filled.TrendingDown,
                    contentDescription = null,
                    tint     = if (allSettled || netIsPositive) SuccessGreen else DangerRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                MoneyText(
                    formatted = when {
                        allSettled    -> stringResource(R.string.net_all_settled)
                        netIsPositive -> stringResource(R.string.net_in_favour, Formatters.money(netAmount, currencySymbol))
                        else          -> stringResource(R.string.net_you_owe_net, Formatters.money(netAmount, currencySymbol))
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (allSettled || netIsPositive) SuccessGreen else DangerRed
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun KhataStatCard(
    label: String,
    amount: Double,
    count: Int,
    currencySymbol: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier  = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            MoneyText(
                formatted = Formatters.money(amount, currencySymbol),
                style     = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color     = amountColor
            )
            Spacer(Modifier.height(4.dp))
            // Party count badge
            Surface(
                shape = RoundedCornerShape(50),
                color = amountColor.copy(alpha = 0.10f)
            ) {
                Text(
                    text  = "$count ${if (count == 1) stringResource(R.string.label_party_singular) else stringResource(R.string.label_party_plural)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = amountColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ── Direction toggle ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KhataToggle(
    showIOwe: Boolean,
    iOweCount: Int,
    theyOweCount: Int,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(
            Triple(true,  R.string.khata_i_owe,    iOweCount),
            Triple(false, R.string.khata_they_owe, theyOweCount)
        ).forEach { (isIOwe, labelRes, count) ->
            val selected = showIOwe == isIOwe
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (selected) AccentIndigo else Color.Transparent)
                    .clickable { onToggle(isIOwe) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                BadgedBox(
                    badge = {
                        if (count > 0) {
                            Badge(
                                containerColor = if (selected) Color.White.copy(alpha = 0.9f)
                                                else AccentIndigo,
                                contentColor   = if (selected) AccentIndigo else Color.White
                            ) {
                                Text("$count", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                ) {
                    Text(
                        text  = stringResource(labelRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else TextSecondary
                    )
                }
            }
        }
    }
}

// ── Party card ─────────────────────────────────────────────────────────────────

@Composable
private fun KhataPartyCard(
    party: KhataPartyEntity,
    balance: Double,
    currencySymbol: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    showUpiButton: Boolean = false,
    onRequestUpi: () -> Unit = {},
    showPayUpiButton: Boolean = false,
    onPayUpi: () -> Unit = {},
    showMarkPaidButton: Boolean = false,
    onMarkPaid: () -> Unit = {}
) {
    val isSettled = balance <= 0.0
    Card(
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSettled) SuccessGreen.copy(alpha = 0.12f)
                        else AccentIndigo.copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Store,
                    contentDescription = null,
                    tint     = if (isSettled) SuccessGreen else AccentIndigo,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text       = party.name,
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (party.phone.isNotBlank()) {
                    Text(
                        text = party.phone,
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDirection = androidx.compose.ui.text.style.TextDirection.Ltr
                        ),
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                if (isSettled) {
                    Text(
                        text  = stringResource(R.string.khata_settled),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        maxLines = 1
                    )
                } else {
                    MoneyText(
                        formatted = Formatters.money(balance, currencySymbol),
                        style     = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color     = if (party.direction == KhataPartyEntity.DIRECTION_I_OWE) DangerRed else SuccessGreen
                    )
                    Text(
                        text  = stringResource(R.string.khata_outstanding),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }
            // Compact 32dp action icons (smaller than IconButton's 48dp default touch target)
            // so up to two of these plus the arrow don't crowd out the party name column.
            if (showUpiButton) {
                IconButton(onClick = onRequestUpi, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.QrCode,
                        contentDescription = stringResource(R.string.khata_request_via_upi),
                        tint = AccentIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (showPayUpiButton) {
                IconButton(onClick = onPayUpi, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.AccountBalance,
                        contentDescription = stringResource(R.string.khata_pay_via_upi),
                        tint = AccentIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (showMarkPaidButton) {
                IconButton(onClick = onMarkPaid, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = stringResource(R.string.khata_mark_as_paid),
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}
