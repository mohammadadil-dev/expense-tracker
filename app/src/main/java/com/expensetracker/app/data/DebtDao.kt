package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    /** Open debts first (newest first within each group), settled ones at the bottom. */
    @Query("SELECT * FROM debts ORDER BY isClosed ASC, id DESC")
    fun observeAll(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts")
    suspend fun getAllOnce(): List<DebtEntity>

    @Insert
    suspend fun insert(debt: DebtEntity): Long

    @Update
    suspend fun update(debt: DebtEntity)

    @Delete
    suspend fun delete(debt: DebtEntity)

    @Query("DELETE FROM debts")
    suspend fun deleteAll()
}
