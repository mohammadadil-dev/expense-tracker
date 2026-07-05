package com.expensetracker.app.ui.components

import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.BorderLight
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.ui.theme.TextSecondary

/**
 * One step in the first-launch guided tour.
 *
 * @param titleRes   String resource ID for the step heading.
 * @param bodyRes    String resource ID for the step body text.
 * @param spotlight  Bounding rect (window coordinates) of the element to highlight.
 *                   Pass null for a full-screen dim without any punch-out circle.
 */
data class CoachmarkStep(
    val titleRes: Int,
    val bodyRes: Int,
    val spotlight: Rect? = null
)

/**
 * Full-screen guided-tour overlay.
 *
 * Draws a semi-transparent scrim over the entire screen with an optional circular
 * spotlight cut-out (using [BlendMode.Clear]) and a pulsing ring around the target.
 * A floating tooltip card explains the highlighted element and lets the user advance
 * or skip the tour.
 *
 * Place this as the **last** child of your root Box so it renders on top of the Scaffold,
 * FAB, and all other content.
 */
@Composable
fun CoachmarkOverlay(
    steps: List<CoachmarkStep>,
    currentStep: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * Extra bottom padding added to the entire overlay. Use this to prevent the tooltip card
     * from being hidden behind a bottom bar (ad banner + navigation bar) whose height is not
     * yet reflected in the Scaffold's innerPadding at first composition (e.g. when the ad
     * loads asynchronously after layout).
     *
     * Typical value: banner ad height (50 dp) + navigation bar height (~80 dp) = 130 dp.
     */
    bottomPadding: Dp = 0.dp
) {
    if (currentStep >= steps.size) return
    val step    = steps[currentStep]
    val isLast  = currentStep == steps.size - 1

    // Outward-expanding pulsing ring around the spotlight
    val infiniteTransition = rememberInfiniteTransition(label = "coachPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseOutCirc), RepeatMode.Restart),
        label = "pulseAlpha"
    )
    val pulseExtraRadius by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 44f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseOutCirc), RepeatMode.Restart),
        label = "pulseRadius"
    )

    val density = LocalDensity.current
    val bottomPaddingPx = with(density) { bottomPadding.toPx() }

    // Bug fix: use minOf so wide cards (fillMaxWidth) don't get an enormous radius.
    // For the FAB (square) minOf == maxOf. For BudgetRingCard (wide, shorter) this gives
    // a sensible circle based on the card height rather than its screen-spanning width.
    // Use dp-based padding so the halo scales correctly across screen densities.
    // 32f raw pixels ≈ 10 dp on xxhdpi — too tight. 24.dp gives ~72 px on xxhdpi.
    val spotPaddingPx = with(density) { 24.dp.toPx() }
    val spotRadius = step.spotlight?.let { (minOf(it.width, it.height) / 2f) + spotPaddingPx } ?: 0f

    val targetCenterY = step.spotlight?.center?.y

    // Bug fix: consume all touches so underlying UI (FAB, cards) cannot be tapped
    // while the coachmark overlay is showing.
    val blockTouches = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) { /* intentionally empty — just consumes the event */ }

    // BoxWithConstraints gives the ACTUAL layout height of this composable (which may be
    // smaller than LocalConfiguration.screenHeightDp if innerPadding is applied by the
    // Scaffold). All card-position math runs inside so it uses the true container height.
    BoxWithConstraints(modifier = modifier.fillMaxSize().then(blockTouches)) {
        val containerPx = with(density) { maxHeight.toPx() }
        // Area available to the tooltip card (above the ad + nav bottom bar).
        val effectivePx = (containerPx - bottomPaddingPx).coerceAtLeast(0f)

        // Decide card placement: target in top 55% → card goes below; bottom 45% → card above.
        // 55% (rather than 50%) ensures elements near the vertical midpoint (like the budget
        // ring) are still treated as "upper half" so the card appears below them.
        val placeBelow = targetCenterY != null && targetCenterY < containerPx * 0.55f

        // Minimum height reserved for the card (title + body + buttons ≈ 240 dp).
        val minCardPx = with(density) { 240.dp.toPx() }

        // Dp offset from the relevant effective-area edge to the near edge of the card.
        // Clamped so the card always has at least minCardPx of space within effectivePx.
        val cardGapPx = with(density) { 20.dp.toPx() }
        val rawOffsetPx = spotRadius + cardGapPx + (targetCenterY?.let {
            if (placeBelow) it else (effectivePx - it).coerceAtLeast(0f)
        } ?: 0f)
        val clampCeilingPx = (effectivePx - minCardPx).coerceAtLeast(0f)
        val cardEdgeOffset: Dp = if (step.spotlight != null) {
            with(density) { rawOffsetPx.coerceAtMost(clampCeilingPx).toDp() }
        } else 0.dp

        // If the offset was clamped, the card would overlap the spotlight.
        // In that case anchor the card to the BOTTOM of the effective area so the
        // spotlight element is clearly visible in the space above the card.
        val wasClamped = step.spotlight != null && placeBelow &&
            rawOffsetPx > clampCeilingPx

        // ── Scrim + punch-out ────────────────────────────────────────────────
        // alpha = 0.99f forces a hardware compositing layer, which is required
        // for BlendMode.Clear to actually erase pixels instead of drawing black.
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.99f }
        ) {
            drawRect(Color.Black.copy(alpha = 0.72f))
            step.spotlight?.let { bounds ->
                val center = Offset(bounds.center.x, bounds.center.y)
                // Outward pulse ring
                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha),
                    radius = spotRadius + pulseExtraRadius,
                    center = center
                )
                // Transparent cut-out over the target
                drawCircle(
                    color = Color.Transparent,
                    radius = spotRadius,
                    center = center,
                    blendMode = BlendMode.Clear
                )
            }
        }

        // ── Tooltip card ─────────────────────────────────────────────────────
        // Placement logic:
        //  • No spotlight (steps 3-5)   → Alignment.Center
        //  • Target in upper 55%        → TopCenter + padding(top = offset) below spotlight
        //  • Clamped (large spotlight)  → BottomCenter, no extra padding — card sits at the
        //                                 bottom of the effective area, spotlight clearly above
        //  • Target in lower 45%        → BottomCenter + padding(bottom = offset) above spotlight
        val tooltipAlignment = when {
            step.spotlight == null -> Alignment.Center
            wasClamped             -> Alignment.BottomCenter
            placeBelow             -> Alignment.TopCenter
            else                   -> Alignment.BottomCenter
        }
        // Inner card Box: constrained to the area above the bottom bar so the tooltip
        // card and its Skip/Next buttons are never hidden behind the ad or nav bar.
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding),
            contentAlignment = tooltipAlignment
        ) {
            val cardModifier = Modifier
                .padding(horizontal = 28.dp)
                .then(
                    when {
                        step.spotlight == null -> Modifier  // centered — Box handles it
                        wasClamped             -> Modifier  // bottom-anchored — no extra padding
                        placeBelow             -> Modifier.padding(top = cardEdgeOffset)
                        else                   -> Modifier.padding(bottom = cardEdgeOffset)
                    }
                )

            Card(
                modifier = cardModifier,
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Step progress dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        steps.indices.forEach { i ->
                            Box(
                                Modifier
                                    .size(if (i == currentStep) 8.dp else 6.dp)
                                    .background(
                                        color  = if (i == currentStep) AccentIndigo else BorderLight,
                                        shape  = CircleShape
                                    )
                            )
                        }
                    }

                    Text(
                        text      = stringResource(step.titleRes),
                        style     = MaterialTheme.typography.titleMedium,
                        fontWeight= FontWeight.Bold,
                        color     = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = stringResource(step.bodyRes),
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onSkip) {
                            Text(stringResource(R.string.tour_skip), color = TextMuted)
                        }
                        Button(
                            onClick = onNext,
                            colors  = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                        ) {
                            Text(
                                text  = if (isLast) stringResource(R.string.tour_done)
                                        else        stringResource(R.string.tour_next),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
