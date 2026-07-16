package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): GoalEntity?

    @Query("SELECT * FROM savings_goals WHERE isCompleted = 0 ORDER BY createdAt ASC")
    fun activeGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM savings_goals ORDER BY createdAt ASC")
    fun allGoals(): Flow<List<GoalEntity>>

    /** Suspend snapshot (not a Flow) — used by BackupManager's full-database export. */
    @Query("SELECT * FROM savings_goals")
    suspend fun getAllOnce(): List<GoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE savings_goals SET savedAmount = :amount WHERE id = :id")
    suspend fun updateSavedAmount(id: Long, amount: Double)

    @Query("UPDATE savings_goals SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)
}
