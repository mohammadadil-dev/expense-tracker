package com.expensetracker.app.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
// import androidx.compose.material.icons.filled.FamilyRestroom  // reserved for Family Mode re-enable
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.util.UpiPaymentHelper
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.BackgroundScrollSignal
import com.expensetracker.app.ui.components.BottomNavVisibility
import com.expensetracker.app.ui.components.rememberIsScrollingUp
// import com.expensetracker.app.ui.components.FamilySetupSheet  // reserved for Family Mode re-enable
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import androidx.compose.ui.platform.LocalConfiguration
import com.expensetracker.app.util.SaudiBanks
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonPink
import com.expensetracker.app.ui.theme.NeonTeal
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import android.app.Activity
import com.expensetracker.app.util.BackupManager
import com.expensetracker.app.util.DriveBackupManager
import com.expensetracker.app.util.ReminderReceiver
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes

private data class CurrencyOption(val symbol: String, val label: String)

private val allCurrencies = listOf(
    CurrencyOption("$",    "$ — US Dollar (USA / Canada / Australia)"),
    CurrencyOption("€",    "€ — Euro (Europe)"),
    CurrencyOption("£",    "£ — British Pound (UK)"),
    CurrencyOption("₹",    "₹ — Indian Rupee (India)"),
    CurrencyOption("₨",    "₨ — Pakistani Rupee (Pakistan)"),
    CurrencyOption("৳",    "৳ — Bangladeshi Taka (Bangladesh)"),
    CurrencyOption("₱",    "₱ — Philippine Peso (Philippines)"),
    CurrencyOption("ر.س",  "SAR — Saudi Riyal (Saudi Arabia)"),
    CurrencyOption("د.إ",  "AED — UAE Dirham (UAE)"),
    CurrencyOption("ج.م",  "EGP — Egyptian Pound (Egypt)"),
    CurrencyOption("د.ك",  "KWD — Kuwaiti Dinar (Kuwait)"),
    CurrencyOption("ر.ق",  "QAR — Qatari Riyal (Qatar)"),
    CurrencyOption("¥",    "¥ — Japanese Yen / Chinese Yuan (JP / CN)"),
    CurrencyOption("₩",    "₩ — Korean Won (South Korea)"),
    CurrencyOption("₺",    "₺ — Turkish Lira (Turkey)"),
    CurrencyOption("₽",    "₽ — Russian Ruble (Russia)"),
    CurrencyOption("R$",   "R$ — Brazilian Real (Brazil)"),
    CurrencyOption("Rp",   "Rp — Indonesian Rupiah (Indonesia)"),
    CurrencyOption("RM",   "RM — Malaysian Ringgit (Malaysia)"),
    CurrencyOption("฿",    "฿ — Thai Baht (Thailand)"),
    CurrencyOption("₫",    "₫ — Vietnamese Dong (Vietnam)"),
    CurrencyOption("₪",    "₪ — Israeli Shekel (Israel)"),
    CurrencyOption("₦",    "₦ — Nigerian Naira (Nigeria)"),
    CurrencyOption("R",    "R — South African Rand (South Africa)"),
    CurrencyOption("KSh",  "KSh — Kenyan Shilling (Kenya)"),
)

/** code, native name (never translated), English name (never translated) — mirrors
 *  OnboardingScreen's OnboardLanguage, which already gets this right: every language is always
 *  written in itself and always carries a plain-English anchor, regardless of the app's
 *  currently active language. See the language dropdown's doc comment below for why. */
private data class SettingsLanguage(val code: String, val native: String, val english: String)

