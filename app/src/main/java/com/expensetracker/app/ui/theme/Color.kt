package com.expensetracker.app.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Brand palette — warm green + amber, matching the reference design ────────

// Primary brand accent: deep forest green used in the budget ring card.
val AccentGreen     = Color(0xFF1A7A4A)   // rich emerald, primary tap targets
val AccentGreenDark = Color(0xFF0D2B1F)   // near-black forest, card gradient start
val AccentGreenMid  = Color(0xFF1E5438)   // card gradient end / mid-tone
val AccentGreenLight = Color(0xFFD4EDDA)  // tint for icons/badges on light surfaces

// Amber / gold — ring progress, "Remaining" figure, FAB colour.
val BrandAmber  = Color(0xFFFFC107)       // golden amber (ring arc, remaining label)
val BrandOrange = Color(0xFFFF8C00)       // deeper orange (FAB, user-initial avatar)

// Coral / tomato — spent label, Budget Cap tile, over-budget ring.
val BrandCoral  = Color(0xFFE8614A)       // coral-red (spent label, budget tile bg)

// Tile backgrounds
val TileGreen   = Color(0xFF43A047)       // salary tile green
val TileCoral   = Color(0xFFE8614A)       // budget cap tile (same as BrandCoral)

// ─── Surface colours ──────────────────────────────────────────────────────────
// Warm cream background — the screen background is a soft peachy off-white, not
// a cool lavender, to complement the green/amber brand palette above.
val BgApp    = Color(0xFFFAF3EA)          // warm cream page background
val CardWhite = Color(0xFFFFFFFF)         // pure white for card surfaces

// ─── Text ─────────────────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF5B6178)
val TextMuted     = Color(0xFF8A8FA3)
val BorderLight   = Color(0xFFE8E0D5)     // warm-tinted border to match cream bg

// ─── Semantic / functional ────────────────────────────────────────────────────
val DangerRed    = Color(0xFFE8614A)      // shares coral for consistency
val SuccessGreen = Color(0xFF43A047)      // matches salary tile
val WarningAmber = Color(0xFFFFC107)      // same as BrandAmber
val StreakOrange  = Color(0xFFFF5722)     // deep orange — streak/fire accent, readable on any bg

// ─── Legacy aliases — kept so existing components that still reference these
//     old names compile without changes. Gradually migrate call-sites to the
//     new names above as they're touched for other reasons.
val AccentIndigo      = AccentGreen
val AccentIndigoDark  = AccentGreenDark
val AccentIndigoLight = AccentGreenLight
val OnAccent          = Color(0xFFFFFFFF)
val NeonCyan          = BrandAmber        // re-mapped to amber for warm palette
val NeonViolet        = BrandCoral        // re-mapped to coral
val NeonPink          = Color(0xFFFF3FA4) // unchanged (not used on main screens)
val NeonTeal          = TileGreen         // re-mapped to tile green

val CategoryPalette = listOf(
    Color(0xFF43A047), Color(0xFFFFC107), Color(0xFFE8614A), Color(0xFF29B6F6),
    Color(0xFFAB47BC), Color(0xFFEF5350), Color(0xFF26A69A), Color(0xFF8D6E63),
    Color(0xFFEC407A), Color(0xFF78909C), Color(0xFFFF7043), Color(0xFF66BB6A)
)
