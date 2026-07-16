package com.expensetracker.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
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
import com.expensetracker.app.util.ExportRow
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.PdfExporter
import com.expensetracker.app.util.ReceiptPhotoStore
import com.expensetracker.app.util.UpiPaymentHelper
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.expensetracker.app.viewmodel.KhataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataDetailScreen(
    partyId: Long,
    khataViewModel: KhataViewModel,
    expenseViewModel: ExpenseViewModel,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {}
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
    var showMarkPaidConfirm by remember { mutableStateOf(false) }
    var viewingPhotoPath by remember { mutableStateOf<String?>(null) }

    // "Request via UPI" is only offered when: they owe the user money, the outstanding
    // balance clears the ₹1 floor, the currency is INR, and the user has set their own
    // UPI ID in Settings. See FEATURE_SPEC_KHATA_UPI_PAYMENTS.md §3b/§10.2.
    val showUpiButton = !isIOwe && balance >= 1.0 &&
        CurrencyLocaleMapper.isInrSymbol(currencySymbol) && myUpiId.isNotBlank()

    // Shown instead of the "Request via UPI" button above when the only thing missing is the
    // user's own UPI ID — surfaces the fix right where it's needed, before they hit Send
    // Reminder and end up sending a message with no way for the party to actually pay via UPI.
    val showAddUpiHint = !isIOwe && balance >= 1.0 &&
        CurrencyLocaleMapper.isInrSymbol(currencySymbol) && myUpiId.isBlank()

    // "Pay via UPI" — the reverse direction: this app's user owes the party, and the party's
    // own UPI ID was captured on the party. Unlike "Request via UPI" this never goes through
    // WhatsApp — it's an app-initiated ACTION_VIEW that Android hands straight to whatever UPI
    // app is installed, so the upi:// scheme actually works here (the WhatsApp-linkify problem
    // only applies to *shared* text, not a direct in-app open).
    // Shown with either a VPA on file (one-tap payment) or just a phone number (opens the
    // UPI app so the user can search the contact themselves) — see payViaUpi() below for why
    // phone-only can't auto-resolve to a VPA.
    val showPayUpiButton = isIOwe && balance >= 1.0 &&
        CurrencyLocaleMapper.isInrSymbol(currencySymbol) &&
        (!party.upiId.isNullOrBlank() || party.phone.isNotBlank())

    // "Mark as Paid" — a general quick-settle shortcut available for any party regardless of
    // direction or currency (not just the UPI flows above), since a debt can be settled in cash,
    // bank transfer, etc. Logs one PAYMENT entry for the full outstanding balance.
    val showMarkPaidButton = balance > 0.0

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
    val reminderMsgOwe     = stringResource(R.string.khata_reminder_msg_owe, party.name, plainAmount)
    val reminderMsgCredit  = stringResource(R.string.khata_reminder_msg_credit, party.name, plainAmount)
    // Signed only if the user has actually set their name in Settings — falling back to a
    // placeholder like "Me" would sign a stranger's reminder with a literal word "Me", which
    // reads as broken rather than just anonymous. No name set = no signature line at all.
    val signature = if (displayName.isNotBlank())
        "\n\n" + stringResource(R.string.khata_reminder_signature, displayName)
    else ""
    // Play Store link appended in code (not in the translated string resources) since the URL
    // itself needs no localization — this reaches the recipient even if they don't have the app
    // yet, which is often the case for a Khata reminder sent to someone outside the user base.
    val reminderMsg = (if (isIOwe) reminderMsgCredit else reminderMsgOwe) + signature +
        "\n\n📲 play.google.com/store/apps/details?id=${context.packageName}"

    // Bill/receipt photos attached to this party's CREDIT entries (see AddKhataEntrySheet's
    // photo attach UI) — collected here so both WhatsApp share paths below can attach them.
    // Khata has no per-entry "settled" flag (payments reduce the whole-party balance, not a
    // specific credit), so this is every CREDIT entry's photo, not just "unpaid" ones; that
    // matches what "Send Reminder" already means here — it's only ever shown while the party
    // has a positive balance (see KhataBalanceCard's `!isSettled` gate above).
    val billPhotoUris = remember(entries) {
        entries.filter { it.type == KhataEntryEntity.TYPE_CREDIT && !it.photoPath.isNullOrBlank() }
            .map { ReceiptPhotoStore.uriFor(context, it.photoPath!!) }
    }

    // Shared by the plain reminder and the "Request via UPI" share. Both prefer
    // Intent.ACTION_VIEW's phone-prefilled click-to-chat link when there's nothing to attach —
    // it opens straight into the right conversation. But that API is text-only; the moment
    // there's an image to attach (a bill photo here, the UPI QR in sendUpiPaymentQr below) this
    // switches to ACTION_SEND(_MULTIPLE), which can carry images but can't pre-fill a recipient,
    // so WhatsApp opens its own contact/chat picker instead — same trade-off already made by
    // the QR share, now made consistently whenever a photo is actually being sent.
    fun sendWhatsAppMessage(message: String) {
        if (billPhotoUris.isEmpty()) {
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
            return
        }

        // Bill photo(s) attached — ACTION_SEND_MULTIPLE so WhatsApp gets every bill in one
        // share (it supports multi-image sends with a single caption).
        val images = ArrayList(billPhotoUris)
        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, images)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, images)
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp.w4b")
            }
            try {
                context.startActivity(fallbackIntent)
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(context, whatsappNotInstalled, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendWhatsApp() = sendWhatsAppMessage(reminderMsg)

    // "Share payment link" from the UPI sheet — sends the actual QR *image* because WhatsApp
    // only auto-linkifies http(s) URLs; a `upi://pay` link shows up as inert, ugly-looking
    // plain text in a chat bubble (percent-encoded characters and all), with zero functional
    // benefit once the scannable QR is already attached — so it's deliberately left out of the
    // caption. No copy-link fallback: sharing always goes through this same WhatsApp flow (§10.3).
    // Bill photos (if any) ride along with the QR in the same multi-image share.
    fun sendUpiPaymentQr(qrImageUri: Uri) {
        val caption = reminderMsg
        if (billPhotoUris.isEmpty()) {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, qrImageUri)
                putExtra(Intent.EXTRA_TEXT, caption)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp")
            }
            try {
                context.startActivity(sendIntent)
            } catch (e: ActivityNotFoundException) {
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, qrImageUri)
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
            showUpiSheet = false
            return
        }

        val images = ArrayList(listOf(qrImageUri) + billPhotoUris)
        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, images)
            putExtra(Intent.EXTRA_TEXT, caption)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            val fallbackIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, images)
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
        showUpiSheet = false
    }

    val noUpiAppInstalled = stringResource(R.string.khata_no_upi_app)
    val markedAsPaidNote = stringResource(R.string.khata_marked_as_paid_note)
    val payViaUpiPhoneFallbackTemplate = stringResource(R.string.khata_pay_via_upi_phone_fallback)

    // "Pay via UPI" — app-initiated ACTION_VIEW, so Android resolves it to an installed UPI
    // app without the WhatsApp-linkify problem that affects the "Request via UPI" share.
    //
    // Two paths depending on what's on file for the party — most people won't have typed in
    // (or been asked to dictate) someone else's UPI ID, so this can't assume a VPA exists:
    //  - VPA on file: one-tap payment, amount and payee pre-filled, exactly like before.
    //  - No VPA but a phone number is on file: there's no public API this app can use to
    //    resolve a phone number to a VPA (that lookup only exists inside licensed UPI/PSP
    //    apps themselves), so instead this copies the phone number to the clipboard and opens
    //    the user's UPI app directly so they can search the contact by number themselves.
    fun payViaUpi() {
        val vpa = party.upiId
        if (!vpa.isNullOrBlank()) {
            val uri = UpiPaymentHelper.buildUpiUri(vpa, party.name, balance, "Khata: ${party.name}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, noUpiAppInstalled, Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (party.phone.isNotBlank()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(ClipData.newPlainText("phone", party.phone))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"))
            try {
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    String.format(payViaUpiPhoneFallbackTemplate, party.name),
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, noUpiAppInstalled, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // "Mark as Paid" confirm — logs a single PAYMENT entry for the full outstanding balance,
    // settling the party regardless of how the money actually changed hands.
    fun markAsPaid() {
        khataViewModel.addEntry(
            partyId = partyId,
            amount = balance,
            note = markedAsPaidNote,
            type = KhataEntryEntity.TYPE_PAYMENT
        )
        showMarkPaidConfirm = false
    }

    // ── PDF statement export ────────────────────────────────────────────────────
    // Same PdfExporter/ExportRow the Dashboard's monthly report uses (see DashboardScreen.kt).
    // Rows are signed so they sum to the outstanding balance like a real ledger: a CREDIT entry
    // shows as a positive amount (adds to what's owed), a PAYMENT shows as negative (reduces
    // it) — the Type column spells out which, so the sign alone never has to carry the meaning.
    val exportReportTitle = stringResource(R.string.khata_export_report_title)
    val exportColDate = stringResource(R.string.date_label)
    val exportColType = stringResource(R.string.khata_export_col_type)
    val exportColDescription = stringResource(R.string.description_label)
    val exportColAmount = stringResource(R.string.amount_label)
    val exportTotalLabel = stringResource(R.string.khata_export_total_label)
    val exportEmptyLabel = stringResource(R.string.khata_export_no_entries)
    val exportChooserTitle = stringResource(R.string.export_pdf_chooser_title)
    val exportStartedLabel = stringResource(R.string.export_started)
    val appNameStr = stringResource(R.string.app_name)
    // Optional business profile (Settings) — stamped onto the export in place of appNameStr
    // when set. See PdfExporter.kt's businessContactLine doc comment.
    val businessName by expenseViewModel.businessName.collectAsState()
    val businessAddress by expenseViewModel.businessAddress.collectAsState()
    val businessPhone by expenseViewModel.businessPhone.collectAsState()
    val exportAppName = businessName.ifBlank { appNameStr }
    val exportBusinessContactLine = listOfNotNull(
        businessAddress.takeIf { it.isNotBlank() },
        businessPhone.takeIf { it.isNotBlank() }
    ).joinToString("   •   ").takeIf { it.isNotBlank() }
    val poweredByFooter = stringResource(R.string.powered_by_footer)
    val creditTypeLabel = stringResource(R.string.khata_export_type_credit)
    val paymentTypeLabel = stringResource(R.string.khata_export_type_payment)
    // Resolved here (composable scope) rather than inside exportStatementAsPdf() below, since
    // stringResource() can only be called from composable code and that function is a plain fun.
    val exportBalanceLabel = stringResource(R.string.khata_export_balance_label, Formatters.money(balance, currencySymbol))

    val exportRows = remember(entries, currencySymbol) {
        entries.sortedByDescending { it.date }.map { e ->
            val isCredit = e.type == KhataEntryEntity.TYPE_CREDIT
            ExportRow(
                dateLabel = e.date,
                categoryLabel = if (isCredit) creditTypeLabel else paymentTypeLabel,
                description = e.note,
                amountLabel = Formatters.money(if (isCredit) e.amount else -e.amount, currencySymbol)
            )
        }
    }

    fun exportStatementAsPdf() {
        if (exportRows.isEmpty()) {
            Toast.makeText(context, exportEmptyLabel, Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, exportStartedLabel, Toast.LENGTH_SHORT).show()
        val uri = PdfExporter.export(
            context = context,
            appName = exportAppName,
            reportTitle = exportReportTitle,
            monthLabel = party.name,
            customerIdLabel = exportBalanceLabel,
            businessContactLine = exportBusinessContactLine,
            colDate = exportColDate,
            colCategory = exportColType,
            colDescription = exportColDescription,
            colAmount = exportColAmount,
            totalLabel = exportTotalLabel,
            totalValue = Formatters.money(balance, currencySymbol),
            footerText = poweredByFooter,
            emptyLabel = exportEmptyLabel,
            rows = exportRows
        )
        PdfExporter.shareOrSave(context, uri, exportChooserTitle)
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
                        IconButton(onClick = { exportStatementAsPdf() }) {
                            Icon(Icons.Filled.PictureAsPdf, contentDescription = stringResource(R.string.export_pdf))
                        }
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
                    creditLimit = party.creditLimit,
                    creditLimitFraction = khataViewModel.creditLimitFraction(party, balance),
                    onSendReminder = { sendWhatsApp() },
                    showUpiButton = showUpiButton,
                    onRequestUpi = { showUpiSheet = true },
                    showAddUpiHint = showAddUpiHint,
                    onAddUpiId = onOpenSettings,
                    showPayUpiButton = showPayUpiButton,
                    onPayUpi = { payViaUpi() },
                    showMarkPaidButton = showMarkPaidButton,
                    onMarkPaid = { showMarkPaidConfirm = true }
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
                                isOverdue = khataViewModel.isOverdue(entry),
                                isDueSoon = khataViewModel.isDueSoon(entry),
                                onDelete = { pendingDelete = entry },
                                onViewPhoto = { viewingPhotoPath = it }
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
            onSave = { amount, note, date, type, dueDate, photoPath ->
                khataViewModel.addEntry(partyId, amount, note, date, type, dueDate, photoPath)
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
            myUpiId = myUpiId,
            onSave = { id, name, phone, direction, _, _, upiId, creditLimit ->
                // Editing an existing party — initial amount fields are hidden, pass-through ignored
                khataViewModel.saveParty(id, name, phone, direction, upiId = upiId, creditLimit = creditLimit)
                showEditParty = false
            },
            onDismiss = { showEditParty = false }
        )
    }

    if (showMarkPaidConfirm) {
        AlertDialog(
            onDismissRequest = { showMarkPaidConfirm = false },
            title = { Text(stringResource(R.string.khata_mark_as_paid)) },
            text = { Text(stringResource(R.string.khata_mark_as_paid_confirm, Formatters.money(balance, currencySymbol))) },
            confirmButton = {
                TextButton(onClick = { markAsPaid() }) {
                    Text(stringResource(R.string.khata_mark_as_paid))
                }
            },
            dismissButton = {
                TextButton(onClick = { showMarkPaidConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showUpiSheet) {
        RequestUpiPaymentSheet(
            myUpiId = myUpiId,
            // Deliberately NOT falling back to "Me" here (unlike the WhatsApp text signature
            // below) — that fallback is fine as a signed reminder line, but wrong to embed as
            // the payee name a stranger's UPI app would show them. UpiPaymentHelper.buildUpiUri
            // omits the `pn` param entirely when this is blank.
            payeeDisplayName = displayName,
            partyName = party.name,
            amount = balance,
            currencySymbol = currencySymbol,
            onShareQr = { qrUri -> sendUpiPaymentQr(qrUri) },
            onDismiss = { showUpiSheet = false }
        )
    }

    viewingPhotoPath?.let { path ->
        androidx.compose.ui.window.Dialog(onDismissRequest = { viewingPhotoPath = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            ) {
                androidx.compose.foundation.Image(
                    painter = coil.compose.rememberAsyncImagePainter(java.io.File(path)),
                    contentDescription = stringResource(R.string.khata_receipt_photo_label),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().height(420.dp)
                )
                IconButton(
                    onClick = { viewingPhotoPath = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close), tint = Color.White)
                }
            }
        }
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
    creditLimit: Double? = null,
    creditLimitFraction: Float? = null,
    onSendReminder: () -> Unit,
    showUpiButton: Boolean = false,
    onRequestUpi: () -> Unit = {},
    showAddUpiHint: Boolean = false,
    onAddUpiId: () -> Unit = {},
    showPayUpiButton: Boolean = false,
    onPayUpi: () -> Unit = {},
    showMarkPaidButton: Boolean = false,
    onMarkPaid: () -> Unit = {}
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

            // Credit limit progress bar — only shown when a limit is actually set. Purely a
            // local warning, not a hard cap (the app never blocks adding a credit entry that
            // would push the balance over it).
            if (creditLimit != null && creditLimitFraction != null && !isSettled) {
                val fraction = creditLimitFraction.coerceIn(0f, 1f)
                val isOverLimit = creditLimitFraction >= 1f
                val isNearLimit = creditLimitFraction >= 0.8f
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isOverLimit) Color(0xFFFFCDD2) else Color.White)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = when {
                            isOverLimit -> stringResource(R.string.khata_credit_limit_over, Formatters.money(creditLimit, currencySymbol))
                            isNearLimit -> stringResource(R.string.khata_credit_limit_near, Formatters.money(creditLimit, currencySymbol))
                            else -> stringResource(R.string.khata_credit_limit_of, Formatters.money(creditLimit, currencySymbol))
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

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

            // Nudge shown instead of the button above when the only reason "Request via UPI"
            // is hidden is a missing UPI ID for this app's own user — tapping it jumps to
            // Settings so it can be fixed right here, before a reminder goes out with no way
            // for the party to actually pay via UPI.
            if (showAddUpiHint) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    onClick = onAddUpiId,
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.16f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.khata_add_upi_hint),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.80f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // "Pay via UPI" — the reverse of Request via UPI: this app's user owes the party,
            // and the party's own UPI ID was captured. App-initiated (not shared), so the
            // upi:// deep link works directly here.
            if (showPayUpiButton) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onPayUpi,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.70f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.khata_pay_via_upi))
                }
            }

            // "Mark as Paid" — general quick-settle shortcut, any direction/currency, lighter
            // weight than the buttons above since it's a closing action, not a payment method.
            if (showMarkPaidButton) {
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = onMarkPaid) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.90f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.khata_mark_as_paid),
                        color = Color.White.copy(alpha = 0.90f),
                        style = MaterialTheme.typography.labelMedium
                    )
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
    isOverdue: Boolean = false,
    isDueSoon: Boolean = false,
    onDelete: () -> Unit,
    onViewPhoto: (String) -> Unit = {}
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
            // Receipt photo thumbnail, if attached — tap to view full screen.
            entry.photoPath?.let { path ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onViewPhoto(path) }
                ) {
                    androidx.compose.foundation.Image(
                        painter = coil.compose.rememberAsyncImagePainter(java.io.File(path)),
                        contentDescription = stringResource(R.string.khata_receipt_photo_label),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.width(10.dp))
            }

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
                if (entry.dueDate != null) {
                    Spacer(Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            isOverdue -> DangerRed.copy(alpha = 0.12f)
                            isDueSoon -> Color(0xFFFFA000).copy(alpha = 0.14f)
                            else -> TextMuted.copy(alpha = 0.10f)
                        }
                    ) {
                        Text(
                            text = stringResource(
                                if (isOverdue) R.string.khata_due_overdue else R.string.khata_due_on,
                                entry.dueDate
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                isOverdue -> DangerRed
                                isDueSoon -> Color(0xFFE65100)
                                else -> TextMuted
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
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
