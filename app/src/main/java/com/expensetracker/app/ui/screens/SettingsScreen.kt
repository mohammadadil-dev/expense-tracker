package com.expensetracker.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.BackgroundScrollSignal
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonPink
import com.expensetracker.app.ui.theme.NeonTeal
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.viewmodel.ExpenseViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val clipboardManager = LocalClipboardManager.current
    val languagePref by viewModel.languagePref.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val smsDetectionEnabled by viewModel.smsDetectionEnabled.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    var nameInput by remember(displayName) { mutableStateOf(displayName) }
    var showResetStep1 by remember { mutableStateOf(false) }
    var showResetStep2 by remember { mutableStateOf(false) }
    var pendingCurrencySymbol by remember { mutableStateOf<String?>(null) }
    var rateText by remember { mutableStateOf("") }
    var rateError by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    var currExpanded by remember { mutableStateOf(false) }

    val detectedCurrency = remember { CurrencyLocaleMapper.detectFromDevice(context) }
    val customerIdCopiedLabel = stringResource(R.string.customer_id_copied)

    // Drives a gentle staggered fade/slide-in for each section the first time this screen
    // appears, instead of everything popping in at once.
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    // Switching the in-app language needs the Activity to be torn down and rebuilt so
    // every stringResource() call re-reads from the new locale's resources. The OS does
    // this automatically on API 33+, but we also force it explicitly so it's instant and
    // reliable across emulators/devices instead of waiting on the framework callback.
    fun changeLanguage(pref: String) {
        viewModel.setLanguagePref(pref)
        onBack()  // Pop back to dashboard before recreating so the back stack is clean
        activity?.recreate()
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

    // Settings gets its own touch-reactive background instance — cyan/pink/teal instead of
    // Dashboard's violet/indigo/green, so the two screens feel like distinct "places".
    Box(modifier = Modifier.fillMaxSize()) {
    AnimatedBlobBackground(
        blobColors = listOf(NeonCyan, NeonPink, NeonTeal),
        modifier = Modifier.fillMaxSize()
    )
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
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

            AnimatedSection(visible = contentVisible, delayMillis = 15) {
                SettingsSectionHeader(icon = Icons.Filled.Language, title = stringResource(R.string.language_label))
                Spacer(Modifier.height(8.dp))
                val langOptions = listOf(
                    "en" to stringResource(R.string.language_english),
                    "ar" to stringResource(R.string.language_arabic),
                    "hi" to stringResource(R.string.language_hindi),
                    "ur" to stringResource(R.string.language_urdu),
                    "tl" to stringResource(R.string.language_tagalog),
                )
                val currentLangLabel = langOptions.firstOrNull { it.first == languagePref }?.second ?: "English"
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
                        langOptions.forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = { langExpanded = false; changeLanguage(code) },
                                trailingIcon = if (languagePref == code) {
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
                Text(
                    text = stringResource(R.string.detected_region_note, detectedCurrency),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
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
                SettingsFooter()
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

/** Bottom-of-screen brand footer — small, muted, out of the way. */
@Composable
private fun SettingsFooter() {
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
            text = stringResource(R.string.app_version_label),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}
