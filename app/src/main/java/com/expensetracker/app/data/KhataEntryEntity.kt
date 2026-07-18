package com.expensetracker.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One line in a Khata party's ledger — either a credit (goods/services taken on tab,
 * increasing the outstanding balance) or a payment (cash settled, decreasing the balance).
 *
 * Outstanding balance for a party = Σ credit amounts − Σ payment amounts.
 * This is always computed live; no balance column is stored.
 *
 * [type] is [TYPE_CREDIT] or [TYPE_PAYMENT].
 * [date] is an ISO-8601 date string ("yyyy-MM-dd"), matching the rest of the app.
 */
@Entity(
    tableName = "khata_entries",
    foreignKeys = [
        ForeignKey(
            entity = KhataPartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["partyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("partyId")]
)
data class KhataEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val partyId: Long,
    val amount: Double,
    val note: String = "",
    val date: String,
    val type: String,
    /** Non-null for I-OWE CREDIT entries — points at the auto-generated ExpenseEntity row so
     *  deleting this entry also removes the linked expense from the dashboard. */
    val linkedExpenseId: Long? = null,
    /** Optional "pay/collect by" date (ISO "yyyy-MM-dd") — only meaningful on CREDIT entries.
     *  Lets a single purchase-on-credit or a customer's tab carry its own due date instead of
     *  only ever tracking a whole-party balance with no timeline. Null = no due date set. */
    val dueDate: String? = null,
    /** Optional absolute path to a receipt/bill photo saved in this app's private storage
     *  (`filesDir/receipts/`), so a credit entry keeps visual proof of the purchase. Null =
     *  no photo attached. See [com.expensetracker.app.util.ReceiptPhotoStore]. */
    val photoPath: String? = null
) {
    companion object {
        /** Goods/services taken on credit — increases the outstanding balance. */
        const val TYPE_CREDIT = "CREDIT"

        /** Cash or in-kind payment — decreases the outstanding balance. */
        const val TYPE_PAYMENT = "PAYMENT"
    }
}
