package com.expensetracker.app.ui.theme

import androidx.compose.ui.graphics.Color

// Core neon-violet brand accent — the app's signature color, a vivid electric violet-indigo
// that still pops on the light surfaces below (saturated brand accents read fine against
// either a light or dark canvas, so this pair didn't need to change with the theme).
val AccentIndigo = Color(0xFF7C5CFF)
val AccentIndigoDark = Color(0xFF2D1F73)
// Pale lavender tint of the brand accent — a soft tonal fill behind brand-colored icons/
// badges and the "today" highlight in the calendars, so it now actually reads as "light"
// (this constant used to hold a near-black value, a relic from the original all-dark theme).
val AccentIndigoLight = Color(0xFFE9E4FF)

// Always-white foreground for text/icons/dots drawn on top of a neon-filled accent shape
// (e.g. the selected calendar day, the selected segmented-control chip) — kept distinct from
// [CardWhite] below so the two unrelated meanings ("page background" vs "text on neon fill")
// can never collide again if either palette changes independently.
val OnAccent = Color(0xFFFFFFFF)

// Page background and card surfaces: a soft, light, faintly cool-violet off-white, with
// cards in true white so they still read as gently "raised" against the page even at zero
// elevation. Flipped from the original near-black/dark-navy pair — an all-dark theme was hard
// to read for a lot of people, and this app needs to work just as well for someone who isn't
// particularly tech-savvy as it does for a power user.
val BgApp = Color(0xFFF5F6FB)
val CardWhite = Color(0xFFFFFFFF)

// Text tiers tuned for solid contrast against the light surfaces above (roughly 16:1, 6:1,
// and 4.7:1 against white for Primary/Secondary/Muted respectively) — comfortably readable at
// a glance, not just on close inspection.
val TextPrimary = Color(0xFF181A2A)
val TextSecondary = Color(0xFF5B6178)
val TextMuted = Color(0xFF6E7388)
val BorderLight = Color(0xFFDDE0EC)

val DangerRed = Color(0xFFFF5C7A)
val SuccessGreen = Color(0xFF2EE6A6)
val WarningAmber = Color(0xFFFFB648)

// Extra neon hues reserved for the per-screen ambient blob backgrounds, so each screen gets
// its own distinct color identity instead of reusing the brand accent everywhere.
val NeonCyan = Color(0xFF34E1FF)
val NeonPink = Color(0xFFFF3FA4)
val NeonViolet = Color(0xFF8B5CF6)
val NeonTeal = Color(0xFF1FE3C4)

val CategoryPalette = listOf(
    Color(0xFF8B7CFF), Color(0xFFFFB648), Color(0xFF2EE6A6), Color(0xFF5CC8FF),
    Color(0xFFFF6FB8), Color(0xFFFF5C7A), Color(0xFFB18CFF), Color(0xFF2EE6C4),
    Color(0xFFC2F542), Color(0xFFA0A6C2), Color(0xFFFF9A52), Color(0xFF34E1FF)
)
