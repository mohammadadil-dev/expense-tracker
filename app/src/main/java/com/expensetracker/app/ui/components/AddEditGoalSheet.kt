package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.GoalEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.util.Formatters

private val GOAL_EMOJIS = listOf(
    "🎯", "🏖️", "📱", "🎓", "🏠", "🚗", "💍", "🌍", "🎮", "🏋️",
    "🎉", "📚", "💊", "✈️", "🍽️", "👶", "🐕", "🎸", "🏊", "🛋️"
)

/**
 * Bottom sheet for creating a new savings goal or editing an existing one.
 *
 * [existing] — pass a [GoalEntity] to pre-fill fields for editing; null for a new goal.
 * [onSave]   — called with (name, emoji, targetAmount, targetDate).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditGoalSheet(
    existing: GoalEntity? = null,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (name: String, emoji: String, targetAmount: Double, targetDate: String) -> Unit
) {
    var name         by remember { mutableStateOf(existing?.name ?: "") }
    var selectedEmoji by remember { mutableStateOf(existing?.emoji ?: "🎯") }
    var amountText   by remember { mutableStateOf(existing?.targetAmount?.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() } ?: "") }
    var targetDate   by remember { mutableStateOf(existing?.targetDate ?: "") }

    var nameError   by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(if (existing == null) R.string.goals_add else R.string.goals_edit),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.goals_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(Modifier.height(20.dp))

            // ── Emoji picker ──────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.goals_emoji_label),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GOAL_EMOJIS.forEach { emoji ->
                    val isSelected = emoji == selectedEmoji
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) AccentIndigo.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) AccentIndigo else androidx.compose.ui.graphics.Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))

            // ── Goal name ─────────────────────────────────────────────────────
            Text(stringResource(R.string.goals_name_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                placeholder = { Text(stringResource(R.string.goals_name_hint)) },
                isError = nameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError) {
                Text(
                    text = stringResource(R.string.goals_name_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(14.dp))

            // ── Target amount ─────────────────────────────────────────────────
            Text(stringResource(R.string.goals_target_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it; amountError = false },
                placeholder = { Text(stringResource(R.string.goals_target_hint)) },
                leadingIcon = {
                    if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
                        androidx.compose.material3.Icon(
                            painter = androidx.compose.ui.res.painterResource(R.drawable.ic_saudi_riyal),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(currencySymbol, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                isError = amountError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            if (amountError) {
                Text(
                    text = stringResource(R.string.goals_target_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(14.dp))

            // ── Target date (optional) ────────────────────────────────────────
            Text(stringResource(R.string.goals_date_label), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = targetDate,
                onValueChange = { targetDate = it },
                placeholder = { Text("yyyy-MM-dd  " + stringResource(R.string.goals_date_optional)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // ── Save button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val amount = amountText.trim().toDoubleOrNull()
                    if (trimmedName.isEmpty()) { nameError = true; return@Button }
                    if (amount == null || amount <= 0.0) { amountError = true; return@Button }
                    onSave(trimmedName, selectedEmoji, amount, targetDate.trim())
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.done),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
