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
 */
@Entity(tableName = "khata_parties")
data class KhataPartyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val direction: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        /** Shop / vendor the user has a running tab at. */
        const val DIRECTION_I_OWE = "I_OWE"

        /** Customer / person who has an outstanding balance with the user. */
        const val DIRECTION_THEY_OWE = "THEY_OWE"
    }
}
