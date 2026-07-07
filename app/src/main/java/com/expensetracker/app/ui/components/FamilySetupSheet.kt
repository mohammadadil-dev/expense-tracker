package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.FamilyMemberEntity

private val PALETTE = listOf(
    "#4CAF50", "#2196F3", "#E91E63", "#FF9800",
    "#9C27B0", "#00BCD4", "#F44336", "#795548"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilySetupSheet(
    members: List<FamilyMemberEntity>,
    onDismiss: () -> Unit,
    onAddMember: (name: String, colorHex: String, emoji: String) -> Unit,
    onUpdateMember: (FamilyMemberEntity) -> Unit,
    onDeleteMember: (FamilyMemberEntity) -> Unit,
    onGenerateCode: (onResult: (String) -> Unit) -> Unit,
    onImportCode: (code: String, onSuccess: (List<String>) -> Unit, onError: (String) -> Unit) -> Unit,
    onDisableFamilyMode: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var editingMember by remember { mutableStateOf<FamilyMemberEntity?>(null) }
    var memberToDelete by remember { mutableStateOf<FamilyMemberEntity?>(null) }
    var showDisableConfirm by remember { mutableStateOf(false) }
    var inviteCode by remember { mutableStateOf("") }
    var showCodeDialog by remember { mutableStateOf(false) }
    var showImportForm by remember { mutableStateOf(false) }
    var importCodeText by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf("") }
    var importSuccess by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                stringResource(R.string.family_mode_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.family_mode_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // ── Member list ───────────────────────────────────────────────────
            members.forEach { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarColor = try {
                        Color(android.graphics.Color.parseColor(member.colorHex))
                    } catch (_: Exception) { MaterialTheme.colorScheme.primary }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val label = member.emoji.ifBlank { member.name.take(1).uppercase() }
                        Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.name, style = MaterialTheme.typography.bodyLarge)
                            if (member.isMe) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    stringResource(R.string.family_me_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Edit (all members)
                    IconButton(
                        onClick = { editingMember = member },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.family_edit_member),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Delete only non-owner members
                    if (!member.isMe) {
                        IconButton(
                            onClick = { memberToDelete = member },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.family_delete_member),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Add member button ─────────────────────────────────────────────
            TextButton(
                onClick = { showAddForm = true; editingMember = null },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.family_add_member))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // ── Invite code section ───────────────────────────────────────────
            Text(
                stringResource(R.string.family_share_code_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.family_share_code_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    onGenerateCode { code ->
                        inviteCode = code
                        showCodeDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.family_generate_code))
            }

            Spacer(Modifier.height(12.dp))

            // ── Join with code ────────────────────────────────────────────────
            Text(
                stringResource(R.string.family_join_code_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            if (showImportForm) {
                OutlinedTextField(
                    value = importCodeText,
                    onValueChange = { importCodeText = it; importError = ""; importSuccess = "" },
                    label = { Text(stringResource(R.string.family_enter_code_hint)) },
                    isError = importError.isNotEmpty(),
                    supportingText = when {
                        importError.isNotEmpty() -> { { Text(importError, color = MaterialTheme.colorScheme.error) } }
                        importSuccess.isNotEmpty() -> { { Text(importSuccess, color = Color(0xFF4CAF50)) } }
                        else -> null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { showImportForm = false; importCodeText = "" }) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        onImportCode(
                            importCodeText.trim(),
                            { names ->
                                importSuccess = if (names.isEmpty())
                                    context.getString(R.string.family_import_up_to_date)
                                else
                                    context.getString(R.string.family_import_added, names.joinToString(", "))
                                importCodeText = ""
                            },
                            { err -> importError = err }
                        )
                    }) { Text(stringResource(R.string.family_join)) }
                }
            } else {
                TextButton(
                    onClick = { showImportForm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.family_join_with_code))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // ── Disable family mode ───────────────────────────────────────────
            TextButton(
                onClick = { showDisableConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.GroupOff, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.family_disable))
            }
        }
    }

    // ── Add / Edit member dialog ──────────────────────────────────────────────
    val memberDialogOpen = showAddForm || editingMember != null
    if (memberDialogOpen) {
        var memberName by remember(editingMember) {
            mutableStateOf(editingMember?.name ?: "")
        }
        var memberEmoji by remember(editingMember) {
            mutableStateOf(editingMember?.emoji ?: "")
        }
        var memberColor by remember(editingMember) {
            mutableStateOf(editingMember?.colorHex ?: PALETTE[0])
        }

        AlertDialog(
            onDismissRequest = { showAddForm = false; editingMember = null },
            title = {
                Text(
                    if (editingMember == null) stringResource(R.string.family_add_member)
                    else stringResource(R.string.family_edit_member)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text(stringResource(R.string.family_member_name_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = memberEmoji,
                        onValueChange = { if (it.length <= 2) memberEmoji = it },
                        label = { Text(stringResource(R.string.family_member_emoji_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        stringResource(R.string.family_pick_color),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PALETTE.forEach { hex ->
                            val color = try { Color(android.graphics.Color.parseColor(hex)) }
                            catch (_: Exception) { Color.Gray }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (hex == memberColor)
                                            Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { memberColor = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = memberName.trim()
                        if (name.isBlank()) return@TextButton
                        val existing = editingMember
                        if (existing == null) {
                            onAddMember(name, memberColor, memberEmoji.trim())
                        } else {
                            onUpdateMember(existing.copy(name = name, colorHex = memberColor, emoji = memberEmoji.trim()))
                        }
                        showAddForm = false
                        editingMember = null
                    }
                ) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddForm = false; editingMember = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // ── Delete member confirmation ────────────────────────────────────────────
    memberToDelete?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            title = { Text(stringResource(R.string.family_delete_confirm_title)) },
            text = { Text(stringResource(R.string.family_delete_confirm_body, member.name)) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteMember(member)
                    memberToDelete = null
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    // ── Disable family mode confirmation ──────────────────────────────────────
    if (showDisableConfirm) {
        AlertDialog(
            onDismissRequest = { showDisableConfirm = false },
            title = { Text(stringResource(R.string.family_disable_confirm_title)) },
            text = { Text(stringResource(R.string.family_disable_confirm_body)) },
            confirmButton = {
                TextButton(onClick = {
                    onDisableFamilyMode()
                    showDisableConfirm = false
                    onDismiss()
                }) { Text(stringResource(R.string.family_disable), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDisableConfirm = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    // ── Invite code display dialog ────────────────────────────────────────────
    if (showCodeDialog && inviteCode.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showCodeDialog = false },
            title = { Text(stringResource(R.string.family_invite_code_title)) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        inviteCode,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.family_invite_code_desc),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    clipboard.setText(AnnotatedString(inviteCode))
                    showCodeDialog = false
                }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.family_copy_code))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCodeDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }
}
