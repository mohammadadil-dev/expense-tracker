package com.expensetracker.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The entire app's storage is this single on-device SQLite database via Room.
 * There is no network/server component — everything lives in this file on the phone,
 * which is what keeps this app at zero ongoing infrastructure cost.
 */
@Database(
    entities = [
        CategoryEntity::class, ExpenseEntity::class, PendingSmsExpense::class,
        BudgetEntity::class, DebtEntity::class, DebtPaymentEntity::class,
        KhataPartyEntity::class, KhataEntryEntity::class, IncomeEntity::class,
        GoalEntity::class, FamilyMemberEntity::class,
        SplitGroupEntity::class, SplitMemberEntity::class,
        SplitExpenseEntity::class, SplitExpenseShareEntity::class
    ],
    version = 14,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun pendingSmsExpenseDao(): PendingSmsExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao
    abstract fun debtPaymentDao(): DebtPaymentDao
    abstract fun khataDao(): KhataDao
    abstract fun incomeDao(): IncomeDao
    abstract fun goalDao(): GoalDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun splitGroupDao(): SplitGroupDao
    abstract fun splitMemberDao(): SplitMemberDao
    abstract fun splitExpenseDao(): SplitExpenseDao
    abstract fun splitExpenseShareDao(): SplitExpenseShareDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // v1 -> v2: adds the SMS-import review queue table. Written by hand (rather than
        // falling back to a destructive migration) so upgrading the app never wipes a real
        // user's existing expenses/categories.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `pending_sms_expenses` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rawMessage` TEXT NOT NULL,
                        `amount` REAL NOT NULL,
                        `description` TEXT NOT NULL,
                        `suggestedCategoryId` INTEGER,
                        `date` TEXT NOT NULL,
                        `receivedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // v2 -> v3: adds the standing budgets table (overall + per-category monthly targets).
        // No monthKey column on purpose — only the computed spend resets each month, the
        // target itself persists until the user edits or clears it. Written by hand, same as
        // MIGRATION_1_2, so upgrading never wipes a real user's existing data.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `budgets` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `categoryId` INTEGER,
                        `amount` REAL NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // v3 -> v4: adds debt/loan tracking (both money owed by the user and money owed to the
        // user), plus its payment history. `principal` is the only persisted balance figure —
        // the outstanding balance is always computed live as principal minus the sum of this
        // debt's debt_payments rows, the same standing-target-vs-computed-total split used by
        // the budgets table. Written by hand, same as the earlier migrations, so upgrading
        // never wipes a real user's existing data.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `debts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `direction` TEXT NOT NULL,
                        `principal` REAL NOT NULL,
                        `interestRatePercent` REAL NOT NULL DEFAULT 0.0,
                        `minimumPayment` REAL NOT NULL DEFAULT 0.0,
                        `startDate` TEXT NOT NULL,
                        `notes` TEXT,
                        `isClosed` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `debt_payments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `debtId` INTEGER NOT NULL,
                        `amount` REAL NOT NULL,
                        `date` TEXT NOT NULL,
                        `note` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_debtId` ON `debt_payments` (`debtId`)")
            }
        }

        // v4 -> v5: EMI/debt repayments now count as real monthly spend, since paying down a
        // debt is money leaving the user just as much as any other expense. Adds a
        // `linkedExpenseId` column to debt_payments so each repayment (direction == OWE only —
        // collections on money owed *to* the user are incoming, not spend) can point at the
        // ExpenseEntity row it generates; ExpenseRepository keeps the two in sync on insert and
        // delete from here on. Also seeds a dedicated "Debt & Loan Payments" category and
        // retroactively backfills a linked expense for every OWE payment recorded before this
        // migration existed, so historical Total Spent/Budget/Trend figures reflect debt
        // payments too, not just ones made after upgrading. Written by hand, same as the
        // earlier migrations, so upgrading never wipes or skips a real user's existing data.
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE debt_payments ADD COLUMN linkedExpenseId INTEGER")

                var nextSortOrder = 0
                db.query("SELECT COUNT(*) FROM categories").use { cursor ->
                    if (cursor.moveToFirst()) nextSortOrder = cursor.getInt(0)
                }

                db.execSQL(
                    "INSERT INTO categories (nameKey, customName, colorHex, sortOrder) VALUES (?, NULL, ?, ?)",
                    arrayOf<Any?>("cat_debt_payments", "#F97316", nextSortOrder)
                )

                var debtCategoryId = -1L
                db.query("SELECT id FROM categories WHERE nameKey = 'cat_debt_payments' ORDER BY id DESC LIMIT 1").use { cursor ->
                    if (cursor.moveToFirst()) debtCategoryId = cursor.getLong(0)
                }

                if (debtCategoryId != -1L) {
                    // Fully materialize the rows to backfill *before* writing anything back —
                    // this table is both what we're reading here and what we're about to update,
                    // so the read cursor is closed first rather than risking a write landing
                    // mid-iteration of the very rows it's reading.
                    data class OwePayment(val id: Long, val amount: Double, val date: String, val debtName: String)
                    val owePayments = mutableListOf<OwePayment>()
                    db.query(
                        """
                        SELECT dp.id, dp.amount, dp.date, d.name
                        FROM debt_payments dp
                        INNER JOIN debts d ON dp.debtId = d.id
                        WHERE d.direction = 'OWE'
                        """.trimIndent()
                    ).use { cursor ->
                        while (cursor.moveToNext()) {
                            owePayments.add(
                                OwePayment(
                                    id = cursor.getLong(0),
                                    amount = cursor.getDouble(1),
                                    date = cursor.getString(2),
                                    debtName = cursor.getString(3)
                                )
                            )
                        }
                    }

                    for (payment in owePayments) {
                        val monthKey = if (payment.date.length >= 7) payment.date.substring(0, 7) else payment.date

                        db.execSQL(
                            "INSERT INTO expenses (categoryId, description, amount, date, monthKey) VALUES (?, ?, ?, ?, ?)",
                            arrayOf<Any?>(debtCategoryId, payment.debtName, payment.amount, payment.date, monthKey)
                        )

                        var newExpenseId = -1L
                        db.query("SELECT last_insert_rowid()").use { idCursor ->
                            if (idCursor.moveToFirst()) newExpenseId = idCursor.getLong(0)
                        }

                        if (newExpenseId != -1L) {
                            db.execSQL(
                                "UPDATE debt_payments SET linkedExpenseId = ? WHERE id = ?",
                                arrayOf<Any?>(newExpenseId, payment.id)
                            )
                        }
                    }
                }
            }
        }

        // v5 -> v6: adds the Khata (local shop credit ledger) feature — two new tables:
        //   khata_parties  — shops or customers with a running credit tab.
        //   khata_entries  — individual credit / payment lines per party.
        // ForeignKey CASCADE on khata_entries.partyId means deleting a party automatically
        // wipes its full history, same behaviour as debt_payments → debts. Written by hand so
        // upgrading never wipes existing expense/debt data.
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `khata_parties` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `phone` TEXT NOT NULL DEFAULT '',
                        `direction` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `khata_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `partyId` INTEGER NOT NULL,
                        `amount` REAL NOT NULL,
                        `note` TEXT NOT NULL DEFAULT '',
                        `date` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        FOREIGN KEY(`partyId`) REFERENCES `khata_parties`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_khata_entries_partyId` ON `khata_entries` (`partyId`)")
            }
        }

        // v6 -> v7: adds optional loanType column to debts so users can tag each debt/loan
        // with a category (personal, home, car, education, gold, business, informal, other).
        // NULL = unclassified (backwards-compatible — existing rows just have no type badge).
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE debts ADD COLUMN loanType TEXT")
            }
        }

        // v7 -> v8: Khata I-OWE credit entries now auto-generate a linked ExpenseEntity row so
        // they appear in the dashboard's monthly totals and budget tracker — the same pattern
        // debt/EMI payments have used since v4→v5. Adds a nullable linkedExpenseId column to
        // khata_entries, seeds the "Khata" expense category, and backfills linked expenses for
        // every existing I-OWE CREDIT entry so historical figures are correct after upgrade.
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Add the new column (NULL = no linked expense, i.e. PAYMENT / THEY-OWE entries).
                db.execSQL("ALTER TABLE khata_entries ADD COLUMN linkedExpenseId INTEGER")

                // 2. Seed the Khata category — find the next sortOrder from existing categories.
                var nextSortOrder = 0
                db.query("SELECT COUNT(*) FROM categories").use { c ->
                    if (c.moveToFirst()) nextSortOrder = c.getInt(0)
                }
                db.execSQL(
                    "INSERT INTO categories (nameKey, customName, colorHex, sortOrder) VALUES (?, NULL, ?, ?)",
                    arrayOf<Any?>("cat_khata", "#7C3AED", nextSortOrder)
                )
                var khataCategoryId = -1L
                db.query("SELECT id FROM categories WHERE nameKey = 'cat_khata' ORDER BY id DESC LIMIT 1").use { c ->
                    if (c.moveToFirst()) khataCategoryId = c.getLong(0)
                }

                // 3. Backfill: for every I-OWE CREDIT entry that existed before this migration,
                //    create a matching expense and update the entry's linkedExpenseId.
                if (khataCategoryId == -1L) return  // shouldn't happen, but guard anyway

                data class PendingBackfill(val entryId: Long, val amount: Double, val date: String, val partyName: String)
                val toBackfill = mutableListOf<PendingBackfill>()
                db.query(
                    """
                    SELECT ke.id, ke.amount, ke.date, kp.name
                    FROM khata_entries ke
                    INNER JOIN khata_parties kp ON ke.partyId = kp.id
                    WHERE kp.direction = 'I_OWE' AND ke.type = 'CREDIT'
                    """.trimIndent()
                ).use { c ->
                    while (c.moveToNext()) {
                        toBackfill.add(PendingBackfill(c.getLong(0), c.getDouble(1), c.getString(2), c.getString(3)))
                    }
                }

                for (row in toBackfill) {
                    val monthKey = if (row.date.length >= 7) row.date.substring(0, 7) else row.date
                    db.execSQL(
                        "INSERT INTO expenses (categoryId, description, amount, date, monthKey) VALUES (?, ?, ?, ?, ?)",
                        arrayOf<Any?>(khataCategoryId, row.partyName, row.amount, row.date, monthKey)
                    )
                    var newExpenseId = -1L
                    db.query("SELECT last_insert_rowid()").use { c ->
                        if (c.moveToFirst()) newExpenseId = c.getLong(0)
                    }
                    if (newExpenseId != -1L) {
                        db.execSQL(
                            "UPDATE khata_entries SET linkedExpenseId = ? WHERE id = ?",
                            arrayOf<Any?>(newExpenseId, row.entryId)
                        )
                    }
                }
            }
        }

        // v8 -> v9: adds recurring-expense support to the expenses table.
        //   isRecurring       — 1 if this row is a monthly template, 0 for normal / auto-copy.
        //   recurringPeriod   — "MONTHLY" on templates; NULL everywhere else.
        //   recurringSourceId — ID of the template that spawned this copy; NULL on templates
        //                       and normal expenses.
        // Written by hand (same as every prior migration) so upgrading never wipes existing data.
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE expenses ADD COLUMN isRecurring INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE expenses ADD COLUMN recurringPeriod TEXT")
                db.execSQL("ALTER TABLE expenses ADD COLUMN recurringSourceId INTEGER")
            }
        }

        // v9 -> v10: adds income_entries table for manual income logging.
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `income_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `amount` REAL NOT NULL,
                        `source` TEXT NOT NULL DEFAULT '',
                        `note` TEXT NOT NULL DEFAULT '',
                        `date` TEXT NOT NULL,
                        `monthKey` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_entries_monthKey` ON `income_entries` (`monthKey`)")
            }
        }

        // v10 -> v11: adds isRecurring column to income_entries for monthly auto-generation.
        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE income_entries ADD COLUMN isRecurring INTEGER NOT NULL DEFAULT 0")
            }
        }

        // v11 -> v12: adds the savings_goals table for goal-tracking feature.
        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `savings_goals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `emoji` TEXT NOT NULL DEFAULT '🎯',
                        `targetAmount` REAL NOT NULL,
                        `savedAmount` REAL NOT NULL DEFAULT 0.0,
                        `targetDate` TEXT NOT NULL DEFAULT '',
                        `createdAt` INTEGER NOT NULL,
                        `isCompleted` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        // v13 -> v14: Splits / Group Expense Tracker feature.
        //   split_groups        — a named group (trip, flat mates, etc.)
        //   split_members       — people in that group; isMe=1 for the device owner
        //   split_expenses      — expenses logged inside a group (who paid, how much)
        //   split_expense_shares— per-member share breakdown for each expense
        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `split_groups` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `emoji` TEXT NOT NULL DEFAULT '🤝',
                        `createdDate` TEXT NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `split_members` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `groupId` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `colorHex` TEXT NOT NULL DEFAULT '#4CAF50',
                        `emoji` TEXT NOT NULL DEFAULT '',
                        `isMe` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `split_expenses` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `groupId` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `amount` REAL NOT NULL,
                        `paidByMemberId` INTEGER NOT NULL,
                        `date` TEXT NOT NULL,
                        `note` TEXT NOT NULL DEFAULT '',
                        `isSettlement` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `split_expense_shares` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `expenseId` INTEGER NOT NULL,
                        `memberId` INTEGER NOT NULL,
                        `shareAmount` REAL NOT NULL
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_split_members_groupId` ON `split_members` (`groupId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_split_expenses_groupId` ON `split_expenses` (`groupId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_split_shares_expenseId` ON `split_expense_shares` (`expenseId`)")
            }
        }

        // v12 -> v13: Family / Couple Mode.
        //   family_members — named household profiles (owner + up to 4 partners).
        //   expenses.memberId — nullable FK tagging each expense to a member;
        //                       NULL = shared / unassigned (backwards-compatible).
        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `family_members` (
                        `id`        INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name`      TEXT NOT NULL,
                        `colorHex`  TEXT NOT NULL DEFAULT '#4CAF50',
                        `emoji`     TEXT NOT NULL DEFAULT '',
                        `isMe`      INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                // memberId = NULL on all existing rows — they predate family mode.
                db.execSQL("ALTER TABLE expenses ADD COLUMN memberId INTEGER")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker.db"
                ).addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                    MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9,
                    MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13,
                    MIGRATION_13_14
                ).build().also { INSTANCE = it }
            }
        }
    }
}
