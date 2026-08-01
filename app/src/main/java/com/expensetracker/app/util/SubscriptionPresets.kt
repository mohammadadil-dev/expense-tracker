package com.expensetracker.app.util

/**
 * Curated catalog of common subscription services for the Add-Subscription picker. Each preset
 * is just a display name + emoji badge (no bundled logo assets), so the grid stays lightweight
 * and works offline. Names are brand names and are intentionally NOT localized.
 *
 * Ordering groups services by type (streaming → music → AI/tools → cloud → everyday) so the
 * grid reads sensibly even without visible section headers. Coverage is global plus the two
 * markets the app targets: India and Saudi Arabia / MENA.
 *
 * Picking a preset only pre-fills the name + icon on the sheet — every subscription is still
 * stored as a recurring expense under the "Subscriptions" category, so the dashboard's
 * subscription-cost card keeps working unchanged.
 */
data class SubscriptionPreset(val name: String, val emoji: String)

object SubscriptionPresets {

    val ALL: List<SubscriptionPreset> = listOf(
        // Streaming (global)
        SubscriptionPreset("Netflix", "🎬"),
        SubscriptionPreset("YouTube Premium", "▶️"),
        SubscriptionPreset("Amazon Prime", "📦"),
        SubscriptionPreset("Disney+", "🏰"),
        // Streaming (India)
        SubscriptionPreset("JioHotstar", "⭐"),
        SubscriptionPreset("JioCinema", "🎥"),
        // Streaming (Saudi / MENA)
        SubscriptionPreset("Shahid", "📺"),
        SubscriptionPreset("STC TV", "📡"),

        // Music
        SubscriptionPreset("Spotify", "🎵"),
        SubscriptionPreset("Apple Music", "🎧"),
        SubscriptionPreset("Anghami", "🎼"),
        SubscriptionPreset("Gaana", "🎶"),

        // AI & productivity
        SubscriptionPreset("ChatGPT", "🤖"),
        SubscriptionPreset("Claude", "🧠"),
        SubscriptionPreset("Microsoft 365", "💼"),

        // Cloud & storage
        SubscriptionPreset("Google One", "☁️"),
        SubscriptionPreset("iCloud+", "🍏"),

        // Everyday
        SubscriptionPreset("Mobile recharge", "📱"),
        SubscriptionPreset("Gym", "🏋️"),
        SubscriptionPreset("Online course", "📚"),
        SubscriptionPreset("News", "📰"),
        SubscriptionPreset("Gaming", "🎮"),
    )
}
