package com.expensetracker.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.WarningAmber
import com.expensetracker.app.util.FinancialInsights

// ── Export chips ──────────────────────────────────────────────────────────────

@Composable
internal fun ExportCsvChip(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = tween(120),
        label = "exportCsvChipPress"
    )
    val tint = if (enabled) SuccessGreen else TextMuted
    val bg   = if (enabled) SuccessGreen.copy(alpha = 0.10f) else TextMuted.copy(alpha = 0.10f)

    Box(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.GridOn,
            contentDescription = stringResource(R.string.export_csv),
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
internal fun ExportPdfChip(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = tween(120),
        label = "exportChipPress"
    )
    val tint = if (enabled) AccentIndigo else TextMuted
    val bg   = if (enabled) AccentIndigo.copy(alpha = 0.1f) else TextMuted.copy(alpha = 0.1f)

    // Icon-only chip so it never overflows the row in any locale's long section title.
    Box(
        modifier = Modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.PictureAsPdf,
            contentDescription = stringResource(R.string.export_pdf),
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── SMS pending banner ────────────────────────────────────────────────────────

@Composable
internal fun PendingSmsBanner(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "smsBannerPress"
    )

    Row(
        modifier = modifier
            .scale(pressScale)
            .clip(RoundedCornerShape(14.dp))
            .background(AccentIndigo.copy(alpha = 0.12f))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Sms, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.sms_pending_banner, count),
            style = MaterialTheme.typography.bodyMedium,
            color = AccentIndigo,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(18.dp))
    }
}

// ── Pure utility functions ────────────────────────────────────────────────────

internal fun colorFromHex(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(TextMuted)

@Composable
internal fun greetingText(name: String): String =
    if (name.isBlank()) stringResource(R.string.greeting_hello)
    else stringResource(R.string.greeting_hello_named, name)

internal fun insightAccentColor(kind: FinancialInsights.InsightKind): Color = when (kind) {
    FinancialInsights.InsightKind.POSITIVE -> SuccessGreen
    FinancialInsights.InsightKind.WARNING  -> WarningAmber
    FinancialInsights.InsightKind.NEUTRAL  -> NeonCyan
}

@Composable
internal fun insightText(insight: FinancialInsights.Insight, categoryNameById: Map<Long, String>): String {
    val resId = when (insight.template) {
        FinancialInsights.InsightTemplate.TREND_DOWN          -> R.string.insight_trend_down
        FinancialInsights.InsightTemplate.TREND_UP            -> R.string.insight_trend_up
        FinancialInsights.InsightTemplate.TREND_FLAT          -> R.string.insight_trend_flat
        FinancialInsights.InsightTemplate.CATEGORY_INCREASE   -> R.string.insight_category_increase
        FinancialInsights.InsightTemplate.CATEGORY_DECREASE   -> R.string.insight_category_decrease
        FinancialInsights.InsightTemplate.PROJECTED_SAVINGS   -> R.string.insight_projected_savings
        FinancialInsights.InsightTemplate.PROJECTED_OVERSPEND -> R.string.insight_projected_overspend
        FinancialInsights.InsightTemplate.STRONGEST_DAY       -> R.string.insight_strongest_day
        FinancialInsights.InsightTemplate.SUBSCRIPTION_LOAD   -> R.string.insight_subscription_load
        FinancialInsights.InsightTemplate.DAILY_AVERAGE       -> R.string.insight_daily_average
        FinancialInsights.InsightTemplate.NOT_ENOUGH_DATA     -> R.string.insight_not_enough_data
    }
    val args: List<String> = buildList {
        insight.categoryId?.let { id -> add(categoryNameById[id] ?: "") }
        addAll(insight.args)
    }
    return if (args.isEmpty()) stringResource(resId) else stringResource(resId, *args.toTypedArray())
}
