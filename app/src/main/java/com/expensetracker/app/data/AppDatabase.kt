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
    entities = [CategoryEntity::class, ExpenseEntity::class, PendingSmsExpense::class, BudgetEntity::class, DebtEntity::class, DebtPaymentEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun pendingSmsExpenseDao(): PendingSmsExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao
    abstract fun debtPaymentDao(): DebtPaymentDao

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

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build().also { INSTANCE = it }
            }
        }
    }
}
