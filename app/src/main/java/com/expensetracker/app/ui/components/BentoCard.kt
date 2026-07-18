package com.expensetracker.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * One gradient "glass" tile in the Home dashboard's Bento Grid (Monthly Spending, Budget
 * Remaining, Savings Goal, Subscription Cost). Each tile owns its own [gradientColors] for a
 * distinct identity, a soft top-start highlight + hairline border standing in for a glass
 * surface (true backdrop blur via `Modifier.blur()` only takes effect on API 31+, so this
 * gradient-based approximation is used everywhere so every device looks the same), a
 * press-dip, and a fade/slide/scale-in entrance staggered by [entranceDelayMillis].
 */
@Composable
fun BentoCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    subtitle: String? = null,
    progress: Float? = null,
    visible: Boolean = true,
    entranceDelayMillis: Int = 0,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 360, delayMillis = entranceDelayMillis)) +
            slideInVertically(
                animationSpec = tween(durationMillis = 360, delayMillis = entranceDelayMillis),
                initialOffsetY = { it / 4 }
            ) +
            scaleIn(
                animationSpec = tween(durationMillis = 360, delayMillis = entranceDelayMillis),
                initialScale = 0.92f
            )
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val pressScale by animateFloatAsState(
            targetValue = if (isPressed) 0.96f else 1f,
            animationSpec = tween(120),
            label = "bentoCardPress"
        )
        val shape = RoundedCornerShape(22.dp)
        val clickableModifier = if (onClick != null) {
            Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
        } else {
            Modifier
        }

        Box(
            modifier = modifier
                .scale(pressScale)
                .shadow(
                    // Reduced from 16.dp — the dashboard stacks many of these tiles, and the
                    // heavier shadow made the whole screen feel denser/more "bulky" than the
                    // content actually is.
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = gradientColors.last().copy(alpha = 0.3f),
                    spotColor = gradientColors.first().copy(alpha = 0.35f)
                )
                .clip(shape)
                .drawBehind {
                    // Guard against zero-size: Android's LinearGradient throws
                    // IllegalArgumentException when start == end (both map to 0,0
                    // if size is zero during first layout pass).
                    if (size.width > 0f && size.height > 0f) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = gradientColors,
                                start = Offset.Zero,
                                end = Offset(size.width, size.height)
                            )
                        )
                    }
                }
                .border(width = 1.dp, color = Color.White.copy(alpha = 0.16f), shape = shape)
                .then(clickableModifier)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                            radius = 240f
                        )
                    )
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.White.copy(alpha = 0.20f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Crossfade(targetState = value, animationSpec = tween(220), label = "bentoValue") { animatedValue ->
                    // MoneyText (not plain Text) so a Saudi Riyal amount renders as the dedicated
                    // icon glyph, never the raw "ر.س" text abbreviation.
                    MoneyText(
                        formatted = animatedValue,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    MoneyText(
                        formatted = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (progress != null) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
}
