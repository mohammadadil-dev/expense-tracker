package com.expensetracker.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.ui.theme.AccentGreenDark
import com.expensetracker.app.ui.theme.AccentGreenMid
import com.expensetracker.app.ui.theme.BrandAmber

// ─── Language list ────────────────────────────────────────────────────────────

private data class OnboardLanguage(
    val code: String,
    val name: String,       // English name
    val native: String,     // Name in its own script
    val flag: String
)

private val ONBOARD_LANGUAGES = listOf(
    OnboardLanguage("en", "English",   "English",  "🇬🇧"),
    OnboardLanguage("ar", "Arabic",    "العربية",  "🇸🇦"),
    OnboardLanguage("hi", "Hindi",     "हिंदी",    "🇮🇳"),
    OnboardLanguage("ur", "Urdu",      "اردو",     "🇵🇰"),
    OnboardLanguage("bn", "Bengali",   "বাংলা",    "🇧🇩"),
    OnboardLanguage("tl", "Filipino",  "Filipino", "🇵🇭"),
    OnboardLanguage("ta", "Tamil",     "தமிழ்",    "🇮🇳"),
    OnboardLanguage("te", "Telugu",    "తెలుగు",   "🇮🇳"),
    OnboardLanguage("kn", "Kannada",   "ಕನ್ನಡ",    "🇮🇳"),
    OnboardLanguage("ml", "Malayalam", "മലയാളം",   "🇮🇳"),
    OnboardLanguage("mr", "Marathi",   "मराठी",    "🇮🇳"),
    OnboardLanguage("gu", "Gujarati",  "ગુજરાતી",  "🇮🇳"),
    OnboardLanguage("pa", "Punjabi",   "ਪੰਜਾਬੀ",   "🇮🇳"),
)

// ─── Currency list ────────────────────────────────────────────────────────────

private data class OnboardCurrency(val symbol: String, val name: String, val region: String)

private val ONBOARD_CURRENCIES = listOf(
    OnboardCurrency("ر.س", "Saudi Riyal",        "Saudi Arabia 🇸🇦"),
    OnboardCurrency("د.إ", "UAE Dirham",          "UAE 🇦🇪"),
    OnboardCurrency("₹",   "Indian Rupee",        "India 🇮🇳"),
    OnboardCurrency("₨",   "Pakistani Rupee",     "Pakistan 🇵🇰"),
    OnboardCurrency("৳",   "Bangladeshi Taka",    "Bangladesh 🇧🇩"),
    OnboardCurrency("₱",   "Philippine Peso",     "Philippines 🇵🇭"),
    OnboardCurrency("$",   "US Dollar",           "USA / Canada 🇺🇸"),
    OnboardCurrency("€",   "Euro",                "Europe 🇪🇺"),
    OnboardCurrency("£",   "British Pound",       "United Kingdom 🇬🇧"),
    OnboardCurrency("ج.م", "Egyptian Pound",      "Egypt 🇪🇬"),
    OnboardCurrency("د.ك", "Kuwaiti Dinar",       "Kuwait 🇰🇼"),
    OnboardCurrency("ر.ق", "Qatari Riyal",        "Qatar 🇶🇦"),
    OnboardCurrency("¥",   "Japanese Yen",        "Japan / China 🇯🇵"),
    OnboardCurrency("₩",   "Korean Won",          "South Korea 🇰🇷"),
    OnboardCurrency("₦",   "Nigerian Naira",      "Nigeria 🇳🇬"),
    OnboardCurrency("R",   "South African Rand",  "South Africa 🇿🇦"),
)

// ─── Main screen ──────────────────────────────────────────────────────────────

