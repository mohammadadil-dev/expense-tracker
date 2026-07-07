package com.expensetracker.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.SplitExpenseEntity
import com.expensetracker.app.data.SplitMemberEntity
import com.expensetracker.app.util.Settlement
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
    onDismiss: () -> Unit,
    onMarkPaid: (Settlement) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
                    SettlementRow(
                        settlement = settlement,
                        currencySymbol = currencySymbol,
                        onMarkPaid = { onMarkPaid(settlement) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SettlementRow(
    settlement: Settlement,
    currencySymbol: String,
    onMarkPaid: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
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

            OutlinedButton(
                onClick = onMarkPaid,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(stringResource(R.string.split_mark_paid), style = MaterialTheme.typography.labelMedium)
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
 *   _Tracked with Expense Tracker_ 🧾
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
    sb.appendLine(divider)
    sb.append("_${lFooter}_ 🧾")

    return sb.toString()
}
