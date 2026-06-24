package com.expensetracker.app.data

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

    val categories: Flow<List<CategoryEntity>> = db.categoryDao().observeAll()
    val allExpenses: Flow<List<ExpenseEntity>> = db.expenseDao().observeAll()
    val pendingSmsExpenses: Flow<List<PendingSmsExpense>> = db.pendingSmsExpenseDao().observeAll()
    val budgets: Flow<List<BudgetEntity>> = db.budgetDao().observeAll()

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
        CategoryEntity(nameKey = "cat_other", colorHex = "#94A3B8", sortOrder = 9)
    )

    suspend fun addOrUpdateExpense(
        id: Long?,
        categoryId: Long,
        description: String,
        amount: Double,
        date: String
    ) {
        val monthKey = date.substring(0, 7)
        if (id == null) {
            db.expenseDao().insert(
                ExpenseEntity(categoryId = categoryId, description = description, amount = amount, date = date, monthKey = monthKey)
            )
        } else {
            db.expenseDao().update(
                ExpenseEntity(id = id, categoryId = categoryId, description = description, amount = amount, date = date, monthKey = monthKey)
            )
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
        seedDefaultCategoriesIfNeeded()
    }
}
