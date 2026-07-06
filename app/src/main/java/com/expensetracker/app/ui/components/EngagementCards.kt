package com.expensetracker.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.R
import com.expensetracker.app.data.GoalEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.ui.theme.StreakOrange
import com.expensetracker.app.util.Formatters

// ── Streak Card ───────────────────────────────────────────────────────────────

/**
 * Full-width card showing daily logging streak, animated progress to next
 * milestone, and best-streak badge. No text ever wraps.
 */
@Composable
fun StreakCard(
    streak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    // Ordered milestone checkpoints
    val milestones = listOf(3, 7, 14, 30, 60, 100)
    val nextMilestone = milestones.firstOrNull { it > streak }
    val prevMilestone = if (nextMilestone != null)
        milestones.lastOrNull { it < nextMilestone && it <= streak } ?: 0
    else
        milestones.last { it <= streak }

    val rawProgress = when {
        nextMilestone == null -> 1f
        nextMilestone == prevMilestone -> 1f
        else -> ((streak - prevMilestone).toFloat() / (nextMilestone - prevMilestone)).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(900),
        label = "streakProgress"
    )

    // Icon/badge shown inside the circle
    val badgeEmoji = when {
        streak >= 100 -> "🏆"
        streak >= 60  -> "🥇"
        streak >= 30  -> "🥈"
        streak >= 14  -> "⭐"
        streak >= 7   -> "🔥"
        streak >= 3   -> "✨"
        else          -> "🔥"
    }
    // Next milestone label: emoji + number (language-agnostic)
    val nextMilestoneEmoji = when {
        nextMilestone == null  -> null
        nextMilestone <= 3     -> "✨"
        nextMilestone <= 7     -> "🔥"
        nextMilestone <= 14    -> "⭐"
        nextMilestone <= 30    -> "🥈"
        nextMilestone <= 60    -> "🥇"
        else                   -> "🏆"
    }

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(
            containerColor = StreakOrange.copy(alpha = 0.07f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Large emoji circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(StreakOrange.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(badgeEmoji, fontSize = 28.sp)
            }

            // Right-side content — all in a single column, nothing side-by-side
            Column(modifier = Modifier.weight(1f)) {
                // Label row: title on left, best on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.streak_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = StreakOrange.copy(alpha = 0.85f),
                        maxLines = 1
                    )
                    if (bestStreak > 0) {
                        Text(
                            text = "${stringResource(R.string.streak_best_label)}: $bestStreak",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                // Big number + "days" suffix on the same baseline
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$streak",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = StreakOrange
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.streak_days_suffix),
                        style = MaterialTheme.typography.bodyMedium,
                        color = StreakOrange.copy(alpha = 0.65f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Animated milestone progress bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = StreakOrange,
                    trackColor = StreakOrange.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(Modifier.height(5.dp))
                // Milestone hint below bar — emoji + number, no translatable text needed
                if (nextMilestone != null && nextMilestoneEmoji != null) {
                    Text(
                        text = "$nextMilestoneEmoji ${nextMilestone - streak} ${stringResource(R.string.streak_days_suffix)}  →  $nextMilestoneEmoji $nextMilestone",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "🏆 100+ ${stringResource(R.string.streak_days_suffix)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ── Payday Countdown Card ─────────────────────────────────────────────────────

/**
 * Full-width card. When not configured, the whole card is a clear CTA with
 * chevron + hint text. When configured, shows large countdown + daily budget
 * and a small edit button so the user can change the date inline.
 */
@Composable
fun PaydayCard(
    daysUntilPayday: Int?,
    dailyBudgetRemaining: Double?,
    currencySymbol: String,
    paydayDayOfMonth: Int = 0,
    onConfigurePayday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(
            containerColor = SuccessGreen.copy(alpha = 0.07f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.28f))
    ) {
        if (daysUntilPayday == null) {
            // ── NOT CONFIGURED: full card is a tap-to-setup CTA ──────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onConfigurePayday)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 28.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.payday_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen.copy(alpha = 0.85f),
                        maxLines = 1
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = stringResource(R.string.payday_not_set),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.payday_picker_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
                // Chevron arrow — visual affordance that this is tappable
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(26.dp)
                )
            }
        } else {
            // ── CONFIGURED: countdown + budget + edit button ──────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 28.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    // Label row with inline edit button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.payday_title),
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen.copy(alpha = 0.85f),
                            maxLines = 1
                        )
                        // Small edit chip — shows configured day-of-month + pencil
                        Box(
                            modifier = Modifier
                                .background(
                                    SuccessGreen.copy(alpha = 0.12f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(onClick = onConfigurePayday)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(10.dp)
                                )
                                if (paydayDayOfMonth > 0) {
                                    Text(
                                        text = stringResource(R.string.payday_day_label, paydayDayOfMonth),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    // Big countdown number
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$daysUntilPayday",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = SuccessGreen
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(
                            text = stringResource(R.string.payday_days_away),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessGreen.copy(alpha = 0.65f),
                            modifier = Modifier.padding(bottom = 8.dp),
                            maxLines = 1
                        )
                    }
                    // Daily budget (only when budget is set)
                    if (dailyBudgetRemaining != null && dailyBudgetRemaining > 0) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MoneyText(
                                formatted = Formatters.money(dailyBudgetRemaining, currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = SuccessGreen
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "/ ${stringResource(R.string.payday_per_day)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Goal Progress Card ────────────────────────────────────────────────────────

/**
 * Compact card showing progress toward a savings goal with a labelled progress bar.
 * Tapping opens the goals screen or an add-contribution sheet.
 */
@Composable
fun GoalProgressCard(
    goals: List<GoalEntity>,
    currencySymbol: String,
    onAddGoal: () -> Unit,
    onGoalTap: (GoalEntity) -> Unit,
    modifier: Modifier = Modifier
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
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Savings,
                        contentDescription = null,
                        tint = AccentIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.goals_section_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = AccentIndigo
                    )
                }
                IconButton(onClick = onAddGoal, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.goals_add),
                        tint = AccentIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (goals.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.goals_empty_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                Spacer(Modifier.height(10.dp))
                // Show up to 3 goals
                goals.take(3).forEach { goal ->
                    GoalProgressRow(
                        goal = goal,
                        currencySymbol = currencySymbol,
                        onClick = { onGoalTap(goal) }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun GoalProgressRow(
    goal: GoalEntity,
    currencySymbol: String,
    onClick: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) {
        (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "goalProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    text = if (goal.emoji.isBlank()) "🎯" else goal.emoji,
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = if (progress >= 1f) SuccessGreen else AccentIndigo,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (progress >= 1f) SuccessGreen else AccentIndigo,
            trackColor = AccentIndigo.copy(alpha = 0.12f),
            strokeCap = StrokeCap.Round
        )
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            MoneyText(
                formatted = Formatters.money(goal.savedAmount, currencySymbol),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = " / ",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            MoneyText(
                formatted = Formatters.money(goal.targetAmount, currencySymbol),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
