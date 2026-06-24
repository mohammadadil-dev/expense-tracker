package com.expensetracker.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.TextPrimary

/**
 * One card in the Home dashboard's "AI Insights Feed" — styled like a short social-media post
 * (round emoji badge + one line of text) rather than a plain stat row, per the "social-media
 * -style card" feed the redesign calls for. [accentColor] is decided by the caller from the
 * insight's kind (positive/warning/neutral) so this file stays free of any engine-specific
 * enum import.
 */
@Composable
fun InsightFeedCard(
    emoji: String,
    text: String,
    accentColor: Color,
    visible: Boolean = true,
    entranceDelayMillis: Int = 0,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 320, delayMillis = entranceDelayMillis)) +
            slideInHorizontally(
                animationSpec = tween(durationMillis = 320, delayMillis = entranceDelayMillis),
                initialOffsetX = { it / 8 }
            )
    ) {
        val shape = RoundedCornerShape(16.dp)
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(CardWhite)
                .border(width = 1.dp, color = accentColor.copy(alpha = 0.28f), shape = shape)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(accentColor.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            // MoneyText (not plain Text) so an embedded Saudi Riyal amount renders as the
            // dedicated icon glyph — never the raw "ر.س" text abbreviation — while still
            // wrapping normally across multiple lines like any other sentence.
            MoneyText(
                formatted = text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
