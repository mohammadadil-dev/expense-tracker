package com.expensetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.ExpenseApp
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.data.KhataPartyEntity
import com.expensetracker.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KhataViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as ExpenseApp).khataRepository

    /** All parties, all directions — used for summary totals on dashboard. */
    val allParties: StateFlow<List<KhataPartyEntity>> = repo.allParties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** All entries across all parties — used to compute per-party balances reactively. */
    val allEntries: StateFlow<List<KhataEntryEntity>> = repo.allEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Per-direction filtered lists (drives the KhataScreen toggle UI) ────────

    val iOweParties: StateFlow<List<KhataPartyEntity>> =
        repo.partiesByDirection(KhataPartyEntity.DIRECTION_I_OWE)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val theyOweParties: StateFlow<List<KhataPartyEntity>> =
        repo.partiesByDirection(KhataPartyEntity.DIRECTION_THEY_OWE)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Balance helpers ────────────────────────────────────────────────────────

    /**
     * Outstanding balance for [partyId] derived from the already-loaded [allEntries] snapshot.
     * Credits add to the balance; payments reduce it. Clamp to 0 — a negative balance just
     * means overpaid, which is surfaced as SAR 0.00 rather than a confusing negative number.
     */
    fun balanceForParty(partyId: Long, entries: List<KhataEntryEntity>): Double {
        val partyEntries = entries.filter { it.partyId == partyId }
        val credits  = partyEntries.filter { it.type == KhataEntryEntity.TYPE_CREDIT  }.sumOf { it.amount }
        val payments = partyEntries.filter { it.type == KhataEntryEntity.TYPE_PAYMENT }.sumOf { it.amount }
        return (credits - payments).coerceAtLeast(0.0)
    }

    /** Total outstanding balance across all I-OWE parties. */
    fun totalIOwe(parties: List<KhataPartyEntity>, entries: List<KhataEntryEntity>): Double =
        parties.filter { it.direction == KhataPartyEntity.DIRECTION_I_OWE }
            .sumOf { balanceForParty(it.id, entries) }

    /** Total outstanding balance across all THEY-OWE parties. */
    fun totalTheyOwe(parties: List<KhataPartyEntity>, entries: List<KhataEntryEntity>): Double =
        parties.filter { it.direction == KhataPartyEntity.DIRECTION_THEY_OWE }
            .sumOf { balanceForParty(it.id, entries) }

    // ── Mutations ──────────────────────────────────────────────────────────────

    fun saveParty(
        id: Long?,
        name: String,
        phone: String,
        direction: String,
        initialAmount: Double = 0.0,
        initialNote: String = "",
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val partyId = repo.addOrUpdateParty(id, name, phone, direction)
            // Auto-create the first entry when a new party is saved with an opening balance.
            if (id == null && initialAmount > 0.0) {
                repo.addEntry(
                    partyId = partyId,
                    amount  = initialAmount,
                    note    = initialNote,
                    date    = DateUtils.todayIso(),
                    type    = KhataEntryEntity.TYPE_CREDIT
                )
            }
            onDone()
        }
    }

    fun deleteParty(party: KhataPartyEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteParty(party)
            onDone()
        }
    }

    fun addEntry(
        partyId: Long,
        amount: Double,
        note: String,
        date: String = DateUtils.todayIso(),
        type: String,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repo.addEntry(partyId, amount, note, date, type)
            onDone()
        }
    }

    fun deleteEntry(entry: KhataEntryEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteEntry(entry)
            onDone()
        }
    }

    // ── Detail screen: entries for one party ──────────────────────────────────

    /** Returns a hot StateFlow of entries for a specific party. Each call creates a new
     *  collector; callers should hoist this inside a [remember] keyed on [partyId]. */
    fun entriesForParty(partyId: Long): StateFlow<List<KhataEntryEntity>> =
        repo.entriesForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
