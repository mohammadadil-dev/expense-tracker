package com.expensetracker.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper

/**
 * Drop-in replacement for `Text(text = ..., ...)` for any string that might contain a Saudi
 * Riyal amount — whether [formatted] is a bare money string ("ر.س1,234.00") or a full localized
 * sentence with the figure embedded mid-sentence ("You're projected to go ر.س200.00 over
 * budget"). Wherever the "ر.س" text abbreviation appears, it's swapped for the official symbol
 * (drawn as a vector, since no shipped Android font has the U+20C1 glyph yet) so only the icon
 * is ever shown — never the raw Arabic abbreviation as text alongside it.
 *
 * Single-line values (the default, [maxLines] == 1 — Bento tile values, row/grid amounts, etc.)
 * use a Row-based before/icon/after split, forced LTR so the symbol always sits to the left of
 * its number per SAMA's placement guideline, regardless of the surrounding layout direction.
 *
 * Multi-line values ([maxLines] > 1 — full sentences like an AI insight that need to wrap)
 * can't use that Row split, since a Row can't wrap text across lines while keeping the icon
 * pinned mid-sentence. Instead the icon is woven into the string as inline content on an
 * AnnotatedString — the same mechanism Compose uses for inline emoji/images — so it flows and
 * wraps naturally with the rest of the sentence.
 */
@Composable
fun MoneyText(
    formatted: String,
    style: TextStyle,
    color: Color = Color.Unspecified,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    modifier: Modifier = Modifier
) {
    val symbol = CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL
    val symbolIndex = formatted.indexOf(symbol)
    if (symbolIndex == -1) {
        Text(
            text = formatted,
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            modifier = modifier
        )
    } else if (maxLines == 1) {
        val before = formatted.substring(0, symbolIndex)
        val after = formatted.substring(symbolIndex + symbol.length)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                val iconHeight = with(LocalDensity.current) { style.fontSize.toDp() }
                if (before.isNotEmpty()) {
                    Text(text = before, style = style, color = color, maxLines = 1, overflow = overflow)
                    Spacer(Modifier.width(2.dp))
                }
                Icon(
                    painter = painterResource(R.drawable.ic_saudi_riyal),
                    contentDescription = null,
                    tint = if (color == Color.Unspecified) LocalContentColor.current else color,
                    modifier = Modifier
                        .height(iconHeight)
                        .width(iconHeight * (685f / 765f))
                )
                if (after.isNotEmpty()) {
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = after,
                        style = style,
                        color = color,
                        maxLines = maxLines,
                        overflow = overflow
                    )
                }
            }
        }
    } else {
        val iconId = "riyal"
        val annotated = buildAnnotatedString {
            var remaining = formatted
            while (true) {
                val idx = remaining.indexOf(symbol)
                if (idx == -1) {
                    append(remaining)
                    break
                }
                append(remaining.substring(0, idx))
                appendInlineContent(iconId, "[SAR]")
                remaining = remaining.substring(idx + symbol.length)
            }
        }
        val density = LocalDensity.current
        val iconHeight = with(density) { style.fontSize.toDp() }
        val inlineContent = mapOf(
            iconId to InlineTextContent(
                placeholder = Placeholder(
                    width = with(density) { (iconHeight * (685f / 765f)).toSp() },
                    height = with(density) { iconHeight.toSp() },
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_saudi_riyal),
                    contentDescription = null,
                    tint = if (color == Color.Unspecified) LocalContentColor.current else color
                )
            }
        )
        Text(
            text = annotated,
            inlineContent = inlineContent,
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            modifier = modifier
        )
    }
}
