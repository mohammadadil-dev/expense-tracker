package com.expensetracker.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.IncomeEntity
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.Formatters
import java.util.Locale

/**
 * Expandable income-tracking card for the current month.
 *
 * Shows the total logged income and — when expanded — lists individual income
 * entries with source, date, note and a delete button. An "Add income" button
 * is always visible to the right of the header row.
 */
@Composable
internal fun DashboardIncomeSection(
    monthIncomeTotal:   Double,
    monthIncomeEntries: List<IncomeEntity>,
    currencySymbol:     String,
    locale:             Locale,
    expanded:           Boolean,
    onExpandToggle:     () -> Unit,
    onAddIncome:        () -> Unit,
    onDeleteIncome:     (IncomeEntity) -> Unit,
    modifier:           Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border    = BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header row: total + expand toggle + Add button ────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = onExpandToggle
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = stringResource(R.string.income_history_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = SuccessGreen
                    )
                    MoneyText(
                        formatted = Formatters.money(monthIncomeTotal, currencySymbol),
                        style     = MaterialTheme.typography.titleMedium,
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis
                    )
                }
                TextButton(onClick = onAddIncome) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.add_income),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // ── Expandable entry list ─────────────────────────────────────────
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (monthIncomeEntries.isEmpty()) {
                        Text(
                            text  = stringResource(R.string.no_income_this_month),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        monthIncomeEntries.forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    MoneyText(
                                        formatted = Formatters.money(entry.amount, currencySymbol),
                                        style     = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = buildString {
                                            if (entry.source.isNotBlank()) {
                                                append(entry.source)
                                                append(" · ")
                                            }
                                            append(DateUtils.formatExpenseDate(entry.date, locale))
                                            if (entry.note.isNotBlank()) append(" · ${entry.note}")
                                        },
                                        style    = MaterialTheme.typography.labelSmall,
                                        color    = TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    // Badge for recurring income templates
                                    if (entry.isRecurring) {
                                        Spacer(Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Repeat,
                                                contentDescription = null,
                                                tint     = SuccessGreen,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(Modifier.width(3.dp))
                                            Text(
                                                text  = stringResource(R.string.income_recurring_badge),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SuccessGreen
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { onDeleteIncome(entry) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete),
                                        tint     = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
