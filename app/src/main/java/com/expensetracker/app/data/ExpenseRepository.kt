package com.expensetracker.app.data

import com.expensetracker.app.util.DateUtils
import kotlinx.coroutines.flow.Flow

sealed class DeleteCategoryResult {
    object MustKeepOne : DeleteCategoryResult()
    data class Deleted(val wasInUse: Boolean) : DeleteCategoryResult()
}

/**
 * Single source of truth for all expense/category data. Backed entirely by the local
 * Room database — no network calls anywhere in this class.
 */
class ExpenseRepository(private val db: AppDatabase) {

    companion object {
        /** Built-in category every auto-generated debt/EMI repayment expense files under —
         * kept as constants so [defaultCategorySeed], [ensureDebtPaymentCategory], and
         * `MIGRATION_4_5`'s backfill all agree on the exact same key/color. */
        private const val DEBT_PAYMENT_CATEGORY_KEY = "cat_debt_payments"
        private const val DEBT_PAYMENT_CATEGORY_COLOR = "#F97316"
    }

    val categories: Flow<List<CategoryEntity>> = db.categoryDao().observeAll()
    val allExpenses: Flow<List<ExpenseEntity>> = db.expenseDao().observeAll()
    val pendingSmsExpenses: Flow<List<PendingSmsExpense>> = db.pendingSmsExpenseDao().observeAll()
    val budgets: Flow<List<BudgetEntity>> = db.budgetDao().observeAll()
    val debts: Flow<List<DebtEntity>> = db.debtDao().observeAll()
    val debtPayments: Flow<List<DebtPaymentEntity>> = db.debtPaymentDao().observeAll()

    fun expensesForMonth(monthKey: String): Flow<List<ExpenseEntity>> =
        db.expenseDao().observeByMonth(monthKey)

    suspend fun seedDefaultCategoriesIfNeeded() {
        if (db.categoryDao().count() == 0) {
            defaultCategorySeed().forEach { db.categoryDao().insert(it) }
        }
    }

    private fun defaultCategorySeed(): List<CategoryEntity> = listOf(
        CategoryEntity(nameKey = "cat_housing", colorHex = "#6366F1", sortOrder = 0),
        CategoryEntity(nameKey = "cat_food", colorHex = "#F59E0B", sortOrder = 1),
        CategoryEntity(nameKey = "cat_transport", colorHex = "#10B981", sortOrder = 2),
        CategoryEntity(nameKey = "cat_utilities", colorHex = "#3B82F6", sortOrder = 3),
        CategoryEntity(nameKey = "cat_entertainment", colorHex = "#EC4899", sortOrder = 4),
        CategoryEntity(nameKey = "cat_healthcare", colorHex = "#EF4444", sortOrder = 5),
        CategoryEntity(nameKey = "cat_shopping", colorHex = "#8B5CF6", sortOrder = 6),
        CategoryEntity(nameKey = "cat_subscriptions", colorHex = "#14B8A6", sortOrder = 7),
        CategoryEntity(nameKey = "cat_savings", colorHex = "#84CC16", sortOrder = 8),
        CategoryEntity(nameKey = "cat_other", colorHex = "#94A3B8", sortOrder = 9),
        CategoryEntity(nameKey = DEBT_PAYMENT_CATEGORY_KEY, colorHex = DEBT_PAYMENT_CATEGORY_COLOR, sortOrder = 10),
        CategoryEntity(nameKey = "cat_mobile_recharge", colorHex = "#06B6D4", sortOrder = 11),
        CategoryEntity(nameKey = "cat_electricity",     colorHex = "#EAB308", sortOrder = 12),
        CategoryEntity(nameKey = "cat_fuel",            colorHex = "#64748B", sortOrder = 13),
        CategoryEntity(nameKey = "cat_farming",         colorHex = "#22C55E", sortOrder = 14),
        CategoryEntity(nameKey = "cat_khata",           colorHex = "#7C3AED", sortOrder = 15),
        CategoryEntity(nameKey = "cat_education",       colorHex = "#0EA5E9", sortOrder = 16)
    )

