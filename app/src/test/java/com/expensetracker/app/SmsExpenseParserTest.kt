package com.expensetracker.app

import com.expensetracker.app.data.CategoryEntity
import com.expensetracker.app.util.SmsExpenseParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-JVM tests for the region-aware SMS transaction parser. No Android dependencies — runs
 * under `./gradlew testDebugUnitTest`. Covers the Saudi (SAR, Arabic + English) formats added
 * for the KSA market, the retained India (INR) formats, and the non-transaction messages that
 * must be silently skipped so they never reach the review queue.
 */
class SmsExpenseParserTest {

    // ── Saudi Arabia — should parse ──────────────────────────────────────────

    @Test
    fun alRajhi_arabic_mada_purchase_parsesAmountAndMerchant() {
        val sms = "شراء\nمدى-أهلي\nبطاقة:0000؛4321\nمبلغ:150.00 ريال\nلدى:JARIR BOOKSTORE\nفي:01/07"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(150.0, r!!.amount, 0.001)
        assertTrue(r.merchantOrNote.contains("JARIR"))
    }

    @Test
    fun snb_arabic_pos_riyalSymbol_parses() {
        val sms = "عملية شراء عبر نقاط البيع\nالمبلغ ﷼ 250\nلدى AMAZON SA"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(250.0, r!!.amount, 0.001)
        assertTrue(r.merchantOrNote.contains("AMAZON"))
    }

    @Test
    fun riyadBank_arabicIndicDigits_parses() {
        // ٩٩٫٠٠ = 99.00 using Arabic-Indic digits and the Arabic decimal separator.
        val sms = "عملية شراء\nبقيمة ٩٩٫٠٠ ر.س\nمن STARBUCKS\nبطاقة مدى"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(99.0, r!!.amount, 0.001)
    }

    @Test
    fun sabb_english_pos_parsesAmountMerchantAndCategory() {
        val sms = "Purchase of SAR 45.00 at PANDA on 02/07 via mada card"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(45.0, r!!.amount, 0.001)
        assertEquals("PANDA", r.merchantOrNote)
        assertEquals("cat_food", r.categoryNameKeyGuess)
    }

    @Test
    fun stcPay_english_wallet_parsesTransportCategory() {
        val sms = "You paid SAR 50.00 to CAREEM via stc pay"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(50.0, r!!.amount, 0.001)
        assertEquals("cat_transport", r.categoryNameKeyGuess)
    }

    @Test
    fun stcPay_arabic_to_marker_parsesMerchant() {
        val sms = "تم دفع 30.00 ريال الى HUNGERSTATION عبر stc pay"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(30.0, r!!.amount, 0.001)
        assertTrue(r.merchantOrNote.contains("HUNGERSTATION"))
    }

    // ── India — retained, should still parse ─────────────────────────────────

    @Test
    fun india_upi_debit_stillParses() {
        val sms = "Rs.1,250.50 debited via UPI to swiggy@icici. Ref 883"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(1250.50, r!!.amount, 0.001)
        assertEquals("cat_food", r.categoryNameKeyGuess)
    }

    @Test
    fun india_card_inr_stillParses() {
        val sms = "INR 999 spent at AMAZON on your HDFC card"
        val r = SmsExpenseParser.parse(sms)
        assertNotNull(r)
        assertEquals(999.0, r!!.amount, 0.001)
    }

    // ── Must be skipped (returns null) ───────────────────────────────────────

    @Test
    fun salary_arabic_isSkipped() {
        assertNull(SmsExpenseParser.parse("تم إيداع راتب بقيمة 8000.00 ريال في حسابك"))
    }

    @Test
    fun otp_arabic_isSkipped() {
        assertNull(SmsExpenseParser.parse("رمز التحقق الخاص بك هو 4587 لا تشاركه مع أحد"))
    }

    @Test
    fun refund_english_isSkipped() {
        assertNull(SmsExpenseParser.parse("SAR 120.00 refund credited to your card ending 4321"))
    }

    @Test
    fun balanceEnquiry_arabic_isSkipped() {
        assertNull(SmsExpenseParser.parse("الرصيد المتاح في حسابك 5,230.00 ريال"))
    }

    // ── Category id resolution ───────────────────────────────────────────────

    @Test
    fun guessCategoryId_matchesByNameKey() {
        val categories = listOf(
            CategoryEntity(id = 7, nameKey = "cat_food", customName = null, colorHex = "#fff", sortOrder = 0),
            CategoryEntity(id = 9, nameKey = "cat_transport", customName = null, colorHex = "#000", sortOrder = 1)
        )
        assertEquals(7L, SmsExpenseParser.guessCategoryId("cat_food", categories))
        assertNull(SmsExpenseParser.guessCategoryId("cat_unknown", categories))
        assertNull(SmsExpenseParser.guessCategoryId(null, categories))
    }
}
