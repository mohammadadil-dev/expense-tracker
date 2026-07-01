package com.expensetracker.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentGreenDark
import com.expensetracker.app.ui.theme.AccentGreenMid
import com.expensetracker.app.ui.theme.BrandAmber
import kotlinx.coroutines.delay

private const val SPLASH_HOLD_MS = 1700L

/**
 * Brand splash: deep forest-green gradient background (matching the budget ring card),
 * wallet icon with amber coin dropping in, app name + tagline fade up.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val walletScale = remember { Animatable(0.7f) }
    val walletAlpha = remember { Animatable(0f) }
    val coinDrop = remember { Animatable(-1.4f) }
    val textAlpha = remember { Animatable(0f) }
    val ringProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        walletScale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        walletAlpha.animateTo(1f, animationSpec = tween(400, easing = LinearOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(280)
        coinDrop.animateTo(
            0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 220f)
        )
    }
    LaunchedEffect(Unit) {
        delay(400)
        ringProgress.animateTo(0.72f, animationSpec = tween(900, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(500)
        textAlpha.animateTo(1f, animationSpec = tween(450))
    }
    LaunchedEffect(Unit) {
        delay(SPLASH_HOLD_MS)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Float.MAX_VALUE * √2 overflows float32 to Infinity, making
                // Skia's gradient matrix degenerate → IllegalArgumentException.
                // Use actual draw-scope size for a well-formed diagonal gradient.
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentGreenDark, AccentGreenMid),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Icon area: amber ring + wallet-coin combo
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(walletScale.value)
                    .alpha(walletAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w / 2f, h / 2f)

                    // ── Amber progress ring around the icon ──────────────────
                    val ringStroke = 8.dp.toPx()
                    val ringInset = ringStroke / 2f + 4.dp.toPx()
                    val ringSize = Size(w - ringInset * 2, h - ringInset * 2)
                    // Track
                    drawArc(
                        color = Color.White.copy(alpha = 0.12f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset(ringInset, ringInset),
                        size = ringSize,
                        style = Stroke(width = ringStroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                    // Progress arc (amber)
                    if (ringProgress.value > 0f) {
                        drawArc(
                            color = BrandAmber,
                            startAngle = 135f,
                            sweepAngle = 270f * ringProgress.value,
                            useCenter = false,
                            topLeft = Offset(ringInset, ringInset),
                            size = ringSize,
                            style = Stroke(width = ringStroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }

                    // ── Wallet body ──────────────────────────────────────────
                    val walletW = w * 0.52f
                    val walletH = h * 0.36f
                    val walletLeft = center.x - walletW / 2f
                    val walletTop = center.y - walletH / 2f
                    val corner = CornerRadius(walletW * 0.14f, walletW * 0.14f)
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(walletLeft, walletTop),
                        size = Size(walletW, walletH),
                        cornerRadius = corner
                    )
                    // Flap — forest green mid-tone on white wallet
                    drawRoundRect(
                        color = AccentGreenMid,
                        topLeft = Offset(walletLeft, walletTop),
                        size = Size(walletW, walletH * 0.24f),
                        cornerRadius = CornerRadius(walletW * 0.14f, walletW * 0.14f)
                    )

                    // ── Amber coin drops in ──────────────────────────────────
                    val coinR = walletW * 0.18f
                    val restCenter = Offset(walletLeft + walletW * 0.76f, walletTop + walletH * 0.54f)
                    val travel = h * 0.5f
                    translate(top = coinDrop.value * travel) {
                        // Amber coin body
                        drawCircle(color = BrandAmber, radius = coinR, center = restCenter)
                        // Inner ring detail
                        drawCircle(
                            color = Color(0xFFFF8C00),
                            radius = coinR * 0.60f,
                            center = restCenter,
                            style = Stroke(width = coinR * 0.12f)
                        )
                        // Shine
                        drawCircle(
                            color = Color.White.copy(alpha = 0.50f),
                            radius = coinR * 0.30f,
                            center = restCenter.copy(x = restCenter.x - coinR * 0.15f, y = restCenter.y - coinR * 0.15f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = BrandAmber.copy(alpha = 0.90f),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}
