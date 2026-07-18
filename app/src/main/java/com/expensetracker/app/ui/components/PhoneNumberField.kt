package com.expensetracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextSecondary

/**
 * Shared country-code phone entry, used by every place in the app that captures a party's or
 * split member's phone number for WhatsApp/SMS reminders (Khata's Add/Edit Party sheet, Split's
 * "set their phone" dialog, etc.).
 *
 * Centralised here after two independent bugs: (1) the local-number field had no digit cap, so
 * a full international number (with country code baked in) or a pasted string of any length
 * could be typed into what's meant to be just the *national* number, producing an unusably long
 * phone string; (2) one call site (Split's phone dialog) had no country-code picker at all, so
 * the stored number never got a "+<code>" prefix and WhatsApp rejected it outright ("+51375230
 * is not a valid phone number"). Every call site should go through [CountryCodePhoneField] +
 * [CountryPickerDialog] instead of rolling its own text field.
 */

data class CountryEntry(
    val flag: String,
    val name: String,
    val dial: String,
    /** National significant number length for this country's mobile numbers — e.g. India's
     *  10-digit mobiles, Saudi's 9-digit ones. Caps what can be typed into the local-number
     *  field so a stray extra digit (or an accidentally pasted full international number)
     *  can't silently produce an invalid, oversized phone string. */
    val maxDigits: Int
)

val COUNTRY_ENTRIES = listOf(
    CountryEntry("🇸🇦", "Saudi Arabia",  "+966", 9),
    CountryEntry("🇮🇳", "India",          "+91",  10),
    CountryEntry("🇵🇰", "Pakistan",       "+92",  10),
    CountryEntry("🇧🇩", "Bangladesh",     "+880", 10),
    CountryEntry("🇵🇭", "Philippines",    "+63",  10),
    CountryEntry("🇦🇪", "UAE",            "+971", 9),
    CountryEntry("🇶🇦", "Qatar",          "+974", 8),
    CountryEntry("🇰🇼", "Kuwait",         "+965", 8),
    CountryEntry("🇧🇭", "Bahrain",        "+973", 8),
    CountryEntry("🇴🇲", "Oman",           "+968", 8),
    CountryEntry("🇪🇬", "Egypt",          "+20",  10),
    CountryEntry("🇯🇴", "Jordan",         "+962", 9),
    CountryEntry("🇱🇧", "Lebanon",        "+961", 8),
    CountryEntry("🇮🇶", "Iraq",           "+964", 10),
    CountryEntry("🇸🇾", "Syria",          "+963", 9),
    CountryEntry("🇾🇪", "Yemen",          "+967", 9),
    CountryEntry("🇱🇰", "Sri Lanka",      "+94",  9),
    CountryEntry("🇳🇵", "Nepal",          "+977", 10),
    CountryEntry("🇬🇧", "UK",             "+44",  10),
    CountryEntry("🇺🇸", "USA / Canada",   "+1",   10),
    CountryEntry("🇦🇺", "Australia",      "+61",  9),
)

/** Auto-detect country from device locale; falls back to Saudi Arabia. */
fun defaultCountryEntry(): CountryEntry {
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
fun splitPhone(stored: String): Pair<String, String> {
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

/** Builds the full "+<code><digits>" phone string to persist, or "" if [localNumber] is blank. */
fun buildFullPhone(country: CountryEntry, localNumber: String): String =
    if (localNumber.isBlank()) "" else "${country.dial}${localNumber.trim()}"

/**
 * Country badge (flag + dial code, tap to open [CountryPickerDialog]) plus the local-number
 * field. Always laid out left-to-right regardless of the surrounding layout direction, since a
 * phone number reads LTR even inside an RTL (Arabic) layout.
 *
 * Input is filtered to digits only and capped at [CountryEntry.maxDigits] as the user types —
 * this is the fix for numbers like "8604371544500" (13 digits typed into a 10-digit Indian
 * mobile field) ending up saved and sent to WhatsApp as-is.
 */
@Composable
fun CountryCodePhoneField(
    selectedCountry: CountryEntry,
    onCountryClick: () -> Unit,
    localNumber: String,
    onLocalNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "512 345 678"
) {
    CompositionLocalProvider(   LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onCountryClick,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.heightIn(min = 56.dp)
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
                        contentDescription = stringResource(R.string.select_country_code),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            OutlinedTextField(
                value = localNumber,
                onValueChange = { raw ->
                    val digitsOnly = raw.filter { it.isDigit() }.take(selectedCountry.maxDigits)
                    onLocalNumberChange(digitsOnly)
                },
                placeholder = { Text(placeholder) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CountryPickerDialog(
    selectedCountry: CountryEntry,
    onSelect: (CountryEntry) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                                else Color.Transparent
                            )
                            .clickable { onSelect(entry) }
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
