package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.SplitRepository

private val GROUP_EMOJIS = listOf("🤝", "🏖️", "🏠", "🍽️", "✈️", "🎉", "🏕️", "💼", "🎓", "⚽")

private val PALETTE = SplitRepository.PALETTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSplitGroupSheet(
    ownerName: String,
    onDismiss: () -> Unit,
    onSave: (name: String, emoji: String, extraMembers: List<Pair<String, String>>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf(GROUP_EMOJIS[0]) }

    // Extra members beyond the device owner
    val extraMembers = remember { mutableStateListOf<Pair<String, String>>() }
    var newMemberName by remember { mutableStateOf("") }

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
                .padding(bottom = 32.dp)
        ) {
            Text(
                stringResource(R.string.split_new_group),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.split_new_group_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            // Emoji picker
            Text(stringResource(R.string.split_group_emoji), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GROUP_EMOJIS.take(5).forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (emoji == selectedEmoji) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .then(
                                if (emoji == selectedEmoji)
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GROUP_EMOJIS.drop(5).forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (emoji == selectedEmoji) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .then(
                                if (emoji == selectedEmoji)
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Group name
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text(stringResource(R.string.split_group_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Members
            Text(stringResource(R.string.split_members_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            // Owner row (always first)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(android.graphics.Color.parseColor(PALETTE[0])), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        ownerName.take(1).uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "$ownerName (${stringResource(R.string.family_me_label)})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            // Extra members
            extraMembers.forEachIndexed { index, (name, colorHex) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val color = try { Color(android.graphics.Color.parseColor(colorHex)) }
                    catch (_: Exception) { MaterialTheme.colorScheme.primary }
                    Box(
                        modifier = Modifier.size(36.dp).background(color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            name.take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    IconButton(onClick = { extraMembers.removeAt(index) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.family_delete_member),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Add member input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = { newMemberName = it },
                    label = { Text(stringResource(R.string.split_add_member_hint)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                IconButton(
                    onClick = {
                        val trimmed = newMemberName.trim()
                        if (trimmed.isNotBlank()) {
                            val color = PALETTE[(extraMembers.size + 1) % PALETTE.size]
                            extraMembers += trimmed to color
                            newMemberName = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.split_add_member_hint))
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val name = groupName.trim()
                    if (name.isNotBlank()) {
                        onSave(name, selectedEmoji, extraMembers.toList())
                    }
                },
                enabled = groupName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.split_create_group))
            }
        }
    }
}
