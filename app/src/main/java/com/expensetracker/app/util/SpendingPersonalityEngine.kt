package com.expensetracker.app.util

import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.ExpenseEntity

/**
 * Derives a "spending personality" from the current month's expenses.
 *
 * Logic: find the category with the highest total spend this month,
 * then map it to a named personality with emoji, label string key,
 * and a tint colour int (used by the card).
 *
 * Returns null when there are fewer than 3 expenses (too little data to
 * make a meaningful statement).
 */
object SpendingPersonalityEngine {

    data class Personality(
        val emoji: String,
        /** String resource key — e.g. "personality_foodie" */
        val labelKey: String,
        /** Category display name (localised at call site via stringResource) */
        val categoryNameKey: String,
        /** Percentage of total month spend this category represents (0–100) */
        val sharePercent: Int,
        /** ARGB tint for the card background / accent (0xFF______) */
        val color: Long
    )

    private data class PersonalityDef(
        val emoji: String,
        val labelKey: String,
        val color: Long
    )

    private val MAP: Map<String, PersonalityDef> = mapOf(
        "cat_food"            to PersonalityDef("🍔", "personality_foodie",       0xFF43A047),
        "cat_transport"       to PersonalityDef("🚗", "personality_commuter",     0xFF1E88E5),
        "cat_housing"         to PersonalityDef("🏠", "personality_homebody",     0xFF8E24AA),
        "cat_utilities"       to PersonalityDef("💡", "personality_bill_payer",   0xFFFB8C00),
        "cat_entertainment"   to PersonalityDef("🎬", "personality_entertainer",  0xFFE53935),
        "cat_healthcare"      to PersonalityDef("💊", "personality_health_first", 0xFF00ACC1),
        "cat_shopping"        to PersonalityDef("🛍️", "personality_shopaholic",   0xFFD81B60),
        "cat_subscriptions"   to PersonalityDef("📱", "personality_tech_buff",    0xFF3949AB),
        "cat_savings"         to PersonalityDef("💰", "personality_saver",        0xFF00897B),
        "cat_fuel"            to PersonalityDef("⛽", "personality_road_warrior", 0xFF6D4C41),
        "cat_education"       to PersonalityDef("📚", "personality_scholar",      0xFF5E35B1),
        "cat_mobile_recharge" to PersonalityDef("📶", "personality_connected",    0xFF039BE5),
        "cat_electricity"     to PersonalityDef("⚡", "personality_power_user",   0xFFF4511E),
        "cat_farming"         to PersonalityDef("🌾", "personality_farmer",       0xFF558B2F),
        "cat_khata"           to PersonalityDef("📒", "personality_trader",       0xFF6D4C41),
        "cat_debt_payments"   to PersonalityDef("💳", "personality_debt_crusher", 0xFF37474F),
        "cat_other"           to PersonalityDef("🎯", "personality_versatile",    0xFF546E7A),
    )

    fun compute(
        expenses: List<ExpenseEntity>,
        categories: List<CategoryEntity>
    ): Personality? {
        if (expenses.size < 3) return null

        val totalSpend = expenses.sumOf { it.amount }
        if (totalSpend <= 0.0) return null

        // Build categoryId → nameKey lookup
        val nameKeyById = categories.associateBy({ it.id }, { it.nameKey })

        // Sum spend per nameKey (skip cat_debt_payments — auto-managed, not user-chosen)
        val spendByKey = expenses
            .mapNotNull { e -> nameKeyById[e.categoryId]?.let { key -> key to e.amount } }
            .filter { (key, _) -> key != "cat_debt_payments" }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, amounts) -> amounts.sum() }

        val (topKey, topAmount) = spendByKey.maxByOrNull { it.value } ?: return null

        val def = MAP[topKey] ?: PersonalityDef("🎯", "personality_versatile", 0xFF546E7A)
        val share = ((topAmount / totalSpend) * 100).toInt().coerceIn(1, 100)

        return Personality(
            emoji           = def.emoji,
            labelKey        = def.labelKey,
            categoryNameKey = topKey,
            sharePercent    = share,
            color           = def.color
        )
    }
}
