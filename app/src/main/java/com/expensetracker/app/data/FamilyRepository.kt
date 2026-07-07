package com.expensetracker.app.data

import android.util.Base64
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs

/**
 * Repository for Family / Couple Mode data.
 *
 * ## Invite Code
 * The invite code encodes a compact JSON snapshot of the current member list
 * (names + colors + emojis, but NOT expense data) as Base64, displayed in a
 * user-friendly `XXXX-XXXX-XXXX` format.  The receiving device decodes it and
 * imports the members — it does NOT import any expense rows.  Actual expense
 * data is shared separately via Drive backup / restore.
 *
 * Example flow:
 *   1. Person A enables Family Mode → their profile "Mohammad" is auto-created.
 *   2. Person A taps "Add member" → enters "Sara" → saves.
 *   3. Person A taps "Share invite code" → code displayed as `A3F9-KZ2M-B7QX`.
 *   4. Person B installs the app, goes to Settings → Family Mode → "Join household".
 *   5. Person B enters the code → "Mohammad" and "Sara" are imported.
 *   6. Both devices use Drive backup/restore to share expense data.
 */
class FamilyRepository(private val dao: FamilyMemberDao) {

    // ── Flows ────────────────────────────────────────────────────────────────

    val allMembers: Flow<List<FamilyMemberEntity>> = dao.getAllMembers()

    // ── CRUD ────────────────────────────────────────────────────────────────

    suspend fun addMember(name: String, colorHex: String, emoji: String, isMe: Boolean = false): Long =
        dao.insert(FamilyMemberEntity(name = name, colorHex = colorHex, emoji = emoji, isMe = isMe))

    suspend fun updateMember(member: FamilyMemberEntity) = dao.update(member)

    suspend fun deleteMember(member: FamilyMemberEntity) = dao.delete(member)

    /** Ensure the owner profile ("Me") exists. Called when Family Mode is first enabled. */
    suspend fun ensureOwnerProfile(ownerName: String) {
        if (dao.getMyProfile() == null) {
            dao.insert(
                FamilyMemberEntity(
                    name     = ownerName.ifBlank { "Me" },
                    colorHex = PALETTE[0],
                    isMe     = true
                )
            )
        }
    }

    /** Delete all members and reset to clean state (called when Family Mode is disabled). */
    suspend fun reset() = dao.deleteAll()

    // ── Invite code ──────────────────────────────────────────────────────────

    /** Encode the current member list as a shareable invite code string. */
    suspend fun generateInviteCode(): String {
        val members = dao.getAllMembersSnapshot()

        val membersArray = JSONArray()
        members.forEach { m ->
            membersArray.put(
                JSONObject().apply {
                    put("name",     m.name)
                    put("colorHex", m.colorHex)
                    put("emoji",    m.emoji)
                    put("isMe",     m.isMe)
                }
            )
        }

        val payload = JSONObject().apply {
            put("members", membersArray)
        }

        val json    = payload.toString()
        val encoded = Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        // Format as XXXX-XXXX-… groups for readability; full Base64 is preserved
        return encoded.chunked(4).joinToString("-")
    }

    /**
     * Decode an invite code and import the members it contains.
     * Existing members (matched by lowercase name) are skipped.
     *
     * @return The list of newly imported member names, or throws on invalid code.
     */
    suspend fun importFromCode(code: String): List<String> {
        val raw     = code.replace("-", "").replace(" ", "")
        val decoded = String(Base64.decode(raw, Base64.NO_WRAP), Charsets.UTF_8)
        val payload = JSONObject(decoded)
        val membersArray: JSONArray = payload.getJSONArray("members")

        val existing = dao.getAllMembersSnapshot().map { it.name.lowercase() }.toSet()
        val imported = mutableListOf<String>()

        for (i in 0 until membersArray.length()) {
            val m        = membersArray.getJSONObject(i)
            val name     = m.getString("name")
            val colorHex = m.optString("colorHex", "")
            val emoji    = m.optString("emoji", "")

            if (name.lowercase() !in existing) {
                val autoColor = PALETTE[abs(name.hashCode()) % PALETTE.size]
                dao.insert(
                    FamilyMemberEntity(
                        name     = name,
                        colorHex = colorHex.ifBlank { autoColor },
                        emoji    = emoji,
                        isMe     = false   // imported members are never "me" on this device
                    )
                )
                imported += name
            }
        }
        return imported
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    companion object {
        /** Colour palette for auto-assigning member avatar colours. */
        val PALETTE = listOf(
            "#4CAF50", "#2196F3", "#E91E63", "#FF9800",
            "#9C27B0", "#00BCD4", "#F44336", "#795548"
        )
    }
}
