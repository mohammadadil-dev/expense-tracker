package com.expensetracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.JamiyaCircleEntity
import com.expensetracker.app.data.JamiyaMemberEntity
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.CountryCodePhoneField
import com.expensetracker.app.ui.components.CountryPickerDialog
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.buildFullPhone
import com.expensetracker.app.ui.components.defaultCountryEntry
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonViolet
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.JamiyaMath
import com.expensetracker.app.util.JamiyaShare
import com.expensetracker.app.util.SmsFallback
import com.expensetracker.app.viewmodel.JamiyaViewModel

/**
 * One circle's rounds view: pick the round, see whose turn it is to collect the pot, tick who
 * has paid their contribution, and nudge whoever hasn't (via the device SMS app — the same
 * one-tap-confirmed [SmsFallback] path Khata reminders fall back to). Pure record-keeping: no
 * money moves through the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamiyaDetailScreen(
    circleId: Long,
    viewModel: JamiyaViewModel,
    currencySymbol: String,
    onBack: () -> Unit
) {
    LaunchedEffect(circleId) { viewModel.selectCircle(circleId) }

    val circle by viewModel.selectedCircle.collectAsState()
    val members by viewModel.selectedCircleMembers.collectAsState()
    val contributions by viewModel.selectedCircleContributions.collectAsState()
    val context = LocalContext.current

    var showAddMember by remember { mutableStateOf(false) }
    var showEditCircle by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    val c = circle ?: return
    val memberCount = members.size
    val totalRounds = JamiyaMath.totalRounds(memberCount).coerceAtLeast(1)
    val round = c.currentRound.coerceIn(1, totalRounds)
    val pot = JamiyaMath.potPerRound(c.contributionAmount, memberCount)
    val recipient = JamiyaMath.recipientForRound(members, round)
    val paidMemberIds = remember(contributions, round) {
        contributions.filter { it.roundNumber == round }.map { it.memberId }.toSet()
    }
    val collected = JamiyaMath.collectedForRound(contributions, round)
    val circleComplete = JamiyaMath.isCircleComplete(members, contributions)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBlobBackground(
            blobColors = listOf(NeonViolet, NeonCyan, AccentIndigo),
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(c.name) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                        }
                    },
                    actions = {
                        if (members.isNotEmpty()) {
                            IconButton(onClick = {
                                JamiyaShare.shareText(
                                    context,
                                    JamiyaShare.buildSummary(context, c, members, contributions, currencySymbol)
                                )
                            }) {
                                Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.jamiya_share), tint = AccentIndigo)
                            }
                        }
                        IconButton(onClick = { showEditCircle = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = AccentIndigo)
                        }
                        IconButton(onClick = { confirmDelete = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = DangerRed)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddMember = true },
                    containerColor = AccentIndigo
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = stringResource(R.string.jamiya_add_member))
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    RoundHeaderCard(
                        round = round,
                        totalRounds = totalRounds,
                        pot = pot,
                        collected = collected,
                        currencySymbol = currencySymbol,
                        recipientName = recipient?.name,
                        canPrev = round > 1,
                        canNext = round < totalRounds,
                        onPrev = { viewModel.setCurrentRound(c, round - 1) },
                        onNext = { viewModel.setCurrentRound(c, round + 1) }
                    )
                }

                // Completion banner — the "what do I do next?" answer once every round has been
                // collected. Also shown (in its completed state) for a circle already closed.
                if (members.isNotEmpty() && (circleComplete || c.isClosed)) {
                    item {
                        CompletionCard(
                            closed = c.isClosed,
                            onMarkComplete = { viewModel.setClosed(c, true) },
                            onReopen = { viewModel.setClosed(c, false) }
                        )
                    }
                }

                if (members.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.jamiya_no_members),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    itemsIndexed(members, key = { _, m -> m.id }) { index, member ->
                        val isPaid = member.id in paidMemberIds
                        val isRecipient = member.payoutPosition == round
                        MemberRoundRow(
                            member = member,
                            isPaid = isPaid,
                            isRecipient = isRecipient,
                            turnNumber = index + 1,
                            contributionLabel = Formatters.money(c.contributionAmount, currencySymbol),
                            // Reorder collection turns while the circle is open. Moving a member
                            // reassigns everyone's turn to their new list order (reorderPayout
                            // renumbers 1..n), so the payout rotation is whatever order you set.
                            canReorder = !c.isClosed && members.size > 1,
                            canMoveUp = index > 0,
                            canMoveDown = index < members.size - 1,
                            onMoveUp = {
                                val ids = members.map { it.id }.toMutableList()
                                val tmp = ids[index]; ids[index] = ids[index - 1]; ids[index - 1] = tmp
                                viewModel.reorderPayout(c.id, ids)
                            },
                            onMoveDown = {
                                val ids = members.map { it.id }.toMutableList()
                                val tmp = ids[index]; ids[index] = ids[index + 1]; ids[index + 1] = tmp
                                viewModel.reorderPayout(c.id, ids)
                            },
                            onToggle = {
                                if (isPaid) viewModel.markUnpaid(c.id, member.id, round)
                                else viewModel.markPaid(c, member.id, round)
                            },
                            onRemind = {
                                // Personalised WhatsApp message (their turn, this round's
                                // status, the terms); falls back to SMS if WhatsApp isn't
                                // installed. Uses "SAR" text, not the Riyal icon.
                                val msg = JamiyaShare.buildMemberMessage(
                                    context, c, member, members, contributions, round, currencySymbol
                                )
                                val sent = JamiyaShare.sendWhatsApp(context, member.phone, msg)
                                if (!sent) SmsFallback.send(context, member.phone, msg)
                            }
                        )
                    }
                }
                item { Spacer(Modifier.height(96.dp)) }
            }
        }
    }

    if (showAddMember) {
        AddMemberDialog(
            existingNames = members.map { it.name },
            onDismiss = { showAddMember = false },
            onAdd = { name, phone, isMe ->
                viewModel.addMember(c.id, name, phone, isMe = isMe)
                showAddMember = false
            }
        )
    }

    if (showEditCircle) {
        EditCircleDialog(
            circle = c,
            onDismiss = { showEditCircle = false },
            onSave = { name, amount, frequency ->
                viewModel.updateCircle(c.copy(name = name, contributionAmount = amount, frequency = frequency))
                showEditCircle = false
            }
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.jamiya_delete_circle_title)) },
            text = { Text(stringResource(R.string.jamiya_delete_circle_confirm, c.name)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.deleteCircle(c)
                    onBack()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun CompletionCard(
    closed: Boolean,
    onMarkComplete: () -> Unit,
    onReopen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (closed) Icons.Filled.CheckCircle else Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(if (closed) R.string.jamiya_completed_title else R.string.jamiya_complete_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(if (closed) R.string.jamiya_completed_body else R.string.jamiya_complete_body),
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            if (closed) {
                TextButton(onClick = onReopen) { Text(stringResource(R.string.jamiya_reopen)) }
            } else {
                Button(
                    onClick = onMarkComplete,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) { Text(stringResource(R.string.jamiya_mark_complete)) }
            }
        }
    }
}

@Composable
private fun RoundHeaderCard(
    round: Int,
    totalRounds: Int,
    pot: Double,
    collected: Double,
    currencySymbol: String,
    recipientName: String?,
    canPrev: Boolean,
    canNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPrev, enabled = canPrev) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = stringResource(R.string.jamiya_prev_round))
                }
                Text(
                    text = stringResource(R.string.jamiya_round_of, round, totalRounds),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                IconButton(onClick = onNext, enabled = canNext) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = stringResource(R.string.jamiya_next_round))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.jamiya_pot_label),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )
            MoneyText(
                formatted = Formatters.money(pot, currencySymbol),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            if (recipientName != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = AccentIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.jamiya_collects_this_round, recipientName),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TextPrimary
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            val progress = if (pot > 0.0) (collected / pot).toFloat().coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                color = SuccessGreen
            )
            Spacer(Modifier.height(4.dp))
            MoneyText(
                formatted = stringResource(
                    R.string.jamiya_collected_of,
                    Formatters.money(collected, currencySymbol),
                    Formatters.money(pot, currencySymbol)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun MemberRoundRow(
    member: JamiyaMemberEntity,
    isPaid: Boolean,
    isRecipient: Boolean,
    turnNumber: Int,
    contributionLabel: String,
    canReorder: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggle: () -> Unit,
    onRemind: () -> Unit
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPaid) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (isPaid) SuccessGreen else TextMuted,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = TextPrimary
                    )
                    if (isRecipient) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = stringResource(R.string.jamiya_recipient_badge),
                            tint = AccentIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                val turnPrefix = stringResource(R.string.jamiya_turn, turnNumber)
                val statusSuffix = if (isPaid) stringResource(R.string.jamiya_paid)
                    else stringResource(R.string.jamiya_owes, contributionLabel)
                MoneyText(
                    formatted = "$turnPrefix · $statusSuffix",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPaid) SuccessGreen else TextMuted
                )
            }
            if (member.phone.isNotBlank()) {
                IconButton(onClick = onRemind) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = stringResource(R.string.jamiya_share_member_cd),
                        tint = AccentIndigo
                    )
                }
            }
            // Up/down controls to set this member's collection turn (payout order).
            if (canReorder) {
                Column {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = canMoveUp,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.jamiya_move_up),
                            tint = if (canMoveUp) AccentIndigo else TextMuted
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        enabled = canMoveDown,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.jamiya_move_down),
                            tint = if (canMoveDown) AccentIndigo else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCircleDialog(
    circle: JamiyaCircleEntity,
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, frequency: String) -> Unit
) {
    var name by remember { mutableStateOf(circle.name) }
    var amountText by remember {
        mutableStateOf(
            if (circle.contributionAmount % 1.0 == 0.0) circle.contributionAmount.toLong().toString()
            else circle.contributionAmount.toString()
        )
    }
    var frequency by remember { mutableStateOf(circle.frequency) }

    val amount = amountText.replace(",", "").trim().toDoubleOrNull() ?: 0.0
    val canSave = name.isNotBlank() && amount > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.jamiya_edit_circle)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.jamiya_circle_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(stringResource(R.string.jamiya_contribution_amount)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = frequency == "MONTHLY",
                        onClick = { frequency = "MONTHLY" },
                        label = { Text(stringResource(R.string.jamiya_monthly)) }
                    )
                    FilterChip(
                        selected = frequency == "WEEKLY",
                        onClick = { frequency = "WEEKLY" },
                        label = { Text(stringResource(R.string.jamiya_weekly)) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(enabled = canSave, onClick = { onSave(name.trim(), amount, frequency) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun AddMemberDialog(
    existingNames: List<String>,
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, isMe: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf(defaultCountryEntry()) }
    var localNumber by remember { mutableStateOf("") }
    var isMe by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }

    val trimmed = name.trim()
    // Members must be unique within a circle (case-insensitive) — two "adil"s made the rotation
    // and paid/unpaid tracking ambiguous.
    val isDuplicate = trimmed.isNotBlank() && existingNames.any { it.trim().equals(trimmed, ignoreCase = true) }
    val canAdd = trimmed.isNotBlank() && !isDuplicate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.jamiya_add_member)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.jamiya_member_name)) },
                    singleLine = true,
                    isError = isDuplicate,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isDuplicate) {
                    Text(
                        text = stringResource(R.string.jamiya_duplicate_member),
                        style = MaterialTheme.typography.bodySmall,
                        color = DangerRed
                    )
                }
                Text(
                    text = stringResource(R.string.jamiya_member_phone),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                CountryCodePhoneField(
                    selectedCountry = country,
                    onCountryClick = { showCountryPicker = true },
                    localNumber = localNumber,
                    onLocalNumberChange = { localNumber = it }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isMe, onCheckedChange = { isMe = it })
                    Text(stringResource(R.string.jamiya_this_is_me), color = TextPrimary)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canAdd,
                onClick = { onAdd(trimmed, buildFullPhone(country, localNumber), isMe) }
            ) { Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )

    if (showCountryPicker) {
        CountryPickerDialog(
            selectedCountry = country,
            onSelect = { country = it; showCountryPicker = false },
            onDismiss = { showCountryPicker = false }
        )
    }
}