    /**
     * Inserts any built-in categories that are missing from the DB — safe to call on every
     * app launch. New categories added in a later release appear automatically for existing
     * users without wiping their data.
     */
    suspend fun ensureNewBuiltinCategories() {
        val existing = db.categoryDao().getAllOnce().mapNotNull { it.nameKey }.toSet()
        val toAdd = listOf(
            CategoryEntity(nameKey = "cat_mobile_recharge", colorHex = "#06B6D4", sortOrder = 11),
            CategoryEntity(nameKey = "cat_electricity",     colorHex = "#EAB308", sortOrder = 12),
            CategoryEntity(nameKey = "cat_fuel",            colorHex = "#64748B", sortOrder = 13),
            CategoryEntity(nameKey = "cat_farming",         colorHex = "#22C55E", sortOrder = 14),
            CategoryEntity(nameKey = "cat_khata",           colorHex = "#7C3AED", sortOrder = 15),
            CategoryEntity(nameKey = "cat_education",       colorHex = "#0EA5E9", sortOrder = 16)
        )
        toAdd.forEach { cat ->
            if (cat.nameKey !in existing) db.categoryDao().insert(cat)
        }
    }

    suspend fun addOrUpdateExpense(
        id: Long?,
        categoryId: Long,
        description: String,
        amount: Double,
        date: String,
        isRecurring: Boolean = false,
        recurringPeriod: String? = null
    ) {
        val monthKey = date.substring(0, 7)
        val period = if (isRecurring) (recurringPeriod ?: "MONTHLY") else null
        if (id == null) {
            db.expenseDao().insert(
                ExpenseEntity(
                    categoryId = categoryId, description = description,
                    amount = amount, date = date, monthKey = monthKey,
                    isRecurring = isRecurring, recurringPeriod = period
                )
            )
        } else {
            db.expenseDao().update(
                ExpenseEntity(
                    id = id, categoryId = categoryId, description = description,
                    amount = amount, date = date, monthKey = monthKey,
                    isRecurring = isRecurring, recurringPeriod = period
                )
            )
        }
    }

