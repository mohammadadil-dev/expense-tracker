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
        upiId: String? = null,
        creditLimit: Double? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val partyId = repo.addOrUpdateParty(id, name, phone, direction, upiId, creditLimit)
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
        dueDate: String? = null,
        photoPath: String? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repo.addEntry(partyId, amount, note, date, type, dueDate, photoPath)
            onDone()
        }
    }

    // ── Due-date / credit-limit helpers ─────────────────────────────────────

    /** True when [entry] has a due date that has already passed and is still unsettled
     *  (a CREDIT entry with no concept of "already paid" per-entry — see class doc — so this
     *  is a simplification: it flags the purchase's own deadline, not whether the party's
     *  overall balance has since been paid down). */
    fun isOverdue(entry: KhataEntryEntity): Boolean {
        val due = entry.dueDate ?: return false
        return due < DateUtils.todayIso()
    }

    /** True when [entry]'s due date is today or within the next [withinDays] days. */
    fun isDueSoon(entry: KhataEntryEntity, withinDays: Int = 3): Boolean {
        val due = entry.dueDate ?: return false
        val today = DateUtils.todayIso()
        if (due < today) return false
        val daysAway = DateUtils.daysBetween(today, due)
        return daysAway in 0..withinDays
    }

    /** Fraction of [party]'s credit limit currently used by [balance] (0f–1f+), or null if
     *  the party has no credit limit set. Used to drive a progress bar / warning color. */
    fun creditLimitFraction(party: KhataPartyEntity, balance: Double): Float? {
        val limit = party.creditLimit ?: return null
        if (limit <= 0.0) return null
        return (balance / limit).toFloat()
    }

    fun deleteEntry(entry: KhataEntryEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.deleteEntry(entry)
            onDone()
        }
    }

    // ── Collections: aging / bulk reminders (THEY_OWE parties) ─────────────────

    /**
     * How many days [partyId]'s outstanding balance has been aging, or null if the party is
     * settled (balance <= 0). Khata has no per-entry "paid" flag (see [balanceForParty]'s doc),
     * so this is a simplification consistent with [isOverdue]/[isDueSoon]: among the party's
     * CREDIT entries, it takes the *older* of (a) the earliest already-passed due date and
     * (b) the earliest entry date overall — i.e. "how long since the oldest still-unpaid signal
     * appeared" — rather than just always preferring a passed due date.
     *
     * Bug fixed here: an earlier version preferred the earliest passed due date *unconditionally*,
     * even when the party had a much older un-due-dated CREDIT entry — e.g. a credit from over a
     * year ago with no due date, plus a small recent credit whose due date passed last week, would
     * report "7 days" instead of "365+ days" and hide the party from urgent-collections buckets.
     */
    fun agingDaysForParty(partyId: Long, entries: List<KhataEntryEntity>): Int? {
        val balance = balanceForParty(partyId, entries)
        if (balance <= 0.0) return null
        val credits = entries.filter { it.partyId == partyId && it.type == KhataEntryEntity.TYPE_CREDIT }
        if (credits.isEmpty()) return null
        val today = DateUtils.todayIso()
        val earliestPassedDueDate = credits.mapNotNull { it.dueDate }.filter { it < today }.minOrNull()
        val earliestEntryDate = credits.map { it.date }.minOrNull()
        val refDate = listOfNotNull(earliestPassedDueDate, earliestEntryDate).minOrNull() ?: return null
        return DateUtils.daysBetween(refDate, today).toInt().coerceAtLeast(0)
    }

    /** Aging bucket index for [days]: 0 = 0–30, 1 = 31–60, 2 = 61–90, 3 = 90+. */
    fun agingBucket(days: Int): Int = when {
        days <= 30 -> 0
        days <= 60 -> 1
        days <= 90 -> 2
        else       -> 3
    }

    // ── Today's summary (KhataScreen hero header) ───────────────────────────────
    // Scoped to THEY_OWE parties only — this is meant to answer the two questions a
    // shopkeeper actually wants at the end of the day: "how much cash did I collect today"
    // and "how much did I let go on credit today." I_OWE-side activity (what the shop owner
    // paid suppliers) is a separate concern and isn't folded into these two numbers.

    /** Total PAYMENT amount logged today against THEY_OWE parties — cash/UPI actually
     *  collected from customers today. */
    fun todaysCollections(parties: List<KhataPartyEntity>, entries: List<KhataEntryEntity>): Double {
        val theyOweIds = parties.filter { it.direction == KhataPartyEntity.DIRECTION_THEY_OWE }
            .map { it.id }.toSet()
        val today = DateUtils.todayIso()
        return entries.filter {
            it.partyId in theyOweIds && it.type == KhataEntryEntity.TYPE_PAYMENT && it.date == today
        }.sumOf { it.amount }
    }

    /** Total CREDIT amount logged today against THEY_OWE parties — new tabs/purchases-on-
     *  credit extended to customers today. */
    fun todaysCreditGiven(parties: List<KhataPartyEntity>, entries: List<KhataEntryEntity>): Double {
        val theyOweIds = parties.filter { it.direction == KhataPartyEntity.DIRECTION_THEY_OWE }
            .map { it.id }.toSet()
        val today = DateUtils.todayIso()
        return entries.filter {
            it.partyId in theyOweIds && it.type == KhataEntryEntity.TYPE_CREDIT && it.date == today
        }.sumOf { it.amount }
    }

    // ── Detail screen: entries for one party ──────────────────────────────────

    /** Returns a hot StateFlow of entries for a specific party. Each call creates a new
     *  collector; callers should hoist this inside a [remember] keyed on [partyId]. */
    fun entriesForParty(partyId: Long): StateFlow<List<KhataEntryEntity>> =
        repo.entriesForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
