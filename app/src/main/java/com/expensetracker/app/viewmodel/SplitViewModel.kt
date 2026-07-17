package com.expensetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.ExpenseApp
import com.expensetracker.app.data.ItemDraft
import com.expensetracker.app.data.SplitExpenseEntity
import com.expensetracker.app.data.SplitExpenseItemEntity
import com.expensetracker.app.data.SplitExpenseItemMemberEntity
import com.expensetracker.app.data.SplitExpenseShareEntity
import com.expensetracker.app.data.SplitGroupEntity
import com.expensetracker.app.data.SplitMemberEntity
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.Settlement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SplitViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ExpenseApp
    private val repository = app.splitRepository

    // ── Groups list ──────────────────────────────────────────────────────────

    val groups: StateFlow<List<SplitGroupEntity>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Selected group (detail screen) ───────────────────────────────────────

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId

    val selectedGroupMembers: StateFlow<List<SplitMemberEntity>> = _selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.getMembersForGroup(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedGroupExpenses: StateFlow<List<SplitExpenseEntity>> = _selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.getExpensesForGroup(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectGroup(groupId: Long) { _selectedGroupId.value = groupId }

    // ── Group CRUD ───────────────────────────────────────────────────────────

    fun createGroup(
        name: String,
        emoji: String,
        ownerName: String,
        ownerColor: String,
        extraMembers: List<Pair<String, String>>  // (name, colorHex)
    ) {
        viewModelScope.launch {
            val today = DateUtils.todayIso()
            val groupId = repository.createGroup(name, emoji, today)
            // First member is always the device owner
            repository.addMember(groupId, ownerName, ownerColor, isMe = true)
            extraMembers.forEach { (mName, mColor) ->
                repository.addMember(groupId, mName, mColor)
            }
            _selectedGroupId.value = groupId
        }
    }

    fun updateGroup(group: SplitGroupEntity) {
        viewModelScope.launch { repository.updateGroup(group) }
    }

    fun deleteGroup(group: SplitGroupEntity) {
        viewModelScope.launch { repository.deleteGroup(group) }
    }

    // ── Member CRUD ──────────────────────────────────────────────────────────

    fun addMember(groupId: Long, name: String, colorHex: String) {
        viewModelScope.launch { repository.addMember(groupId, name, colorHex) }
    }

    fun updateMember(member: SplitMemberEntity) {
        viewModelScope.launch { repository.updateMember(member) }
    }

    fun deleteMember(member: SplitMemberEntity) {
        viewModelScope.launch { repository.deleteMember(member) }
    }

    /** Sets/clears a member's UPI ID — used by the Settle Up sheet's inline prompt. */
    fun setMemberUpiId(member: SplitMemberEntity, upiId: String) {
        viewModelScope.launch { repository.setMemberUpiId(member, upiId) }
    }

    /** Sets/clears a member's phone number — used by the Settle Up sheet's inline prompt
     *  before sending that member an individual settlement reminder. */
    fun setMemberPhone(member: SplitMemberEntity, phone: String) {
        viewModelScope.launch { repository.setMemberPhone(member, phone) }
    }

    // ── Expense CRUD ─────────────────────────────────────────────────────────

    fun addExpense(
        groupId: Long,
        description: String,
        amount: Double,
        paidByMemberId: Long,
        splitAmongIds: List<Long>,
        date: String,
        customShares: Map<Long, Double>? = null,
        items: List<ItemDraft>? = null
    ) {
        viewModelScope.launch {
            repository.addExpense(
                groupId          = groupId,
                description      = description,
                amount           = amount,
                paidByMemberId   = paidByMemberId,
                splitAmongIds    = splitAmongIds,
                date             = date,
                customShares     = customShares,
                items            = items
            )
        }
    }

    fun deleteExpense(expense: SplitExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    // ── Settlement ───────────────────────────────────────────────────────────

    /** Returns the current settlement transfers for the selected group. */
    suspend fun getSettlement(groupId: Long): List<Settlement> =
        repository.calculateSettlement(groupId)

    /** Returns net balance map for all members in the selected group. */
    suspend fun getNetBalances(groupId: Long): Map<Long, Double> =
        repository.getNetBalances(groupId)

    /**
     * Marks a settlement payment as done by inserting a settlement expense.
     * The next call to [getSettlement] will reflect the reduced balances automatically.
     */
    fun markSettlementPaid(groupId: Long, fromMemberId: Long, toMemberId: Long, amount: Double) {
        viewModelScope.launch {
            repository.markSettlementPaid(
                groupId        = groupId,
                fromMemberId   = fromMemberId,
                toMemberId     = toMemberId,
                amount         = amount,
                date           = DateUtils.todayIso()
            )
        }
    }

    /** Returns shares for a specific group (for display in detail) */
    suspend fun getSharesForGroup(groupId: Long): List<SplitExpenseShareEntity> =
        repository.getSharesForGroup(groupId)

    /** Returns a one-shot snapshot of members for the given group (for summary calculations). */
    suspend fun getMembersSnapshot(groupId: Long): List<SplitMemberEntity> =
        repository.getMembersSnapshot(groupId)

    /** Itemized breakdown for one expense — empty if it wasn't itemized. */
    suspend fun getItemsForExpense(expenseId: Long): List<SplitExpenseItemEntity> =
        repository.getItemsForExpense(expenseId)

    /** Which members share each item on an itemized expense. */
    suspend fun getItemMembersForExpense(expenseId: Long): List<SplitExpenseItemMemberEntity> =
        repository.getItemMembersForExpense(expenseId)
}
