package com.expensetracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitGroupDao {
    @Query("SELECT * FROM split_groups ORDER BY createdDate DESC")
    fun getAllGroups(): Flow<List<SplitGroupEntity>>

    @Query("SELECT * FROM split_groups WHERE id = :id")
    suspend fun getById(id: Long): SplitGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: SplitGroupEntity): Long

    @Update
    suspend fun update(group: SplitGroupEntity)

    @Delete
    suspend fun delete(group: SplitGroupEntity)
}
