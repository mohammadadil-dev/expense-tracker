package com.expensetracker.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.categoryDisplayName

/**
 * A single expense row card. Tapping opens the action sheet (Edit / Delete).
 * Shared by the Day, Category, and Calendar list layouts.
 */
@Composable
fun ExpenseRowCard(
    expense: ExpenseEntity,
    category: CategoryEntity?,
    currencySymbol: String,
    dateText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorFromHex: (String) -> Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "expenseRowPress"
    )

    val categoryColor = category?.let { colorFromHex(it.colorHex) } ?: TextMuted
    val tileGradient = listOf(
        lerp(categoryColor, Color.White, 0.90f),
        lerp(categoryColor, Color.White, 0.80f)
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.fillMaxWidth().scale(pressScale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (size.width > 0f && size.height > 0f) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = tileGradient,
                                start = Offset.Zero,
                                end = Offset(size.width, 0f)
                            )
                        )
                    }
                }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = categoryColor, shape = CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description.ifBlank {
                        category?.let { categoryDisplayName(it) } ?: ""
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${category?.let { categoryDisplayName(it) } ?: ""} · $dateText",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            MoneyText(
                formatted = Formatters.money(expense.amount, currencySymbol),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
