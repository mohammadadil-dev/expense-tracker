package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One party in the Khata ledger — either a shop/person the user owes money to
 * ([direction] == [DIRECTION_I_OWE]) or a customer who owes the user money
 * ([direction] == [DIRECTION_THEY_OWE]).
 *
 * [phone] is optional — needed only when the user wants to send a WhatsApp reminder.
 * It is stored as a raw string (digits + optional +/country-code prefix) so the UI
 * can format it however the OS expects for the wa.me deep-link.
 *
 * [upiId] is optional and India-only (UPI VPA, e.g. "name@bank") — this is the party's
 * *own* UPI ID, only relevant for a future "pay them" flow. Not used by the current
 * "request payment via UPI" flow, which uses the app owner's own UPI ID instead
 * (see [SettingsRepository.myUpiId]); captured here now so that flow doesn't need a
 * second data-entry pass later.
 */
@Entity(tableName = "khata_parties")
data class KhataPartyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val direction: String,
    val upiId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    /** Optional cap on this party's outstanding balance — a shop's credit limit for I_OWE
     *  parties, or a limit the user extends to a customer for THEY_OWE parties. Purely a
     *  local warning threshold (this app never blocks an entry from being added); null =
     *  no limit tracked. */
    val creditLimit: Double? = null
) {
    companion object {
        /** Shop / vendor the user has a running tab at. */
        const val DIRECTION_I_OWE = "I_OWE"

        /** Customer / person who has an outstanding balance with the user. */
        const val DIRECTION_THEY_OWE = "THEY_OWE"
    }
}