/**
 * Full-screen first-launch wizard (5 steps).
 *
 * Step 0 — Welcome + name
 * Step 1 — Language picker
 * Step 2 — Currency picker        ← rendered in the chosen locale
 * Step 3 — Monthly income + budget
 * Step 4 — Daily reminder toggle
 *
 * When the user picks a language on Step 1 and taps Continue, [onApplyLanguage] is invoked
 * instead of simply incrementing the step counter. That callback saves state to prefs and
 * calls [LocaleHelper.applyLanguagePreference], which recreates the Activity. The wizard
 * then reopens with [initialStep] = 2, [initialName], and [initialLanguageCode] restored
 * from prefs so the user picks up exactly where they left off — now fully in their language.
 *
 * @param initialStep         Step to open at on first composition (0 for a fresh install, 2
 *                            after a mid-wizard locale change).
 * @param initialName         Name pre-filled from prefs after a mid-wizard recreation.
 * @param initialLanguageCode Language code pre-selected from prefs after a mid-wizard recreation.
 * @param onApplyLanguage     Called when Continue is pressed on Step 1; saves progress + applies locale.
 * @param onDone              Called when the wizard is fully completed.
 */
@Composable
fun OnboardingScreen(
    initialStep: Int = 0,
    initialName: String = "",
    initialLanguageCode: String = "en",
    onApplyLanguage: (name: String, languageCode: String) -> Unit = { _, _ -> },
    onDone: (
        name: String,
        languageCode: String,
        currencySymbol: String,
        salary: Double,
        budget: Double,
        reminderEnabled: Boolean,
        reminderHour: Int
    ) -> Unit
) {
    val totalSteps = 5
    var step by remember { mutableIntStateOf(initialStep) }

    // Data collected across steps.
    // If we resumed mid-wizard (initialStep >= 2), restore name + language from prefs.
    var name             by remember { mutableStateOf(initialName) }
    var selectedLanguage by remember {
        mutableStateOf(
            if (initialStep >= 2)
                ONBOARD_LANGUAGES.find { it.code == initialLanguageCode } ?: ONBOARD_LANGUAGES[0]
            else ONBOARD_LANGUAGES[0]
        )
    }
    var selectedCurrency by remember { mutableStateOf(ONBOARD_CURRENCIES[0]) }
    var salaryText       by remember { mutableStateOf("") }
    var budgetText       by remember { mutableStateOf("") }
    var reminderEnabled  by remember { mutableStateOf(true) }
    var reminderHour     by remember { mutableIntStateOf(21) }

    val bgGradient = Brush.verticalGradient(
        listOf(AccentGreenDark, AccentGreenMid, Color(0xFF1C5E3A))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // Step indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0 until totalSteps).forEach { i ->
                    Box(
                        modifier = Modifier
                            .then(
                                if (i == step) Modifier.size(24.dp, 8.dp)
                                else Modifier.size(8.dp)
                            )
                            .clip(CircleShape)
                            .background(
                                if (i <= step) BrandAmber
                                else Color.White.copy(alpha = 0.35f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Animated step content
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                    }
                },
                modifier = Modifier.weight(1f),
                label = "onboardingStep"
            ) { currentStep ->
                when (currentStep) {
                    0 -> WelcomeStep(name = name, onNameChange = { name = it })
                    1 -> LanguagePickStep(
                        selected = selectedLanguage,
                        onSelect  = { selectedLanguage = it }
                    )
                    2 -> CurrencyPickStep(
                        selected = selectedCurrency,
                        onSelect  = { selectedCurrency = it }
                    )
                    3 -> FinancialStep(
                        salaryText     = salaryText,
                        budgetText     = budgetText,
                        currencySymbol = selectedCurrency.symbol,
                        onSalaryChange = { salaryText = it },
                        onBudgetChange = { budgetText = it }
                    )
                    4 -> ReminderStep(
                        enabled      = reminderEnabled,
                        hour         = reminderHour,
                        onToggle     = { reminderEnabled = it },
                        onHourChange = { reminderHour = it }
                    )
                }
            }

            // Navigation row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back — hidden on step 0; on resumed-from-step-2 we allow going back to step 1
                if (step > 0) {
                    TextButton(onClick = { step-- }) {
                        Text(
                            if (step >= 2) stringResource(R.string.onboard_back) else "← Back",
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                } else {
                    Spacer(Modifier.width(80.dp))
                }

                // Skip (financial and reminder steps only)
                if (step >= 3) {
                    TextButton(onClick = {
                        if (step < totalSteps - 1) {
                            step++
                        } else {
                            onDone(name.trim(), selectedLanguage.code,
                                selectedCurrency.symbol, 0.0, 0.0,
                                reminderEnabled, reminderHour)
                        }
                    }) {
                        Text(stringResource(R.string.onboard_skip), color = Color.White.copy(alpha = 0.55f))
                    }
                }

                // Continue / Get Started
                Button(
                    onClick = {
                        when {
                            step == 1 -> {
                                // Step 1 = language picker. Applying the locale usually recreates
                                // the Activity (and the wizard resumes at step 2 from prefs).
                                // If the chosen locale is ALREADY active, the OS skips recreation
                                // and the screen would freeze. Incrementing step here covers both
                                // cases: if the Activity recreates, this state is discarded; if it
                                // doesn't (same locale selected), we advance normally.
                                onApplyLanguage(name.trim(), selectedLanguage.code)
                                step++
                            }
                            step < totalSteps - 1 -> step++
                            else -> {
                                val salary = salaryText.replace(",", "").toDoubleOrNull() ?: 0.0
                                val budget = budgetText.replace(",", "").toDoubleOrNull() ?: 0.0
                                onDone(
                                    name.trim(), selectedLanguage.code,
                                    selectedCurrency.symbol, salary, budget,
                                    reminderEnabled, reminderHour
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when {
                            step >= 2 && step < totalSteps - 1 -> stringResource(R.string.onboard_continue)
                            step == totalSteps - 1 -> stringResource(R.string.onboard_get_started)
                            else -> "Continue →"   // steps 0-1 always shown in English
                        },
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Step 0: Welcome + name ───────────────────────────────────────────────────

@Composable
private fun WelcomeStep(name: String, onNameChange: (String) -> Unit) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor    = BrandAmber,
        unfocusedBorderColor  = Color.White.copy(alpha = 0.35f),
        focusedLabelColor     = BrandAmber,
        unfocusedLabelColor   = Color.White.copy(alpha = 0.6f),
        cursorColor           = BrandAmber,
        focusedTextColor      = Color.White,
        unfocusedTextColor    = Color.White,
        focusedPlaceholderColor   = Color.White.copy(alpha = 0.4f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👋", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Welcome to\nBaqaya",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Track spending. Reach your goals.",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.7f)),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))
        OutlinedTextField(
            value         = name,
            onValueChange = onNameChange,
            label         = { Text("Your name (optional)") },
            placeholder   = { Text("e.g. Mohammad") },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            colors        = fieldColors
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = "We'll use this for a personal greeting on the home screen.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.5f)),
            textAlign = TextAlign.Center
        )
    }
}

// ─── Step 1: Language picker ──────────────────────────────────────────────────

@Composable
private fun LanguagePickStep(selected: OnboardLanguage, onSelect: (OnboardLanguage) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text("🌐", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Pick your language",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White, fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "The app will appear in your chosen language.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.65f)),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ONBOARD_LANGUAGES) { lang ->
                val isSelected = lang.code == selected.code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) BrandAmber.copy(alpha = 0.18f)
                            else Color.White.copy(alpha = 0.07f)
                        )
                        .then(
                            if (isSelected) Modifier.border(
                                width = 1.5.dp,
                                color = BrandAmber,
                                shape = RoundedCornerShape(12.dp)
                            ) else Modifier
                        )
                        .clickable { onSelect(lang) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lang.flag,
                        fontSize = 26.sp,
                        modifier = Modifier.width(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = lang.native,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isSelected) BrandAmber else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (lang.native != lang.name) {
                            Text(
                                text = lang.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.55f)
                                )
                            )
                        }
                    }
                    if (isSelected) {
                        Spacer(Modifier.weight(1f))
                        Text("✓", color = BrandAmber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// ─── Step 2: Currency picker ──────────────────────────────────────────────────

@Composable
private fun CurrencyPickStep(selected: OnboardCurrency, onSelect: (OnboardCurrency) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text("💱", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.onboard_currency_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White, fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.onboard_currency_body),
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.65f)),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ONBOARD_CURRENCIES) { currency ->
                val isSelected = currency.symbol == selected.symbol
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) BrandAmber.copy(alpha = 0.18f)
                            else Color.White.copy(alpha = 0.07f)
                        )
                        .then(
                            if (isSelected) Modifier.border(
                                width = 1.5.dp,
                                color = BrandAmber,
                                shape = RoundedCornerShape(12.dp)
                            ) else Modifier
                        )
                        .clickable { onSelect(currency) }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val symbolColor = if (isSelected) BrandAmber else Color.White
                    Box(
                        modifier = Modifier.width(42.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (currency.symbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
                            Icon(
                                painter = painterResource(R.drawable.ic_saudi_riyal),
                                contentDescription = null,
                                tint = symbolColor,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text  = currency.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color      = symbolColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            currency.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color      = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            currency.region,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─── Step 3: Financial targets (salary + budget) ──────────────────────────────

/** Label like "Monthly income (ر.س)" but swaps the text symbol for the riyal icon. */
@Composable
private fun FinancialFieldLabel(text: String, currencySymbol: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text  = "$text (",
            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
        )
        if (currencySymbol == CurrencyLocaleMapper.SAUDI_RIYAL_SYMBOL) {
            Icon(
                painter            = painterResource(R.drawable.ic_saudi_riyal),
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(14.dp)
            )
        } else {
            Text(
                text  = currencySymbol,
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
            )
        }
        Text(
            text  = ")",
            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
        )
    }
}

@Composable
private fun FinancialStep(
    salaryText: String,
    budgetText: String,
    currencySymbol: String,
    onSalaryChange: (String) -> Unit,
    onBudgetChange: (String) -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor    = BrandAmber,
        unfocusedBorderColor  = Color.White.copy(alpha = 0.35f),
        focusedLabelColor     = BrandAmber,
        unfocusedLabelColor   = Color.White.copy(alpha = 0.6f),
        cursorColor           = BrandAmber,
        focusedTextColor      = Color.White,
        unfocusedTextColor    = Color.White,
        focusedPlaceholderColor   = Color.White.copy(alpha = 0.4f),
        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💰", fontSize = 48.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.onboard_finances_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White, fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.onboard_finances_body),
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.65f)),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        FinancialFieldLabel(text = stringResource(R.string.onboard_income_label), currencySymbol = currencySymbol)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value             = salaryText,
            onValueChange     = onSalaryChange,
            placeholder       = { Text("e.g. 5,000") },
            keyboardOptions   = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine        = true,
            modifier          = Modifier.fillMaxWidth(),
            colors            = fieldColors
        )

        Spacer(Modifier.height(20.dp))

        FinancialFieldLabel(text = stringResource(R.string.onboard_budget_label), currencySymbol = currencySymbol)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value             = budgetText,
            onValueChange     = onBudgetChange,
            placeholder       = { Text("e.g. 3,000") },
            keyboardOptions   = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine        = true,
            modifier          = Modifier.fillMaxWidth(),
            colors            = fieldColors
        )
    }
}

