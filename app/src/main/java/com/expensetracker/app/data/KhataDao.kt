package com.expensetracker.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KhataDao {

    // ── Parties ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM khata_parties WHERE direction = :direction ORDER BY name ASC")
    fun partiesByDirection(direction: String): Flow<List<KhataPartyEntity>>

    @Query("SELECT * FROM khata_parties ORDER BY name ASC")
    fun allParties(): Flow<List<KhataPartyEntity>>

    @Query("SELECT * FROM khata_parties ORDER BY name ASC")
    suspend fun getAllPartiesOnce(): List<KhataPartyEntity>

    @Query("SELECT * FROM khata_parties WHERE id = :id")
    suspend fun partyById(id: Long): KhataPartyEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertParty(party: KhataPartyEntity): Long

    /** Use for edits — avoids the DELETE+INSERT that REPLACE does, which would
     *  cascade-delete all entries for the party. */
    @Update
    suspend fun updateParty(party: KhataPartyEntity)

    @Delete
    suspend fun deleteParty(party: KhataPartyEntity)

    // ── Entries ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM khata_entries WHERE partyId = :partyId ORDER BY date DESC, id DESC")
    fun entriesForParty(partyId: Long): Flow<List<KhataEntryEntity>>

    @Query("SELECT * FROM khata_entries WHERE partyId = :partyId")
    suspend fun entriesForPartyOnce(partyId: Long): List<KhataEntryEntity>

    @Query("SELECT * FROM khata_entries ORDER BY date DESC, id DESC")
    fun allEntries(): Flow<List<KhataEntryEntity>>

    @Query("SELECT * FROM khata_entries ORDER BY date DESC, id DESC")
    suspend fun getAllEntriesOnce(): List<KhataEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: KhataEntryEntity): Long

    @Delete
    suspend fun deleteEntry(entry: KhataEntryEntity)

    // ── Aggregate helpers ──────────────────────────────────────────────────────

    /** Sum of all CREDIT entries for a party (the gross tab). */
    @Query("SELECT COALESCE(SUM(amount),0) FROM khata_entries WHERE partyId = :partyId AND type = 'CREDIT'")
    suspend fun totalCreditForParty(partyId: Long): Double

    /** Sum of all PAYMENT entries for a party (what has been settled). */
    @Query("SELECT COALESCE(SUM(amount),0) FROM khata_entries WHERE partyId = :partyId AND type = 'PAYMENT'")
    suspend fun totalPaymentForParty(partyId: Long): Double

    /** Rescales every ledger entry when the user converts currency. */
    @Query("UPDATE khata_entries SET amount = amount * :rate")
    suspend fun scaleAllEntryAmounts(rate: Double)

    /** Rescales any credit-limit caps set on parties (skips parties with no limit). */
    @Query("UPDATE khata_parties SET creditLimit = creditLimit * :rate WHERE creditLimit IS NOT NULL")
    suspend fun scaleAllCreditLimits(rate: Double)
}
