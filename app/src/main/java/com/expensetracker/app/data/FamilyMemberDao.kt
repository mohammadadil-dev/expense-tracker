package com.expensetracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {

    @Query("SELECT * FROM family_members ORDER BY isMe DESC, createdAt ASC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members WHERE isMe = 1 LIMIT 1")
    suspend fun getMyProfile(): FamilyMemberEntity?

    @Query("SELECT * FROM family_members WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FamilyMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: FamilyMemberEntity): Long

    @Update
    suspend fun update(member: FamilyMemberEntity)

    @Delete
    suspend fun delete(member: FamilyMemberEntity)

    @Query("DELETE FROM family_members WHERE isMe = 0")
    suspend fun deleteAllNonOwners()

    @Query("DELETE FROM family_members")
    suspend fun deleteAll()

    /** One-shot suspend query — used by FamilyRepository for invite code generation. */
    @Query("SELECT * FROM family_members ORDER BY isMe DESC, createdAt ASC")
    suspend fun getAllMembersSnapshot(): List<FamilyMemberEntity>

    @Query("SELECT COUNT(*) FROM family_members")
    suspend fun count(): Int
}
