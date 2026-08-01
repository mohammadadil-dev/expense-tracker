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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.SharedBillMemberDraft

private val GROUP_EMOJIS = listOf("🏠", "🏢", "🏘️", "🏬", "💡", "🚪", "🧾", "🛏️")

private data class GroupMemberUi(val name: String, val isMe: Boolean, val personCount: Int = 1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSharedBillGroupSheet(
    ownerName: String,
    onDismiss: () -> Unit,
    onCreate: (name: String, emoji: String, splitMode: Int, members: List<SharedBillMemberDraft>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf(GROUP_EMOJIS[0]) }
    var flatMode by remember { mutableStateOf(false) }
    var newMemberName by remember { mutableStateOf("") }
    var duplicateError by remember { mutableStateOf(false) }
    val members = remember {
        mutableStateListOf(GroupMemberUi(name = ownerName.ifBlank { "Me" }, isMe = true))
    }
    val myFlatLabel = stringResource(R.string.shared_bill_my_flat)
    val meLabel = stringResource(R.string.family_me_label)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(stringResource(R.string.shared_bill_new_group), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            // Emoji badge picker
            Text(stringResource(R.string.split_group_emoji), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GROUP_EMOJIS.take(4).forEach { EmojiBadge(it, it == selectedEmoji) { selectedEmoji = it } }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GROUP_EMOJIS.drop(4).forEach { EmojiBadge(it, it == selectedEmoji) { selectedEmoji = it } }
            }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.shared_bill_group_name_hint)) },
                placeholder = { Text(stringResource(R.string.shared_bill_group_name_example)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Split by
            Text(stringResource(R.string.shared_bill_split_by), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !flatMode, onClick = { flatMode = false }, label = { Text(stringResource(R.string.shared_bill_mode_person)) })
                FilterChip(selected = flatMode, onClick = { flatMode = true }, label = { Text(stringResource(R.string.shared_bill_mode_flat)) })
            }
            Spacer(Modifier.height(16.dp))

            // Members / flats
            Text(
                stringResource(if (flatMode) R.string.shared_bill_flats else R.string.shared_bill_people),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(8.dp))
            members.forEachIndexed { index, m ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            m.isMe && flatMode -> "$myFlatLabel ($meLabel)"
                            m.isMe -> "${m.name} ($meLabel)"
                            else -> m.name
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    if (flatMode) {
                        IconButton(onClick = { if (m.personCount > 1) members[index] = m.copy(personCount = m.personCount - 1) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(15.dp))
                        }
                        Text(m.personCount.toString(), style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { members[index] = m.copy(personCount = m.personCount + 1) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        }
                    }
                    if (!m.isMe) {
                        IconButton(onClick = { members.removeAt(index) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = { newMemberName = it; duplicateError = false },
                    label = { Text(stringResource(if (flatMode) R.string.shared_bill_flat_hint else R.string.split_add_member_hint)) },
                    singleLine = true,
                    isError = duplicateError,
                    supportingText = if (duplicateError) {
                        { Text(stringResource(R.string.split_member_duplicate), color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        val n = newMemberName.trim()
                        when {
                            n.isBlank() -> {}
                            members.any { it.name.equals(n, ignoreCase = true) } -> duplicateError = true
                            else -> {
                                members.add(GroupMemberUi(name = n, isMe = false))
                                newMemberName = ""
                                duplicateError = false
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.split_add_member)) }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val finalName = name.trim()
                    if (finalName.isNotBlank()) {
                        val drafts = members.map {
                            SharedBillMemberDraft(
                                name = if (it.isMe && flatMode) myFlatLabel else it.name.trim(),
                                isMe = it.isMe,
                                daysAbsent = 0,
                                personCount = if (flatMode) it.personCount else 1
                            )
                        }
                        onCreate(finalName, selectedEmoji, if (flatMode) 1 else 0, drafts)
                    }
                },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.shared_bill_create_group))
            }
        }
    }
}

@Composable
private fun EmojiBadge(emoji: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .then(if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
    }
}
