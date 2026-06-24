package com.expensetracker.app.data;

import android.database.Cursor;
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
public final class PendingSmsExpenseDao_Impl implements PendingSmsExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PendingSmsExpense> __insertionAdapterOfPendingSmsExpense;

  private final EntityDeletionOrUpdateAdapter<PendingSmsExpense> __deletionAdapterOfPendingSmsExpense;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public PendingSmsExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPendingSmsExpense = new EntityInsertionAdapter<PendingSmsExpense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `pending_sms_expenses` (`id`,`rawMessage`,`amount`,`description`,`suggestedCategoryId`,`date`,`receivedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PendingSmsExpense entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRawMessage() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getRawMessage());
        }
        statement.bindDouble(3, entity.getAmount());
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescription());
        }
        if (entity.getSuggestedCategoryId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getSuggestedCategoryId());
        }
        if (entity.getDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDate());
        }
        statement.bindLong(7, entity.getReceivedAt());
      }
    };
    this.__deletionAdapterOfPendingSmsExpense = new EntityDeletionOrUpdateAdapter<PendingSmsExpense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `pending_sms_expenses` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PendingSmsExpense entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_sms_expenses";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PendingSmsExpense item, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPendingSmsExpense.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final PendingSmsExpense item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPendingSmsExpense.handle(item);
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
  public Flow<List<PendingSmsExpense>> observeAll() {
    final String _sql = "SELECT * FROM pending_sms_expenses ORDER BY receivedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pending_sms_expenses"}, new Callable<List<PendingSmsExpense>>() {
      @Override
      @NonNull
      public List<PendingSmsExpense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRawMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "rawMessage");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSuggestedCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggestedCategoryId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "receivedAt");
          final List<PendingSmsExpense> _result = new ArrayList<PendingSmsExpense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PendingSmsExpense _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpRawMessage;
            if (_cursor.isNull(_cursorIndexOfRawMessage)) {
              _tmpRawMessage = null;
            } else {
              _tmpRawMessage = _cursor.getString(_cursorIndexOfRawMessage);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final Long _tmpSuggestedCategoryId;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategoryId)) {
              _tmpSuggestedCategoryId = null;
            } else {
              _tmpSuggestedCategoryId = _cursor.getLong(_cursorIndexOfSuggestedCategoryId);
            }
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final long _tmpReceivedAt;
            _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
            _item = new PendingSmsExpense(_tmpId,_tmpRawMessage,_tmpAmount,_tmpDescription,_tmpSuggestedCategoryId,_tmpDate,_tmpReceivedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
