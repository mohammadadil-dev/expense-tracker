package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.data.DebtPaymentEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.DebtInsights
import com.expensetracker.app.util.Formatters
import java.util.Locale

/**
 * One card in the Debts & Loans screen: direction badge + name + live outstanding balance,
 * an animated paid-off progress bar, an optional payoff projection + "pay off sooner" AI
 * insight (skipped when the debt has no monthly payment to project from), and an expandable
 * payment history list. Mirrors [BudgetOverviewCard]'s shadow/status-color styling and
 * [InsightFeedCard]'s emoji-badge insight row.
 */
@Composable
fun DebtListItem(
    progress: DebtInsights.DebtProgress,
    payments: List<DebtPaymentEntity>,
    currencySymbol: String,
    locale: Locale,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onRecordPayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleClosed: () -> Unit,
    onDeletePayment: (DebtPaymentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val debt = progress.debt
    val isOwe = debt.direction == DebtEntity.DIRECTION_OWE
    val directionColor = if (isOwe) DangerRed else SuccessGreen
    val shape = RoundedCornerShape(18.dp)

    Card(
        modifier = modifier.shadow(
            elevation = 10.dp,
            shape = shape,
            ambientColor = directionColor.copy(alpha = 0.22f),
            spotColor = directionColor.copy(alpha = 0.3f)
        ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = androidx.compose.foundation.LocalIndication.current,
                        onClick = onToggleExpand
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(directionColor.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isOwe) Icons.Filled.TrendingDown else Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = directionColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = debt.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    MoneyText(
                        formatted = stringResource(
                            R.string.debt_paid_of_principal,
                            Formatters.money(progress.totalPaid, currencySymbol),
                            Formatters.money(debt.principal, currencySymbol)
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    if (debt.isClosed) {
                        Text(
                            text = stringResource(R.string.debt_settled_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = SuccessGreen
                        )
                    } else {
                        MoneyText(
                            formatted = Formatters.money(progress.outstandingBalance, currencySymbol),
                            style = MaterialTheme.typography.titleMedium,
                            color = directionColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            Spacer(Modifier.height(12.dp))
            BudgetProgressBar(
                fraction = progress.paidFraction,
                color = if (debt.isClosed) SuccessGreen else directionColor,
                modifier = Modifier.fillMaxWidth().height(6.dp)
            )

            if (!debt.isClosed) {
                Spacer(Modifier.height(10.dp))
                if (progress.payoffDate != null && progress.monthsToPayoff != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Event, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(
                                R.string.debt_payoff_projection,
                                DateUtils.formatExpenseDate(progress.payoffDate.toString(), locale),
                                progress.monthsToPayoff
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (progress.monthsSavedWithExtra != null && progress.suggestedExtraPayment > 0.0) {
                        Spacer(Modifier.height(8.dp))
                        InsightFeedCard(
                            emoji = "💡",
                            text = stringResource(
                                R.string.debt_insight_accelerate,
                                progress.monthsSavedWithExtra,
                                Formatters.money(progress.suggestedExtraPayment, currencySymbol)
                            ),
                            accentColor = AccentIndigo
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.debt_no_emi_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!debt.isClosed) {
                    RecordPaymentChip(onClick = onRecordPayment)
                } else {
                    Spacer(Modifier)
                }
                Row {
                    IconButton(onClick = onToggleClosed) {
                        Icon(
                            imageVector = if (debt.isClosed) Icons.Filled.Undo else Icons.Filled.CheckCircle,
                            contentDescription = stringResource(if (debt.isClosed) R.string.debt_reopen else R.string.debt_mark_settled),
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = TextSecondary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = DangerRed)
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column {
                    Spacer(Modifier.height(4.dp))
                    Divider(color = BorderLight)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.debt_history_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    if (payments.isEmpty()) {
                        Text(
                            text = stringResource(R.string.debt_no_payments),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        payments.forEach { payment ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    MoneyText(
                                        formatted = Formatters.money(payment.amount, currencySymbol),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = buildString {
                                            append(DateUtils.formatExpenseDate(payment.date, locale))
                                            if (!payment.note.isNullOrBlank()) append(" · ${payment.note}")
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(onClick = { onDeletePayment(payment) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.delete),
                                        tint = TextMuted,
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

@Composable
private fun RecordPaymentChip(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(120),
        label = "recordPaymentPress"
    )

    Row(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(20.dp))
            .background(AccentIndigo.copy(alpha = 0.1f))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.debt_record_payment),
            style = MaterialTheme.typography.labelMedium,
            color = AccentIndigo,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
