package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.data.PaymentAccountEntity
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.accountDisplayName
import com.expensetracker.app.util.accountEmoji

/**
 * Dashboard card showing this month's spend broken down by payment account (Cash / Bank /
 * Card / etc.). Deliberately mirrors [FamilyModeCard]'s layout — same visual language for
 * "spend broken down by a tag" cards. Only rendered by the caller when at least two distinct
 * tagged accounts have spend this month, so it never clutters the dashboard for users who
 * don't bother tagging expenses with an account.
 */
@Composable
fun AccountBreakdownCard(
    accounts: List<PaymentAccountEntity>,
    monthExpenses: List<ExpenseEntity>,
    currencySymbol: String,
    onManageAccounts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalsByAccount: Map<Long?, Double> = monthExpenses
        .groupBy { it.accountId }
        .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
    val maxSpent = totalsByAccount.values.maxOrNull() ?: 1.0
    val taggedAccounts = accounts.filter { (totalsByAccount[it.id] ?: 0.0) > 0.0 }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.account_breakdown_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onManageAccounts, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.manage_payment_accounts),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            taggedAccounts.forEach { account ->
                val spent = totalsByAccount[account.id] ?: 0.0
                val fraction = if (maxSpent > 0) (spent / maxSpent).toFloat().coerceIn(0f, 1f) else 0f
                val accountColor = runCatching { Color(android.graphics.Color.parseColor(account.colorHex)) }
                    .getOrDefault(MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(28.dp).background(accountColor.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(accountEmoji(account.type), style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                accountDisplayName(account),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            MoneyText(
                                formatted = Formatters.money(spent, currencySymbol),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = accountColor
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = accountColor,
                            trackColor = accountColor.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // Untagged spend — expenses with no accountId, shown the same way Family Mode
            // shows a "Shared" bucket for unassigned expenses.
            val untagged = totalsByAccount[null] ?: 0.0
            if (untagged > 0.0) {
                val fraction = if (maxSpent > 0) (untagged / maxSpent).toFloat().coerceIn(0f, 1f) else 0f
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("❔", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(R.string.payment_account_not_set),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            MoneyText(
                                formatted = Formatters.money(untagged, currencySymbol),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.outline,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }
    }
}
