package com.expensetracker.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.ui.theme.AccentGreen
import com.expensetracker.app.ui.theme.AccentGreenDark
import com.expensetracker.app.ui.theme.AccentGreenMid
import com.expensetracker.app.ui.theme.BrandAmber
import com.expensetracker.app.ui.theme.BrandCoral
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.ui.theme.TileGreen
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.categoryDisplayName
import com.expensetracker.app.util.categoryEmoji
import com.expensetracker.app.util.DateUtils
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Budget Progress Ring  (Canvas arc)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Circular arc ring showing budget consumption. Track is dark-green translucent;
 * arc is amber → coral when over budget. Centre shows the percentage consumed.
 */
@Composable
fun BudgetProgressRing(
    spentFraction: Float,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = spentFraction.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "budgetRingProgress"
    )
    val isOverBudget = spentFraction > 1f
    val arcColor = if (isOverBudget) BrandCoral else BrandAmber
    val pct = (spentFraction * 100).toInt().coerceIn(0, 999)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 20.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val arcTopLeft = Offset(inset, inset)
            val startAngle = 135f
            val sweepTotal = 270f

            // Track arc — semi-transparent white on dark green
            drawArc(
                color = Color.White.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            if (animatedFraction > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = startAngle,
                    sweepAngle = sweepTotal * animatedFraction,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$pct%",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = stringResource(R.string.stat_spent_label).lowercase(Locale.getDefault()),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.70f)
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Budget Ring Card  (dark forest green, reference-matching)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Deep forest-green gradient card matching the reference screenshot:
 * - Top row: "Month's Budget" label + month navigation arrows
 * - Left: Budget progress ring showing percentage
 * - Right: Spent (coral) and Remaining (amber) amounts stacked
 * - "No budget set" prompt when budget is null
 */
@Composable
fun BudgetRingCard(
    currentMonthKey: String,
    monthSlideDirection: Int,
    monthDisplayLabel: String,
    totalSpent: Double,
    overallBudget: Double?,
    currencySymbol: String,
    onNavigatePrev: () -> Unit,
    onNavigateNext: () -> Unit,
    onManageBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spentFraction = if ((overallBudget ?: 0.0) > 0.0) {
        (totalSpent / overallBudget!!).toFloat()
    } else 0f

    val cardShape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = cardShape,
                ambientColor = AccentGreenDark.copy(alpha = 0.40f),
                spotColor = AccentGreenDark.copy(alpha = 0.50f)
            )
            .clip(cardShape)
            .drawBehind {
                // Float.MAX_VALUE * √2 overflows float32 → Infinity, which makes
                // Skia's gradient matrix degenerate → IllegalArgumentException in
                // LinearGradient.nativeCreate. Use the actual draw-scope size instead.
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentGreenDark, AccentGreenMid),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    )
                }
            }
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {

            // ── Month navigation row ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.months_budget_label, monthDisplayLabel),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onNavigatePrev, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.ChevronLeft,
                        contentDescription = stringResource(R.string.cd_previous_month),
                        tint = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                AnimatedContent(
                    targetState = currentMonthKey,
                    transitionSpec = {
                        if (monthSlideDirection >= 0) {
                            (slideInHorizontally(tween(180)) { it / 3 } + fadeIn(tween(180))) togetherWith
                                (slideOutHorizontally(tween(180)) { -it / 3 } + fadeOut(tween(180)))
                        } else {
                            (slideInHorizontally(tween(180)) { -it / 3 } + fadeIn(tween(180))) togetherWith
                                (slideOutHorizontally(tween(180)) { it / 3 } + fadeOut(tween(180)))
                        }
                    },
                    label = "ringCardMonth"
                ) { _ ->
                    Text(
                        text = monthDisplayLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                val isCurrentMonth = currentMonthKey == DateUtils.currentMonthKey()
                IconButton(
                    onClick = onNavigateNext,
                    enabled = !isCurrentMonth,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = stringResource(R.string.cd_next_month),
                        tint = Color.White.copy(alpha = if (isCurrentMonth) 0.25f else 0.75f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (overallBudget != null && overallBudget > 0.0) {
                // ── Ring centred, stats in a full-width row below ─────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BudgetProgressRing(
                        spentFraction = spentFraction,
                        modifier = Modifier.size(130.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Spent | divider | Remaining — each half gets the full card width
                    val remaining = (overallBudget - totalSpent).coerceAtLeast(0.0)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Spent
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MoneyText(
                                formatted = Formatters.money(totalSpent, currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = BrandCoral,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.stat_spent_label),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.60f)
                                )
                            )
                        }
                        // Thin divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(Color.White.copy(alpha = 0.18f))
                        )
                        // Remaining
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MoneyText(
                                formatted = Formatters.money(remaining, currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = BrandAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.stat_remaining_label),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.60f)
                                )
                            )
                        }
                    }
                }
            } else {
                // No budget set — prompt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onManageBudget() }
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Ring with 0% even when no budget set
                        BudgetProgressRing(
                            spentFraction = 0f,
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.bento_budget_no_target),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Income + Budget Cap tiles  (side by side, reference colors)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Two tiles side by side matching the reference screenshot:
 * Left = bright green (Salary), Right = coral-red (Budget Cap).
 */
@Composable
fun IncomeBudgetTiles(
    monthlySalary: Double,
    overallBudget: Double?,
    currencySymbol: String,
    onSetSalary: () -> Unit,
    onManageBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileShape = RoundedCornerShape(18.dp)

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // ── Salary tile (green) ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, tileShape, ambientColor = TileGreen.copy(alpha = 0.30f))
                .clip(tileShape)
                .background(TileGreen)
                .clickable { onSetSalary() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.salary_label),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White.copy(alpha = 0.90f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(text = "💰", fontSize = 18.sp)
                }
                Spacer(Modifier.height(8.dp))
                MoneyText(
                    formatted = if (monthlySalary > 0.0) Formatters.money(monthlySalary, currencySymbol) else "—",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ── Budget Cap tile (coral) ───────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, tileShape, ambientColor = BrandCoral.copy(alpha = 0.30f))
                .clip(tileShape)
                .background(BrandCoral)
                .clickable { onManageBudget() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.budget_cap_label),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White.copy(alpha = 0.90f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(text = "🎯", fontSize = 18.sp)
                }
                Spacer(Modifier.height(8.dp))
                MoneyText(
                    formatted = if ((overallBudget ?: 0.0) > 0.0) Formatters.money(overallBudget!!, currencySymbol) else "—",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Salary Set Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SalarySetDialog(
    currentSalary: Double,
    currencySymbol: String,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var raw by remember { mutableStateOf(if (currentSalary > 0.0) currentSalary.toLong().toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.salary_set_title)) },
        text = {
            OutlinedTextField(
                value = raw,
                onValueChange = { raw = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text(stringResource(R.string.salary_set_label)) },
                placeholder = { Text(stringResource(R.string.salary_set_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_saudi_riyal),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).padding(start = 4.dp)
                        )
                    } else {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(raw.toDoubleOrNull() ?: 0.0) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Spending by Category section
// ─────────────────────────────────────────────────────────────────────────────

/**
 * White card listing top-6 categories. Each row is itself a white rounded card
 * with a squircle emoji icon, name, spend/budget label, and coloured progress bar —
 * matching the reference design closely.
 */
@Composable
fun SpendingCategorySection(
    categoryTotals: Map<Long, Double>,
    categories: List<CategoryEntity>,
    totalThisMonth: Double,
    currencySymbol: String,
    onManageCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topCategories = remember(categoryTotals, categories) {
        categories
            .mapNotNull { cat ->
                val amt = categoryTotals[cat.id]
                if (amt != null && amt > 0.0) Pair(cat, amt) else null
            }
            .sortedByDescending { it.second }
            .take(6)
    }

    Column(modifier = modifier) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.spending_by_category),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.view_all_label),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = BrandCoral,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onManageCategories() }
            )
        }

        Spacer(Modifier.height(12.dp))

        if (topCategories.isEmpty()) {
            Text(
                text = stringResource(R.string.no_expenses_breakdown),
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                topCategories.forEach { (cat, amt) ->
                    CategorySpendRow(
                        category       = cat,
                        amount         = amt,
                        totalThisMonth = totalThisMonth,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }
    }
}

/**
 * One category row: white rounded card, squircle emoji, name, amount, progress bar.
 */
@Composable
fun CategorySpendRow(
    category: CategoryEntity,
    amount: Double,
    totalThisMonth: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val fraction = if (totalThisMonth > 0) (amount / totalThisMonth).toFloat().coerceIn(0f, 1f) else 0f
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(600),
        label = "catRowProgress"
    )

    val barColor = remember(category.colorHex) {
        runCatching { Color(android.graphics.Color.parseColor(category.colorHex)) }.getOrElse { AccentGreen }
    }
    val rowShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, rowShape, ambientColor = Color.Black.copy(alpha = 0.06f))
            .clip(rowShape)
            .background(CardWhite)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Squircle emoji icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(barColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = categoryEmoji(category.nameKey), fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryDisplayName(category),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    MoneyText(
                        formatted = Formatters.money(amount, currencySymbol),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
                Spacer(Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { animatedFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = barColor,
                    trackColor = barColor.copy(alpha = 0.12f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Recent Transactions section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun RecentTransactionsSection(
    expenses: List<ExpenseEntity>,
    categoryById: Map<Long, CategoryEntity>,
    currencySymbol: String,
    onViewAll: () -> Unit,
    maxItems: Int = 5,
    modifier: Modifier = Modifier
) {
    val recent = remember(expenses) {
        expenses.sortedByDescending { it.date }.take(maxItems)
    }

    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.recent_transactions_label),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            if (expenses.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.view_all_label),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BrandCoral,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (recent.isEmpty()) {
            Text(
                text = stringResource(R.string.no_expenses_this_month),
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recent.forEach { expense ->
                    val cat = categoryById[expense.categoryId]
                    RecentTransactionRow(
                        expense = expense,
                        category = cat,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }
    }
}

@Composable
fun RecentTransactionRow(
    expense: ExpenseEntity,
    category: CategoryEntity?,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val iconColor = remember(category?.colorHex) {
        runCatching {
            Color(android.graphics.Color.parseColor(category?.colorHex ?: "#43A047"))
        }.getOrElse { AccentGreen }
    }
    val rowShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, rowShape, ambientColor = Color.Black.copy(alpha = 0.06f))
            .clip(rowShape)
            .background(CardWhite)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = categoryEmoji(category?.nameKey), fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description.ifBlank { category?.let { categoryDisplayName(it) } ?: "—" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = expense.date,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
            }

            Spacer(Modifier.width(8.dp))

            MoneyText(
                formatted = Formatters.money(expense.amount, currencySymbol),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandCoral
                ),
                maxLines = 1
            )
        }
    }
}
