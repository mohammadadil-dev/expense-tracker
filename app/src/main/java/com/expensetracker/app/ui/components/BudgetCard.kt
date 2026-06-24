package com.expensetracker.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.expensetracker.app.data.BudgetEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.ui.theme.WarningAmber
import com.expensetracker.app.util.Formatters

/** Green while comfortably under budget, amber once close, red once over. */
fun budgetStatusColor(fraction: Float): Color = when {
    fraction >= 1f -> DangerRed
    fraction >= 0.8f -> WarningAmber
    else -> SuccessGreen
}

/**
 * Dashboard card summarizing this month's single overall budget against actual spend so far.
 * Shows a prompt + CTA when no budget has been set yet; otherwise an animated progress bar,
 * a left/over-budget line, and a pacing hint ("₹X/day for the next Y days" — only for the live
 * current month, since "days remaining" is meaningless while browsing a past month).
 */
@Composable
fun BudgetOverviewCard(
    overallBudget: BudgetEntity?,
    totalSpend: Double,
    currencySymbol: String,
    daysRemaining: Int,
    onManageClick: () -> Unit
) {
    // Glow color tracks the budget's own status (green/amber/red) once one is set, so the
    // card's shadow quietly tells the same story as the progress bar inside it.
    val accentColor = if (overallBudget != null && overallBudget.amount > 0) {
        budgetStatusColor((totalSpend / overallBudget.amount).toFloat())
    } else {
        AccentIndigo
    }
    val shape = RoundedCornerShape(18.dp)
    Card(
        modifier = Modifier.shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = accentColor.copy(alpha = 0.3f),
            spotColor = accentColor.copy(alpha = 0.4f)
        ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.budget_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                ManageBudgetButton(onClick = onManageClick)
            }
            Spacer(Modifier.height(12.dp))

            if (overallBudget == null) {
                Text(
                    stringResource(R.string.budget_set_prompt),
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
                SetBudgetChip(onClick = onManageClick)
            } else {
                val fraction = if (overallBudget.amount > 0) {
                    (totalSpend / overallBudget.amount).toFloat()
                } else 0f
                val statusColor = budgetStatusColor(fraction)
                val remaining = overallBudget.amount - totalSpend

                MoneyText(
                    formatted = stringResource(
                        R.string.budget_spent_of,
                        Formatters.money(totalSpend, currencySymbol),
                        Formatters.money(overallBudget.amount, currencySymbol)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                BudgetProgressBar(fraction = fraction, color = statusColor, modifier = Modifier.fillMaxWidth().height(8.dp))
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (remaining < 0) {
                        MoneyText(
                            formatted = stringResource(R.string.budget_over_by, Formatters.money(-remaining, currencySymbol)),
                            style = MaterialTheme.typography.labelMedium,
                            color = DangerRed,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        MoneyText(
                            formatted = stringResource(R.string.budget_left, Formatters.money(remaining, currencySymbol)),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (remaining > 0 && daysRemaining > 0) {
                        MoneyText(
                            formatted = stringResource(
                                R.string.budget_pacing_hint,
                                Formatters.money(remaining / daysRemaining, currencySymbol),
                                daysRemaining
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/** Animated, rounded-corner progress bar behind the overall budget card.
 * [fraction] is the raw spend/budget ratio — can exceed 1 (over budget), so it's clamped only
 * for the bar's own width, not for the color/status decision made by the caller. */
@Composable
fun BudgetProgressBar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(450),
        label = "budgetProgress"
    )
    LinearProgressIndicator(
        progress = animatedFraction,
        modifier = modifier.clip(RoundedCornerShape(50)),
        color = color,
        trackColor = color.copy(alpha = 0.15f)
    )
}

@Composable
private fun ManageBudgetButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(120),
        label = "manageBudgetPress"
    )

    Row(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Savings, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentIndigo)
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.manage_budget),
            style = MaterialTheme.typography.labelMedium,
            color = AccentIndigo,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SetBudgetChip(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(120),
        label = "setBudgetPress"
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
        Text(
            text = stringResource(R.string.budget_set_cta),
            style = MaterialTheme.typography.labelMedium,
            color = AccentIndigo
        )
        Spacer(Modifier.width(4.dp))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
    }
}