    /**
     * Auto-creates expense entries for the current month for every recurring-expense template
     * that doesn't already have one. Called once per app startup (in a background coroutine) —
     * idempotent, so running it multiple times is safe.
     *
     * Backfills any missed months: if the app hasn't been opened since e.g. March and today is
     * June, it inserts April, May, and June all in one pass, so the user never silently loses
     * recurring entries for months they were away.
     */
    suspend fun createRecurringExpensesForCurrentMonth() {
        val currentMonth = DateUtils.currentMonthKey()
        val templates = db.expenseDao().recurringTemplatesOnce()

        for (template in templates) {
            // Start from the month after the template was created
            var month = DateUtils.shiftMonthKey(template.monthKey, 1)
            while (month <= currentMonth) {
                val alreadyExists =
                    db.expenseDao().countRecurringInstance(template.id, month) > 0
                if (!alreadyExists) {
                    db.expenseDao().insert(
                        ExpenseEntity(
                            categoryId        = template.categoryId,
                            description       = template.description,
                            amount            = template.amount,
                            date              = "$month-01",
                            monthKey          = month,
                            isRecurring       = false,
                            recurringPeriod   = null,
                            recurringSourceId = template.id
                        )
                    )
                }
                month = DateUtils.shiftMonthKey(month, 1)
            }
        }
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = db.expenseDao().delete(expense)

    /** Queues a parser-detected SMS transaction for the user to review before it becomes a
     * real expense. */
    suspend fun addPendingSmsExpense(item: PendingSmsExpense): Long = db.pendingSmsExpenseDao().insert(item)

    /** Removes a pending SMS item — used both when the user dismisses it outright and when
     * it's been accepted (turned into a real expense) and no longer needs to sit in the queue. */
    suspend fun dismissPendingSmsExpense(item: PendingSmsExpense) = db.pendingSmsExpenseDao().delete(item)

    /** Multiplies every stored expense amount by [rate] — used when the user switches
     * currency and supplies a manual exchange rate. Purely local arithmetic, no network call. */
    suspend fun convertAllAmounts(rate: Double) = db.expenseDao().scaleAllAmounts(rate)

    suspend fun addCategory(name: String, colorHex: String): Long {
        val sortOrder = db.categoryDao().count()
        return db.categoryDao().insert(CategoryEntity(customName = name, colorHex = colorHex, sortOrder = sortOrder))
    }

    suspend fun renameCategory(category: CategoryEntity, newName: String) {
        // Once a user edits the name, it's no longer tied to a localized string key.
        db.categoryDao().update(category.copy(customName = newName, nameKey = null))
    }

    suspend fun recolorCategory(category: CategoryEntity, colorHex: String) {
        db.categoryDao().update(category.copy(colorHex = colorHex))
    }

    suspend fun isCategoryInUse(categoryId: Long): Boolean = db.expenseDao().categoryInUse(categoryId)

    suspend fun deleteCategory(category: CategoryEntity): DeleteCategoryResult {
        val all = db.categoryDao().getAllOnce()
        if (all.size <= 1) return DeleteCategoryResult.MustKeepOne

        val inUse = db.expenseDao().categoryInUse(category.id)
        val fallback = all.first { it.id != category.id }
        if (inUse) db.expenseDao().reassignCategory(category.id, fallback.id)
        db.categoryDao().delete(category)
        // No FK/cascade on this table, so a deleted category's budget target (if any) would
        // otherwise be silently orphaned — clean it up explicitly.
        db.budgetDao().deleteForCategory(category.id)
        return DeleteCategoryResult.Deleted(wasInUse = inUse)
    }

    /**
     * Creates, updates, or clears the standing budget target for [categoryId] (null = overall).
     * Upserting is done here at the app layer — rather than a DB-level unique index — because
     * SQLite treats every NULL in a unique index as distinct, so it wouldn't actually stop
     * duplicate "overall" rows from piling up. An [amount] of 0 or less clears the budget
     * instead of saving a zero target.
     */
    suspend fun setBudget(categoryId: Long?, amount: Double) {
        val existing = db.budgetDao().getAllOnce().find { it.categoryId == categoryId }
        when {
            amount <= 0.0 && existing != null -> db.budgetDao().delete(existing)
            amount <= 0.0 -> Unit
            existing != null -> db.budgetDao().update(existing.copy(amount = amount))
            else -> db.budgetDao().insert(BudgetEntity(categoryId = categoryId, amount = amount))
        }
    }

    suspend fun resetAllData() {
        db.expenseDao().deleteAll()
        db.categoryDao().deleteAll()
        db.pendingSmsExpenseDao().deleteAll()
        db.budgetDao().deleteAll()
        db.debtDao().deleteAll()
        db.debtPaymentDao().deleteAll()
        db.incomeDao().deleteAll()
        seedDefaultCategoriesIfNeeded()
    }

    suspend fun addOrUpdateDebt(
        id: Long?,
        name: String,
        direction: String,
        principal: Double,
        interestRatePercent: Double,
        minimumPayment: Double,
        startDate: String,
        notes: String?,
        loanType: String?
    ) {
        if (id == null) {
            val entity = DebtEntity(
                name = name,
                direction = direction,
                principal = principal,
                interestRatePercent = interestRatePercent,
                minimumPayment = minimumPayment,
                startDate = startDate,
                notes = notes,
                loanType = loanType
            )
            val newId = db.debtDao().insert(entity)
            // When the user adds a new "I owe" loan/EMI, automatically record the first
            // payment as an expense so it immediately shows up in Monthly Expenses and the
            // budget ring. (Each subsequent EMI still needs to be recorded manually via the
            // Debts screen — this seeds the current month without the user having to tap twice.)
            if (direction == DebtEntity.DIRECTION_OWE && minimumPayment > 0) {
                recordDebtPayment(entity.copy(id = newId), minimumPayment, startDate, null)
            }
        } else {
            val existing = db.debtDao().getAllOnce().find { it.id == id } ?: return
            db.debtDao().update(
                existing.copy(
                    name = name,
                    direction = direction,
                    principal = principal,
                    interestRatePercent = interestRatePercent,
                    minimumPayment = minimumPayment,
                    startDate = startDate,
                    notes = notes,
                    loanType = loanType
                )
            )
        }
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        // Clean up any expenses this debt's payments auto-generated *before* deleting the
        // payment rows themselves, or the linkedExpenseId references would be lost.
        db.debtPaymentDao().getForDebt(debt.id).forEach { payment ->
            payment.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
        }
        db.debtDao().delete(debt)
        // No FK/cascade on this table — clean up this debt's payment history explicitly,
        // same orphan-cleanup pattern as deleteCategory() above.
        db.debtPaymentDao().deleteForDebt(debt.id)
    }

    /** Manual close/reopen — for forgiving a debt, or settling it for less than full principal. */
    suspend fun setDebtClosed(debt: DebtEntity, isClosed: Boolean) {
        db.debtDao().update(debt.copy(isClosed = isClosed))
    }

    /**
     * Finds the built-in "Debt & Loan Payments" category, recreating it if the user has since
     * deleted it (categories can always be deleted, see [deleteCategory]) — so a repayment can
     * never be left with nowhere to file its auto-generated expense.
     */
    private suspend fun ensureDebtPaymentCategory(): Long {
        val existing = db.categoryDao().getAllOnce().find { it.nameKey == DEBT_PAYMENT_CATEGORY_KEY }
        if (existing != null) return existing.id
        val sortOrder = db.categoryDao().count()
        return db.categoryDao().insert(
            CategoryEntity(nameKey = DEBT_PAYMENT_CATEGORY_KEY, colorHex = DEBT_PAYMENT_CATEGORY_COLOR, sortOrder = sortOrder)
        )
    }

    /**
     * Records a payment against [debt] and auto-closes it once cumulative payments reach the
     * principal, so most users never have to remember to manually mark a debt settled. Manual
     * close/reopen (e.g. forgiving a debt, or settling for less than the full principal) stays
     * available separately via [setDebtClosed].
     *
     * EMI/debt repayments are real money leaving the user, so they impact the monthly budget
     * exactly like any other expense: when [debt] is [DebtEntity.DIRECTION_OWE] (money owed
     * *by* the user), this also creates a linked [ExpenseEntity] under the dedicated debt
     * category, which is what makes the payment show up in Total Spent, Budget Remaining, and
     * the Breakdown-by-Category chart. Collections on [DebtEntity.DIRECTION_OWED] debts are
     * money coming *in*, not spend, so no expense is created for those.
     */
    suspend fun recordDebtPayment(debt: DebtEntity, amount: Double, date: String, note: String?) {
        var linkedExpenseId: Long? = null
        if (debt.direction == DebtEntity.DIRECTION_OWE) {
            val categoryId = ensureDebtPaymentCategory()
            val monthKey = date.substring(0, 7)
            linkedExpenseId = db.expenseDao().insert(
                ExpenseEntity(categoryId = categoryId, description = debt.name, amount = amount, date = date, monthKey = monthKey)
            )
        }
        db.debtPaymentDao().insert(
            DebtPaymentEntity(debtId = debt.id, amount = amount, date = date, note = note, linkedExpenseId = linkedExpenseId)
        )
        val totalPaid = db.debtPaymentDao().totalPaidFor(debt.id)
        if (!debt.isClosed && totalPaid >= debt.principal) {
            db.debtDao().update(debt.copy(isClosed = true))
        }
    }

    suspend fun deleteDebtPayment(payment: DebtPaymentEntity) {
        db.debtPaymentDao().delete(payment)
        // Keep the linked expense from outliving the payment it came from — otherwise deleting
        // a repayment here would leave a phantom expense still counted in the monthly budget.
        payment.linkedExpenseId?.let { db.expenseDao().deleteById(it) }
    }

    // ── Income ────────────────────────────────────────────────────────────────

    fun incomeForMonth(monthKey: String) = db.incomeDao().observeForMonth(monthKey)

    fun monthlyIncomeTotal(monthKey: String) = db.incomeDao().totalForMonth(monthKey)

    suspend fun addIncome(
        amount: Double,
        source: String,
        note: String,
        date: String,
        isRecurring: Boolean = false
    ) {
        val monthKey = date.substring(0, 7)
        db.incomeDao().insert(
            IncomeEntity(
                amount      = amount,
                source      = source,
                note        = note,
                date        = date,
                monthKey    = monthKey,
                isRecurring = isRecurring
            )
        )
    }

    suspend fun deleteIncome(income: IncomeEntity) = db.incomeDao().delete(income)

    /**
     * Auto-generates non-recurring copies of every recurring income template for the current
     * month — mirroring [createRecurringExpensesForCurrentMonth] for income.
     *
     * Guards against duplicates: if an entry with the same source+amount already exists for
     * the current month (non-recurring), it is skipped. Called once per app launch from
     * [ExpenseApp] so users always see their monthly salary pre-filled.
     */
    suspend fun createRecurringIncomeForCurrentMonth() {
        val currentMonth = DateUtils.currentMonthKey()
        val templates = db.incomeDao().getRecurringTemplates()

        for (template in templates) {
            // Only generate for months *after* the template was created
            var month = DateUtils.shiftMonthKey(template.monthKey, 1)
            while (month <= currentMonth) {
                val alreadyExists =
                    db.incomeDao().countMonthlyInstance(month, template.source, template.amount) > 0
                if (!alreadyExists) {
                    db.incomeDao().insert(
                        IncomeEntity(
                            amount      = template.amount,
                            source      = template.source,
                            note        = template.note,
                            date        = "$month-01",
                            monthKey    = month,
                            isRecurring = false   // generated copy is a normal entry
                        )
                    )
                }
                month = DateUtils.shiftMonthKey(month, 1)
            }
        }
    }
}
