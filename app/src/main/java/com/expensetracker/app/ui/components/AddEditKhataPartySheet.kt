package com.expensetracker.app.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.KhataPartyEntity
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.util.UpiPaymentHelper
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ── Country code data ──────────────────────────────────────────────────────────

private data class CountryEntry(
    val flag: String,
    val name: String,
    val dial: String
)

private val COUNTRY_ENTRIES = listOf(
    CountryEntry("🇸🇦", "Saudi Arabia",  "+966"),
    CountryEntry("🇮🇳", "India",          "+91"),
    CountryEntry("🇵🇰", "Pakistan",       "+92"),
    CountryEntry("🇧🇩", "Bangladesh",     "+880"),
    CountryEntry("🇵🇭", "Philippines",    "+63"),
    CountryEntry("🇦🇪", "UAE",            "+971"),
    CountryEntry("🇶🇦", "Qatar",          "+974"),
    CountryEntry("🇰🇼", "Kuwait",         "+965"),
    CountryEntry("🇧🇭", "Bahrain",        "+973"),
    CountryEntry("🇴🇲", "Oman",           "+968"),
    CountryEntry("🇪🇬", "Egypt",          "+20"),
    CountryEntry("🇯🇴", "Jordan",         "+962"),
    CountryEntry("🇱🇧", "Lebanon",        "+961"),
    CountryEntry("🇮🇶", "Iraq",           "+964"),
    CountryEntry("🇸🇾", "Syria",          "+963"),
    CountryEntry("🇾🇪", "Yemen",          "+967"),
    CountryEntry("🇱🇰", "Sri Lanka",      "+94"),
    CountryEntry("🇳🇵", "Nepal",          "+977"),
    CountryEntry("🇬🇧", "UK",             "+44"),
    CountryEntry("🇺🇸", "USA / Canada",   "+1"),
    CountryEntry("🇦🇺", "Australia",      "+61"),
)

/** Auto-detect country from device locale; falls back to Saudi Arabia. */
private fun defaultCountryEntry(): CountryEntry {
    val country = java.util.Locale.getDefault().country.uppercase()
    val dial = when (country) {
        "SA"       -> "+966"
        "IN"       -> "+91"
        "PK"       -> "+92"
        "BD"       -> "+880"
        "PH"       -> "+63"
        "AE"       -> "+971"
        "QA"       -> "+974"
        "KW"       -> "+965"
        "BH"       -> "+973"
        "OM"       -> "+968"
        "EG"       -> "+20"
        "JO"       -> "+962"
        "LB"       -> "+961"
        "IQ"       -> "+964"
        "SY"       -> "+963"
        "YE"       -> "+967"
        "LK"       -> "+94"
        "NP"       -> "+977"
        "GB"       -> "+44"
        "US", "CA" -> "+1"
        "AU", "NZ" -> "+61"
        else       -> "+966"   // default to Saudi Arabia
    }
    return COUNTRY_ENTRIES.firstOrNull { it.dial == dial } ?: COUNTRY_ENTRIES.first()
}

// Known country codes sorted longest-first so "+880" is tried before "+88" etc.
private val KNOWN_CODES = listOf(
    "+880", "+960", "+961", "+962", "+963", "+964", "+965", "+966",
    "+967", "+968", "+971", "+972", "+973", "+974", "+975", "+976", "+977",
    "+20",  "+27",  "+30",  "+31",  "+32",  "+33",  "+34",  "+36",
    "+39",  "+40",  "+41",  "+43",  "+44",  "+45",  "+46",  "+47",
    "+48",  "+49",  "+51",  "+52",  "+53",  "+54",  "+55",  "+56",
    "+57",  "+58",  "+60",  "+61",  "+62",  "+63",  "+64",  "+65",
    "+66",  "+81",  "+82",  "+84",  "+86",  "+90",  "+91",  "+92",
    "+93",  "+94",  "+95",  "+98",
    "+1",   "+7"
)

