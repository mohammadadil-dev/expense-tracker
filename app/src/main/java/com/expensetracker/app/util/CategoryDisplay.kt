package com.expensetracker.app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.expensetracker.app.R
import com.expensetracker.app.data.CategoryEntity

/**
 * Resolves a category's display name. Built-in categories carry a [CategoryEntity.nameKey]
 * which is mapped to a localized string resource, so they automatically show in Arabic or
 * English depending on the app's current language. User-renamed/created categories just use
 * the free-text [CategoryEntity.customName] as typed.
 */
@Composable
fun categoryDisplayName(category: CategoryEntity): String {
    val resId = when (category.nameKey) {
        "cat_housing" -> R.string.cat_housing
        "cat_food" -> R.string.cat_food
        "cat_transport" -> R.string.cat_transport
        "cat_utilities" -> R.string.cat_utilities
        "cat_entertainment" -> R.string.cat_entertainment
        "cat_healthcare" -> R.string.cat_healthcare
        "cat_shopping" -> R.string.cat_shopping
        "cat_subscriptions" -> R.string.cat_subscriptions
        "cat_savings" -> R.string.cat_savings
        "cat_other" -> R.string.cat_other
        "cat_debt_payments"   -> R.string.cat_debt_payments
        "cat_mobile_recharge" -> R.string.cat_mobile_recharge
        "cat_electricity"     -> R.string.cat_electricity
        "cat_fuel"            -> R.string.cat_fuel
        "cat_farming"         -> R.string.cat_farming
        "cat_khata"           -> R.string.cat_khata
        "cat_education"       -> R.string.cat_education
        "cat_splits"          -> R.string.cat_splits
        else -> null
    }
    return if (resId != null) stringResource(resId) else (category.customName ?: "")
}
