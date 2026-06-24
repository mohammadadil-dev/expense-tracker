package com.expensetracker.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import com.expensetracker.app.ui.theme.SuccessGreen
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlinx.coroutines.launch

/**
 * Continuous "how far has the user scrolled" signal. Each screen with its own scrollable
 * content (Dashboard's LazyColumn, Settings' Column) writes its current scroll position here
 * from a tiny side effect; the background reads it directly inside its Canvas draw block to
 * nudge the blobs, layering a subtle scroll parallax on top of the touch reaction below.
 */
object BackgroundScrollSignal {
    val pixels = mutableFloatStateOf(0f)
}

private class Blob(
    val color: Color,
    val baseXFrac: Float,
    val baseYFrac: Float,
    val radiusFrac: Float,
    val driftMs: Int,
    val pulseMs: Int,
    val phase: Float,
    val parallax: Float
)

private class Ripple(val offset: Offset, val color: Color, val progress: Animatable<Float, *>)

// Shared positions/sizes/timings for every screen instance — only the colors change per
// screen (via [blobColors]) so each screen reads as its own "place" while the motion
// language — drift speed, pulse, layout — stays consistent app-wide.
private fun layoutFor(blobColors: List<Color>): List<Blob> {
    fun colorAt(i: Int) = blobColors[i % blobColors.size]
    return listOf(
        Blob(colorAt(0), baseXFrac = 0.12f, baseYFrac = 0.08f, radiusFrac = 0.55f, driftMs = 17000, pulseMs = 9000, phase = 0f, parallax = 0.10f),
        Blob(colorAt(1), baseXFrac = 0.92f, baseYFrac = 0.28f, radiusFrac = 0.62f, driftMs = 21000, pulseMs = 11000, phase = 2.3f, parallax = 0.16f),
        Blob(colorAt(2), baseXFrac = 0.28f, baseYFrac = 0.88f, radiusFrac = 0.50f, driftMs = 19000, pulseMs = 10000, phase = 4.6f, parallax = 0.22f)
    )
}

/**
 * Soft, slowly-drifting, gently "breathing" blobs painted behind a screen's content — and,
 * unlike a static gradient, genuinely touch-reactive: dragging a finger across the screen
 * pulls nearby blobs toward it (springing back once released), and every touch-down sends out
 * a soft expanding glow from that point. Each screen mounts its own instance with its own
 * [blobColors] trio so Dashboard, Settings, etc. each get a distinct color identity instead of
 * sharing one global background.
 *
 * Important: the gesture loop below only *observes* pointer positions — it never calls
 * `consume()` on anything — so it stays purely decorative and never steals touches from real
 * UI underneath (buttons, list item taps, scrolling all keep working exactly as before).
 */
@Composable
fun AnimatedBlobBackground(
    blobColors: List<Color> = listOf(AccentIndigo, AccentIndigoLight, SuccessGreen),
    modifier: Modifier = Modifier
) {
    val blobs = remember(blobColors) { layoutFor(blobColors) }
    val transition = rememberInfiniteTransition(label = "blobBackground")

    val driftAngles = blobs.mapIndexed { i, blob ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(blob.driftMs, easing = LinearEasing)),
            label = "blobDrift_$i"
        )
    }
    val pulses = blobs.mapIndexed { i, blob ->
        transition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                tween(blob.pulseMs, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blobPulse_$i"
        )
    }

    // Raw finger position while pressed (null once lifted). Blobs chase this with a spring
    // instead of jumping straight to it, so the pull feels elastic rather than robotic, and
    // settle back to their natural drift path on release via the same spring relaxing to 0.
    var touchTarget by remember { mutableStateOf<Offset?>(null) }
    val pullX by animateFloatAsState(
        targetValue = touchTarget?.x ?: -1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "blobPullX"
    )
    val pullY by animateFloatAsState(
        targetValue = touchTarget?.y ?: -1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "blobPullY"
    )
    val pullActive by animateFloatAsState(
        targetValue = if (touchTarget != null) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "blobPullActive"
    )

    val ripples = remember { mutableStateListOf<Ripple>() }
    val coroutineScope = rememberCoroutineScope()

    fun spawnRipple(offset: Offset) {
        val anim = Animatable(0f)
        val ripple = Ripple(offset, blobs.first().color, anim)
        ripples.add(ripple)
        coroutineScope.launch {
            anim.animateTo(1f, animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing))
            ripples.remove(ripple)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val pointerId = down.id
                    touchTarget = down.position
                    spawnRipple(down.position)
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == pointerId }
                        if (change != null && change.pressed) {
                            touchTarget = change.position
                        }
                    } while (event.changes.any { it.id == pointerId && it.pressed })
                    touchTarget = null
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val shortSide = minOf(w, h)
        // Background drifts a little slower than (and opposite to) the foreground scroll —
        // the classic parallax illusion — rather than moving 1:1 with it.
        val scrollDrift = BackgroundScrollSignal.pixels.floatValue * -0.12f
        val touchInfluenceRadius = shortSide * 0.55f

        blobs.forEachIndexed { i, blob ->
            val angle = driftAngles[i].value + blob.phase
            val amplitude = shortSide * 0.07f
            var cx = w * blob.baseXFrac + cos(angle) * amplitude
            var cy = h * blob.baseYFrac + sin(angle * 0.8f) * amplitude + scrollDrift * blob.parallax
            val radius = shortSide * blob.radiusFrac * pulses[i].value

            if (pullActive > 0.001f) {
                val dx = pullX - cx
                val dy = pullY - cy
                val dist = hypot(dx, dy)
                if (dist in 0.001f..touchInfluenceRadius) {
                    val falloff = 1f - (dist / touchInfluenceRadius)
                    val strength = falloff * falloff * 0.35f * pullActive
                    cx += dx * strength
                    cy += dy * strength
                }
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blob.color.copy(alpha = 0.24f), blob.color.copy(alpha = 0f)),
                    center = Offset(cx, cy),
                    radius = radius
                ),
                radius = radius,
                center = Offset(cx, cy)
            )
        }

        ripples.forEach { ripple ->
            val progress = ripple.progress.value
            val maxRadius = shortSide * 0.38f
            val r = (progress * maxRadius).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ripple.color.copy(alpha = (1f - progress) * 0.30f),
                        ripple.color.copy(alpha = 0f)
                    ),
                    center = ripple.offset,
                    radius = r
                ),
                radius = r,
                center = ripple.offset
            )
        }
    }
}
