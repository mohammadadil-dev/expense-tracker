package com.expensetracker.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.ExpenseApp
import com.expensetracker.app.data.BudgetEntity
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.data.DebtPaymentEntity
import com.expensetracker.app.data.DeleteCategoryResult
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.data.PendingSmsExpense
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.LocaleHelper
import com.expensetracker.app.util.SmsExpenseParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ExpenseApp
    private val repository = app.repository
    private val settings = app.settings

    private val _currentMonthKey = MutableStateFlow(DateUtils.currentMonthKey())
    val currentMonthKey: StateFlow<String> = _currentMonthKey

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val monthExpenses: StateFlow<List<ExpenseEntity>> = _currentMonthKey
        .flatMapLatest { key -> repository.expensesForMonth(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** False until user explicitly picks a currency on the setup screen. */
    val isCurrencySetupDone: Boolean get() = settings.currencySetupDone

    fun markCurrencySetupDone() { settings.currencySetupDone = true }

    private val _currencySymbol = MutableStateFlow(settings.currencySymbol)
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _languagePref = MutableStateFlow(settings.languagePref)
    val languagePref: StateFlow<String> = _languagePref

    private val _displayName = MutableStateFlow(settings.displayName)
    val displayName: StateFlow<String> = _displayName

    private val _smsDetectionEnabled = MutableStateFlow(settings.smsDetectionEnabled)
    val smsDetectionEnabled: StateFlow<Boolean> = _smsDetectionEnabled

    private val _monthlySalary = MutableStateFlow(settings.monthlySalary)
    val monthlySalary: StateFlow<Double> = _monthlySalary

    val pendingSmsExpenses: StateFlow<List<PendingSmsExpense>> = repository.pendingSmsExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Standing budget targets (overall + per-category) — only the spend they're compared
    // against resets each month; these rows themselves persist until edited or cleared.
    val budgets: StateFlow<List<BudgetEntity>> = repository.budgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Debts/loans (both directions) and their full payment history. Outstanding balance is
    // never stored — always derived live from principal minus payments, see DebtEntity.
    val debts: StateFlow<List<DebtEntity>> = repository.debts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debtPayments: StateFlow<List<DebtPaymentEntity>> = repository.debtPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stable for the lifetime of the install — generated once and persisted, never reassigned.
    val customerId: String = settings.customerId

    fun navigateMonth(delta: Long) {
        _currentMonthKey.value = DateUtils.shiftMonthKey(_currentMonthKey.value, delta)
    }

    fun saveExpense(
        id: Long?,
        categoryId: Long,
        description: String,
        amount: Double,
        date: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addOrUpdateExpense(id, categoryId, description, amount, date)
            _currentMonthKey.value = DateUtils.monthKeyFromDate(date)
            onDone()
        }
    }

    fun deleteExpense(expense: ExpenseEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            onDone()
        }
    }

    fun addCategory(name: String, colorHex: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addCategory(name, colorHex)
            onDone()
        }
    }

    fun renameCategory(category: CategoryEntity, newName: String) {
        viewModelScope.launch { repository.renameCategory(category, newName) }
    }

    fun recolorCategory(category: CategoryEntity, colorHex: String) {
        viewModelScope.launch { repository.recolorCategory(category, colorHex) }
    }

    fun isCategoryInUse(categoryId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.isCategoryInUse(categoryId)) }
    }

    fun deleteCategory(category: CategoryEntity, onResult: (DeleteCategoryResult) -> Unit) {
        viewModelScope.launch { onResult(repository.deleteCategory(category)) }
    }

    /** Sets, updates, or (passing 0 or less) clears the standing budget target for
     * [categoryId] — null means the overall monthly budget rather than a per-category one. */
    fun setBudget(categoryId: Long?, amount: Double) {
        viewModelScope.launch { repository.setBudget(categoryId, amount) }
    }

    fun setLanguagePref(pref: String) {
        settings.languagePref = pref
        _languagePref.value = pref
        LocaleHelper.applyLanguagePreference(pref)
    }

    fun setDisplayName(name: String) {
        settings.displayName = name
        _displayName.value = name
    }

    fun setCurrencySymbol(symbol: String) {
        settings.currencySymbol = symbol
        _currencySymbol.value = symbol
    }

    /** Switches currency and rescales every stored expense by [rate] (1 old-currency unit
     * = [rate] new-currency units). All arithmetic is local — no network call. */
    fun convertCurrency(newSymbol: String, rate: Double, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.convertAllAmounts(rate)
            settings.currencySymbol = newSymbol
            _currencySymbol.value = newSymbol
            onDone()
        }
    }

    fun resetAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.resetAllData()
            _currentMonthKey.value = DateUtils.currentMonthKey()
            onDone()
        }
    }

    fun setSmsDetectionEnabled(enabled: Boolean) {
        settings.smsDetectionEnabled = enabled
        _smsDetectionEnabled.value = enabled
    }

    fun setMonthlySalary(amount: Double) {
        settings.monthlySalary = amount
        _monthlySalary.value = amount
    }

    /**
     * Called with the raw text of an SMS the user just approved via the system consent prompt.
     * Silently does nothing if the message doesn't look like a debit transaction, or if SMS
     * detection has since been turned off — nothing is ever saved as an expense here, it only
     * lands in the review queue.
     */
    fun handleIncomingSms(message: String) {
        if (!_smsDetectionEnabled.value) return
        val parsed = SmsExpenseParser.parse(message) ?: return
        viewModelScope.launch {
            val categoryId = SmsExpenseParser.guessCategoryId(parsed.categoryNameKeyGuess, categories.value)
            repository.addPendingSmsExpense(
                PendingSmsExpense(
                    rawMessage = message,
                    amount = parsed.amount,
                    description = parsed.merchantOrNote,
                    suggestedCategoryId = categoryId,
                    date = DateUtils.todayIso(),
                    receivedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun dismissPendingSms(item: PendingSmsExpense) {
        viewModelScope.launch { repository.dismissPendingSmsExpense(item) }
    }

    /** User reviewed (and possibly edited) a pending SMS item and confirmed it as a real expense. */
    fun acceptPendingSms(
        item: PendingSmsExpense,
        categoryId: Long,
        description: String,
        amount: Double,
        date: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addOrUpdateExpense(null, categoryId, description, amount, date)
            repository.dismissPendingSmsExpense(item)
            onDone()
        }
    }

    fun saveDebt(
        id: Long?,
        name: String,
        direction: String,
        principal: Double,
        interestRatePercent: Double,
        minimumPayment: Double,
        startDate: String,
        notes: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addOrUpdateDebt(id, name, direction, principal, interestRatePercent, minimumPayment, startDate, notes)
            onDone()
        }
    }

    fun deleteDebt(debt: DebtEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
            onDone()
        }
    }

    fun setDebtClosed(debt: DebtEntity, isClosed: Boolean) {
        viewModelScope.launch { repository.setDebtClosed(debt, isClosed) }
    }

    fun recordDebtPayment(debt: DebtEntity, amount: Double, date: String, note: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.recordDebtPayment(debt, amount, date, note)
            onDone()
        }
    }

    fun deleteDebtPayment(payment: DebtPaymentEntity) {
        viewModelScope.launch { repository.deleteDebtPayment(payment) }
    }
}
