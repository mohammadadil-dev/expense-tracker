package com.expensetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.ExpenseApp
import com.expensetracker.app.data.JamiyaCircleEntity
import com.expensetracker.app.data.JamiyaContributionEntity
import com.expensetracker.app.data.JamiyaMemberEntity
import com.expensetracker.app.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Screen state + actions for the Jam'iya (rotating savings circle) feature. Structured exactly
 * like [SplitViewModel]: a reactive list of circles, a selected-circle id that fans out into the
 * selected circle's members and contributions, and thin action methods that delegate to
 * [com.expensetracker.app.data.JamiyaRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JamiyaViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ExpenseApp
    private val repository = app.jamiyaRepository

    // ── Circles list ───────────────────────────────────────────────────────────

    val circles: StateFlow<List<JamiyaCircleEntity>> = repository.allCircles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Selected circle (detail screen) ─────────────────────────────────────────

    private val _selectedCircleId = MutableStateFlow<Long?>(null)
    val selectedCircleId: StateFlow<Long?> = _selectedCircleId

    val selectedCircle: StateFlow<JamiyaCircleEntity?> =
        combine(_selectedCircleId, circles) { id, all -> all.firstOrNull { it.id == id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedCircleMembers: StateFlow<List<JamiyaMemberEntity>> = _selectedCircleId
        .flatMapLatest { id ->
            if (id != null) repository.getMembersForCircle(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCircleContributions: StateFlow<List<JamiyaContributionEntity>> = _selectedCircleId
        .flatMapLatest { id ->
            if (id != null) repository.getContributionsForCircle(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCircle(circleId: Long) { _selectedCircleId.value = circleId }

    // ── Circle CRUD ──────────────────────────────────────────────────────────

    fun createCircle(
        name: String,
        emoji: String,
        contributionAmount: Double,
        frequency: String,
        startDate: String = DateUtils.todayIso(),
        notes: String = "",
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.createCircle(name, emoji, contributionAmount, frequency, startDate, notes)
            onCreated(id)
        }
    }

    fun updateCircle(circle: JamiyaCircleEntity) {
        viewModelScope.launch { repository.updateCircle(circle) }
    }

    fun setCurrentRound(circle: JamiyaCircleEntity, round: Int) {
        viewModelScope.launch { repository.setCurrentRound(circle, round) }
    }

    fun setClosed(circle: JamiyaCircleEntity, closed: Boolean) {
        viewModelScope.launch { repository.setClosed(circle, closed) }
    }

    fun deleteCircle(circle: JamiyaCircleEntity) {
        viewModelScope.launch { repository.deleteCircle(circle) }
    }

    // ── Member actions ─────────────────────────────────────────────────────────

    fun addMember(
        circleId: Long,
        name: String,
        phone: String = "",
        emoji: String = "",
        isMe: Boolean = false
    ) {
        viewModelScope.launch { repository.addMember(circleId, name, phone, emoji, isMe) }
    }

    fun updateMember(member: JamiyaMemberEntity) {
        viewModelScope.launch { repository.updateMember(member) }
    }

    fun deleteMember(member: JamiyaMemberEntity) {
        viewModelScope.launch { repository.deleteMember(member) }
    }

    fun reorderPayout(circleId: Long, orderedMemberIds: List<Long>) {
        viewModelScope.launch { repository.reorderPayout(circleId, orderedMemberIds) }
    }

    // ── Contribution actions ─────────────────────────────────────────────────────

    /** Ticks "member paid this round" using the circle's standard contribution amount. */
    fun markPaid(circle: JamiyaCircleEntity, memberId: Long, round: Int, date: String = DateUtils.todayIso()) {
        viewModelScope.launch {
            repository.recordContribution(circle.id, memberId, round, circle.contributionAmount, date)
        }
    }

    fun markUnpaid(circleId: Long, memberId: Long, round: Int) {
        viewModelScope.launch { repository.unrecordContribution(circleId, memberId, round) }
    }
}
