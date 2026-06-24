package com.expensetracker.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import com.expensetracker.app.ui.theme.TextMuted

data class TrendPoint(val label: String, val value: Double, val highlighted: Boolean)

/**
 * A simple bar chart for the 12-month trend, built from plain Compose layout boxes
 * (no charting library dependency needed).
 */
@Composable
fun TrendBarChart(points: List<TrendPoint>, modifier: Modifier = Modifier) {
    val maxValue = (points.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        points.forEach { point ->
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val targetFraction = (point.value / maxValue).toFloat().coerceIn(0f, 1f)
                val animatedFraction by animateFloatAsState(
                    targetValue = targetFraction,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    label = "barHeight"
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight(animatedFraction.coerceAtLeast(0.015f))
                        .width(14.dp)
                        .background(
                            color = if (point.highlighted) AccentIndigo else AccentIndigoLight,
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}
