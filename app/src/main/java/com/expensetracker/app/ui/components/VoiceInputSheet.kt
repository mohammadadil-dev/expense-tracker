package com.expensetracker.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.VoiceExpenseParser
import com.expensetracker.app.util.VoiceInputManager
import java.util.Locale

/**
 * Result the caller receives when the user confirms a voice-parsed expense.
 */
data class VoiceExpenseResult(
    val amount: Double?,
    val description: String,
    val suggestedCategoryKey: String?
)

// ── State machine (must be file-level; sealed classes cannot be local) ────────
private sealed class VoiceUiState {
    object Listening : VoiceUiState()
    data class Result(val parsed: VoiceExpenseParser.Result, val raw: String) : VoiceUiState()
    data class Error(val message: String) : VoiceUiState()
}

/**
 * Bottom sheet that handles the full voice-entry flow:
 *  Listening → (result preview OR error) → Confirm / Try Again
 *
 * The sheet starts listening automatically when opened.
 *
 * @param locale   App locale — passed to the speech recogniser.
 * @param onConfirm  Called with parsed result; caller pre-fills AddEditExpenseSheet.
 * @param onDismiss  Called when user dismisses without confirming.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceInputSheet(
    sheetState: SheetState,
    locale: Locale = Locale.getDefault(),
    onConfirm: (VoiceExpenseResult) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var uiState by remember { mutableStateOf<VoiceUiState>(VoiceUiState.Listening) }
    val manager = remember { VoiceInputManager(context) }

    fun startListening() {
        uiState = VoiceUiState.Listening
        manager.start(
            locale = locale,
            onResult = { text ->
                uiState = if (text.isBlank()) {
                    VoiceUiState.Error(context.getString(R.string.voice_no_speech))
                } else {
                    VoiceUiState.Result(VoiceExpenseParser.parse(text), text)
                }
            },
            onError = { code ->
                uiState = VoiceUiState.Error(VoiceInputManager.errorLabel(code))
            }
        )
    }

    // Start immediately when sheet opens
    LaunchedEffect(Unit) { startListening() }

    // Clean up recognizer when sheet closes
    DisposableEffect(Unit) { onDispose { manager.destroy() } }

    // ── Pulse animation (shown while listening) ───────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.22f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // ── Sheet ─────────────────────────────────────────────────────────────────
    ModalBottomSheet(
        onDismissRequest = { manager.destroy(); onDismiss() },
        sheetState       = sheetState,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Handle ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .background(TextMuted.copy(alpha = 0.3f), CircleShape)
            )
            Spacer(Modifier.height(20.dp))

            when (val state = uiState) {

                // ── LISTENING STATE ───────────────────────────────────────────
                is VoiceUiState.Listening -> {
                    Text(
                        text  = stringResource(R.string.voice_listening),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(32.dp))

                    // Pulsing mic circle
                    Box(contentAlignment = Alignment.Center) {
                        // Outer pulse ring
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .scale(pulseScale)
                                .background(AccentIndigo.copy(alpha = 0.12f), CircleShape)
                        )
                        // Inner mic button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(AccentIndigo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Mic,
                                contentDescription = null,
                                tint   = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text  = stringResource(R.string.voice_tap_to_speak),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))

                    OutlinedButton(
                        onClick = { manager.destroy(); onDismiss() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }

                // ── RESULT STATE ──────────────────────────────────────────────
                is VoiceUiState.Result -> {
                    Text(
                        text  = stringResource(R.string.voice_result_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(20.dp))

                    // Raw transcript chip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                AccentIndigo.copy(alpha = 0.07f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text  = "\"${state.raw}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentIndigo,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(16.dp))

                    // Parsed fields
                    ParsedFieldRow(
                        label = stringResource(R.string.voice_field_amount),
                        value = state.parsed.amount?.let {
                            "%.2f".format(it)
                        } ?: stringResource(R.string.voice_not_detected),
                        valueColor = if (state.parsed.amount != null) SuccessGreen else DangerRed
                    )
                    Spacer(Modifier.height(8.dp))
                    ParsedFieldRow(
                        label = stringResource(R.string.voice_field_description),
                        value = state.parsed.description.ifBlank {
                            stringResource(R.string.voice_not_detected)
                        }
                    )
                    if (state.parsed.suggestedCategoryKey != null) {
                        Spacer(Modifier.height(8.dp))
                        ParsedFieldRow(
                            label = stringResource(R.string.voice_field_category),
                            value = state.parsed.suggestedCategoryKey
                                .removePrefix("cat_")
                                .replaceFirstChar { it.uppercase() },
                            valueColor = AccentIndigo
                        )
                    }
                    Spacer(Modifier.height(24.dp))

                    // Action buttons
                    Button(
                        onClick = {
                            onConfirm(
                                VoiceExpenseResult(
                                    amount = state.parsed.amount,
                                    description = state.parsed.description,
                                    suggestedCategoryKey = state.parsed.suggestedCategoryKey
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                    ) {
                        Text(
                            stringResource(R.string.voice_confirm),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { startListening() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Mic, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.voice_try_again))
                    }
                }

                // ── ERROR STATE ───────────────────────────────────────────────
                is VoiceUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(DangerRed.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.MicOff,
                            contentDescription = null,
                            tint   = DangerRed,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text  = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { startListening() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                    ) {
                        Icon(Icons.Filled.Mic, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.voice_try_again))
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { manager.destroy(); onDismiss() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Small helper composable ───────────────────────────────────────────────────

@Composable
private fun ParsedFieldRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (valueColor == Color.Unspecified)
                MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
}
