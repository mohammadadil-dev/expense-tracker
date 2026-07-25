package com.expensetracker.app.util

/**
 * Maps a category [nameKey] to a single emoji glyph for dashboard display.
 * User-created categories (nameKey == null or blank) fall back to the generic card emoji.
 */
fun categoryEmoji(nameKey: String?): String = when (nameKey) {
    "cat_housing"         -> "🏠"
    "cat_food"            -> "🍔"
    "cat_transport"       -> "🚗"
    "cat_utilities"       -> "⚡"
    "cat_entertainment"   -> "🎬"
    "cat_healthcare"      -> "💊"
    "cat_shopping"        -> "🛍️"
    "cat_subscriptions"   -> "📱"
    "cat_savings"         -> "💰"
    "cat_other"           -> "📦"
    "cat_debt_payments"   -> "🏦"
    "cat_mobile_recharge" -> "📶"
    "cat_electricity"     -> "💡"
    "cat_fuel"            -> "⛽"
    "cat_farming"         -> "🌾"
    "cat_khata"           -> "📒"
    "cat_education"       -> "📚"
    "cat_splits"          -> "🤝"
    "cat_charity"         -> "🤲"
    else                  -> "💳"
}
