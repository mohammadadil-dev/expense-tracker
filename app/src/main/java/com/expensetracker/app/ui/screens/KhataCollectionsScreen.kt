package com.expensetracker.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.expensetracker.app.data.KhataPartyEntity
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonPink
import com.expensetracker.app.ui.theme.NeonTeal
import com.expensetracker.app.ui.theme.StreakOrange
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.ui.theme.WarningAmber
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.SmsFallback
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.expensetracker.app.viewmodel.KhataViewModel

/** One THEY_OWE party with an aging, unsettled balance. */
private data class AgingRow(val party: KhataPartyEntity, val balance: Double, val days: Int)

private val BUCKET_LABELS = listOf(
    R.string.khata_aging_bucket_0_30,
    R.string.khata_aging_bucket_31_60,
    R.string.khata_aging_bucket_61_90,
    R.string.khata_aging_bucket_90_plus
)

/**
 * Collections: an aging breakdown of who owes the user money (0–30 / 31–60 / 61–90 / 90+ days)
 * plus a bulk WhatsApp reminder flow. Reachable from KhataScreen's "They Owe" tab.
 *
 * Bulk sending is deliberately NOT automated — WhatsApp's own send action must still be tapped
 * by the user for every single message (see [BulkReminderDialog]). There is no accessibility
 * service, no background sending, and no auto-tapping of WhatsApp's UI; this only chains
 * Android's normal share/deep-link intents one at a time, which is the same mechanism the
 * existing single-party "Send Reminder" button already uses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataCollectionsScreen(
    khataViewModel: KhataViewModel,
    expenseViewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currencySymbol by expenseViewModel.currencySymbol.collectAsState()
    val displayName     by expenseViewModel.displayName.collectAsState()
    val theyOweParties  by khataViewModel.theyOweParties.collectAsState()
    val allEntries      by khataViewModel.allEntries.collectAsState()

    val whatsappNotInstalled = stringResource(R.string.khata_whatsapp_not_installed)
    val signatureTemplate    = stringResource(R.string.khata_reminder_signature)
    val reminderMsgTemplate  = stringResource(R.string.khata_reminder_msg_owe)
    val doneTemplate         = stringResource(R.string.khata_bulk_reminder_done)

    val agingRows = remember(theyOweParties, allEntries) {
        theyOweParties.mapNotNull { party ->
            val days = khataViewModel.agingDaysForParty(party.id, allEntries) ?: return@mapNotNull null
            AgingRow(party, khataViewModel.balanceForParty(party.id, allEntries), days)
        }.sortedByDescending { it.days }
    }

    var selectedBucket by remember { mutableStateOf<Int?>(null) }
    val filteredRows = if (selectedBucket == null) agingRows
        else agingRows.filter { khataViewModel.agingBucket(it.days) == selectedBucket }

    val selectedIds = remember { mutableStateListOf<Long>() }
    // Keep the selection valid as the underlying data changes (e.g. a party gets paid off
    // or a new one crosses into "overdue" while this screen is open).
    LaunchedEffect(agingRows) {
        val validIds = agingRows.map { it.party.id }.toSet()
        val stale = selectedIds.filterNot { it in validIds }
        selectedIds.removeAll(stale)
    }

    // ── Bulk-send queue state ───────────────────────────────────────────────
    var bulkQueue by remember { mutableStateOf<List<AgingRow>?>(null) }
    var bulkIndex by remember { mutableStateOf(0) }
    var bulkSent  by remember { mutableStateOf(0) }

    fun buildReminderMessage(row: AgingRow): String {
        val formatted = String.format(java.util.Locale.US, "%.2f", row.balance)
        val plainAmount = if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) "SAR $formatted"
            else "$currencySymbol $formatted"
        val signature = if (displayName.isNotBlank())
            "\n\n" + String.format(signatureTemplate, displayName)
        else ""
        return String.format(reminderMsgTemplate, row.party.name, plainAmount) + signature +
            "\n\n📲 play.google.com/store/apps/details?id=${context.packageName}"
    }

    fun sendWhatsAppTo(row: AgingRow) {
        val message = buildReminderMessage(row)
        // WhatsApp API requires E.164 WITHOUT the leading '+' and WITHOUT spaces/dashes.
        val phone = row.party.phone
            .replace("+", "").replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp") }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent2 = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp.w4b") }
            try {
                context.startActivity(intent2)
            } catch (e2: ActivityNotFoundException) {
                // WhatsApp isn't installed — fall back to a plain SMS (see SmsFallback's doc
                // comment for why this is text-only and why that's fine for this audience).
                if (!SmsFallback.send(context, row.party.phone, message)) {
                    Toast.makeText(context, whatsappNotInstalled, Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                    title = { Text(stringResource(R.string.khata_collections_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text  = stringResource(R.string.khata_collections_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(Modifier.height(12.dp))

                // ── Aging bucket summary row ─────────────────────────────────
                val bucketColors = listOf(SuccessGreen, WarningAmber, StreakOrange, DangerRed)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (0..3).forEach { bucket ->
                        val bucketRows = agingRows.filter { khataViewModel.agingBucket(it.days) == bucket }
                        AgingBucketChip(
                            label     = stringResource(BUCKET_LABELS[bucket]),
                            count     = bucketRows.size,
                            total     = bucketRows.sumOf { it.balance },
                            color     = bucketColors[bucket],
                            selected  = selectedBucket == bucket,
                            currencySymbol = currencySymbol,
                            onClick   = { selectedBucket = if (selectedBucket == bucket) null else bucket },
                            modifier  = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (agingRows.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.khata_collections_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // ── Select all row ────────────────────────────────────────
                    val selectableIds = filteredRows.filter { it.party.phone.isNotBlank() }.map { it.party.id }
                    val allSelected = selectableIds.isNotEmpty() && selectableIds.all { it in selectedIds }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (allSelected) selectedIds.removeAll(selectableIds)
                                else selectableIds.forEach { if (it !in selectedIds) selectedIds.add(it) }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = allSelected,
                            onCheckedChange = {
                                if (allSelected) selectedIds.removeAll(selectableIds)
                                else selectableIds.forEach { id -> if (id !in selectedIds) selectedIds.add(id) }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = AccentIndigo)
                        )
                        Text(
                            text  = stringResource(R.string.khata_collections_select_all),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredRows, key = { it.party.id }) { row ->
                            AgingPartyRow(
                                row = row,
                                currencySymbol = currencySymbol,
                                checked = row.party.id in selectedIds,
                                onCheckedChange = { checked ->
                                    if (checked) { if (row.party.id !in selectedIds) selectedIds.add(row.party.id) }
                                    else selectedIds.remove(row.party.id)
                                }
                            )
                        }
                    }

                    // ── Bulk send button ──────────────────────────────────────
                    if (selectedIds.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                val queue = agingRows.filter { it.party.id in selectedIds }
                                bulkQueue = queue
                                bulkIndex = 0
                                bulkSent = 0
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.khata_collections_remind_selected, selectedIds.size)
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Bulk reminder queue dialog ───────────────────────────────────────────
    val queue = bulkQueue
    if (queue != null) {
        if (bulkIndex < queue.size) {
            val row = queue[bulkIndex]
            BulkReminderDialog(
                row = row,
                index = bulkIndex,
                total = queue.size,
                currencySymbol = currencySymbol,
                onSend = {
                    sendWhatsAppTo(row)
                    bulkSent += 1
                    bulkIndex += 1
                },
                onSkip = { bulkIndex += 1 },
                onStop = { bulkQueue = null }
            )
        } else {
            Toast.makeText(context, String.format(doneTemplate, bulkSent, queue.size), Toast.LENGTH_LONG).show()
            bulkQueue = null
        }
    }
}

@Composable
private fun AgingBucketChip(
    label: String,
    count: Int,
    total: Double,
    color: Color,
    selected: Boolean,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) color.copy(alpha = 0.16f) else CardWhite,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) color else TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            if (count > 0) {
                Text(
                    text = Formatters.money(total, currencySymbol),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AgingPartyRow(
    row: AgingRow,
    currencySymbol: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val hasPhone = row.party.phone.isNotBlank()
    val bucket = when {
        row.days <= 30 -> SuccessGreen
        row.days <= 60 -> WarningAmber
        row.days <= 90 -> StreakOrange
        else           -> DangerRed
    }
    Card(
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = hasPhone,
                colors = CheckboxDefaults.colors(checkedColor = AccentIndigo)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AccentIndigo.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Store, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = row.party.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (hasPhone) row.party.phone else stringResource(R.string.khata_collections_no_phone),
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDirection = androidx.compose.ui.text.style.TextDirection.Ltr
                    ),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                MoneyText(
                    formatted = Formatters.money(row.balance, currencySymbol),
                    style     = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color     = SuccessGreen
                )
                Surface(shape = RoundedCornerShape(50), color = bucket.copy(alpha = 0.12f)) {
                    Text(
                        text = stringResource(R.string.khata_collections_days_overdue, row.days),
                        style = MaterialTheme.typography.labelSmall,
                        color = bucket,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BulkReminderDialog(
    row: AgingRow,
    index: Int,
    total: Int,
    currencySymbol: String,
    onSend: () -> Unit,
    onSkip: () -> Unit,
    onStop: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onStop,
        title = {
            Text(stringResource(R.string.khata_bulk_reminder_progress, index + 1, total))
        },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = index / total.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = row.party.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = Formatters.money(row.balance, currencySymbol),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DangerRed
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSend) { Text(stringResource(R.string.khata_bulk_reminder_send)) }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onSkip) { Text(stringResource(R.string.khata_bulk_reminder_skip)) }
                TextButton(onClick = onStop) { Text(stringResource(R.string.khata_bulk_reminder_stop)) }
            }
        }
    )
}
