package com.expensetracker.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

data class CategorySlice(val color: Color, val amount: Double)

/**
 * A simple ring/donut chart drawn with Canvas — no charting library dependency needed.
 *
 * Sweeps animate in (0 -> full angle) every time the slice data actually changes — e.g.
 * switching months — instead of snapping straight to the new shape.
 */
@Composable
fun DonutChart(slices: List<CategorySlice>, modifier: Modifier = Modifier) {
    val total = slices.sumOf { it.amount }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing))
    }
    val drawProgress = progress.value

    Canvas(modifier = modifier) {
        if (total <= 0.0 || slices.isEmpty()) return@Canvas

        val strokeWidth = size.minDimension * 0.22f
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = Size(diameter, diameter)

        var startAngle = -90f
        slices.forEach { slice ->
            val sweep = (slice.amount / total * 360.0).toFloat() * drawProgress
            if (sweep > 0f) {
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
            startAngle += sweep
        }
    }
}
