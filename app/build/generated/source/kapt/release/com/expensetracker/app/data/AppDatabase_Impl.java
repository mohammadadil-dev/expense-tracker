package com.expensetracker.app.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CategoryDao _categoryDao;

  private volatile ExpenseDao _expenseDao;

  private volatile PendingSmsExpenseDao _pendingSmsExpenseDao;

  private volatile BudgetDao _budgetDao;

  private volatile DebtDao _debtDao;

  private volatile DebtPaymentDao _debtPaymentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nameKey` TEXT, `customName` TEXT, `colorHex` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER NOT NULL, `description` TEXT NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `monthKey` TEXT NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_monthKey` ON `expenses` (`monthKey`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_categoryId` ON `expenses` (`categoryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_sms_expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `rawMessage` TEXT NOT NULL, `amount` REAL NOT NULL, `description` TEXT NOT NULL, `suggestedCategoryId` INTEGER, `date` TEXT NOT NULL, `receivedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER, `amount` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `debts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `direction` TEXT NOT NULL, `principal` REAL NOT NULL, `interestRatePercent` REAL NOT NULL, `minimumPayment` REAL NOT NULL, `startDate` TEXT NOT NULL, `notes` TEXT, `isClosed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `debt_payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `debtId` INTEGER NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `note` TEXT, `linkedExpenseId` INTEGER)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_debtId` ON `debt_payments` (`debtId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f24a0831bdbb15f4e857ccbd781bf52c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `expenses`");
        db.execSQL("DROP TABLE IF EXISTS `pending_sms_expenses`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `debts`");
        db.execSQL("DROP TABLE IF EXISTS `debt_payments`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(5);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("nameKey", new TableInfo.Column("nameKey", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("customName", new TableInfo.Column("customName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("colorHex", new TableInfo.Column("colorHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.expensetracker.app.data.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenses = new HashMap<String, TableInfo.Column>(6);
        _columnsExpenses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("monthKey", new TableInfo.Column("monthKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenses = new HashSet<TableInfo.Index>(2);
        _indicesExpenses.add(new TableInfo.Index("index_expenses_monthKey", false, Arrays.asList("monthKey"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_categoryId", false, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        final TableInfo _infoExpenses = new TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses, _indicesExpenses);
        final TableInfo _existingExpenses = TableInfo.read(db, "expenses");
        if (!_infoExpenses.equals(_existingExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "expenses(com.expensetracker.app.data.ExpenseEntity).\n"
                  + " Expected:\n" + _infoExpenses + "\n"
                  + " Found:\n" + _existingExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsPendingSmsExpenses = new HashMap<String, TableInfo.Column>(7);
        _columnsPendingSmsExpenses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSmsExpenses.put("rawMessage", new TableInfo.Column("rawMessage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSmsExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSmsExpenses.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSmsExpenses.put("suggestedCategoryId", new TableInfo.Column("suggestedCategoryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSmsExpenses.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingSmsExpenses.put("receivedAt", new TableInfo.Column("receivedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingSmsExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingSmsExpenses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingSmsExpenses = new TableInfo("pending_sms_expenses", _columnsPendingSmsExpenses, _foreignKeysPendingSmsExpenses, _indicesPendingSmsExpenses);
        final TableInfo _existingPendingSmsExpenses = TableInfo.read(db, "pending_sms_expenses");
        if (!_infoPendingSmsExpenses.equals(_existingPendingSmsExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_sms_expenses(com.expensetracker.app.data.PendingSmsExpense).\n"
                  + " Expected:\n" + _infoPendingSmsExpenses + "\n"
                  + " Found:\n" + _existingPendingSmsExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(3);
        _columnsBudgets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.expensetracker.app.data.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsDebts = new HashMap<String, TableInfo.Column>(9);
        _columnsDebts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("direction", new TableInfo.Column("direction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("principal", new TableInfo.Column("principal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("interestRatePercent", new TableInfo.Column("interestRatePercent", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("minimumPayment", new TableInfo.Column("minimumPayment", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("startDate", new TableInfo.Column("startDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("isClosed", new TableInfo.Column("isClosed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDebts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDebts = new TableInfo("debts", _columnsDebts, _foreignKeysDebts, _indicesDebts);
        final TableInfo _existingDebts = TableInfo.read(db, "debts");
        if (!_infoDebts.equals(_existingDebts)) {
          return new RoomOpenHelper.ValidationResult(false, "debts(com.expensetracker.app.data.DebtEntity).\n"
                  + " Expected:\n" + _infoDebts + "\n"
                  + " Found:\n" + _existingDebts);
        }
        final HashMap<String, TableInfo.Column> _columnsDebtPayments = new HashMap<String, TableInfo.Column>(6);
        _columnsDebtPayments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("debtId", new TableInfo.Column("debtId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("linkedExpenseId", new TableInfo.Column("linkedExpenseId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebtPayments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDebtPayments = new HashSet<TableInfo.Index>(1);
        _indicesDebtPayments.add(new TableInfo.Index("index_debt_payments_debtId", false, Arrays.asList("debtId"), Arrays.asList("ASC")));
        final TableInfo _infoDebtPayments = new TableInfo("debt_payments", _columnsDebtPayments, _foreignKeysDebtPayments, _indicesDebtPayments);
        final TableInfo _existingDebtPayments = TableInfo.read(db, "debt_payments");
        if (!_infoDebtPayments.equals(_existingDebtPayments)) {
          return new RoomOpenHelper.ValidationResult(false, "debt_payments(com.expensetracker.app.data.DebtPaymentEntity).\n"
                  + " Expected:\n" + _infoDebtPayments + "\n"
                  + " Found:\n" + _existingDebtPayments);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f24a0831bdbb15f4e857ccbd781bf52c", "1efb498a8e1b136c9ac3ac3fe625469f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "categories","expenses","pending_sms_expenses","budgets","debts","debt_payments");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `expenses`");
      _db.execSQL("DELETE FROM `pending_sms_expenses`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `debts`");
      _db.execSQL("DELETE FROM `debt_payments`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseDao.class, ExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingSmsExpenseDao.class, PendingSmsExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DebtDao.class, DebtDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DebtPaymentDao.class, DebtPaymentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public ExpenseDao expenseDao() {
    if (_expenseDao != null) {
      return _expenseDao;
    } else {
      synchronized(this) {
        if(_expenseDao == null) {
          _expenseDao = new ExpenseDao_Impl(this);
        }
        return _expenseDao;
      }
    }
  }

  @Override
  public PendingSmsExpenseDao pendingSmsExpenseDao() {
    if (_pendingSmsExpenseDao != null) {
      return _pendingSmsExpenseDao;
    } else {
      synchronized(this) {
        if(_pendingSmsExpenseDao == null) {
          _pendingSmsExpenseDao = new PendingSmsExpenseDao_Impl(this);
        }
        return _pendingSmsExpenseDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public DebtDao debtDao() {
    if (_debtDao != null) {
      return _debtDao;
    } else {
      synchronized(this) {
        if(_debtDao == null) {
          _debtDao = new DebtDao_Impl(this);
        }
        return _debtDao;
      }
    }
  }

  @Override
  public DebtPaymentDao debtPaymentDao() {
    if (_debtPaymentDao != null) {
      return _debtPaymentDao;
    } else {
      synchronized(this) {
        if(_debtPaymentDao == null) {
          _debtPaymentDao = new DebtPaymentDao_Impl(this);
        }
        return _debtPaymentDao;
      }
    }
  }
}
