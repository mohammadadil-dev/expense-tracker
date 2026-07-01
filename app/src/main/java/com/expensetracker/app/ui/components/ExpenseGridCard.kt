package com.expensetracker.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
 * A compact card used in the 2-column Grid layout — same data as [ExpenseRowCard], denser
 * layout, and the same pale per-category gradient tile treatment.
 */
@Composable
fun ExpenseGridCard(
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
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(120),
        label = "gridCardPress"
    )

    val categoryColor = category?.let { colorFromHex(it.colorHex) } ?: TextMuted
    val tileGradient = listOf(
        lerp(categoryColor, Color.White, 0.90f),
        lerp(categoryColor, Color.White, 0.78f)
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.scale(pressScale)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (size.width > 0f && size.height > 0f) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = tileGradient,
                                start = Offset.Zero,
                                end = Offset(0f, size.height)
                            )
                        )
                    }
                }
                .padding(12.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = categoryColor, shape = CircleShape)
            )
            Spacer(Modifier.height(8.dp))
            MoneyText(
                formatted = Formatters.money(expense.amount, currencySymbol),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = expense.description.ifBlank {
                    category?.let { categoryDisplayName(it) } ?: ""
                },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
