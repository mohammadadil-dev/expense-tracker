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
import androidx.compose.material.icons.filled.FamilyRestroom
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
import com.expensetracker.app.data.FamilyMemberEntity
import com.expensetracker.app.util.Formatters

/**
 * Dashboard card that shows per-member spending for the current month.
 *
 * Shown only when Family Mode is enabled. Tapping the settings icon opens
 * [FamilySetupSheet].
 */
@Composable
fun FamilyModeCard(
    members: List<FamilyMemberEntity>,
    monthExpenses: List<ExpenseEntity>,
    currencySymbol: String,
    onSetupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Compute per-member totals for the current month
    val memberTotals: Map<Long?, Double> = monthExpenses
        .groupBy { it.memberId }
        .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

    val totalSpent = monthExpenses.sumOf { it.amount }
    val maxSpent = memberTotals.values.maxOrNull() ?: 1.0

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.FamilyRestroom,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.family_mode_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSetupClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.family_setup),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (members.isEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.family_no_members_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(Modifier.height(12.dp))

                // Per-member spending bars
                members.forEach { member ->
                    val memberColor = try {
                        Color(android.graphics.Color.parseColor(member.colorHex))
                    } catch (_: Exception) { MaterialTheme.colorScheme.primary }

                    val memberSpent = memberTotals[member.id] ?: 0.0
                    val fraction = if (maxSpent > 0) (memberSpent / maxSpent).toFloat().coerceIn(0f, 1f) else 0f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar dot
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(memberColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val label = member.emoji.ifBlank { member.name.take(1).uppercase() }
                            Text(
                                label,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    member.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                MoneyText(
                                    formatted = Formatters.money(memberSpent, currencySymbol),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = memberColor
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = memberColor,
                                trackColor = memberColor.copy(alpha = 0.15f)
                            )
                        }
                    }
                }

                // Shared / unassigned row
                val sharedSpent = memberTotals[null] ?: 0.0
                if (sharedSpent > 0.0) {
                    val fraction = if (maxSpent > 0) (sharedSpent / maxSpent).toFloat().coerceIn(0f, 1f) else 0f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(MaterialTheme.colorScheme.outline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏠", style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    stringResource(R.string.family_shared),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                MoneyText(
                                    formatted = Formatters.money(sharedSpent, currencySymbol),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.outline,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                MoneyText(
                    formatted = stringResource(R.string.family_total_spent, currencySymbol, totalSpent),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
