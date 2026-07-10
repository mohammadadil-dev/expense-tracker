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
import com.expensetracker.app.data.FamilyMemberEntity
import com.expensetracker.app.data.IncomeEntity
import com.expensetracker.app.data.PendingSmsExpense
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import com.expensetracker.app.util.BackupManager
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.DriveBackupManager
import com.expensetracker.app.util.LocaleHelper
import com.expensetracker.app.util.ReminderScheduler
import com.expensetracker.app.util.SmsExpenseParser
import com.expensetracker.app.widget.ExpenseWidget
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as ExpenseApp
    private val repository = app.repository
    private val familyRepository = app.familyRepository
    private val settings = app.settings

    private val _currentMonthKey = MutableStateFlow(DateUtils.currentMonthKey())
    val currentMonthKey: StateFlow<String> = _currentMonthKey

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .map { list ->
            // Keep "Other" pinned to last so every picker always ends with it,
            // regardless of when the row was inserted or what sortOrder it has.
            val other = list.filter { it.nameKey == "cat_other" }
            val rest  = list.filter { it.nameKey != "cat_other" }
            rest + other
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val monthExpenses: StateFlow<List<ExpenseEntity>> = _currentMonthKey
        .flatMapLatest { key -> repository.expensesForMonth(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Refresh the home-screen widget after any data mutation. Fire-and-forget. */
    private fun refreshWidget() {
        viewModelScope.launch { ExpenseWidget.refresh(getApplication()) }
    }

    /** False until user explicitly picks a currency on the setup screen. */
    val isCurrencySetupDone: Boolean get() = settings.currencySetupDone

    fun markCurrencySetupDone() { settings.currencySetupDone = true }

    /** False until the user has finished (or bypassed) the first-launch onboarding wizard. */
    val isOnboardingDone: Boolean get() = settings.onboardingDone

    fun markOnboardingDone() { settings.onboardingDone = true }

    /** False until the user has completed or skipped the guided coachmark tour. */
    val isCoachmarksSeen: Boolean get() = settings.hasSeenCoachmarks

    fun markCoachmarksSeen() { settings.hasSeenCoachmarks = true }

    // ── Coachmark tour state (shared between DashboardScreen and AppNav) ──────
    /** Current tour step. Int.MAX_VALUE = tour completed / dismissed. */
    var coachmarkStep by mutableStateOf(if (settings.hasSeenCoachmarks) Int.MAX_VALUE else 0)

    /** Bounds of the FAB (+) button — spotlight for step 1. */
    var fabBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the BudgetRingCard — used to derive a smaller ring spotlight for step 2. */
    var budgetCardBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the IncomeBudgetTiles row — spotlight for step 3. */
    var incomeTilesBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the Debts bottom-nav tab — spotlight for step 4. */
    var debtsNavBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the Ledger (Khata) bottom-nav tab — spotlight for step 5. */
    var ledgerNavBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the receipt-scan FAB — spotlight for step 6. */
    var receiptScanBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the mic FAB — spotlight for step 7. */
    var micButtonBounds by mutableStateOf<Rect?>(null)
    /** Bounds of the Splits bottom-nav tab — spotlight for step 8. */
    var splitsNavBounds by mutableStateOf<Rect?>(null)

    // ── Screenshot mode ───────────────────────────────────────────────────────
    /** When true the banner ad is hidden so Play Store screenshots look clean.
     *  Toggle via long-press on the version label in Settings. */
    var screenshotMode by mutableStateOf(settings.screenshotMode)

    fun toggleScreenshotMode() {
        screenshotMode = !screenshotMode
        settings.screenshotMode = screenshotMode
    }

    // ── Onboarding mid-wizard locale resume ──────────────────────────────────
    /** The wizard step to resume from after a mid-flow Activity recreation (locale change). 0 = start from beginning. */
    val onboardingResumeStep: Int get() = settings.onboardingResumeStep

    /** Name entered on Step 0 — survives the Activity recreation triggered by locale change. */
    val onboardingPendingName: String get() = settings.onboardingPendingName

    /**
     * Called when the user taps Continue on Step 1 (language picker).
     * Saves the name + language to prefs and sets the resume step to 2 so that after the
     * Activity recreates due to the locale change the wizard reopens at Step 2 (currency).
     */
    fun saveOnboardingProgress(name: String, languageCode: String) {
        settings.onboardingPendingName = name
        settings.languagePref = languageCode
        settings.onboardingResumeStep = 2
    }

    /** Clears the mid-wizard resume state once onboarding completes or is skipped. */
    fun resetOnboardingResume() {
        settings.onboardingResumeStep = 0
        settings.onboardingPendingName = ""
    }

    private val _currencySymbol = MutableStateFlow(settings.currencySymbol)
    val currencySymbol: StateFlow<String> = _currencySymbol

    private val _languagePref = MutableStateFlow(settings.languagePref)
    val languagePref: StateFlow<String> = _languagePref

    private val _displayName = MutableStateFlow(settings.displayName)
    val displayName: StateFlow<String> = _displayName

    private val _smsDetectionEnabled = MutableStateFlow(settings.smsDetectionEnabled)
    val smsDetectionEnabled: StateFlow<Boolean> = _smsDetectionEnabled

    private val _reminderEnabled = MutableStateFlow(settings.reminderEnabled)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled

    private val _reminderHour = MutableStateFlow(settings.reminderHour)
    val reminderHour: StateFlow<Int> = _reminderHour

    private val _reminder2Enabled = MutableStateFlow(settings.reminder2Enabled)
    val reminder2Enabled: StateFlow<Boolean> = _reminder2Enabled

    private val _reminderHour2 = MutableStateFlow(settings.reminderHour2)
    val reminderHour2: StateFlow<Int> = _reminderHour2

    /** Whether we've ever actually shown the POST_NOTIFICATIONS system prompt — see
     * [SettingsRepository.notifPermissionRequested]. Read directly (not a StateFlow) since it's
     * only checked once per Activity lifecycle moment, not observed continuously by UI. */
    val notifPermissionRequested: Boolean get() = settings.notifPermissionRequested
    fun markNotifPermissionRequested() { settings.notifPermissionRequested = true }

    private val _monthlySalary = MutableStateFlow(settings.monthlySalary)
    val monthlySalary: StateFlow<Double> = _monthlySalary

    // ── Logging streak ────────────────────────────────────────────────────────
    private val _logStreak = MutableStateFlow(settings.logStreak)
    val logStreak: StateFlow<Int> = _logStreak

    private val _logStreakBest = MutableStateFlow(settings.logStreakBest)
    val logStreakBest: StateFlow<Int> = _logStreakBest

    // ── Payday countdown ──────────────────────────────────────────────────────
    private val _paydayDayOfMonth = MutableStateFlow(settings.paydayDayOfMonth)
    val paydayDayOfMonth: StateFlow<Int> = _paydayDayOfMonth

    // ── Family / Couple Mode ──────────────────────────────────────────────────
    private val _familyModeEnabled = MutableStateFlow(settings.familyModeEnabled)
    val familyModeEnabled: StateFlow<Boolean> = _familyModeEnabled

    val familyMembers: StateFlow<List<FamilyMemberEntity>> = familyRepository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFamilyModeEnabled(enabled: Boolean) {
        settings.familyModeEnabled = enabled
        _familyModeEnabled.value = enabled
        if (enabled) {
            viewModelScope.launch {
                familyRepository.ensureOwnerProfile(settings.displayName)
            }
        }
    }

    fun disableFamilyMode() {
        viewModelScope.launch {
            familyRepository.reset()
            settings.familyModeEnabled = false
            _familyModeEnabled.value = false
        }
    }

    fun addFamilyMember(name: String, colorHex: String, emoji: String = "") {
        viewModelScope.launch { familyRepository.addMember(name, colorHex, emoji) }
    }

    fun updateFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch { familyRepository.updateMember(member) }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch { familyRepository.deleteMember(member) }
    }

    fun generateFamilyInviteCode(onResult: (String) -> Unit) {
        viewModelScope.launch { onResult(familyRepository.generateInviteCode()) }
    }

    fun importFamilyCode(code: String, onSuccess: (List<String>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val imported = familyRepository.importFromCode(code)
                onSuccess(imported)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Invalid code")
            }
        }
    }

    val pendingSmsExpenses: StateFlow<List<PendingSmsExpense>> = repository.pendingSmsExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Active savings goals ──────────────────────────────────────────────────
    val activeGoals: StateFlow<List<com.expensetracker.app.data.GoalEntity>> = repository.activeGoals
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

    // ── Budget alert events ──────────────────────────────────────────────────
    // Emitted after saveExpense when the new total crosses 80 % or 100 % of the
    // overall budget.  UI collects this as a one-shot Snackbar.
    // Guard: track the highest level already shown per month so each threshold
    // fires at most once — without this, every new expense above 80 % would
    // re-show the WARNING snackbar.
    enum class BudgetAlertLevel { WARNING, EXCEEDED }
    private val _budgetAlertEvent = MutableSharedFlow<BudgetAlertLevel>(extraBufferCapacity = 1)
    val budgetAlertEvent: SharedFlow<BudgetAlertLevel> = _budgetAlertEvent.asSharedFlow()
    private var lastAlertMonthKey = ""
    private var lastAlertLevel: BudgetAlertLevel? = null

    // One-shot signal to launch Google's in-app review dialog — fired the moment the 5th
    // expense is logged. UI collects this and calls InAppReviewManager.requestReview(activity),
    // since the Play Core API needs an Activity, which the ViewModel doesn't have.
    private val _requestReviewEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val requestReviewEvent: SharedFlow<Unit> = _requestReviewEvent.asSharedFlow()
    private companion object { const val REVIEW_PROMPT_EXPENSE_COUNT = 5 }

    fun navigateMonth(delta: Long) {
        val next = DateUtils.shiftMonthKey(_currentMonthKey.value, delta)
        // Never navigate into future months — clamp at current calendar month.
        if (next <= DateUtils.currentMonthKey()) {
            _currentMonthKey.value = next
        }
    }

    fun saveExpense(
        id: Long?,
        categoryId: Long,
        description: String,
        amount: Double,
        date: String,
        isRecurring: Boolean = false,
        memberId: Long? = null,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addOrUpdateExpense(id, categoryId, description, amount, date, isRecurring, memberId = memberId)
            // Update logging streak only for new expenses, not edits.
            if (id == null) updateLogStreak(date)
            val monthKey = DateUtils.monthKeyFromDate(date)
            _currentMonthKey.value = monthKey

            // Ask for a Play Store rating exactly once, right after the 5th expense ever
            // logged — enough usage to have an opinion, early enough to catch first-week
            // enthusiasm. New expenses only (not edits), and only if we haven't asked before.
            if (id == null && !settings.hasRequestedReview) {
                val totalCount = repository.countExpenses()
                if (totalCount >= REVIEW_PROMPT_EXPENSE_COUNT) {
                    settings.hasRequestedReview = true
                    _requestReviewEvent.tryEmit(Unit)
                }
            }

            // Check overall budget threshold — only for new expenses, not edits.
            val overallBudget = if (id == null) budgets.first().firstOrNull { it.categoryId == null }?.amount else null
            if (overallBudget != null && overallBudget > 0.0) {
                val totalSpent = repository.expensesForMonth(monthKey).first().sumOf { it.amount }
                val fraction = totalSpent / overallBudget
                val newLevel = when {
                    fraction >= 1.0 -> BudgetAlertLevel.EXCEEDED
                    fraction >= 0.8 -> BudgetAlertLevel.WARNING
                    else            -> null
                }
                // Only emit if this month's alert hasn't already fired at this level or higher.
                // ordinal: WARNING=0, EXCEEDED=1 — so EXCEEDED > WARNING.
                if (newLevel != null) {
                    val alreadyShown = monthKey == lastAlertMonthKey &&
                        lastAlertLevel != null &&
                        newLevel.ordinal <= lastAlertLevel!!.ordinal
                    if (!alreadyShown) {
                        lastAlertMonthKey = monthKey
                        lastAlertLevel    = newLevel
                        _budgetAlertEvent.tryEmit(newLevel)
                    }
                }
            }
            refreshWidget()
            onDone()
        }
    }

    fun deleteExpense(expense: ExpenseEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            refreshWidget()
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
        viewModelScope.launch {
            repository.setBudget(categoryId, amount)
            refreshWidget()
        }
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

    fun setReminderEnabled(enabled: Boolean) {
        settings.reminderEnabled = enabled
        _reminderEnabled.value = enabled
        val ctx = getApplication<Application>().applicationContext
        if (enabled) {
            ReminderScheduler.schedule(ctx, settings.reminderHour, slot = 1, forceReschedule = true)
            // The master toggle gates the second reminder too — re-arm it if it was already on.
            if (settings.reminder2Enabled) {
                ReminderScheduler.schedule(ctx, settings.reminderHour2, slot = 2, forceReschedule = true)
            }
        } else {
            ReminderScheduler.cancel(ctx, slot = 1)
            ReminderScheduler.cancel(ctx, slot = 2)
        }
    }

    fun setReminderHour(hour: Int) {
        settings.reminderHour = hour
        _reminderHour.value = hour
        if (settings.reminderEnabled) {
            ReminderScheduler.schedule(
                getApplication<Application>().applicationContext,
                hour,
                slot = 1,
                forceReschedule = true
            )
        }
    }

    /** Toggles the optional second daily reminder. Only takes effect while [reminderEnabled]
     * (the master toggle) is also on — enabling this alone with the master off just persists
     * the preference for whenever the master toggle is turned on next. */
    fun setReminder2Enabled(enabled: Boolean) {
        settings.reminder2Enabled = enabled
        _reminder2Enabled.value = enabled
        val ctx = getApplication<Application>().applicationContext
        if (enabled && settings.reminderEnabled) {
            ReminderScheduler.schedule(ctx, settings.reminderHour2, slot = 2, forceReschedule = true)
        } else {
            ReminderScheduler.cancel(ctx, slot = 2)
        }
    }

    fun setReminderHour2(hour: Int) {
        settings.reminderHour2 = hour
        _reminderHour2.value = hour
        if (settings.reminderEnabled && settings.reminder2Enabled) {
            ReminderScheduler.schedule(
                getApplication<Application>().applicationContext,
                hour,
                slot = 2,
                forceReschedule = true
            )
        }
    }

    fun setMonthlySalary(amount: Double) {
        settings.monthlySalary = amount
        _monthlySalary.value = amount
    }

    val biometricEnabled: Boolean get() = settings.biometricEnabled

    fun setBiometricEnabled(enabled: Boolean) {
        settings.biometricEnabled = enabled
    }

    // ── Backup / Restore ─────────────────────────────────────────────────────
    fun exportBackup(onUri: (Uri) -> Unit, onError: (String) -> Unit) {
        val ctx = getApplication<android.app.Application>().applicationContext
        val db  = (ctx as com.expensetracker.app.ExpenseApp).database
        viewModelScope.launch {
            try {
                val uri = BackupManager.export(ctx, db)
                onUri(uri)
            } catch (e: Exception) {
                onError(e.message ?: "Export failed")
            }
        }
    }

    fun importBackup(
        uri: Uri,
        onSuccess: (BackupManager.ImportResult) -> Unit,
        onError: (String) -> Unit
    ) {
        val ctx = getApplication<android.app.Application>().applicationContext
        val db  = (ctx as com.expensetracker.app.ExpenseApp).database
        viewModelScope.launch {
            try {
                val result = BackupManager.import(ctx, db, uri)
                onSuccess(result)
            } catch (e: Exception) {
                onError(e.message ?: "Import failed")
            }
        }
    }

    // ── Google Drive Backup ───────────────────────────────────────────────────

    /** True while a Drive upload or download is in progress. */
    var driveBackupLoading by mutableStateOf(false)
        private set

    /**
     * Serialises the local database to JSON and uploads it to the signed-in
     * account's private Drive appDataFolder. Network I/O runs on [Dispatchers.IO];
     * callbacks are delivered back on the Main thread.
     */
    fun uploadToDrive(
        account: GoogleSignInAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val ctx = getApplication<Application>().applicationContext
        val db  = (ctx as com.expensetracker.app.ExpenseApp).database
        viewModelScope.launch {
            driveBackupLoading = true
            try {
                val json  = BackupManager.exportJson(ctx, db)
                withContext(Dispatchers.IO) {
                    val drive = DriveBackupManager.buildDriveService(ctx, account)
                    DriveBackupManager.upload(drive, json)
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Upload failed")
            } finally {
                driveBackupLoading = false
            }
        }
    }

    /**
     * Downloads the backup JSON from Drive and imports it into the local database.
     * Throws (via [onError]) with the special message "no_backup" if no backup
     * file exists yet in Drive.
     */
    fun downloadFromDrive(
        account: GoogleSignInAccount,
        onSuccess: (BackupManager.ImportResult) -> Unit,
        onError: (String) -> Unit
    ) {
        val ctx = getApplication<Application>().applicationContext
        val db  = (ctx as com.expensetracker.app.ExpenseApp).database
        viewModelScope.launch {
            driveBackupLoading = true
            try {
                val json = withContext(Dispatchers.IO) {
                    val drive = DriveBackupManager.buildDriveService(ctx, account)
                    DriveBackupManager.download(drive)
                } ?: throw IllegalStateException("no_backup")

                val result = BackupManager.importFromJson(db, json)
                onSuccess(result)
            } catch (e: Exception) {
                onError(e.message ?: "Restore failed")
            } finally {
                driveBackupLoading = false
            }
        }
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
        loanType: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addOrUpdateDebt(id, name, direction, principal, interestRatePercent, minimumPayment, startDate, notes, loanType)
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

    // ── Income ────────────────────────────────────────────────────────────────

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val monthIncomeEntries: StateFlow<List<IncomeEntity>> = _currentMonthKey
        .flatMapLatest { key -> repository.incomeForMonth(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val monthIncomeTotal: StateFlow<Double> = _currentMonthKey
        .flatMapLatest { key -> repository.monthlyIncomeTotal(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addIncome(
        amount: Double,
        source: String,
        note: String,
        date: String,
        isRecurring: Boolean = false,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addIncome(amount, source, note, date, isRecurring)
            refreshWidget()
            onDone()
        }
    }

    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
            refreshWidget()
        }
    }

    // ── Streak management ─────────────────────────────────────────────────────

    /**
     * Called when a *new* expense is saved.
     * - Same day as last log → no change (idempotent).
     * - Previous day → increment streak.
     * - Older → reset to 1 (gap in logging).
     */
    private fun updateLogStreak(expenseDateIso: String) {
        val today = DateUtils.todayIso()
        val loggedDate = expenseDateIso  // may be a past date if user back-fills
        val lastDate = settings.logStreakLastDate

        // Only count today's date for the streak — back-filled past expenses don't extend it.
        if (loggedDate != today) return

        val newStreak = when {
            lastDate.isEmpty() -> 1
            lastDate == today -> settings.logStreak  // already counted today
            lastDate == DateUtils.yesterdayIso() -> settings.logStreak + 1
            else -> 1  // gap — reset
        }
        settings.logStreak = newStreak
        settings.logStreakLastDate = today
        if (newStreak > settings.logStreakBest) settings.logStreakBest = newStreak

        _logStreak.value = newStreak
        _logStreakBest.value = settings.logStreakBest
    }

    // ── Payday settings ───────────────────────────────────────────────────────

    fun setPaydayDayOfMonth(day: Int) {
        settings.paydayDayOfMonth = day
        _paydayDayOfMonth.value = day
    }

    // ── Savings goals ─────────────────────────────────────────────────────────

    fun addGoal(name: String, emoji: String, targetAmount: Double, targetDate: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addGoal(name, emoji, targetAmount, targetDate)
            onDone()
        }
    }

    fun updateGoal(goal: com.expensetracker.app.data.GoalEntity, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateGoal(goal)
            onDone()
        }
    }

    fun contributeToGoal(goalId: Long, additionalAmount: Double, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.contributeToGoal(goalId, additionalAmount)
            onDone()
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch { repository.deleteGoal(goalId) }
    }

    fun markGoalCompleted(goalId: Long) {
        viewModelScope.launch { repository.markGoalCompleted(goalId) }
    }
}
