package com.expensetracker.app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.expensetracker.app.R
import com.expensetracker.app.data.PaymentAccountEntity

/**
 * Resolves a [PaymentAccountEntity]'s display name — built-in accounts (seeded with
 * [PaymentAccountEntity.nameKey] set) localize automatically; user-created accounts only
 * ever have [PaymentAccountEntity.customName] set. Mirrors [categoryDisplayName].
 */
@Composable
fun accountDisplayName(account: PaymentAccountEntity): String {
    val resId = when (account.nameKey) {
        "account_cash" -> R.string.account_cash
        "account_bank" -> R.string.account_bank
        "account_card" -> R.string.account_card
        else -> null
    }
    return if (resId != null) stringResource(resId) else account.customName ?: ""
}

/** Small emoji shown next to an account, chosen by [PaymentAccountEntity.type]. Mirrors
 * [categoryEmoji] — purely decorative, has no bearing on any calculation. */
fun accountEmoji(type: String): String = when (type) {
    "CASH" -> "💵"   // 💵
    "BANK" -> "🏦"   // 🏦
    "CARD" -> "💳"   // 💳
    "UPI"  -> "📲"   // 📲
    "WALLET" -> "👛" // 👛
    else -> "💰"     // 💰
}
