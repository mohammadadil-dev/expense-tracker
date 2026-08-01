package com.expensetracker.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.expensetracker.app.R
import com.expensetracker.app.data.JamiyaCircleEntity
import com.expensetracker.app.data.JamiyaContributionEntity
import com.expensetracker.app.data.JamiyaMemberEntity

/**
 * Builds and shares Jam'iya (savings-circle) messages over WhatsApp — a group summary broadcast
 * and a per-member personalised message. All amounts use [Formatters.moneyWithCode] (the "SAR"
 * text form), never the Riyal icon, because WhatsApp/SMS is plain text and can't render the
 * vector glyph. No money moves and nothing is sent silently: WhatsApp's own send button is
 * always the final step, matching the app's existing Khata/Splits share pattern.
 */
object JamiyaShare {

    /** Play Store link appended to shared messages — doubles as an organic install funnel. */
    private fun playLink(context: Context) =
        "https://play.google.com/store/apps/details?id=${context.packageName}"

    // ── Message builders ─────────────────────────────────────────────────────

    /** Full circle breakdown for broadcasting to the whole group. */
    fun buildSummary(
        context: Context,
        circle: JamiyaCircleEntity,
        members: List<JamiyaMemberEntity>,
        contributions: List<JamiyaContributionEntity>,
        currencySymbol: String
    ): String {
        val pot = JamiyaMath.potPerRound(circle.contributionAmount, members.size)
        val totalRounds = JamiyaMath.totalRounds(members.size).coerceAtLeast(1)
        val round = circle.currentRound.coerceIn(1, totalRounds)
        val recipient = JamiyaMath.recipientForRound(members, round)
        val paidIds = contributions.filter { it.roundNumber == round }.map { it.memberId }.toSet()
        val paidCount = members.count { it.id in paidIds }
        val contribStr = Formatters.moneyWithCode(circle.contributionAmount, currencySymbol)
        val potStr = Formatters.moneyWithCode(pot, currencySymbol)

        val locale = context.resources.configuration.locales[0]
        val dateStr = DateUtils.formatExpenseDate(DateUtils.todayIso(), locale)
        val terms = if (circle.frequency == "WEEKLY") R.string.jamiya_share_terms_weekly
            else R.string.jamiya_share_terms_monthly

        // WhatsApp markdown: *bold* for the title, round, and section header. Light emojis and
        // blank lines give the message structure; amounts stay as "SAR" text (moneyWithCode).
        val sb = StringBuilder()
        sb.appendLine("${circle.emoji} *${context.getString(R.string.jamiya_share_header, circle.name)}*")
        sb.appendLine("🗓️ ${context.getString(R.string.jamiya_share_asof, dateStr)}")
        sb.appendLine()
        sb.appendLine("💵 ${context.getString(terms, contribStr, members.size)}")
        sb.appendLine("🪙 ${context.getString(R.string.jamiya_share_pot, potStr)}")
        sb.appendLine()
        val roundLine = if (recipient != null)
            context.getString(R.string.jamiya_share_round, round, totalRounds, recipient.name)
        else
            context.getString(R.string.jamiya_share_round_norecipient, round, totalRounds)
        sb.appendLine("🎯 *$roundLine*")
        sb.appendLine("✔️ ${context.getString(R.string.jamiya_share_paid, paidCount, members.size)}")
        sb.appendLine()
        sb.appendLine("*${context.getString(R.string.jamiya_share_turns)}*")
        members.forEach { m ->
            val marker = if (m.id in paidIds) "✅" else "⏳"
            sb.appendLine("${m.payoutPosition}. ${m.name}  $marker")
        }
        sb.appendLine()
        sb.appendLine("— ${context.getString(R.string.jamiya_share_footer)}")
        sb.append(playLink(context))
        return sb.toString()
    }

    /** Message tailored to one member: their collection turn, this round's status, the terms. */
    fun buildMemberMessage(
        context: Context,
        circle: JamiyaCircleEntity,
        member: JamiyaMemberEntity,
        members: List<JamiyaMemberEntity>,
        contributions: List<JamiyaContributionEntity>,
        round: Int,
        currencySymbol: String
    ): String {
        val pot = JamiyaMath.potPerRound(circle.contributionAmount, members.size)
        val isPaid = contributions.any { it.roundNumber == round && it.memberId == member.id }
        val contribStr = Formatters.moneyWithCode(circle.contributionAmount, currencySymbol)
        val potStr = Formatters.moneyWithCode(pot, currencySymbol)
        val status = if (isPaid) context.getString(R.string.jamiya_share_member_paid)
            else context.getString(R.string.jamiya_share_member_due, contribStr)

        val locale = context.resources.configuration.locales[0]
        val dateStr = DateUtils.formatExpenseDate(DateUtils.todayIso(), locale)

        val sb = StringBuilder()
        sb.appendLine(context.getString(R.string.jamiya_share_member_greeting, member.name))
        sb.appendLine(
            context.getString(
                R.string.jamiya_share_member_body,
                circle.name,
                member.payoutPosition,
                potStr,
                round,
                status,
                contribStr
            )
        )
        sb.appendLine()
        sb.appendLine("🗓️ ${context.getString(R.string.jamiya_share_asof, dateStr)}")
        sb.appendLine("— ${context.getString(R.string.jamiya_share_footer)}")
        sb.append(playLink(context))
        return sb.toString()
    }

    // ── Send / share intents ───────────────────────────────────────────────────

    /**
     * Opens a WhatsApp (or WhatsApp Business) chat with [phone], message prefilled. [phone] is
     * the stored "+<code><digits>" form; WhatsApp's click-to-chat wants E.164 digits with no
     * leading '+'. Returns false if neither WhatsApp variant is installed (caller can fall back
     * to SMS).
     */
    fun sendWhatsApp(context: Context, phone: String, text: String): Boolean {
        val e164 = phone.filter { it.isDigit() }
        if (e164.isBlank()) return false
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$e164&text=${Uri.encode(text)}")
        for (pkg in listOf("com.whatsapp", "com.whatsapp.w4b")) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, uri).apply { setPackage(pkg) })
                return true
            } catch (_: ActivityNotFoundException) { /* try next package */ }
        }
        return false
    }

    /**
     * Generic text share sheet — used for the group summary, so the organiser can pick their
     * circle's WhatsApp group (which can't be targeted by phone number). Returns false only if
     * no app can handle a text share at all.
     */
    fun shareText(context: Context, text: String): Boolean {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        return try {
            context.startActivity(Intent.createChooser(send, null))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
