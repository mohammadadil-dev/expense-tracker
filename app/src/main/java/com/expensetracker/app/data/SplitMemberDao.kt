package com.expensetracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SplitMemberDao {
    @Query("SELECT * FROM split_members WHERE groupId = :groupId ORDER BY id ASC")
    fun getMembersForGroup(groupId: Long): Flow<List<SplitMemberEntity>>

    @Query("SELECT * FROM split_members WHERE groupId = :groupId ORDER BY id ASC")
    suspend fun getMembersSnapshot(groupId: Long): List<SplitMemberEntity>

    /** Unscoped snapshot of every member across every group — used by BackupManager's
     *  full-database export. */
    @Query("SELECT * FROM split_members")
    suspend fun getAllOnce(): List<SplitMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: SplitMemberEntity): Long

    @Update
    suspend fun update(member: SplitMemberEntity)

    @Delete
    suspend fun delete(member: SplitMemberEntity)

    @Query("DELETE FROM split_members WHERE groupId = :groupId")
    suspend fun deleteAllForGroup(groupId: Long)
}
