package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SplitExpenseItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SplitExpenseItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemMembers(members: List<SplitExpenseItemMemberEntity>)

    @Query("SELECT * FROM split_expense_items WHERE expenseId = :expenseId")
    suspend fun getItemsForExpense(expenseId: Long): List<SplitExpenseItemEntity>

    /** Unscoped snapshots across every expense — used by BackupManager's full-database export. */
    @Query("SELECT * FROM split_expense_items")
    suspend fun getAllItemsOnce(): List<SplitExpenseItemEntity>

    @Query("SELECT * FROM split_expense_item_members")
    suspend fun getAllItemMembersOnce(): List<SplitExpenseItemMemberEntity>

    @Query("""
        SELECT * FROM split_expense_item_members
        WHERE itemId IN (SELECT id FROM split_expense_items WHERE expenseId = :expenseId)
    """)
    suspend fun getItemMembersForExpense(expenseId: Long): List<SplitExpenseItemMemberEntity>

    @Query("DELETE FROM split_expense_items WHERE expenseId = :expenseId")
    suspend fun deleteItemsForExpense(expenseId: Long)

    @Query("""
        DELETE FROM split_expense_item_members
        WHERE itemId IN (SELECT id FROM split_expense_items WHERE expenseId = :expenseId)
    """)
    suspend fun deleteItemMembersForExpense(expenseId: Long)

    @Query("""
        DELETE FROM split_expense_item_members
        WHERE itemId IN (
            SELECT id FROM split_expense_items
            WHERE expenseId IN (SELECT id FROM split_expenses WHERE groupId = :groupId)
        )
    """)
    suspend fun deleteAllItemMembersForGroup(groupId: Long)

    @Query("""
        DELETE FROM split_expense_items
        WHERE expenseId IN (SELECT id FROM split_expenses WHERE groupId = :groupId)
    """)
    suspend fun deleteAllItemsForGroup(groupId: Long)
}