// ─── Step 4: Daily reminder ───────────────────────────────────────────────────

@Composable
private fun ReminderStep(
    enabled: Boolean,
    hour: Int,
    onToggle: (Boolean) -> Unit,
    onHourChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔔", fontSize = 48.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.onboard_reminder_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White, fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.onboard_reminder_body),
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.65f)),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.09f))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.onboard_reminder_toggle),
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
            Switch(
                checked         = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor  = Color.White,
                    checkedTrackColor  = BrandAmber,
                    uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }

        if (enabled) {
            Spacer(Modifier.height(20.dp))
            val displayHour = when {
                hour == 0  -> "12 AM"
                hour < 12  -> "$hour AM"
                hour == 12 -> "12 PM"
                else       -> "${hour - 12} PM"
            }
            Text(
                stringResource(R.string.onboard_reminder_time, displayHour),
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
            )
            Spacer(Modifier.height(8.dp))
            Slider(
                value         = hour.toFloat(),
                onValueChange = { onHourChange(it.toInt()) },
                valueRange    = 6f..23f,
                steps         = 16,
                colors = SliderDefaults.colors(
                    thumbColor        = BrandAmber,
                    activeTrackColor  = BrandAmber,
                    inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BrandAmber.copy(alpha = 0.15f))
                .border(1.dp, BrandAmber.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "✅ ${stringResource(R.string.onboard_all_set_title)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BrandAmber, fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.onboard_all_set_body),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
