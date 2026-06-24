package com.expensetracker.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColors = lightColorScheme(
    primary = AccentIndigo,
    onPrimary = OnAccent,
    primaryContainer = AccentIndigoLight,
    onPrimaryContainer = AccentIndigo,
    background = BgApp,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = BgApp,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    error = DangerRed
)

/**
 * Light, premium, neon-accented by design — soft off-white surfaces with a vivid violet brand
 * accent, regardless of the phone's system light/dark setting, so the app keeps one consistent
 * identity everywhere instead of flipping with the OS theme. Built on [lightColorScheme] (not
 * [androidx.compose.material3.darkColorScheme]) so every Material default this theme doesn't
 * explicitly override — ripples, default component tints, and so on — also assumes a light
 * canvas instead of a dark one.
 */
@Composable
fun ExpenseTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        content = content
    )
}
