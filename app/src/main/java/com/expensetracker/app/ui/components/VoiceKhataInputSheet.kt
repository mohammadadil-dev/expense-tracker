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
 * Result the caller receives when the user confirms a voice-parsed Khata entry.
 *
 * Deliberately amount + note only — no CREDIT/PAYMENT guess. Money direction is the one
 * thing this sheet won't try to infer from speech: misreading "gave"/"got" the wrong way
 * on a customer's ledger is a real-money mistake, not a cosmetic one, so the existing
 * Credit/Payment toggle in [AddKhataEntrySheet] stays the single source of truth for that;
 * voice only fills in the two fields that are safe to get wrong and easy to fix (amount is
 * shown back to the user before it's ever saved, same as the Dashboard's voice-expense flow).
 */
data class VoiceKhataResult(
    val amount: Double?,
    val note: String
)

// ── State machine (must be file-level; sealed classes cannot be local) ────────
private sealed class VoiceKhataUiState {
    object Listening : VoiceKhataUiState()
    data class Result(val parsed: VoiceExpenseParser.Result, val raw: String) : VoiceKhataUiState()
    data class Error(val message: String) : VoiceKhataUiState()
}

/**
 * Bottom sheet that handles voice entry for a Khata credit/payment amount + note, mirroring
 * [VoiceInputSheet]'s Listening → Result/Error → Confirm flow. Reuses [VoiceExpenseParser] for
 * the actual amount/description extraction (that logic is generic — only its category
 * suggestion is expense-specific, which this sheet simply ignores).
 *
 * @param locale     App locale — passed to the speech recogniser.
 * @param onConfirm  Called with parsed result; caller fills its own amount/note fields.
 * @param onDismiss  Called when user dismisses without confirming.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceKhataInputSheet(
    sheetState: SheetState,
    locale: Locale = Locale.getDefault(),
    onConfirm: (VoiceKhataResult) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var uiState by remember { mutableStateOf<VoiceKhataUiState>(VoiceKhataUiState.Listening) }
    val manager = remember { VoiceInputManager(context) }

    fun startListening() {
        uiState = VoiceKhataUiState.Listening
        manager.start(
            locale = locale,
            onResult = { text ->
                uiState = if (text.isBlank()) {
                    VoiceKhataUiState.Error(context.getString(R.string.voice_no_speech))
                } else {
                    VoiceKhataUiState.Result(VoiceExpenseParser.parse(text), text)
                }
            },
            onError = { code ->
                uiState = VoiceKhataUiState.Error(VoiceInputManager.errorLabel(code))
            }
        )
    }

    LaunchedEffect(Unit) { startListening() }
    DisposableEffect(Unit) { onDispose { manager.destroy() } }

    val infiniteTransition = rememberInfiniteTransition(label = "khataVoicePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.22f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "khataVoicePulseScale"
    )

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
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .background(TextMuted.copy(alpha = 0.3f), CircleShape)
            )
            Spacer(Modifier.height(20.dp))

            when (val state = uiState) {

                is VoiceKhataUiState.Listening -> {
                    Text(
                        text  = stringResource(R.string.voice_listening),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(32.dp))

                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .scale(pulseScale)
                                .background(AccentIndigo.copy(alpha = 0.12f), CircleShape)
                        )
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
                        text  = stringResource(R.string.khata_voice_hint),
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

                is VoiceKhataUiState.Result -> {
                    Text(
                        text  = stringResource(R.string.voice_result_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AccentIndigo.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
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

                    VoiceKhataFieldRow(
                        label = stringResource(R.string.voice_field_amount),
                        value = state.parsed.amount?.let { "%.2f".format(it) }
                            ?: stringResource(R.string.voice_not_detected),
                        valueColor = if (state.parsed.amount != null) SuccessGreen else DangerRed
                    )
                    Spacer(Modifier.height(8.dp))
                    VoiceKhataFieldRow(
                        label = stringResource(R.string.khata_note_hint),
                        value = state.parsed.description.ifBlank {
                            stringResource(R.string.voice_not_detected)
                        }
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                VoiceKhataResult(
                                    amount = state.parsed.amount,
                                    note   = state.parsed.description
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                    ) {
                        Text(
                            stringResource(R.string.khata_voice_confirm),
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

                is VoiceKhataUiState.Error -> {
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

@Composable
private fun VoiceKhataFieldRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
}
