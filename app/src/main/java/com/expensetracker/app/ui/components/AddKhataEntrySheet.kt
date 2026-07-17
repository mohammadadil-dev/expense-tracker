package com.expensetracker.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.expensetracker.app.R
import com.expensetracker.app.data.KhataEntryEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.DateUtils
import com.expensetracker.app.util.ReceiptPhotoStore
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKhataEntrySheet(
    initialType: String = KhataEntryEntity.TYPE_CREDIT,
    onSave: (amount: Double, note: String, date: String, type: String, dueDate: String?, photoPath: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val scrollState = rememberScrollState()
    var amountText  by remember { mutableStateOf("") }
    var note        by remember { mutableStateOf("") }
    var date        by remember { mutableStateOf(DateUtils.todayIso()) }
    var entryType   by remember { mutableStateOf(initialType) }
    var amountError by remember { mutableStateOf(false) }
    var dateError   by remember { mutableStateOf(false) }

    // Due date — only meaningful on CREDIT entries (something owed by a deadline).
    var dueDate           by remember { mutableStateOf<String?>(null) }
    var showDueDatePicker by remember { mutableStateOf(false) }

    // Receipt photo — captured fresh via camera (written straight to a permanent file) or
    // copied in from a gallery pick. Either way, [photoPath] ends up pointing at a file
    // under this app's private filesDir/receipts/, never a transient content:// Uri.
    var photoPath      by remember { mutableStateOf<String?>(null) }
    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    // Cancelling (Cancel button, swipe-down, or back press) must clean up any photo already
    // written to permanent storage — otherwise a captured/picked receipt photo that's never
    // actually saved on an entry leaks forever under filesDir/receipts/ (that directory is
    // never OS-cleared; see ReceiptPhotoStore's class doc). Only the explicit "×" remove-photo
    // button did this before; Save doesn't route through here, so it can't double-delete a
    // photo that's about to be persisted on the new entry.
    val dismissAndCleanup: () -> Unit = {
        ReceiptPhotoStore.delete(photoPath)
        onDismiss()
    }

    // Voice entry — fills amount + note only (never guesses Credit vs Payment; see
    // VoiceKhataResult's doc comment for why). Stays entirely local to this sheet: no new
    // params needed, the voice sheet just stacks on top the same way CalendarDatePickerDialog
    // already does below for the due-date picker.
    var showVoiceSheet by remember { mutableStateOf(false) }
    val voiceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showVoiceSheet = true
        else Toast.makeText(context, context.getString(R.string.voice_permission_denied), Toast.LENGTH_SHORT).show()
    }
    fun onMicClick() {
        val hasPerm = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPerm) showVoiceSheet = true else micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        if (saved) {
            pendingCameraFile?.let { file ->
                // Downscale/re-encode the full-resolution capture before keeping it — see
                // ReceiptPhotoStore's class doc for why (storage on budget devices).
                ReceiptPhotoStore.compressCapturedPhoto(file.absolutePath)
                photoPath = file.absolutePath
            }
        }
        pendingCameraFile = null
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            ReceiptPhotoStore.copyFromGallery(context, uri)?.let { photoPath = it }
        }
    }

    ModalBottomSheet(
        onDismissRequest = dismissAndCleanup,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.khata_add_entry),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            // Credit / Payment toggle buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isCredit = entryType == KhataEntryEntity.TYPE_CREDIT
                Button(
                    onClick = { entryType = KhataEntryEntity.TYPE_CREDIT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCredit) DangerRed else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor   = if (isCredit) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.khata_credit_label)) }

                Button(
                    onClick = { entryType = KhataEntryEntity.TYPE_PAYMENT },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isCredit) SuccessGreen else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor   = if (!isCredit) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.khata_payment_label)) }
            }

            Spacer(Modifier.height(16.dp))

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it; amountError = false },
                label = { Text(stringResource(R.string.khata_amount_hint)) },
                singleLine = true,
                isError = amountError,
                supportingText = if (amountError) {
                    { Text(stringResource(R.string.error_invalid_rate)) }
                } else null,
                trailingIcon = {
                    IconButton(onClick = { onMicClick() }) {
                        Icon(Icons.Filled.Mic, contentDescription = stringResource(R.string.khata_voice_add_entry))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.khata_note_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Date
            OutlinedTextField(
                value = date,
                onValueChange = { date = it; dateError = false },
                label = { Text(stringResource(R.string.khata_entry_date)) },
                singleLine = true,
                isError = dateError,
                supportingText = if (dateError) {
                    { Text(stringResource(R.string.error_invalid_date)) }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            // Due date — CREDIT entries only
            if (entryType == KhataEntryEntity.TYPE_CREDIT) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = dueDate?.let { DateUtils.formatExpenseDate(it, locale) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.khata_due_date_label)) },
                    placeholder = { Text(stringResource(R.string.khata_due_date_optional)) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (dueDate != null) {
                                IconButton(onClick = { dueDate = null }) {
                                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cancel))
                                }
                            }
                            IconButton(onClick = { showDueDatePicker = true }) {
                                Icon(Icons.Filled.CalendarMonth, contentDescription = stringResource(R.string.select_date))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().clickable { showDueDatePicker = true }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Receipt photo (optional) ──────────────────────────────────────
            Text(
                text = stringResource(R.string.khata_receipt_photo_label),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(6.dp))

            if (photoPath != null) {
                Box(modifier = Modifier.size(96.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(File(photoPath!!)),
                        contentDescription = stringResource(R.string.khata_receipt_photo_label),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    IconButton(
                        onClick = {
                            ReceiptPhotoStore.delete(photoPath)
                            photoPath = null
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(R.string.khata_remove_photo),
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = {
                        val (file, uri) = ReceiptPhotoStore.newPhotoUriForCamera(context)
                        pendingCameraFile = file
                        cameraLauncher.launch(uri)
                    }) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.khata_take_photo))
                    }
                    OutlinedButton(onClick = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.khata_choose_photo))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = dismissAndCleanup) { Text(stringResource(R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0.0) { amountError = true; return@Button }
                        val parsedDate = try { java.time.LocalDate.parse(date); date } catch (e: Exception) { null }
                        if (parsedDate == null) { dateError = true; return@Button }
                        onSave(
                            amount, note.trim(), parsedDate, entryType,
                            if (entryType == KhataEntryEntity.TYPE_CREDIT) dueDate else null,
                            photoPath
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) { Text(stringResource(R.string.add)) }
            }
        }
    }

    if (showDueDatePicker) {
        CalendarDatePickerDialog(
            initialDateIso = dueDate ?: DateUtils.todayIso(),
            locale = locale,
            onDismiss = { showDueDatePicker = false },
            onConfirm = { newDateIso ->
                dueDate = newDateIso
                showDueDatePicker = false
            }
        )
    }

    if (showVoiceSheet) {
        VoiceKhataInputSheet(
            sheetState = voiceSheetState,
            locale     = locale,
            onDismiss  = { showVoiceSheet = false },
            onConfirm  = { result ->
                showVoiceSheet = false
                result.amount?.let { amt ->
                    amountText = if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString()
                    amountError = false
                }
                if (result.note.isNotBlank()) note = result.note
            }
        )
    }
}
