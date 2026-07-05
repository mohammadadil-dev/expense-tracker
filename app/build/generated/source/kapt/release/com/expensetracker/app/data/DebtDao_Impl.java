package com.expensetracker.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DebtDao_Impl implements DebtDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DebtEntity> __insertionAdapterOfDebtEntity;

  private final EntityDeletionOrUpdateAdapter<DebtEntity> __deletionAdapterOfDebtEntity;

  private final EntityDeletionOrUpdateAdapter<DebtEntity> __updateAdapterOfDebtEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DebtDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDebtEntity = new EntityInsertionAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `debts` (`id`,`name`,`direction`,`principal`,`interestRatePercent`,`minimumPayment`,`startDate`,`notes`,`isClosed`,`loanType`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getDirection() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDirection());
        }
        statement.bindDouble(4, entity.getPrincipal());
        statement.bindDouble(5, entity.getInterestRatePercent());
        statement.bindDouble(6, entity.getMinimumPayment());
        if (entity.getStartDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStartDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getNotes());
        }
        final int _tmp = entity.isClosed() ? 1 : 0;
        statement.bindLong(9, _tmp);
        if (entity.getLoanType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getLoanType());
        }
      }
    };
    this.__deletionAdapterOfDebtEntity = new EntityDeletionOrUpdateAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `debts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDebtEntity = new EntityDeletionOrUpdateAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `debts` SET `id` = ?,`name` = ?,`direction` = ?,`principal` = ?,`interestRatePercent` = ?,`minimumPayment` = ?,`startDate` = ?,`notes` = ?,`isClosed` = ?,`loanType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getDirection() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDirection());
        }
        statement.bindDouble(4, entity.getPrincipal());
        statement.bindDouble(5, entity.getInterestRatePercent());
        statement.bindDouble(6, entity.getMinimumPayment());
        if (entity.getStartDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStartDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getNotes());
        }
        final int _tmp = entity.isClosed() ? 1 : 0;
        statement.bindLong(9, _tmp);
        if (entity.getLoanType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getLoanType());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM debts";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DebtEntity debt, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDebtEntity.insertAndReturnId(debt);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DebtEntity debt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDebtEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DebtEntity debt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDebtEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DebtEntity>> observeAll() {
    final String _sql = "SELECT * FROM debts ORDER BY isClosed ASC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfPrincipal = CursorUtil.getColumnIndexOrThrow(_cursor, "principal");
          final int _cursorIndexOfInterestRatePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "interestRatePercent");
          final int _cursorIndexOfMinimumPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "minimumPayment");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsClosed = CursorUtil.getColumnIndexOrThrow(_cursor, "isClosed");
          final int _cursorIndexOfLoanType = CursorUtil.getColumnIndexOrThrow(_cursor, "loanType");
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpDirection;
            if (_cursor.isNull(_cursorIndexOfDirection)) {
              _tmpDirection = null;
            } else {
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            }
            final double _tmpPrincipal;
            _tmpPrincipal = _cursor.getDouble(_cursorIndexOfPrincipal);
            final double _tmpInterestRatePercent;
            _tmpInterestRatePercent = _cursor.getDouble(_cursorIndexOfInterestRatePercent);
            final double _tmpMinimumPayment;
            _tmpMinimumPayment = _cursor.getDouble(_cursorIndexOfMinimumPayment);
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsClosed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsClosed);
            _tmpIsClosed = _tmp != 0;
            final String _tmpLoanType;
            if (_cursor.isNull(_cursorIndexOfLoanType)) {
              _tmpLoanType = null;
            } else {
              _tmpLoanType = _cursor.getString(_cursorIndexOfLoanType);
            }
            _item = new DebtEntity(_tmpId,_tmpName,_tmpDirection,_tmpPrincipal,_tmpInterestRatePercent,_tmpMinimumPayment,_tmpStartDate,_tmpNotes,_tmpIsClosed,_tmpLoanType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllOnce(final Continuation<? super List<DebtEntity>> $completion) {
    final String _sql = "SELECT * FROM debts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final int _cursorIndexOfPrincipal = CursorUtil.getColumnIndexOrThrow(_cursor, "principal");
          final int _cursorIndexOfInterestRatePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "interestRatePercent");
          final int _cursorIndexOfMinimumPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "minimumPayment");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsClosed = CursorUtil.getColumnIndexOrThrow(_cursor, "isClosed");
          final int _cursorIndexOfLoanType = CursorUtil.getColumnIndexOrThrow(_cursor, "loanType");
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpDirection;
            if (_cursor.isNull(_cursorIndexOfDirection)) {
              _tmpDirection = null;
            } else {
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            }
            final double _tmpPrincipal;
            _tmpPrincipal = _cursor.getDouble(_cursorIndexOfPrincipal);
            final double _tmpInterestRatePercent;
            _tmpInterestRatePercent = _cursor.getDouble(_cursorIndexOfInterestRatePercent);
            final double _tmpMinimumPayment;
            _tmpMinimumPayment = _cursor.getDouble(_cursorIndexOfMinimumPayment);
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsClosed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsClosed);
            _tmpIsClosed = _tmp != 0;
            final String _tmpLoanType;
            if (_cursor.isNull(_cursorIndexOfLoanType)) {
              _tmpLoanType = null;
            } else {
              _tmpLoanType = _cursor.getString(_cursorIndexOfLoanType);
            }
            _item = new DebtEntity(_tmpId,_tmpName,_tmpDirection,_tmpPrincipal,_tmpInterestRatePercent,_tmpMinimumPayment,_tmpStartDate,_tmpNotes,_tmpIsClosed,_tmpLoanType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
