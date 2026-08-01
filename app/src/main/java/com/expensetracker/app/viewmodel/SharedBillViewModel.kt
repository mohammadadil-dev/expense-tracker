package com.expensetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.ExpenseApp
import com.expensetracker.app.data.SharedBillEntity
import com.expensetracker.app.data.SharedBillGroupEntity
import com.expensetracker.app.data.SharedBillGroupMemberEntity
import com.expensetracker.app.data.SharedBillMemberDraft
import com.expensetracker.app.data.SharedBillMemberEntity
import com.expensetracker.app.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SharedBillViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as ExpenseApp).sharedBillRepository

    // ── Groups (apartments) ───────────────────────────────────────────────────
    val groups: StateFlow<List<SharedBillGroupEntity>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    fun selectGroup(id: Long) { _selectedGroupId.value = id }

    val selectedGroupBills: StateFlow<List<SharedBillEntity>> = _selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.billsForGroup(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedGroupMembers: StateFlow<List<SharedBillGroupMemberEntity>> = _selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.groupMembers(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createGroup(
        name: String,
        emoji: String,
        splitMode: Int,
        members: List<SharedBillMemberDraft>,
        onDone: (Long) -> Unit = {}
    ) {
        viewModelScope.launch { onDone(repository.createGroup(name, emoji, splitMode, members)) }
    }

    fun addGroupMember(groupId: Long, name: String, personCount: Int, isMe: Boolean = false) {
        viewModelScope.launch { repository.addGroupMember(groupId, name, personCount, isMe) }
    }

    fun updateGroupMember(member: SharedBillGroupMemberEntity) {
        viewModelScope.launch { repository.updateGroupMember(member) }
    }

    fun deleteGroupMember(member: SharedBillGroupMemberEntity) {
        viewModelScope.launch { repository.deleteGroupMember(member) }
    }

    fun deleteGroup(group: SharedBillGroupEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch { repository.deleteGroup(group); onDone() }
    }

    // ── Bills within a group ──────────────────────────────────────────────────
    private val _selectedBillId = MutableStateFlow<Long?>(null)
    fun selectBill(id: Long) { _selectedBillId.value = id }

    val selectedBillMembers: StateFlow<List<SharedBillMemberEntity>> = _selectedBillId
        .flatMapLatest { id ->
            if (id != null) repository.membersForBill(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createBill(
        groupId: Long,
        label: String,
        note: String,
        periodStart: String,
        periodEnd: String,
        total: Double,
        members: List<SharedBillMemberDraft>,
        onDone: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.createBill(groupId, label, note, periodStart, periodEnd, total, members)
            onDone(id)
        }
    }

    fun setPaid(memberId: Long, paid: Boolean) {
        viewModelScope.launch { repository.setMemberPaid(memberId, paid) }
    }

    fun deleteBill(bill: SharedBillEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch { repository.deleteBill(bill); onDone() }
    }

    fun logMyShare(bill: SharedBillEntity, amount: Double, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.logMyShare(bill, amount, DateUtils.todayIso())
            onDone()
        }
    }
}
