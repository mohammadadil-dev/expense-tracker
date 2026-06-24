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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoDark
import kotlinx.coroutines.delay

private const val SPLASH_HOLD_MS = 1700L

/**
 * A short, self-animated brand moment shown once on cold start: the same wallet+coin motif
 * as the launcher icon fades/scales in, then a coin "drops" into the wallet with a small
 * bounce, before auto-advancing to the dashboard. Pure Compose — no extra splash-screen
 * library/dependency needed, and no network or assets involved (just drawn shapes), so it
 * costs nothing and stays fully offline like the rest of the app.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val walletScale = remember { Animatable(0.7f) }
    val walletAlpha = remember { Animatable(0f) }
    val coinDrop = remember { Animatable(-1.4f) }
    val textAlpha = remember { Animatable(0f) }

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
            .background(Brush.verticalGradient(listOf(AccentIndigo, AccentIndigoDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(walletScale.value)
                    .alpha(walletAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(112.dp)) {
                    val w = size.width
                    val h = size.height

                    // Wallet body (rounded rect, centered)
                    val walletWidth = w * 0.62f
                    val walletHeight = h * 0.40f
                    val walletLeft = (w - walletWidth) / 2f
                    val walletTop = (h - walletHeight) / 2f
                    val corner = CornerRadius(walletWidth * 0.12f, walletWidth * 0.12f)
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(walletLeft, walletTop),
                        size = Size(walletWidth, walletHeight),
                        cornerRadius = corner
                    )
                    // Flap accent near the top of the wallet — uses the dark brand violet
                    // directly (not AccentIndigoLight, which is now a pale lavender tint built
                    // for light surfaces and would barely show up against this white wallet).
                    drawRoundRect(
                        color = AccentIndigoDark,
                        topLeft = Offset(walletLeft, walletTop),
                        size = Size(walletWidth, walletHeight * 0.22f),
                        cornerRadius = CornerRadius(walletWidth * 0.12f, walletWidth * 0.12f)
                    )

                    // Coin drops in from above into its resting spot on the wallet's edge
                    val coinRadius = walletWidth * 0.16f
                    val restCenter = Offset(walletLeft + walletWidth * 0.74f, walletTop + walletHeight * 0.52f)
                    val travel = h * 0.5f
                    translate(top = coinDrop.value * travel) {
                        drawCircle(color = AccentIndigoDark, radius = coinRadius, center = restCenter)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.55f),
                            radius = coinRadius * 0.4f,
                            center = restCenter
                        )
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
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
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}