/** Split a stored phone string back into (countryCode, localNumber). */
private fun splitPhone(stored: String): Pair<String, String> {
    val stripped = stored.trim().replace(" ", "").replace("-", "")
    if (stripped.isBlank()) return Pair("", "")
    if (stripped.startsWith("+")) {
        for (code in KNOWN_CODES) {
            if (stripped.startsWith(code) && stripped.length > code.length) {
                return Pair(code, stripped.substring(code.length))
            }
        }
    }
    return Pair("", stripped)
}

// ── Sheet ──────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditKhataPartySheet(
    initial: KhataPartyEntity?,
    defaultDirection: String,
    currencySymbol: String,
    myUpiId: String,
    onSave: (
        id: Long?,
        name: String,
        phone: String,
        direction: String,
        initialAmount: Double,
        initialNote: String,
        upiId: String?,
        creditLimit: Double?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val isNew = initial == null

    val (initCode, initLocal) = remember(initial) { splitPhone(initial?.phone ?: "") }
    val initialEntry = remember(initCode) {
        COUNTRY_ENTRIES.firstOrNull { it.dial == initCode } ?: defaultCountryEntry()
    }

    var name              by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var selectedCountry   by remember(initial) { mutableStateOf(initialEntry) }
    var localPhone        by remember(initial) { mutableStateOf(initLocal) }
    var direction         by remember(initial) { mutableStateOf(initial?.direction ?: defaultDirection) }
    var upiIdInput        by remember(initial) { mutableStateOf(initial?.upiId ?: "") }
    var creditLimitText   by remember(initial) { mutableStateOf(initial?.creditLimit?.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() } ?: "") }
    var creditLimitError  by remember { mutableStateOf(false) }
    var nameError         by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }

    // Initial amount fields — only relevant when adding a brand-new party
    var amountText  by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var noteText    by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // ── Scan their UPI QR to fill "Their UPI ID" ────────────────────────────────
    // Typing or being read out someone else's VPA is friction most users won't bother with —
    // almost everyone who has a UPI ID can pull up their own QR code in a few seconds, so
    // scanning it is the realistic way this field actually gets filled in practice.
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var qrScanPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val qrScanErrorMsg = stringResource(R.string.khata_upi_qr_scan_error)
    val qrScanPermissionDenied = stringResource(R.string.receipt_camera_permission_denied)

    val qrScanCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { saved ->
        val uri = qrScanPhotoUri
        if (!saved || uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val vpa = decodeUpiQr(context, uri)
            if (vpa != null) {
                upiIdInput = vpa
            } else {
                Toast.makeText(context, qrScanErrorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val qrScanPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchQrScanCamera(
                context, qrScanCameraLauncher,
                onUriReady = { qrScanPhotoUri = it },
                onError = { Toast.makeText(context, qrScanErrorMsg, Toast.LENGTH_SHORT).show() }
            )
        } else {
            Toast.makeText(context, qrScanPermissionDenied, Toast.LENGTH_SHORT).show()
        }
    }

    // ── Country picker dialog ──────────────────────────────────────────────────
    if (showCountryPicker) {
        AlertDialog(
            onDismissRequest = { showCountryPicker = false },
            title = { Text(stringResource(R.string.select_country_code), style = MaterialTheme.typography.titleMedium) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(COUNTRY_ENTRIES) { entry ->
                        val isSelected = entry.dial == selectedCountry.dial
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) AccentIndigo.copy(alpha = 0.08f)
                                    else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .clickable {
                                    selectedCountry = entry
                                    showCountryPicker = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(entry.flag, fontSize = 24.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                            Text(
                                entry.dial,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Spacer(Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // ── Bottom sheet ───────────────────────────────────────────────────────────
    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                text = if (isNew) stringResource(R.string.khata_add_party)
                       else stringResource(R.string.khata_edit_party),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            // ── Name ──────────────────────────────────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text(stringResource(R.string.khata_party_name_hint)) },
                singleLine = true,
                isError = nameError,
                supportingText = if (nameError) {
                    { Text(stringResource(R.string.enter_category_name)) }
                } else null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ── Phone ─────────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.khata_party_phone_hint),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(4.dp))

            // Always LTR: flag badge on left, number field on right
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ── Country badge (tappable) ───────────────────────────────
                    Surface(
                        onClick = { showCountryPicker = true },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(selectedCountry.flag, fontSize = 20.sp)
                            Text(
                                text = selectedCountry.dial,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Change country code",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // ── Local number field ─────────────────────────────────────
                    OutlinedTextField(
                        value = localPhone,
                        onValueChange = { localPhone = it },
                        placeholder = { Text("512 345 678") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = stringResource(R.string.khata_party_phone_optional),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )

            Spacer(Modifier.height(16.dp))

            // ── Direction ─────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.khata_direction_i_owe_label),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = direction == KhataPartyEntity.DIRECTION_I_OWE,
                    onClick  = { direction = KhataPartyEntity.DIRECTION_I_OWE }
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.khata_i_owe), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(24.dp))
                RadioButton(
                    selected = direction == KhataPartyEntity.DIRECTION_THEY_OWE,
                    onClick  = { direction = KhataPartyEntity.DIRECTION_THEY_OWE }
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.khata_they_owe), style = MaterialTheme.typography.bodyMedium)
            }

            // ── Credit limit (optional) ───────────────────────────────────────
            // A purely local warning threshold — this app never blocks adding a credit entry
            // that would push the balance over the limit, it just surfaces a warning once
            // the outstanding balance gets close to or crosses it (see KhataPartyCard /
            // KhataBalanceCard's progress-bar treatment).
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = creditLimitText,
                onValueChange = { creditLimitText = it; creditLimitError = false },
                label = { Text(stringResource(R.string.khata_credit_limit_label)) },
                placeholder = { Text(stringResource(R.string.khata_credit_limit_optional)) },
                singleLine = true,
                isError = creditLimitError,
                supportingText = if (creditLimitError) {
                    { Text(stringResource(R.string.error_invalid_budget_amount)) }
                } else null,
                prefix = { Text(currencySymbol) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // ── UPI (India-only) ───────────────────────────────────────────────
            // "Request via UPI" always pays the *app user's own* UPI ID (set in Settings) —
            // it never uses a party's UPI ID, so asking for "their UPI ID" only makes sense
            // for I_OWE parties (captured now so a future "pay them" flow doesn't need a
            // second data-entry pass). For THEY_OWE parties, show read-only status about the
            // user's own ID instead of an editable field that would otherwise do nothing.
            if (CurrencyLocaleMapper.isInrSymbol(currencySymbol)) {
                Spacer(Modifier.height(16.dp))
                if (direction == KhataPartyEntity.DIRECTION_I_OWE) {
                    val upiValid = upiIdInput.isBlank() || UpiPaymentHelper.isValidVpa(upiIdInput)
                    OutlinedTextField(
                        value = upiIdInput,
                        onValueChange = { upiIdInput = it },
                        label = { Text(stringResource(R.string.khata_party_upi_hint)) },
                        placeholder = { Text(stringResource(R.string.upi_id_hint)) },
                        singleLine = true,
                        isError = !upiValid,
                        supportingText = if (!upiValid) {
                            { Text(stringResource(R.string.upi_id_invalid), color = DangerRed) }
                        } else {
                            { Text(stringResource(R.string.khata_scan_upi_qr_hint)) }
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                qrScanPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }) {
                                Icon(
                                    Icons.Filled.QrCodeScanner,
                                    contentDescription = stringResource(R.string.khata_scan_upi_qr)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (myUpiId.isNotBlank()) SuccessGreen.copy(alpha = 0.10f)
                                else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (myUpiId.isNotBlank())
                                stringResource(R.string.khata_party_upi_mine_ready, myUpiId)
                            else
                                stringResource(R.string.khata_party_upi_mine_missing),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (myUpiId.isNotBlank()) SuccessGreen else TextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // ── Opening balance (new party only) ──────────────────────────────
            if (isNew) {
                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.khata_initial_amount_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; amountError = false },
                    label = { Text(stringResource(R.string.khata_amount_hint)) },
                    placeholder = { Text("0") },
                    singleLine = true,
                    isError = amountError,
                    supportingText = if (amountError) {
                        { Text(stringResource(R.string.error_invalid_budget_amount)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(stringResource(R.string.khata_note_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Buttons ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (name.isBlank()) { nameError = true; return@Button }
                    // The UPI field only shows (and is only editable) for I_OWE parties — ignore
                    // any stale value if the direction was switched away from I_OWE after typing.
                    val effectiveUpiId = if (direction == KhataPartyEntity.DIRECTION_I_OWE) upiIdInput else ""
                    if (effectiveUpiId.isNotBlank() && !UpiPaymentHelper.isValidVpa(effectiveUpiId)) return@Button

                    val parsedAmount: Double
                    if (isNew && amountText.isNotBlank()) {
                        val v = amountText.trim().toDoubleOrNull()
                        if (v == null || v < 0.0) { amountError = true; return@Button }
                        parsedAmount = v
                    } else {
                        parsedAmount = 0.0
                    }

                    val parsedCreditLimit: Double?
                    if (creditLimitText.isBlank()) {
                        parsedCreditLimit = null
                    } else {
                        val v = creditLimitText.trim().toDoubleOrNull()
                        if (v == null || v < 0.0) { creditLimitError = true; return@Button }
                        parsedCreditLimit = v.takeIf { it > 0.0 }
                    }

                    val fullPhone = if (localPhone.isBlank()) ""
                        else "${selectedCountry.dial}${localPhone.trim().replace(" ", "").replace("-", "")}"

                    onSave(
                        initial?.id, name.trim(), fullPhone, direction, parsedAmount, noteText.trim(),
                        effectiveUpiId.trim().takeIf { it.isNotBlank() },
                        parsedCreditLimit
                    )
                }) {
                    Text(stringResource(R.string.khata_save_party))
                }
            }
        }
    }
}

// ── QR scan helpers ──────────────────────────────────────────────────────────

/**
 * Creates a temp file in cache/camera/, wraps it in a FileProvider URI, and launches the
 * system camera intent pointing at that URI. Mirrors [ReceiptScanSheet]'s identical helper —
 * kept as a separate private copy here since scanning a UPI QR is a fundamentally different
 * follow-up step (barcode decode, not OCR) even though the capture mechanics are the same.
 */
private fun launchQrScanCamera(
    context: Context,
    launcher: ActivityResultLauncher<Uri>,
    onUriReady: (Uri) -> Unit,
    onError: () -> Unit
) {
    try {
        val cameraDir = File(context.cacheDir, "camera").also { it.mkdirs() }
        val photoFile = File.createTempFile("upi_qr_scan_", ".jpg", cameraDir)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        onUriReady(uri)
        launcher.launch(uri)
    } catch (e: IOException) {
        onError()
    } catch (e: IllegalArgumentException) {
        onError()
    }
}

/**
 * Decodes any QR/barcode found in the photo at [uri] via on-device ML Kit Barcode Scanning,
 * and pulls a VPA out of the first one that parses as UPI payment info (see
 * [UpiPaymentHelper.extractVpaFromQrContent]). Returns null if nothing usable was found —
 * the caller shows an error toast rather than silently leaving the field unchanged, so the
 * user isn't left wondering whether the scan worked.
 */
private suspend fun decodeUpiQr(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    val scanner = BarcodeScanning.getClient()
    try {
        val image = InputImage.fromFilePath(context, uri)
        val barcodes = suspendCancellableCoroutine<List<Barcode>> { cont ->
            scanner.process(image)
                .addOnSuccessListener { result -> cont.resume(result) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
        barcodes.firstNotNullOfOrNull { barcode ->
            barcode.rawValue?.let { UpiPaymentHelper.extractVpaFromQrContent(it) }
        }
    } catch (e: Exception) {
        null
    } finally {
        scanner.close()
    }
}