private val SETTINGS_LANGUAGES = listOf(
    SettingsLanguage("en", "English",   "English"),
    SettingsLanguage("ar", "العربية",   "Arabic"),
    SettingsLanguage("hi", "हिन्दी",     "Hindi"),
    SettingsLanguage("ur", "اردو",      "Urdu"),
    SettingsLanguage("tl", "Tagalog",   "Tagalog"),
    SettingsLanguage("bn", "বাংলা",     "Bengali"),
    SettingsLanguage("ta", "தமிழ்",     "Tamil"),
    SettingsLanguage("te", "తెలుగు",    "Telugu"),
    SettingsLanguage("kn", "ಕನ್ನಡ",     "Kannada"),
    SettingsLanguage("ml", "മലയാളം",    "Malayalam"),
    SettingsLanguage("mr", "मराठी",     "Marathi"),
    SettingsLanguage("gu", "ગુજરાતી",   "Gujarati"),
    SettingsLanguage("pa", "ਪੰਜਾਬੀ",    "Punjabi"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val copiedLabel = stringResource(R.string.copied)
    val clipboardManager = LocalClipboardManager.current
    val languagePref by viewModel.languagePref.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val smsDetectionEnabled by viewModel.smsDetectionEnabled.collectAsState()
    val reminderEnabled by viewModel.reminderEnabled.collectAsState()
    val reminderHour by viewModel.reminderHour.collectAsState()
    val reminder2Enabled by viewModel.reminder2Enabled.collectAsState()
    val reminderHour2 by viewModel.reminderHour2.collectAsState()
    val paydayDayOfMonth by viewModel.paydayDayOfMonth.collectAsState()
    @Suppress("UNUSED_VARIABLE") val familyModeEnabled by viewModel.familyModeEnabled.collectAsState()  // reserved for Family Mode re-enable
    @Suppress("UNUSED_VARIABLE") val familyMembers by viewModel.familyMembers.collectAsState()          // reserved for Family Mode re-enable
    val allExpenses by viewModel.allExpenses.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    var nameInput by remember(displayName) { mutableStateOf(displayName) }
    val myUpiId by viewModel.myUpiId.collectAsState()
    var upiInput by remember(myUpiId) { mutableStateOf(myUpiId) }
    val myIban by viewModel.myIban.collectAsState()
    // "SA" is shown as a frozen, uneditable prefix on the field; this state holds ONLY the 22
    // characters after it, so the prefix can never be deleted and length is naturally bounded.
    var ibanInput by remember(myIban) { mutableStateOf(myIban.removePrefix("SA")) }
    // IBAN section mode: show the input+Save while editing (or when nothing saved), else show the
    // saved IBAN with Edit/Remove. Re-derives whenever the saved value changes.
    var ibanEditing by remember(myIban) { mutableStateOf(myIban.isBlank()) }
    val myStcPay by viewModel.myStcPay.collectAsState()
    // "+966" is shown as a frozen prefix; this state holds only the local part (no country code,
    // no trunk 0), so stored "966510375230" edits back to "510375230".
    var stcInput by remember(myStcPay) { mutableStateOf(myStcPay.removePrefix("966").trimStart('0')) }
    var stcEditing by remember(myStcPay) { mutableStateOf(myStcPay.isBlank()) }
    val businessName by viewModel.businessName.collectAsState()
    var businessNameInput by remember(businessName) { mutableStateOf(businessName) }
    // Business Profile is collapsed by default (shopkeeper feature, India-only) to keep Settings tidy.
    var businessProfileExpanded by remember { mutableStateOf(false) }
    val businessAddress by viewModel.businessAddress.collectAsState()
    var businessAddressInput by remember(businessAddress) { mutableStateOf(businessAddress) }
    val businessPhone by viewModel.businessPhone.collectAsState()
    var businessPhoneInput by remember(businessPhone) { mutableStateOf(businessPhone) }
    val paymentAccounts by viewModel.paymentAccounts.collectAsState()
    var showAccountManageSheet by remember { mutableStateOf(false) }
    var showResetStep1 by remember { mutableStateOf(false) }
    var showResetStep2 by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickerHour by remember { mutableStateOf(reminderHour) }
    // Which reminder the open time-picker dialog is editing — 1 = main, 2 = optional second.
    var pickerSlot by remember { mutableStateOf(1) }
    var showPaydayPicker by remember { mutableStateOf(false) }
    var pendingCurrencySymbol by remember { mutableStateOf<String?>(null) }
    var rateText by remember { mutableStateOf("") }
    var rateError by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    var currExpanded by remember { mutableStateOf(false) }

    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.setReminderEnabled(true)
    }

    // ── Backup / Restore launchers ────────────────────────────────────────────
    val backupChooserTitle = stringResource(R.string.backup_chooser_title)
    val backupSuccessMsg   = stringResource(R.string.backup_success)
    val restoreSuccessLabel = stringResource(R.string.restore_success)
    val restoreErrorLabel   = stringResource(R.string.restore_error)

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.importBackup(
            uri = uri,
            onSuccess = { result ->
                Toast.makeText(
                    context,
                    "$restoreSuccessLabel (${result.expenses} exp, ${result.khataParties} khata, ${result.debts} debts, ${result.splitGroups} splits, ${result.goals} goals)",
                    Toast.LENGTH_LONG
                ).show()
            },
            onError = { err ->
                Toast.makeText(context, "$restoreErrorLabel: $err", Toast.LENGTH_LONG).show()
            }
        )
    }

    // ── Google Drive sign-in launcher ─────────────────────────────────────────
    var driveAccount by remember { mutableStateOf(DriveBackupManager.getAuthorizedAccount(context)) }
    val driveBackupLoading = viewModel.driveBackupLoading
    val driveUploadSuccessMsg  = stringResource(R.string.drive_backup_success)
    val driveRestoreSuccessMsg = stringResource(R.string.drive_restore_success)
    val driveNoBackupMsg       = stringResource(R.string.drive_no_backup)
    val driveErrorMsg          = stringResource(R.string.drive_backup_error)

    val driveSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Always call getSignedInAccountFromIntent — it surfaces the actual ApiException
        // (e.g., code 10 = DEVELOPER_ERROR when the SHA-1 isn't registered in Google Cloud
        // Console) even when resultCode == RESULT_CANCELED. The old guard silently swallowed
        // every sign-in failure, making the "tap email → nothing happens" symptom impossible
        // to diagnose. Null data means the user pressed Back — ignore that case.
        val data = result.data ?: return@rememberLauncherForActivityResult
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener { account -> driveAccount = account }
            .addOnFailureListener { e ->
                val code = (e as? com.google.android.gms.common.api.ApiException)?.statusCode
                // Don't show a toast for a plain back-press (code 12 = SIGN_IN_CANCELLED)
                if (code != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    Toast.makeText(context, "$driveErrorMsg: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    val detectedCurrency = remember { CurrencyLocaleMapper.detectFromDevice(context) }
    val customerIdCopiedLabel = stringResource(R.string.customer_id_copied)

    // Drives a gentle staggered fade/slide-in for each section the first time this screen
    // appears, instead of everything popping in at once.
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    // Switching the in-app language needs the Activity to be torn down and rebuilt so every
    // stringResource() call re-reads from the new locale's resources. AppCompatDelegate.
    // setApplicationLocales() (called inside setLanguagePref -> LocaleHelper) already
    // schedules that recreate itself -- on API 33+ via the system LocaleManager, on older
    // versions via AppCompat's own compat layer -- same as the onboarding language step,
    // which relies on it alone.
    //
    // This used to ALSO call activity?.recreate() immediately afterward, to make the switch
    // feel "instant" instead of waiting on that callback. That backfired: the manual recreate
    // fired before the new locale had fully propagated to the Activity's resources, so the
    // rebuilt screen still resolved most stringResource() calls against the OLD locale (only
    // things reading Locale.getDefault() directly, like date formatting, updated right away).
    // AppCompat's own recreate then fired moments later and silently fixed it -- which is what
    // showed up as "translation happens, but only after a delay." Removing the manual call
    // fixes it: there's only ever one recreate, and it happens with the locale already applied.
    fun changeLanguage(pref: String) {
        viewModel.setLanguagePref(pref)
        onBack()  // Pop back to dashboard so the back stack is clean once the recreate happens
    }

    // Changing currency normally opens a dialog asking for an exchange rate, then rescales
    // every stored expense by it (all done locally, no network). But if there's nothing to
    // rescale yet (no expenses recorded at all), asking for a rate is pointless — just switch
    // the symbol directly.
    fun requestCurrencyChange(newSymbol: String) {
        if (newSymbol.isNotBlank() && newSymbol != currencySymbol) {
            if (allExpenses.isEmpty()) {
                viewModel.setCurrencySymbol(newSymbol)
                onBack()
            } else {
                rateText = ""
                rateError = false
                pendingCurrencySymbol = newSymbol
            }
        }
    }

    // Hands off to whatever share targets are installed (WhatsApp included) instead of
    // hard-coding any one platform — the system chooser surfaces them all.
    fun shareApp() {
        val playStoreLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val message = context.getString(
            R.string.share_app_message,
            context.getString(R.string.app_name),
            playStoreLink
        )
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_app)))
    }

    // Tries the Play Store app first (nicer review-prompt UX); falls back to the plain web
    // listing if the Play Store app isn't installed (e.g. some emulators).
    fun rateApp() {
        val packageName = context.packageName
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                    setPackage("com.android.vending")
                }
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }

    fun copyCustomerId() {
        clipboardManager.setText(AnnotatedString(viewModel.customerId))
        Toast.makeText(context, customerIdCopiedLabel, Toast.LENGTH_SHORT).show()
    }

    val scrollState = rememberScrollState()
    // Feeds this screen's own animated background its scroll position so the blobs drift in
    // subtle parallax while scrolling, same idea as Dashboard but with Settings' own instance.
    LaunchedEffect(scrollState.value) {
        BackgroundScrollSignal.pixels.floatValue = scrollState.value.toFloat()
    }

    // Settings has no FAB of its own, but the bottom nav bar still hides while scrolling down
    // here and comes back on scroll-up/stop, same as every other main screen.
    val isScrollingUp by scrollState.rememberIsScrollingUp()
    LaunchedEffect(isScrollingUp) {
        BottomNavVisibility.visible = isScrollingUp
    }

    // Settings gets its own touch-reactive background instance — cyan/pink/teal instead of
    // Dashboard's violet/indigo/green, so the two screens feel like distinct "places".
    Box(modifier = Modifier.fillMaxSize()) {
    AnimatedBlobBackground(
        blobColors = listOf(NeonCyan, NeonPink, NeonTeal),
        modifier = Modifier.fillMaxSize()
    )
    Scaffold(
        containerColor = Color.Transparent,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // ── Inline title (no TopAppBar on tab screens) ─────────────────
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = com.expensetracker.app.ui.theme.TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            AnimatedSection(visible = contentVisible, delayMillis = 0) {
                SettingsSectionHeader(icon = Icons.Filled.Person, title = stringResource(R.string.your_name_label))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(R.string.your_name_hint)) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.setDisplayName(nameInput.trim())
                            onBack()
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.your_name_note),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(24.dp))

            // Optional — stamped onto exported PDF reports (Ledger statement, Split report,
            // Dashboard monthly report) in place of the app's own name/branding, so a shop
            // owner's export reads like it's from their business. All blank by default;
            // PdfExporter call sites fall back to today's behavior when businessName is blank.
            // Shopkeeper feature (stamped on PDF exports) — India-market only, and collapsed by
            // default so it doesn't clutter Settings for the many users who don't run a shop.
            if (CurrencyLocaleMapper.isInrSymbol(currencySymbol)) {
                AnimatedSection(visible = contentVisible, delayMillis = 11) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { businessProfileExpanded = !businessProfileExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SettingsSectionHeader(icon = Icons.Filled.Store, title = stringResource(R.string.settings_business_profile_title))
                            Icon(
                                if (businessProfileExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AnimatedVisibility(visible = businessProfileExpanded) {
                            Column {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.settings_business_profile_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = businessNameInput,
                                    onValueChange = { businessNameInput = it },
                                    label = { Text(stringResource(R.string.business_name_label)) },
                                    placeholder = { Text(stringResource(R.string.business_name_hint)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = businessAddressInput,
                                    onValueChange = { businessAddressInput = it },
                                    label = { Text(stringResource(R.string.business_address_label)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = businessPhoneInput,
                                    onValueChange = { businessPhoneInput = it },
                                    label = { Text(stringResource(R.string.business_phone_label)) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(10.dp))
                                val businessProfileSavedLabel = stringResource(R.string.settings_business_profile_saved)
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setBusinessName(businessNameInput.trim())
                                        viewModel.setBusinessAddress(businessAddressInput.trim())
                                        viewModel.setBusinessPhone(businessPhoneInput.trim())
                                        Toast.makeText(context, businessProfileSavedLabel, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(stringResource(R.string.save))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            // India-only: UPI only works with Indian bank accounts, so this section (and the
            // "Request via UPI" flow it powers on Khata entries) is hidden for every other
            // currency rather than showing a payment method that can't actually be used.
            if (CurrencyLocaleMapper.isInrSymbol(currencySymbol)) {
                AnimatedSection(visible = contentVisible, delayMillis = 22) {
                    SettingsSectionHeader(icon = Icons.Filled.AccountBalance, title = stringResource(R.string.upi_section_title))
                    Spacer(Modifier.height(8.dp))
                    val upiValid = upiInput.isBlank() || UpiPaymentHelper.isValidVpa(upiInput)
                    OutlinedTextField(
                        value = upiInput,
                        onValueChange = { upiInput = it },
                        label = { Text(stringResource(R.string.upi_id_label)) },
                        placeholder = { Text(stringResource(R.string.upi_id_hint)) },
                        singleLine = true,
                        isError = !upiValid,
                        supportingText = if (!upiValid) {
                            { Text(stringResource(R.string.upi_id_invalid), color = DangerRed) }
                        } else null,
                        trailingIcon = {
                            IconButton(
                                enabled = upiValid,
                                onClick = {
                                    viewModel.setMyUpiId(upiInput.trim())
                                    onBack()
                                }
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(24.dp))
            }

            // Saudi/GCC only: the IBAN analog of the UPI section above — shown for the Saudi
            // Riyal so a user can store the IBAN that gets shared in Khata/Split reminders.
            if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) {
                AnimatedSection(visible = contentVisible, delayMillis = 22) {
                    Column {
                        SettingsSectionHeader(icon = Icons.Filled.AccountBalance, title = stringResource(R.string.iban_section_title))
                        Spacer(Modifier.height(8.dp))
                        // Arabic name in an Arabic UI, else English — used both while typing and
                        // in the saved view.
                        val ibanArabic = LocalConfiguration.current.locales[0].language == "ar"
                        val ibanUpdatedLabel = stringResource(R.string.iban_updated)
                        val ibanRemovedLabel = stringResource(R.string.iban_removed)

                        if (myIban.isNotBlank() && !ibanEditing) {
                            // Saved view: IBAN + detected bank on the left, with compact pencil /
                            // trash icon actions on the right — no text labels, so it reads clean.
                            val savedBank = SaudiBanks.nameForIbanBody(myIban.removePrefix("SA"), ibanArabic)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = myIban,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (savedBank != null) {
                                        Text(
                                            text = savedBank,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AccentIndigo
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    clipboard.setText(AnnotatedString(myIban))
                                    Toast.makeText(context, copiedLabel, Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = stringResource(R.string.copied),
                                        tint = AccentIndigo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { ibanEditing = true }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = stringResource(R.string.edit),
                                        tint = AccentIndigo
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.setMyIban("")
                                    ibanInput = ""
                                    ibanEditing = true
                                    Toast.makeText(context, ibanRemovedLabel, Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.remove),
                                        tint = DangerRed
                                    )
                                }
                            }
                        } else {
                            // Editor: a critical-accuracy note, the SA-frozen field, then Save.
                            Text(
                                text = stringResource(R.string.iban_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            val ibanComplete = ibanInput.length == 22
                            val ibanValid = ibanInput.isBlank() || ibanComplete
                            val detectedBank = if (ibanComplete)
                                SaudiBanks.nameForIbanBody(ibanInput, ibanArabic) else null
                            OutlinedTextField(
                                value = ibanInput,
                                onValueChange = { raw ->
                                    // Keep only alphanumerics, uppercase, cap at 22 (SA + 22 = the
                                    // full 24-char Saudi IBAN) so the user can't over-type.
                                    ibanInput = raw.uppercase().filter { it.isLetterOrDigit() }.take(22)
                                },
                                // Frozen country prefix — rendered by the field, not part of the
                                // editable text, so "SA" can never be removed.
                                prefix = { Text("SA") },
                                label = { Text(stringResource(R.string.iban_label)) },
                                placeholder = { Text(stringResource(R.string.iban_hint)) },
                                singleLine = true,
                                isError = !ibanValid,
                                supportingText = if (!ibanValid) {
                                    { Text(stringResource(R.string.iban_invalid), color = DangerRed) }
                                } else if (detectedBank != null) {
                                    { Text(detectedBank!!, color = AccentIndigo) }
                                } else null,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(
                                enabled = ibanComplete,
                                onClick = {
                                    viewModel.setMyIban("SA$ibanInput")
                                    ibanEditing = false
                                    Toast.makeText(context, ibanUpdatedLabel, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.save))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            // Saudi/GCC only: STC Pay mobile-wallet number — a second sharable payment method
            // alongside the IBAN. Reminders include whichever of the two the user has saved, so
            // filling one, the other, or both is how the user picks what to share.
            if (CurrencyLocaleMapper.isSaudiRiyalSymbol(currencySymbol)) {
                AnimatedSection(visible = contentVisible, delayMillis = 22) {
                    Column {
                        SettingsSectionHeader(icon = Icons.Filled.Smartphone, title = stringResource(R.string.stc_section_title))
                        Spacer(Modifier.height(8.dp))
                        val stcUpdatedLabel = stringResource(R.string.stc_updated)
                        val stcRemovedLabel = stringResource(R.string.stc_removed)

                        if (myStcPay.isNotBlank() && !stcEditing) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🇸🇦 +966 " + myStcPay.removePrefix("966").trimStart('0'),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    clipboard.setText(AnnotatedString(myStcPay))
                                    Toast.makeText(context, copiedLabel, Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = stringResource(R.string.copied),
                                        tint = AccentIndigo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { stcEditing = true }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = stringResource(R.string.edit),
                                        tint = AccentIndigo
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.setMyStcPay("")
                                    stcInput = ""
                                    stcEditing = true
                                    Toast.makeText(context, stcRemovedLabel, Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = stringResource(R.string.remove),
                                        tint = DangerRed
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.stc_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = stcInput,
                                onValueChange = { raw ->
                                    // Keep digits only; drop a pasted 966 country code and any
                                    // leading trunk 0 so the field holds just the local number.
                                    var d = raw.filter { it.isDigit() }
                                    if (d.startsWith("966")) d = d.removePrefix("966")
                                    stcInput = d.trimStart('0').take(10)
                                },
                                // Frozen Saudi country code — rendered by the field, not editable.
                                prefix = { Text("🇸🇦 +966 ") },
                                label = { Text(stringResource(R.string.stc_label)) },
                                placeholder = { Text(stringResource(R.string.stc_hint)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedButton(
                                enabled = stcInput.isNotBlank(),
                                onClick = {
                                    viewModel.setMyStcPay("966" + stcInput.trim())
                                    stcEditing = false
                                    Toast.makeText(context, stcUpdatedLabel, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.save))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            AnimatedSection(visible = contentVisible, delayMillis = 23) {
                SettingsSectionHeader(icon = Icons.Filled.AccountBalanceWallet, title = stringResource(R.string.settings_section_payment_accounts))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.payment_accounts_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showAccountManageSheet = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.manage_payment_accounts))
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 15) {
                SettingsSectionHeader(icon = Icons.Filled.Language, title = stringResource(R.string.language_label))
                Spacer(Modifier.height(8.dp))
                // Each language is shown in its OWN name — never translated into whatever
                // language is currently active. This dropdown used to build its labels from
                // stringResource(), which meant switching to Arabic re-translated "English" into
                // "الإنجليزية": once someone who can't read Arabic ends up there, there's no
                // Latin-script "English" left anywhere in the list to tap their way back out.
                // Every language now always reads the same regardless of the app's current
                // language — mirrors OnboardingScreen's language step, which already got this
                // right (native name + small English-name subtitle, both hardcoded).
                val langOptions = SETTINGS_LANGUAGES
                val currentLang = langOptions.firstOrNull { it.code == languagePref } ?: langOptions.first()
                val currentLangLabel = if (currentLang.native == currentLang.english) currentLang.native
                    else "${currentLang.native} (${currentLang.english})"
                ExposedDropdownMenuBox(
                    expanded = langExpanded,
                    onExpandedChange = { langExpanded = it }
                ) {
                    OutlinedTextField(
                        value = currentLangLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = langExpanded,
                        onDismissRequest = { langExpanded = false }
                    ) {
                        langOptions.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(lang.native, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                                        if (lang.native != lang.english) {
                                            Text(
                                                lang.english,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                },
                                onClick = { langExpanded = false; changeLanguage(lang.code) },
                                trailingIcon = if (languagePref == lang.code) {
                                    { Icon(Icons.Filled.Check, contentDescription = null, tint = AccentIndigo) }
                                } else null
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 60) {
                SettingsSectionHeader(icon = Icons.Filled.AttachMoney, title = stringResource(R.string.currency_label))
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.detected_region_note, "").trimEnd(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.width(4.dp))
                    if (CurrencyLocaleMapper.isSaudiRiyalSymbol(detectedCurrency)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_saudi_riyal),
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Text(
                            text = detectedCurrency,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                val currentCurrencyLabel = allCurrencies.firstOrNull { it.symbol == currencySymbol }?.label
                    ?: currencySymbol
                ExposedDropdownMenuBox(
                    expanded = currExpanded,
                    onExpandedChange = { currExpanded = it }
                ) {
                    OutlinedTextField(
                        value = currentCurrencyLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = currExpanded,
                        onDismissRequest = { currExpanded = false }
                    ) {
                        allCurrencies.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = { currExpanded = false; requestCurrencyChange(option.symbol) },
                                trailingIcon = if (currencySymbol == option.symbol) {
                                    { Icon(Icons.Filled.Check, contentDescription = null, tint = AccentIndigo) }
                                } else null
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.data_local_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 90) {
                SettingsSectionHeader(icon = Icons.Filled.Sms, title = stringResource(R.string.sms_detection_title))
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.sms_detection_toggle_label),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = smsDetectionEnabled,
                                onCheckedChange = { viewModel.setSmsDetectionEnabled(it) }
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.sms_detection_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 105) {
                SettingsSectionHeader(icon = Icons.Filled.Alarm, title = stringResource(R.string.reminder_section_title))
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.reminder_toggle_label),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.setReminderEnabled(enabled)
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.reminder_toggle_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        if (reminderEnabled) {
                            Spacer(Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        pickerHour = reminderHour
                                        pickerSlot = 1
                                        showTimePicker = true
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.reminder_time_label),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = formatReminderHour(reminderHour),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AccentIndigo
                                )
                            }
                            // Test notification button — hidden for now

                            // ── Optional second daily reminder ──────────────────────────
                            Spacer(Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.reminder2_toggle_label),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = stringResource(R.string.reminder2_toggle_desc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Switch(
                                    checked = reminder2Enabled,
                                    onCheckedChange = { enabled -> viewModel.setReminder2Enabled(enabled) }
                                )
                            }
                            if (reminder2Enabled) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            pickerHour = reminderHour2
                                            pickerSlot = 2
                                            showTimePicker = true
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.reminder2_time_label),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = formatReminderHour(reminderHour2),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AccentIndigo
                                    )
                                }
                            }
                        }
                        // Payday picker — always visible
                        Spacer(Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPaydayPicker = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.payday_title),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (paydayDayOfMonth > 0)
                                    stringResource(R.string.payday_day_label, paydayDayOfMonth)
                                else stringResource(R.string.payday_not_set),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (paydayDayOfMonth > 0) AccentIndigo
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Security / Biometric lock ─────────────────────────────────────
            AnimatedSection(visible = contentVisible, delayMillis = 115) {
                SettingsSectionHeader(icon = Icons.Filled.Fingerprint, title = stringResource(R.string.security_section_title))
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.biometric_lock_toggle_label),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            var biometricChecked by remember { mutableStateOf(viewModel.biometricEnabled) }
                            Switch(
                                checked = biometricChecked,
                                onCheckedChange = {
                                    biometricChecked = it
                                    viewModel.setBiometricEnabled(it)
                                }
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.biometric_lock_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Family Mode — hidden (feature not yet ready for release)
            // To re-enable: uncomment the block between the markers below.
            // BEGIN_FAMILY_MODE_UI …  END_FAMILY_MODE_UI

            Spacer(Modifier.height(24.dp))

            // ── Data Backup / Restore ─────────────────────────────────────────
            AnimatedSection(visible = contentVisible, delayMillis = 118) {
                SettingsSectionHeader(icon = Icons.Filled.Backup, title = stringResource(R.string.backup_section_title))
                Text(
                    text = stringResource(R.string.backup_scope_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
                Spacer(Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingsActionRow(
                            icon = Icons.Filled.Upload,
                            label = stringResource(R.string.backup_export_label),
                            onClick = {
                                viewModel.exportBackup(
                                    onUri = { uri ->
                                        BackupManager.shareExport(context, uri, backupChooserTitle)
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, "${context.getString(R.string.backup_error)}: $err", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        SettingsActionRow(
                            icon = Icons.Filled.Restore,
                            label = stringResource(R.string.backup_import_label),
                            onClick = {
                                importLauncher.launch(arrayOf("application/json", "*/*"))
                            }
                        )
                    }
                }
            }


            // Google Drive backup section hidden — re-enable in next release once
            // OAuth consent screen is fully configured and debug SHA-1 is registered.

            Spacer(Modifier.height(24.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 120) {
                SettingsSectionHeader(icon = Icons.Filled.Fingerprint, title = stringResource(R.string.about_section_title))
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    CustomerIdRow(customerId = viewModel.customerId, onCopy = { copyCustomerId() })
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 180) {
                SettingsSectionHeader(icon = Icons.Filled.Info, title = stringResource(R.string.more_section_title))
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        SettingsActionRow(
                            icon = Icons.Filled.Share,
                            label = stringResource(R.string.share_app),
                            onClick = { shareApp() }
                        )
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        SettingsActionRow(
                            icon = Icons.Filled.Star,
                            label = stringResource(R.string.rate_us),
                            onClick = { rateApp() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 240) {
                OutlinedButton(
                    onClick = { showResetStep1 = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.reset_all_data))
                }
            }

            Spacer(Modifier.height(32.dp))

            AnimatedSection(visible = contentVisible, delayMillis = 300) {
                SettingsFooter(
                    screenshotMode = viewModel.screenshotMode,
                    onToggle       = { viewModel.toggleScreenshotMode() }
                )
            }
        }
    }
    } // close Box wrapping AnimatedBlobBackground + Scaffold

    pendingCurrencySymbol?.let { newSymbol ->
        AlertDialog(
            onDismissRequest = { pendingCurrencySymbol = null },
            title = { Text(stringResource(R.string.convert_currency_title)) },
            text = {
                Column {
                    // MoneyText so a Saudi Riyal symbol on either side of the conversion renders
                    // as the dedicated icon glyph, never the raw "ر.س" text abbreviation.
                    MoneyText(
                        formatted = stringResource(R.string.convert_currency_message, currencySymbol, newSymbol),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = Int.MAX_VALUE
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = {
                            rateText = it
                            rateError = false
                        },
                        label = { Text(stringResource(R.string.convert_currency_rate_hint)) },
                        isError = rateError,
                        supportingText = if (rateError) {
                            { Text(stringResource(R.string.error_invalid_rate)) }
                        } else null,
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val rate = rateText.toDoubleOrNull()
                    if (rate == null || rate <= 0.0) {
                        rateError = true
                    } else {
                        viewModel.convertCurrency(newSymbol, rate) {
                            pendingCurrencySymbol = null
                            onBack()
                        }
                    }
                }) { Text(stringResource(R.string.convert)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingCurrencySymbol = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showResetStep1) {
        AlertDialog(
            onDismissRequest = { showResetStep1 = false },
            title = { Text(stringResource(R.string.reset_all_data)) },
            text = { Text(stringResource(R.string.reset_confirm_1)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetStep1 = false
                    showResetStep2 = true
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetStep1 = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showResetStep2) {
        val toastMessage = stringResource(R.string.toast_all_reset)
        AlertDialog(
            onDismissRequest = { showResetStep2 = false },
            title = { Text(stringResource(R.string.reset_all_data)) },
            text = { Text(stringResource(R.string.reset_confirm_2)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAllData {
                        showResetStep2 = false
                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                    }
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetStep2 = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(
                    stringResource(
                        if (pickerSlot == 2) R.string.reminder2_time_label
                        else R.string.reminder_time_label
                    )
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatReminderHour(pickerHour),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    // The slider alone can't reliably land on every one of the 24 discrete
                    // hour stops — dragging across a narrow track packed with that many steps
                    // means a normal swipe can overshoot the exact tick you're aiming for
                    // (e.g. landing on 8 when dragging toward 7). The +/- buttons give an
                    // always-exact way to nudge one hour at a time; the slider stays for
                    // quick coarse scrubbing.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { pickerHour = (pickerHour - 1 + 24) % 24 }) {
                            Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.reminder_hour_decrease))
                        }
                        Slider(
                            value = pickerHour.toFloat(),
                            onValueChange = { pickerHour = it.toInt() },
                            valueRange = 0f..23f,
                            steps = 22,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { pickerHour = (pickerHour + 1) % 24 }) {
                            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.reminder_hour_increase))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pickerSlot == 2) viewModel.setReminderHour2(pickerHour)
                    else viewModel.setReminderHour(pickerHour)
                    showTimePicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    // Payday day-of-month picker dialog
    if (showPaydayPicker) {
        var pickerDay by remember { mutableStateOf(paydayDayOfMonth.coerceIn(1, 31).toFloat()) }
        AlertDialog(
            onDismissRequest = { showPaydayPicker = false },
            title = { Text(stringResource(R.string.payday_title)) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.payday_day_label, pickerDay.toInt()),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value = pickerDay,
                        onValueChange = { pickerDay = it },
                        valueRange = 1f..31f,
                        steps = 29    // 31 values → 29 inner steps
                    )
                    Text(
                        text = stringResource(R.string.payday_picker_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setPaydayDayOfMonth(pickerDay.toInt())
                    showPaydayPicker = false
                }) { Text(stringResource(R.string.done)) }
            },
            dismissButton = {
                TextButton(onClick = { showPaydayPicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showAccountManageSheet) {
        com.expensetracker.app.ui.components.PaymentAccountManageSheet(
            accounts = paymentAccounts,
            onDismiss = { showAccountManageSheet = false },
            onRename = { account, name -> viewModel.renameAccount(account, name) },
            onRecolor = { account, hex -> viewModel.recolorAccount(account, hex) },
            onAddAccount = { name, hex -> viewModel.addAccount(name, hex) },
            onDeleteAccount = { account -> viewModel.deleteAccount(account) }
        )
    }
}

/** Wraps a settings block in a gentle staggered fade + slide-up entrance. */
@Composable
private fun AnimatedSection(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 320, delayMillis = delayMillis)) +
            slideInVertically(
                animationSpec = tween(durationMillis = 320, delayMillis = delayMillis),
                initialOffsetY = { it / 6 }
            )
    ) {
        Column { content() }
    }
}

/** Small icon badge + title used to head up each settings block. */
@Composable
private fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(AccentIndigoLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AccentIndigo, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

/** Customer's locally-generated, stable ID — shown here and stamped into every PDF export. */
@Composable
private fun CustomerIdRow(customerId: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.customer_id_label),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(customerId, style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = onCopy) {
            Icon(
                Icons.Filled.ContentCopy,
                contentDescription = stringResource(R.string.customer_id_label),
                tint = AccentIndigo
            )
        }
    }
}

@Composable
private fun SettingsActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "settingsRowPress"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pressScale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(vertical = 14.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AccentIndigo)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}

/** Formats a 24-hour value (0–23) as a friendly 12-hour AM/PM string, e.g. 21 → "9:00 PM". */
private fun formatReminderHour(hour: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$h:00 $amPm"
}

/** Bottom-of-screen brand footer — small, muted, out of the way.
 *  Long-pressing the version label toggles screenshot mode (hides ads for clean captures). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsFooter(
    screenshotMode: Boolean,
    onToggle: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val screenshotModeOnMsg  = stringResource(R.string.screenshot_mode_on)
    val screenshotModeOffMsg = stringResource(R.string.screenshot_mode_off)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.powered_by_footer),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = if (screenshotMode)
                "${stringResource(R.string.app_version_label)}  📷"
            else
                stringResource(R.string.app_version_label),
            style = MaterialTheme.typography.labelSmall,
            color = if (screenshotMode) AccentIndigo else TextMuted,
            modifier = Modifier.combinedClickable(
                onClick    = {},
                onLongClick = {
                    onToggle()
                    val msg = if (!screenshotMode) screenshotModeOnMsg else screenshotModeOffMsg
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
        )
    }
}
