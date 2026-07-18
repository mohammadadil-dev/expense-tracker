package com.expensetracker.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.BudgetEntity
import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.data.DebtEntity
import com.expensetracker.app.data.DebtPaymentEntity
import com.expensetracker.app.data.ExpenseEntity
import com.expensetracker.app.data.GoalEntity
import com.expensetracker.app.data.IncomeEntity
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.data.KhataPartyEntity
import com.expensetracker.app.data.PaymentAccountEntity
import com.expensetracker.app.data.SplitExpenseEntity
import com.expensetracker.app.data.SplitExpenseItemEntity
import com.expensetracker.app.data.SplitExpenseItemMemberEntity
import com.expensetracker.app.data.SplitExpenseShareEntity
import com.expensetracker.app.data.SplitGroupEntity
import com.expensetracker.app.data.SplitMemberEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * On-device JSON backup and restore.
 *
 * Export: serialises categories, expenses, budgets, Khata parties/entries, debts/payments,
 * income, payment accounts, savings goals, and the full Splits feature (groups, members,
 * expenses, shares, itemized line items) to a single JSON file and hands it out via
 * FileProvider so the user can save to Downloads or share to Drive.
 *
 * Import: reads a previously exported JSON file and re-inserts all records, building ID
 * remaps so foreign-key relationships are correctly re-wired on the new device.
 *
 * Deliberately NOT included in any version:
 *  - [ExpenseEntity.memberId] (Family Mode) — Family Mode is hidden/unreleased (see
 *    SettingsScreen.kt's BEGIN_FAMILY_MODE_UI marker), so there is no FamilyMemberEntity
 *    backup/restore to remap this ID against yet.
 *  - [KhataEntryEntity.photoPath] — an absolute path into this device's private
 *    `filesDir/receipts/`, meaningless on another device/reinstall. The backup file format
 *    here is plain JSON (no attached binary files), so a restored entry simply has no photo,
 *    same as if none had ever been attached. Not silently broken — just not carried over.
 *  - [SplitExpenseEntity.linkedExpenseId] / [KhataEntryEntity.linkedExpenseId] /
 *    [DebtPaymentEntity.linkedExpenseId] — same reasoning as before this feature existed:
 *    these reference an auto-generated personal ExpenseEntity row by its old-device ID,
 *    which is already independently restored (or skipped) on its own.
 *
 * Version history:
 *   v1 — categories, expenses, budgets only
 *   v2 — adds khataParties, khataEntries, debts, debtPayments
 *   v3 — adds income entries
 *   v4 — adds paymentAccounts, goals, and the full Splits feature (splitGroups/splitMembers/
 *        splitExpenses/splitExpenseShares/splitExpenseItems/splitExpenseItemMembers); also
 *        starts carrying ExpenseEntity.accountId + recurringDayOfMonth, KhataPartyEntity.upiId
 *        + creditLimit, and KhataEntryEntity.dueDate, none of which existed when v3 shipped.
 *
 * v1–v3 backups import cleanly into v4 (missing sections default to empty arrays/lists).
 */
object BackupManager {

    private const val KEY_VERSION              = "version"
    private const val KEY_EXPORTED_AT          = "exportedAt"
    private const val KEY_CATEGORIES           = "categories"
    private const val KEY_EXPENSES             = "expenses"
    private const val KEY_BUDGETS              = "budgets"
    private const val KEY_KHATA_PARTIES        = "khataParties"
    private const val KEY_KHATA_ENTRIES        = "khataEntries"
    private const val KEY_DEBTS                = "debts"
    private const val KEY_DEBT_PAYMENTS        = "debtPayments"
    private const val KEY_INCOME               = "income"
    private const val KEY_PAYMENT_ACCOUNTS     = "paymentAccounts"
    private const val KEY_GOALS                = "goals"
    private const val KEY_SPLIT_GROUPS         = "splitGroups"
    private const val KEY_SPLIT_MEMBERS        = "splitMembers"
    private const val KEY_SPLIT_EXPENSES       = "splitExpenses"
    private const val KEY_SPLIT_SHARES         = "splitExpenseShares"
    private const val KEY_SPLIT_ITEMS          = "splitExpenseItems"
    private const val KEY_SPLIT_ITEM_MEMBERS   = "splitExpenseItemMembers"
    private const val BACKUP_VERSION           = 4

    // ── Export ────────────────────────────────────────────────────────────────

    suspend fun export(context: Context, db: AppDatabase): Uri {
        val file = File(context.cacheDir, "expense_tracker_backup.json")
        file.writeText(buildJson(db).toString(2))
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /**
     * Same serialisation as [export] but returns the raw JSON string directly —
     * used by the Drive upload path to avoid an unnecessary FileProvider round-trip.
     */
    suspend fun exportJson(context: Context, db: AppDatabase): String =
        buildJson(db).toString(2)

    private suspend fun buildJson(db: AppDatabase): JSONObject {
        val categories       = db.categoryDao().getAllOnce()
        val expenses         = db.expenseDao().getAllOnce()
        val budgets          = db.budgetDao().getAllOnce()
        val khataParties     = db.khataDao().getAllPartiesOnce()
        val khataEntries     = db.khataDao().getAllEntriesOnce()
        val debts            = db.debtDao().getAllOnce()
        val debtPayments     = db.debtPaymentDao().getAllOnce()
        val income           = db.incomeDao().getAllOnce()
        val paymentAccounts  = db.paymentAccountDao().getAllOnce()
        val goals            = db.goalDao().getAllOnce()
        val splitGroups      = db.splitGroupDao().getAllOnce()
        val splitMembers     = db.splitMemberDao().getAllOnce()
        val splitExpenses    = db.splitExpenseDao().getAllOnce()
        val splitShares      = db.splitExpenseShareDao().getAllOnce()
        val splitItems       = db.splitExpenseItemDao().getAllItemsOnce()
        val splitItemMembers = db.splitExpenseItemDao().getAllItemMembersOnce()

        return JSONObject().apply {
            put(KEY_VERSION,            BACKUP_VERSION)
            put(KEY_EXPORTED_AT,        System.currentTimeMillis())
            put(KEY_CATEGORIES,         JSONArray(categories.map       { it.toJson() }))
            put(KEY_EXPENSES,           JSONArray(expenses.map         { it.toJson() }))
            put(KEY_BUDGETS,            JSONArray(budgets.map          { it.toJson() }))
            put(KEY_KHATA_PARTIES,      JSONArray(khataParties.map     { it.toJson() }))
            put(KEY_KHATA_ENTRIES,      JSONArray(khataEntries.map     { it.toJson() }))
            put(KEY_DEBTS,              JSONArray(debts.map            { it.toJson() }))
            put(KEY_DEBT_PAYMENTS,      JSONArray(debtPayments.map     { it.toJson() }))
            put(KEY_INCOME,             JSONArray(income.map           { it.toJson() }))
            put(KEY_PAYMENT_ACCOUNTS,   JSONArray(paymentAccounts.map  { it.toJson() }))
            put(KEY_GOALS,              JSONArray(goals.map            { it.toJson() }))
            put(KEY_SPLIT_GROUPS,       JSONArray(splitGroups.map      { it.toJson() }))
            put(KEY_SPLIT_MEMBERS,      JSONArray(splitMembers.map     { it.toJson() }))
            put(KEY_SPLIT_EXPENSES,     JSONArray(splitExpenses.map    { it.toJson() }))
            put(KEY_SPLIT_SHARES,       JSONArray(splitShares.map      { it.toJson() }))
            put(KEY_SPLIT_ITEMS,        JSONArray(splitItems.map       { it.toJson() }))
            put(KEY_SPLIT_ITEM_MEMBERS, JSONArray(splitItemMembers.map { it.toJson() }))
        }
    }

    fun shareExport(context: Context, uri: Uri, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }

    // ── Import ────────────────────────────────────────────────────────────────

    /**
     * Returns the count of restored records, or throws on malformed JSON.
     *
     * Accepts v1 through v4 backup files — older files simply have no keys for sections that
     * didn't exist yet, which are treated as empty arrays. No data loss on either side.
     *
     * Duplicate handling:
     * - Categories: deduplicated by nameKey (built-ins) or customName (user categories).
     * - Payment accounts: deduplicated by nameKey (built-ins) or customName, same as categories.
     * - Khata parties: deduplicated by name + direction. Same party imported twice keeps
     *   only one row; entries are still appended so partial restores accumulate correctly.
     * - Debts: deduplicated by name + startDate + direction.
     * - Goals: deduplicated by name — goals are named targets, not repeatable transactions.
     * - Split groups: deduplicated by name + createdDate.
     * - Split members: deduplicated by (restored group, name) — same member name imported
     *   twice into the same group keeps only one row.
     * - Expenses / KhataEntries / DebtPayments / Split expenses / shares / items: NOT
     *   deduplicated — importing the same backup twice will double these. Warn the user
     *   before import.
     * - Budgets: always upserted (last write wins for each categoryId slot).
     */
    suspend fun import(context: Context, db: AppDatabase, uri: Uri): ImportResult {
        val text = context.contentResolver.openInputStream(uri)?.use { it.reader().readText() }
            ?: throw IllegalStateException("Could not read backup file")
        return importJson(db, text)
    }

    /** Overload used by the Drive restore path, which already has the JSON string. */
    suspend fun importFromJson(db: AppDatabase, json: String): ImportResult =
        importJson(db, json)

    private suspend fun importJson(db: AppDatabase, text: String): ImportResult {
        val root    = JSONObject(text)
        val version = root.optInt(KEY_VERSION, 1) // v1 files may lack the key
        require(version in 1..BACKUP_VERSION) { "Unsupported backup version: $version" }

        var cats = 0; var exps = 0; var buds = 0
        var kParties = 0; var kEntries = 0; var dDebts = 0; var dPayments = 0; var incomes = 0
        var accounts = 0; var goals = 0
        var sGroups = 0; var sMembers = 0; var sExpenses = 0; var sShares = 0; var sItems = 0; var sItemMembers = 0

        // ── 1. Categories — build oldId → newId remap ────────────────────────
        val catArr          = root.optJSONArray(KEY_CATEGORIES) ?: JSONArray()
        val idRemapCat      = mutableMapOf<Long, Long>()
        val existingCats    = db.categoryDao().getAllOnce()
        val existByNameKey  = existingCats.filter { it.nameKey   != null }.associateBy { it.nameKey!! }
        val existByCustom   = existingCats.filter { it.customName != null }.associateBy { it.customName!! }
        for (i in 0 until catArr.length()) {
            val obj        = catArr.getJSONObject(i)
            val oldId      = obj.getLong("id")
            val nameKey    = obj.optString("nameKey").takeIf   { it.isNotBlank() }
            val customName = obj.optString("customName").takeIf { it.isNotBlank() }
            val existing   = nameKey?.let { existByNameKey[it] } ?: customName?.let { existByCustom[it] }
            if (existing != null) {
                idRemapCat[oldId] = existing.id
            } else {
                val newId = db.categoryDao().insert(
                    CategoryEntity(
                        nameKey    = nameKey,
                        customName = customName,
                        colorHex   = obj.optString("colorHex", "#43A047"),
                        sortOrder  = obj.optInt("sortOrder", 0)
                    )
                )
                idRemapCat[oldId] = newId
                cats++
            }
        }

        // ── 1b. Payment accounts — build oldId → newId remap (v4+) ───────────
        val accountArr        = root.optJSONArray(KEY_PAYMENT_ACCOUNTS) ?: JSONArray()
        val idRemapAccount    = mutableMapOf<Long, Long>()
        val existingAccounts  = db.paymentAccountDao().getAllOnce()
        val existAcctByKey    = existingAccounts.filter { it.nameKey   != null }.associateBy { it.nameKey!! }
        val existAcctByCustom = existingAccounts.filter { it.customName != null }.associateBy { it.customName!! }
        for (i in 0 until accountArr.length()) {
            val obj        = accountArr.getJSONObject(i)
            val oldId      = obj.getLong("id")
            val nameKey    = obj.optString("nameKey").takeIf   { it.isNotBlank() }
            val customName = obj.optString("customName").takeIf { it.isNotBlank() }
            val existing   = nameKey?.let { existAcctByKey[it] } ?: customName?.let { existAcctByCustom[it] }
            if (existing != null) {
                idRemapAccount[oldId] = existing.id
            } else {
                val newId = db.paymentAccountDao().insert(
                    PaymentAccountEntity(
                        nameKey    = nameKey,
                        customName = customName,
                        type       = obj.optString("type", "OTHER"),
                        colorHex   = obj.optString("colorHex", "#4CAF50"),
                        isDefault  = obj.optBoolean("isDefault", false),
                        sortOrder  = obj.optInt("sortOrder", 0),
                        createdAt  = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
                idRemapAccount[oldId] = newId
                accounts++
            }
        }

        // ── 2. Expenses ──────────────────────────────────────────────────────
        val expArr = root.optJSONArray(KEY_EXPENSES) ?: JSONArray()
        for (i in 0 until expArr.length()) {
            val obj       = expArr.getJSONObject(i)
            val newCat    = idRemapCat[obj.getLong("categoryId")] ?: continue
            val oldAcctId = obj.optLong("accountId", 0L).takeIf { it != 0L }
            val newAcctId = oldAcctId?.let { idRemapAccount[it] }
            db.expenseDao().insert(
                ExpenseEntity(
                    categoryId        = newCat,
                    description       = obj.optString("description", ""),
                    amount            = obj.getDouble("amount"),
                    date              = obj.getString("date"),
                    monthKey          = obj.getString("monthKey"),
                    isRecurring       = obj.optBoolean("isRecurring", false),
                    recurringPeriod   = obj.optString("recurringPeriod").takeIf   { it.isNotBlank() },
                    recurringSourceId = obj.optLong("recurringSourceId", 0L).takeIf { it != 0L },
                    recurringDayOfMonth = obj.optInt("recurringDayOfMonth", 0).takeIf { it != 0 },
                    accountId         = newAcctId
                )
            )
            exps++
        }

        // ── 3. Budgets ───────────────────────────────────────────────────────
        val budArr = root.optJSONArray(KEY_BUDGETS) ?: JSONArray()
        for (i in 0 until budArr.length()) {
            val obj      = budArr.getJSONObject(i)
            val oldCat   = if (obj.isNull("categoryId")) null else obj.getLong("categoryId")
            val newCatId = oldCat?.let { idRemapCat[it] }
            db.budgetDao().upsert(BudgetEntity(categoryId = newCatId, amount = obj.getDouble("amount")))
            buds++
        }

        // ── 4. Khata parties — oldId → newId remap ───────────────────────────
        val khataPartyArr   = root.optJSONArray(KEY_KHATA_PARTIES) ?: JSONArray()
        val idRemapParty    = mutableMapOf<Long, Long>()
        val existingParties = db.khataDao().getAllPartiesOnce()
        // Deduplicate by (name, direction) — if the same party exists don't create a duplicate.
        val existPartyKey   = existingParties.associateBy { "${it.name}|${it.direction}" }
        for (i in 0 until khataPartyArr.length()) {
            val obj       = khataPartyArr.getJSONObject(i)
            val oldId     = obj.getLong("id")
            val name      = obj.getString("name")
            val direction = obj.getString("direction")
            val existing  = existPartyKey["$name|$direction"]
            if (existing != null) {
                idRemapParty[oldId] = existing.id
            } else {
                val newId = db.khataDao().insertParty(
                    KhataPartyEntity(
                        name        = name,
                        phone       = obj.optString("phone", ""),
                        direction   = direction,
                        createdAt   = obj.optLong("createdAt", System.currentTimeMillis()),
                        upiId       = obj.optString("upiId").takeIf { it.isNotBlank() },
                        creditLimit = if (obj.has("creditLimit")) obj.getDouble("creditLimit") else null
                    )
                )
                idRemapParty[oldId] = newId
                kParties++
            }
        }

        // ── 5. Khata entries ─────────────────────────────────────────────────
        val khataEntryArr = root.optJSONArray(KEY_KHATA_ENTRIES) ?: JSONArray()
        for (i in 0 until khataEntryArr.length()) {
            val obj        = khataEntryArr.getJSONObject(i)
            val newPartyId = idRemapParty[obj.getLong("partyId")] ?: continue
            db.khataDao().insertEntry(
                KhataEntryEntity(
                    partyId  = newPartyId,
                    amount   = obj.getDouble("amount"),
                    note     = obj.optString("note", ""),
                    date     = obj.getString("date"),
                    type     = obj.getString("type"),
                    dueDate  = obj.optString("dueDate").takeIf { it.isNotBlank() }
                    // linkedExpenseId intentionally dropped — the linked expense row is already
                    // restored (or skipped) independently; re-linking by old ID would be wrong.
                    // photoPath intentionally dropped — see class doc comment.
                )
            )
            kEntries++
        }

        // ── 6. Debts — oldId → newId remap ──────────────────────────────────
        val debtArr      = root.optJSONArray(KEY_DEBTS) ?: JSONArray()
        val idRemapDebt  = mutableMapOf<Long, Long>()
        val existingDebts = db.debtDao().getAllOnce()
        val existDebtKey  = existingDebts.associateBy { "${it.name}|${it.startDate}|${it.direction}" }
        for (i in 0 until debtArr.length()) {
            val obj       = debtArr.getJSONObject(i)
            val oldId     = obj.getLong("id")
            val name      = obj.getString("name")
            val startDate = obj.getString("startDate")
            val direction = obj.getString("direction")
            val existing  = existDebtKey["$name|$startDate|$direction"]
            if (existing != null) {
                idRemapDebt[oldId] = existing.id
            } else {
                val newId = db.debtDao().insert(
                    DebtEntity(
                        name                = name,
                        direction           = direction,
                        principal           = obj.getDouble("principal"),
                        interestRatePercent = obj.optDouble("interestRatePercent", 0.0),
                        minimumPayment      = obj.optDouble("minimumPayment", 0.0),
                        startDate           = startDate,
                        notes               = obj.optString("notes").takeIf { it.isNotBlank() },
                        isClosed            = obj.optBoolean("isClosed", false),
                        loanType            = obj.optString("loanType").takeIf { it.isNotBlank() }
                    )
                )
                idRemapDebt[oldId] = newId
                dDebts++
            }
        }

        // ── 7. Debt payments ─────────────────────────────────────────────────
        val debtPayArr = root.optJSONArray(KEY_DEBT_PAYMENTS) ?: JSONArray()
        for (i in 0 until debtPayArr.length()) {
            val obj       = debtPayArr.getJSONObject(i)
            val newDebtId = idRemapDebt[obj.getLong("debtId")] ?: continue
            db.debtPaymentDao().insert(
                DebtPaymentEntity(
                    debtId = newDebtId,
                    amount = obj.getDouble("amount"),
                    date   = obj.getString("date"),
                    note   = obj.optString("note").takeIf { it.isNotBlank() }
                    // linkedExpenseId dropped for the same reason as khataEntry above.
                )
            )
            dPayments++
        }

        // ── 8. Income entries ────────────────────────────────────────────────
        val incomeArr = root.optJSONArray(KEY_INCOME) ?: JSONArray()
        for (i in 0 until incomeArr.length()) {
            val obj = incomeArr.getJSONObject(i)
            db.incomeDao().insert(
                IncomeEntity(
                    amount      = obj.getDouble("amount"),
                    source      = obj.optString("source", ""),
                    note        = obj.optString("note", ""),
                    date        = obj.getString("date"),
                    monthKey    = obj.getString("monthKey"),
                    isRecurring = obj.optBoolean("isRecurring", false)
                )
            )
            incomes++
        }

        // ── 9. Goals — deduplicated by name (v4+) ────────────────────────────
        val goalArr        = root.optJSONArray(KEY_GOALS) ?: JSONArray()
        val existingGoals  = db.goalDao().getAllOnce()
        val existGoalByName = existingGoals.associateBy { it.name }
        for (i in 0 until goalArr.length()) {
            val obj = goalArr.getJSONObject(i)
            val name = obj.getString("name")
            if (existGoalByName.containsKey(name)) continue
            db.goalDao().insert(
                GoalEntity(
                    name         = name,
                    emoji        = obj.optString("emoji", "🎯"),
                    targetAmount = obj.getDouble("targetAmount"),
                    savedAmount  = obj.optDouble("savedAmount", 0.0),
                    targetDate   = obj.optString("targetDate", ""),
                    createdAt    = obj.optLong("createdAt", System.currentTimeMillis()),
                    isCompleted  = obj.optBoolean("isCompleted", false)
                )
            )
            goals++
        }

        // ── 10. Split groups — build oldId → newId remap (v4+) ───────────────
        val splitGroupArr      = root.optJSONArray(KEY_SPLIT_GROUPS) ?: JSONArray()
        val idRemapSplitGroup  = mutableMapOf<Long, Long>()
        val existingSplitGroups = db.splitGroupDao().getAllOnce()
        val existGroupKey      = existingSplitGroups.associateBy { "${it.name}|${it.createdDate}" }
        for (i in 0 until splitGroupArr.length()) {
            val obj         = splitGroupArr.getJSONObject(i)
            val oldId       = obj.getLong("id")
            val name        = obj.getString("name")
            val createdDate = obj.getString("createdDate")
            val existing    = existGroupKey["$name|$createdDate"]
            if (existing != null) {
                idRemapSplitGroup[oldId] = existing.id
            } else {
                val newId = db.splitGroupDao().insert(
                    SplitGroupEntity(
                        name        = name,
                        emoji       = obj.optString("emoji", "🤝"),
                        createdDate = createdDate
                    )
                )
                idRemapSplitGroup[oldId] = newId
                sGroups++
            }
        }

        // ── 11. Split members — build oldId → newId remap (v4+) ──────────────
        val splitMemberArr     = root.optJSONArray(KEY_SPLIT_MEMBERS) ?: JSONArray()
        val idRemapSplitMember = mutableMapOf<Long, Long>()
        val existingSplitMembers = db.splitMemberDao().getAllOnce()
        // Dedupe within the *restored* group only — same member name can legitimately repeat
        // across different groups (e.g. "Me" in every group).
        val existMemberKey     = existingSplitMembers.associateBy { "${it.groupId}|${it.name}" }
        for (i in 0 until splitMemberArr.length()) {
            val obj        = splitMemberArr.getJSONObject(i)
            val oldId      = obj.getLong("id")
            val newGroupId = idRemapSplitGroup[obj.getLong("groupId")] ?: continue
            val name       = obj.getString("name")
            val existing   = existMemberKey["$newGroupId|$name"]
            if (existing != null) {
                idRemapSplitMember[oldId] = existing.id
            } else {
                val newId = db.splitMemberDao().insert(
                    SplitMemberEntity(
                        groupId  = newGroupId,
                        name     = name,
                        colorHex = obj.optString("colorHex", "#4CAF50"),
                        emoji    = obj.optString("emoji", ""),
                        isMe     = obj.optBoolean("isMe", false),
                        upiId    = obj.optString("upiId").takeIf { it.isNotBlank() },
                        phone    = obj.optString("phone").takeIf { it.isNotBlank() }
                    )
                )
                idRemapSplitMember[oldId] = newId
                sMembers++
            }
        }

        // ── 12. Split expenses — build oldId → newId remap (v4+, NOT deduplicated) ──
        val splitExpenseArr     = root.optJSONArray(KEY_SPLIT_EXPENSES) ?: JSONArray()
        val idRemapSplitExpense = mutableMapOf<Long, Long>()
        for (i in 0 until splitExpenseArr.length()) {
            val obj           = splitExpenseArr.getJSONObject(i)
            val oldId         = obj.getLong("id")
            val newGroupId    = idRemapSplitGroup[obj.getLong("groupId")] ?: continue
            val newPaidById   = idRemapSplitMember[obj.getLong("paidByMemberId")] ?: continue
            val newId = db.splitExpenseDao().insert(
                SplitExpenseEntity(
                    groupId        = newGroupId,
                    description    = obj.optString("description", ""),
                    amount         = obj.getDouble("amount"),
                    paidByMemberId = newPaidById,
                    date           = obj.getString("date"),
                    note           = obj.optString("note", ""),
                    isSettlement   = obj.optBoolean("isSettlement", false)
                    // linkedExpenseId intentionally dropped — same reason as KhataEntryEntity.
                )
            )
            idRemapSplitExpense[oldId] = newId
            sExpenses++
        }

        // ── 13. Split expense shares (v4+, batch insert) ─────────────────────
        val splitShareArr = root.optJSONArray(KEY_SPLIT_SHARES) ?: JSONArray()
        val restoredShares = mutableListOf<SplitExpenseShareEntity>()
        for (i in 0 until splitShareArr.length()) {
            val obj          = splitShareArr.getJSONObject(i)
            val newExpenseId = idRemapSplitExpense[obj.getLong("expenseId")] ?: continue
            val newMemberId  = idRemapSplitMember[obj.getLong("memberId")] ?: continue
            restoredShares.add(
                SplitExpenseShareEntity(
                    expenseId   = newExpenseId,
                    memberId    = newMemberId,
                    shareAmount = obj.getDouble("shareAmount")
                )
            )
        }
        if (restoredShares.isNotEmpty()) {
            db.splitExpenseShareDao().insertAll(restoredShares)
            sShares += restoredShares.size
        }

        // ── 14. Split expense items — build oldId → newId remap (v4+) ────────
        val splitItemArr     = root.optJSONArray(KEY_SPLIT_ITEMS) ?: JSONArray()
        val idRemapSplitItem = mutableMapOf<Long, Long>()
        for (i in 0 until splitItemArr.length()) {
            val obj           = splitItemArr.getJSONObject(i)
            val oldId         = obj.getLong("id")
            val newExpenseId  = idRemapSplitExpense[obj.getLong("expenseId")] ?: continue
            val newId = db.splitExpenseItemDao().insertItem(
                SplitExpenseItemEntity(
                    expenseId = newExpenseId,
                    name      = obj.optString("name", ""),
                    amount    = obj.getDouble("amount")
                )
            )
            idRemapSplitItem[oldId] = newId
            sItems++
        }

        // ── 15. Split expense item members (v4+, batch insert) ───────────────
        val splitItemMemberArr = root.optJSONArray(KEY_SPLIT_ITEM_MEMBERS) ?: JSONArray()
        val restoredItemMembers = mutableListOf<SplitExpenseItemMemberEntity>()
        for (i in 0 until splitItemMemberArr.length()) {
            val obj         = splitItemMemberArr.getJSONObject(i)
            val newItemId   = idRemapSplitItem[obj.getLong("itemId")] ?: continue
            val newMemberId = idRemapSplitMember[obj.getLong("memberId")] ?: continue
            restoredItemMembers.add(
                SplitExpenseItemMemberEntity(
                    itemId   = newItemId,
                    memberId = newMemberId
                )
            )
        }
        if (restoredItemMembers.isNotEmpty()) {
            db.splitExpenseItemDao().insertItemMembers(restoredItemMembers)
            sItemMembers += restoredItemMembers.size
        }

        return ImportResult(
            categories       = cats,
            expenses         = exps,
            budgets          = buds,
            khataParties     = kParties,
            khataEntries     = kEntries,
            debts            = dDebts,
            debtPayments     = dPayments,
            income           = incomes,
            paymentAccounts  = accounts,
            goals            = goals,
            splitGroups      = sGroups,
            splitMembers     = sMembers,
            splitExpenses    = sExpenses,
            splitShares      = sShares,
            splitItems       = sItems,
            splitItemMembers = sItemMembers
        )
    }

    data class ImportResult(
        val categories:   Int,
        val expenses:     Int,
        val budgets:      Int,
        val khataParties: Int = 0,
        val khataEntries: Int = 0,
        val debts:        Int = 0,
        val debtPayments: Int = 0,
        val income:       Int = 0,
        val paymentAccounts: Int = 0,
        val goals:            Int = 0,
        val splitGroups:      Int = 0,
        val splitMembers:     Int = 0,
        val splitExpenses:    Int = 0,
        val splitShares:      Int = 0,
        val splitItems:       Int = 0,
        val splitItemMembers: Int = 0
    )

    // ── JSON serialisers ──────────────────────────────────────────────────────

    private fun CategoryEntity.toJson() = JSONObject().apply {
        put("id",        id)
        if (nameKey    != null) put("nameKey",    nameKey)
        if (customName != null) put("customName", customName)
        put("colorHex",  colorHex)
        put("sortOrder", sortOrder)
    }

    private fun ExpenseEntity.toJson() = JSONObject().apply {
        put("categoryId",  categoryId)
        put("description", description)
        put("amount",      amount)
        put("date",        date)
        put("monthKey",    monthKey)
        put("isRecurring", isRecurring)
        if (recurringPeriod      != null) put("recurringPeriod",      recurringPeriod)
        // Pre-existing gap, not introduced by v4: recurringSourceId is the OLD device's
        // auto-increment expense ID, and this import path builds no id-based remap for
        // expenses (they're intentionally not deduplicated — see class doc). It round-trips
        // as a stale, likely-wrong ID after restore. Left as-is rather than risking a
        // half-fixed remap without a way to test it end-to-end.
        if (recurringSourceId    != null) put("recurringSourceId",    recurringSourceId)
        if (recurringDayOfMonth  != null) put("recurringDayOfMonth",  recurringDayOfMonth)
        if (accountId            != null) put("accountId",           accountId)
        // memberId (Family Mode) intentionally omitted — see class doc comment.
    }

    private fun BudgetEntity.toJson() = JSONObject().apply {
        if (categoryId != null) put("categoryId", categoryId) else put("categoryId", JSONObject.NULL)
        put("amount", amount)
    }

    private fun KhataPartyEntity.toJson() = JSONObject().apply {
        put("id",        id)
        put("name",      name)
        put("phone",     phone)
        put("direction", direction)
        put("createdAt", createdAt)
        if (upiId       != null) put("upiId",       upiId)
        if (creditLimit != null) put("creditLimit", creditLimit)
    }

    private fun KhataEntryEntity.toJson() = JSONObject().apply {
        put("partyId", partyId)
        put("amount",  amount)
        put("note",    note)
        put("date",    date)
        put("type",    type)
        if (dueDate != null) put("dueDate", dueDate)
        // linkedExpenseId is NOT serialised — it references a row in the expenses table by
        // auto-increment ID which will be different on the restored device.
        // photoPath is NOT serialised — see class doc comment (device-local file path, and
        // this backup format carries no binary photo data).
    }

    private fun DebtEntity.toJson() = JSONObject().apply {
        put("id",                  id)
        put("name",                name)
        put("direction",           direction)
        put("principal",           principal)
        put("interestRatePercent", interestRatePercent)
        put("minimumPayment",      minimumPayment)
        put("startDate",           startDate)
        put("isClosed",            isClosed)
        if (notes    != null) put("notes",    notes)
        if (loanType != null) put("loanType", loanType)
    }

    private fun DebtPaymentEntity.toJson() = JSONObject().apply {
        put("debtId", debtId)
        put("amount", amount)
        put("date",   date)
        if (note != null) put("note", note)
        // linkedExpenseId NOT serialised — same reason as KhataEntryEntity.
    }

    private fun IncomeEntity.toJson() = JSONObject().apply {
        put("amount",      amount)
        put("source",      source)
        put("note",        note)
        put("date",        date)
        put("monthKey",    monthKey)
        put("isRecurring", isRecurring)
    }

    private fun PaymentAccountEntity.toJson() = JSONObject().apply {
        put("id",        id)
        if (nameKey    != null) put("nameKey",    nameKey)
        if (customName != null) put("customName", customName)
        put("type",       type)
        put("colorHex",   colorHex)
        put("isDefault",  isDefault)
        put("sortOrder",  sortOrder)
        put("createdAt",  createdAt)
    }

    private fun GoalEntity.toJson() = JSONObject().apply {
        put("name",         name)
        put("emoji",        emoji)
        put("targetAmount", targetAmount)
        put("savedAmount",  savedAmount)
        put("targetDate",   targetDate)
        put("createdAt",    createdAt)
        put("isCompleted",  isCompleted)
    }

    private fun SplitGroupEntity.toJson() = JSONObject().apply {
        put("id",          id)
        put("name",        name)
        put("emoji",       emoji)
        put("createdDate", createdDate)
    }

    private fun SplitMemberEntity.toJson() = JSONObject().apply {
        put("id",       id)
        put("groupId",  groupId)
        put("name",     name)
        put("colorHex", colorHex)
        put("emoji",    emoji)
        put("isMe",     isMe)
        if (upiId != null) put("upiId", upiId)
        if (phone != null) put("phone", phone)
    }

    private fun SplitExpenseEntity.toJson() = JSONObject().apply {
        put("id",             id)
        put("groupId",        groupId)
        put("description",    description)
        put("amount",         amount)
        put("paidByMemberId", paidByMemberId)
        put("date",           date)
        put("note",           note)
        put("isSettlement",   isSettlement)
        // linkedExpenseId NOT serialised — same reason as KhataEntryEntity/DebtPaymentEntity.
    }

    private fun SplitExpenseShareEntity.toJson() = JSONObject().apply {
        put("expenseId",   expenseId)
        put("memberId",    memberId)
        put("shareAmount", shareAmount)
    }

    private fun SplitExpenseItemEntity.toJson() = JSONObject().apply {
        put("id",        id)
        put("expenseId", expenseId)
        put("name",      name)
        put("amount",    amount)
    }

    private fun SplitExpenseItemMemberEntity.toJson() = JSONObject().apply {
        put("itemId",   itemId)
        put("memberId", memberId)
    }
}
