package com.expensetracker.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Single access point for all Khata (local shop credit ledger) data.
 *
 * Takes the full [AppDatabase] so it can also write to the expenses table:
 * every CREDIT entry on an I-OWE party auto-generates an [ExpenseEntity] row
 * (filed under the "Khata" category) so the amount appears in the dashboard's
 * monthly totals, budget tracker, and category breakdown — exactly the same
 * pattern used by debt/EMI payments.
 *
 * Deleting an entry or its parent party cleans up the linked expense so no
 * phantom spend lingers in the dashboard.
 */
class KhataRepository(private val db: AppDatabase) {

    companion object {
        const val KHATA_CATEGORY_KEY   = "cat_khata"
        const val KHATA_CATEGORY_COLOR = "#7C3AED"   // violet — distinct from debt orange
    }

    private val dao = db.khataDao()

    val allParties: Flow<List<KhataPartyEntity>> = dao.allParties()

    fun partiesByDirection(direction: String): Flow<List<KhataPartyEntity>> =
        dao.partiesByDirection(direction)

    fun entriesForParty(partyId: Long): Flow<List<KhataEntryEntity>> =
        dao.entriesForParty(partyId)

    val allEntries: Flow<List<KhataEntryEntity>> = dao.allEntries()

    suspend fun addOrUpdateParty(
        id: Long?,
        name: String,
        phone: String,
        direction: String,
        upiId: String? = null
    ): Long {
        val entity = KhataPartyEntity(
            id = id ?: 0,
            name = name.trim(),
            phone = phone.trim(),
            direction = direction,
            upiId = upiId?.trim()?.takeIf { it.isNotBlank() }
        )
        return if (id == null) {
            // New party — insert and return auto-generated id
            dao.insertParty(entity)
        } else {
            // Edit — use @Update so Room issues a plain SQL UPDATE instead of
            // DELETE + INSERT, which would cascade-delete all entries for the party.
            dao.updateParty(entity)
            id
        }
    }

    suspend fun deleteParty(party: KhataPartyEntity) {
        // Clean up linked expenses before CASCADE removes the entry rows —
        // otherwise the linkedExpenseId references are lost and phantom spend lingers.
        dao.entriesForPartyOnce(party.id).forEach { entry ->
            entry.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
        }
        dao.deleteParty(party)
    }

    /**
     * Adds a new Khata entry. For CREDIT entries on I-OWE parties the amount is
     * real outgoing spend, so an [ExpenseEntity] is created and the link stored.
     * PAYMENT entries (settling the debt) and THEY-OWE entries (incoming money)
     * produce no expense row.
     */
    suspend fun addEntry(
        partyId: Long,
        amount: Double,
        note: String,
        date: String,
        type: String
    ): Long {
        var linkedExpenseId: Long? = null

        if (type == KhataEntryEntity.TYPE_CREDIT) {
            val party = dao.partyById(partyId)
            if (party?.direction == KhataPartyEntity.DIRECTION_I_OWE) {
                val categoryId = ensureKhataCategoryId()
                val monthKey   = if (date.length >= 7) date.substring(0, 7) else date
                val description = party.name  // e.g. "Fresh Mart" shows in the dashboard
                linkedExpenseId = db.expenseDao().insert(
                    ExpenseEntity(
                        categoryId  = categoryId,
                        description = description,
                        amount      = amount,
                        date        = date,
                        monthKey    = monthKey
                    )
                )
            }
        }

        val entry = KhataEntryEntity(
            partyId         = partyId,
            amount          = amount,
            note            = note.trim(),
            date            = date,
            type            = type,
            linkedExpenseId = linkedExpenseId
        )
        return dao.insertEntry(entry)
    }

    suspend fun deleteEntry(entry: KhataEntryEntity) {
        dao.deleteEntry(entry)
        // Remove the phantom expense so the dashboard stays in sync.
        entry.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Returns (or creates) the Khata category and gives back its DB id. */
    private suspend fun ensureKhataCategoryId(): Long {
        val existing = db.categoryDao().getAllOnce().firstOrNull { it.nameKey == KHATA_CATEGORY_KEY }
        if (existing != null) return existing.id
        return db.categoryDao().insert(
            CategoryEntity(nameKey = KHATA_CATEGORY_KEY, colorHex = KHATA_CATEGORY_COLOR, sortOrder = 15)
        )
    }
}
