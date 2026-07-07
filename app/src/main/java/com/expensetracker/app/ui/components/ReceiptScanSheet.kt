package com.expensetracker.app.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.expensetracker.app.R
import com.expensetracker.app.util.ReceiptAmountExtractor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ---------------------------------------------------------------------------
// State machine
// ---------------------------------------------------------------------------

private sealed class ScanUiState {
    object Idle : ScanUiState()
    object Processing : ScanUiState()
    data class Result(val amount: Double, val rawText: String) : ScanUiState()
    data class NoAmount(val rawText: String) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

// ---------------------------------------------------------------------------
// Public composable
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScanSheet(
    onDismiss: () -> Unit,
    onAmountConfirmed: (prefill: ExpensePrefill) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var uiState by remember { mutableStateOf<ScanUiState>(ScanUiState.Idle) }

    // Temp file URI prepared before launching the camera.
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher — fires after user takes (or cancels) a photo.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        if (!saved) {
            // User pressed back without taking a photo — stay Idle.
            uiState = ScanUiState.Idle
            return@rememberLauncherForActivityResult
        }
        val uri = photoUri ?: run {
            uiState = ScanUiState.Error("No photo URI")
            return@rememberLauncherForActivityResult
        }
        uiState = ScanUiState.Processing
        scope.launch {
            runOcr(context, uri,
                onSuccess = { rawText ->
                    val amount = ReceiptAmountExtractor.extract(rawText)
                    uiState = if (amount != null) {
                        ScanUiState.Result(amount, rawText)
                    } else {
                        ScanUiState.NoAmount(rawText)
                    }
                },
                onError = { msg ->
                    uiState = ScanUiState.Error(msg)
                }
            )
        }
    }

    // Permission launcher — requests CAMERA, then opens camera if granted.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera(
                context, cameraLauncher,
                onUriReady = { photoUri = it },
                onError = { msg -> uiState = ScanUiState.Error(msg) }
            )
        } else {
            uiState = ScanUiState.Error(context.getString(R.string.receipt_camera_permission_denied))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.receipt_scan_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.receipt_close))
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "scan_state"
            ) { state ->
                when (state) {
                    is ScanUiState.Idle -> IdleContent(
                        onScanClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )

                    is ScanUiState.Processing -> ProcessingContent()

                    is ScanUiState.Result -> ResultContent(
                        amount = state.amount,
                        onConfirm = { confirmedAmount ->
                            onAmountConfirmed(ExpensePrefill(amount = confirmedAmount))
                            onDismiss()
                        },
                        onRescan = {
                            uiState = ScanUiState.Idle
                        }
                    )

                    is ScanUiState.NoAmount -> NoAmountContent(
                        onRescan = { uiState = ScanUiState.Idle },
                        onManual = {
                            onAmountConfirmed(ExpensePrefill())
                            onDismiss()
                        }
                    )

                    is ScanUiState.Error -> ErrorContent(
                        message = state.message,
                        onRetry = { uiState = ScanUiState.Idle }
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-screens
// ---------------------------------------------------------------------------

@Composable
private fun IdleContent(onScanClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.receipt_scan_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.receipt_open_camera))
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ProcessingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 32.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.receipt_scanning),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ResultContent(
    amount: Double,
    onConfirm: (Double) -> Unit,
    onRescan: () -> Unit
) {
    // Pre-fill with OCR result; user can correct directly without rescanning.
    var amountText by remember {
        mutableStateOf(
            NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
                isGroupingUsed = false
            }.format(amount)
        )
    }
    val parsedAmount = amountText.replace(",", "").toDoubleOrNull()
    val isValid = parsedAmount != null && parsedAmount > 0.0

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color(0xFF43A047)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.receipt_amount_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Editable — user can fix OCR errors without rescanning
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = !isValid && amountText.isNotEmpty(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.receipt_edit_hint),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onConfirm(parsedAmount ?: amount) },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.receipt_use_amount))
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onRescan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.receipt_rescan))
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun NoAmountContent(onRescan: () -> Unit, onManual: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.receipt_no_amount_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.receipt_no_amount_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onRescan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.receipt_rescan))
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onManual,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.receipt_enter_manually))
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.receipt_error_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.receipt_retry))
        }

        Spacer(Modifier.height(12.dp))
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

/**
 * Creates a temp file in cache/camera/, wraps it in a FileProvider URI, and launches
 * the system camera intent pointing at that URI. Sets [onUriReady] before launching.
 */
private fun launchCamera(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onUriReady: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val cameraDir = File(context.cacheDir, "camera").also { it.mkdirs() }
        val photoFile = File.createTempFile("receipt_", ".jpg", cameraDir)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        onUriReady(uri)
        launcher.launch(uri)
    } catch (e: IOException) {
        onError(e.localizedMessage ?: "Could not create temp file for camera")
    } catch (e: IllegalArgumentException) {
        // FileProvider.getUriForFile throws this if the path isn't covered by file_paths.xml
        onError("FileProvider misconfiguration: ${e.localizedMessage}")
    }
}

/**
 * Runs ML Kit Latin text recognition on the given [uri] (suspend-wrapped).
 */
private suspend fun runOcr(
    context: Context,
    uri: Uri,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) = withContext(Dispatchers.IO) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    try {
        val image = InputImage.fromFilePath(context, uri)
        val rawText = suspendCancellableCoroutine<String> { cont ->
            recognizer.process(image)
                .addOnSuccessListener { visionText -> cont.resume(visionText.text) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        withContext(Dispatchers.Main) { onSuccess(rawText) }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) { onError(e.localizedMessage ?: "OCR failed") }
    } finally {
        // Always release the recognizer — even if the coroutine was cancelled.
        recognizer.close()
    }
}
