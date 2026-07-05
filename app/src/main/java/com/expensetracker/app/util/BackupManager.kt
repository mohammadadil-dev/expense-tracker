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
import com.expensetracker.app.data.IncomeEntity
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.data.KhataPartyEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * On-device JSON backup and restore.
 *
 * Export: serialises categories, expenses, budgets, Khata parties/entries, and debts/payments
 * to a single JSON file and hands it out via FileProvider so the user can save to Downloads
 * or share to Drive.
 *
 * Import: reads a previously exported JSON file and re-inserts all records, building ID
 * remaps so foreign-key relationships are correctly re-wired on the new device.
 *
 * Version history:
 *   v1 — categories, expenses, budgets only
 *   v2 — adds khataParties, khataEntries, debts, debtPayments
 *   v3 — adds income entries
 *
 * v1 and v2 backups import cleanly into v3 (missing sections default to empty arrays).
 */
object BackupManager {

    private const val KEY_VERSION         = "version"
    private const val KEY_EXPORTED_AT     = "exportedAt"
    private const val KEY_CATEGORIES      = "categories"
    private const val KEY_EXPENSES        = "expenses"
    private const val KEY_BUDGETS         = "budgets"
    private const val KEY_KHATA_PARTIES   = "khataParties"
    private const val KEY_KHATA_ENTRIES   = "khataEntries"
    private const val KEY_DEBTS           = "debts"
    private const val KEY_DEBT_PAYMENTS   = "debtPayments"
    private const val KEY_INCOME          = "income"
    private const val BACKUP_VERSION      = 3

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
        val categories    = db.categoryDao().getAllOnce()
        val expenses      = db.expenseDao().getAllOnce()
        val budgets       = db.budgetDao().getAllOnce()
        val khataParties  = db.khataDao().getAllPartiesOnce()
        val khataEntries  = db.khataDao().getAllEntriesOnce()
        val debts         = db.debtDao().getAllOnce()
        val debtPayments  = db.debtPaymentDao().getAllOnce()
        val income        = db.incomeDao().getAllOnce()

        return JSONObject().apply {
            put(KEY_VERSION,       BACKUP_VERSION)
            put(KEY_EXPORTED_AT,   System.currentTimeMillis())
            put(KEY_CATEGORIES,    JSONArray(categories.map   { it.toJson() }))
            put(KEY_EXPENSES,      JSONArray(expenses.map     { it.toJson() }))
            put(KEY_BUDGETS,       JSONArray(budgets.map      { it.toJson() }))
            put(KEY_KHATA_PARTIES, JSONArray(khataParties.map { it.toJson() }))
            put(KEY_KHATA_ENTRIES, JSONArray(khataEntries.map { it.toJson() }))
            put(KEY_DEBTS,         JSONArray(debts.map        { it.toJson() }))
            put(KEY_DEBT_PAYMENTS, JSONArray(debtPayments.map { it.toJson() }))
            put(KEY_INCOME,        JSONArray(income.map       { it.toJson() }))
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
     * Accepts both v1 and v2 backup files. v1 files simply have no khata/debt keys,
     * which are treated as empty arrays — no data loss on either side.
     *
     * Duplicate handling:
     * - Categories: deduplicated by nameKey (built-ins) or customName (user categories).
     * - Khata parties: deduplicated by name + direction. Same party imported twice keeps
     *   only one row; entries are still appended so partial restores accumulate correctly.
     * - Debts: deduplicated by name + startDate + direction.
     * - Expenses / KhataEntries / DebtPayments: NOT deduplicated — importing the same
     *   backup twice will double these totals. Warn the user before import.
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

        // ── 2. Expenses ──────────────────────────────────────────────────────
        val expArr = root.optJSONArray(KEY_EXPENSES) ?: JSONArray()
        for (i in 0 until expArr.length()) {
            val obj    = expArr.getJSONObject(i)
            val newCat = idRemapCat[obj.getLong("categoryId")] ?: continue
            db.expenseDao().insert(
                ExpenseEntity(
                    categoryId        = newCat,
                    description       = obj.optString("description", ""),
                    amount            = obj.getDouble("amount"),
                    date              = obj.getString("date"),
                    monthKey          = obj.getString("monthKey"),
                    isRecurring       = obj.optBoolean("isRecurring", false),
                    recurringPeriod   = obj.optString("recurringPeriod").takeIf   { it.isNotBlank() },
                    recurringSourceId = obj.optLong("recurringSourceId", 0L).takeIf { it != 0L }
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
                        name      = name,
                        phone     = obj.optString("phone", ""),
                        direction = direction,
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
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
                    type     = obj.getString("type")
                    // linkedExpenseId intentionally dropped — the linked expense row is already
                    // restored (or skipped) independently; re-linking by old ID would be wrong.
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

        return ImportResult(
            categories   = cats,
            expenses     = exps,
            budgets      = buds,
            khataParties = kParties,
            khataEntries = kEntries,
            debts        = dDebts,
            debtPayments = dPayments,
            income       = incomes
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
        val income:       Int = 0
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
        if (recurringPeriod   != null) put("recurringPeriod",   recurringPeriod)
        if (recurringSourceId != null) put("recurringSourceId", recurringSourceId)
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
    }

    private fun KhataEntryEntity.toJson() = JSONObject().apply {
        put("partyId", partyId)
        put("amount",  amount)
        put("note",    note)
        put("date",    date)
        put("type",    type)
        // linkedExpenseId is NOT serialised — it references a row in the expenses table by
        // auto-increment ID which will be different on the restored device.
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
}
