package com.expensetracker.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary

/**
 * One summary tile on the dashboard (Total Spent, Transactions, Top Category, Daily Average).
 * Tappable with a small press-dip + ripple when [onClick] is supplied; the value text shrinks
 * a step once it gets long so big currency totals never wrap mid-number (the bug that showed
 * "₹78,125.0" / "0" on its own line).
 *
 * [accentColor] gives each tile its own color identity (icon badge, glow shadow, top ribbon,
 * expand strip) instead of every stat card looking identical. The small chevron in the header
 * is a second, independent tap target from [onClick] — tapping it just reveals a little
 * premium-feeling accent flourish below the value, purely for delight; it doesn't change what
 * [onClick] does (e.g. Dashboard still uses that to jump to the category breakdown).
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    subLabel: String? = null,
    subLabelColor: Color = TextMuted,
    icon: ImageVector? = null,
    accentColor: Color = AccentIndigo,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "statCardPress"
    )

    val shape = RoundedCornerShape(18.dp)
    val cardModifier = modifier
        .fillMaxWidth()
        .scale(pressScale)
        .shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = accentColor.copy(alpha = 0.35f),
            spotColor = accentColor.copy(alpha = 0.45f)
        )
    val colors = CardDefaults.cardColors(containerColor = CardWhite)
    val elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)

    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(220),
        label = "statChevronRotate"
    )

    // Gentle continuous "breathing" pulse on the icon badge — small enough to feel alive
    // without being distracting alongside the other ambient motion already on screen.
    val iconPulse by rememberInfiniteTransition(label = "statIconPulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statIconPulseValue"
    )

    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(16.dp)) {
            // Slim top ribbon in the tile's own accent color — a quiet "premium" signature
            // even before the tile is tapped at all.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.15f))
                        )
                    )
            )
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .scale(iconPulse)
                            .background(accentColor.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(chevronRotation)
                    )
                }
            }
            Crossfade(
                targetState = value,
                animationSpec = tween(durationMillis = 220),
                modifier = Modifier.padding(top = 8.dp, bottom = if (subLabel != null) 4.dp else 0.dp),
                label = "statValue"
            ) { animatedValue ->
                MoneyText(
                    formatted = animatedValue,
                    style = if (animatedValue.length > 9) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (subLabel != null) {
                MoneyText(
                    formatted = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = subLabelColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        accentColor.copy(alpha = 0.12f),
                                        accentColor,
                                        accentColor.copy(alpha = 0.12f)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = cardModifier,
            shape = shape,
            colors = colors,
            elevation = elevation
        ) { content() }
    } else {
        Card(modifier = cardModifier, shape = shape, colors = colors, elevation = elevation) { content() }
    }
}
