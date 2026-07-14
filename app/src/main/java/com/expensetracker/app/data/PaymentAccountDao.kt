package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentAccountDao {

    @Query("SELECT * FROM payment_accounts ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<PaymentAccountEntity>>

    @Query("SELECT * FROM payment_accounts ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllOnce(): List<PaymentAccountEntity>

    @Query("SELECT COUNT(*) FROM payment_accounts")
    suspend fun count(): Int

    @Insert
    suspend fun insert(account: PaymentAccountEntity): Long

    @Update
    suspend fun update(account: PaymentAccountEntity)

    @Delete
    suspend fun delete(account: PaymentAccountEntity)

    @Query("DELETE FROM payment_accounts")
    suspend fun deleteAll()

    /** Clears the account tag off every expense that referenced this account — called just
     * before deleting the account itself, so removing an account never deletes expenses,
     * it only un-tags them (same "safe to remove metadata" behavior as unassigning a
     * family member, never a destructive cascade). */
    @Query("UPDATE expenses SET accountId = NULL WHERE accountId = :accountId")
    suspend fun clearFromExpenses(accountId: Long)
}
