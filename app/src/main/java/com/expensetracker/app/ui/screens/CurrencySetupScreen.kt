package com.expensetracker.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.CurrencyLocaleMapper

private data class CurrencySetupOption(val symbol: String, val name: String, val countries: String)

private val setupCurrencies = listOf(
    CurrencySetupOption("ر.س",  "Saudi Riyal",         "Saudi Arabia"),
    CurrencySetupOption("د.إ",  "UAE Dirham",           "UAE"),
    CurrencySetupOption("₹",    "Indian Rupee",         "India"),
    CurrencySetupOption("₨",    "Pakistani Rupee",      "Pakistan"),
    CurrencySetupOption("৳",    "Bangladeshi Taka",     "Bangladesh"),
    CurrencySetupOption("₱",    "Philippine Peso",      "Philippines"),
    CurrencySetupOption("$",    "US Dollar",            "USA / Canada / Australia"),
    CurrencySetupOption("€",    "Euro",                 "Europe"),
    CurrencySetupOption("£",    "British Pound",        "United Kingdom"),
    CurrencySetupOption("ج.م",  "Egyptian Pound",       "Egypt"),
    CurrencySetupOption("د.ك",  "Kuwaiti Dinar",        "Kuwait"),
    CurrencySetupOption("ر.ق",  "Qatari Riyal",         "Qatar"),
    CurrencySetupOption("¥",    "Japanese Yen",         "Japan / China"),
    CurrencySetupOption("₩",    "Korean Won",           "South Korea"),
    CurrencySetupOption("₺",    "Turkish Lira",         "Turkey"),
    CurrencySetupOption("₽",    "Russian Ruble",        "Russia"),
    CurrencySetupOption("R$",   "Brazilian Real",       "Brazil"),
    CurrencySetupOption("Rp",   "Indonesian Rupiah",    "Indonesia"),
    CurrencySetupOption("RM",   "Malaysian Ringgit",    "Malaysia"),
    CurrencySetupOption("฿",    "Thai Baht",            "Thailand"),
    CurrencySetupOption("₫",    "Vietnamese Dong",      "Vietnam"),
    CurrencySetupOption("₪",    "Israeli Shekel",       "Israel"),
    CurrencySetupOption("₦",    "Nigerian Naira",       "Nigeria"),
    CurrencySetupOption("R",    "South African Rand",   "South Africa"),
    CurrencySetupOption("KSh",  "Kenyan Shilling",      "Kenya"),
)

@Composable
fun CurrencySetupScreen(
    onCurrencyChosen: (String) -> Unit
) {
    val context = LocalContext.current
    // Detect fresh from SIM/network — never use the previously-saved (possibly wrong) symbol
    val detectedSymbol = remember { CurrencyLocaleMapper.detectFromDevice(context) }
    // Pre-select the detected currency so the user just needs to tap "Continue →"
    var selected by remember { mutableStateOf<String?>(detectedSymbol.takeIf { sym ->
        setupCurrencies.any { it.symbol == sym }
    }) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D2B1F), Color(0xFF1E5438))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
            ) {
                Text(
                    text = "Welcome 👋",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Choose your currency to get started." +
                        if (selected != null) " We detected one for you — confirm or change it." else "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFB0D4C0)
                )
            }

            // Currency list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(setupCurrencies) { option ->
                    val isSelected = option.symbol == selected
                    CurrencyRow(
                        option = option,
                        isSelected = isSelected,
                        onClick = { selected = option.symbol }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            // Continue button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = { selected?.let { onCurrencyChosen(it) } },
                    enabled = selected != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107),
                        contentColor = Color(0xFF0D2B1F),
                        disabledContainerColor = Color(0xFF2A5C3F),
                        disabledContentColor = Color(0xFF6B9C7A)
                    )
                ) {
                    Text(
                        text = if (selected != null) "Continue →" else "Select a currency",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyRow(
    option: CurrencySetupOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Color(0xFF2A5C3F) else Color(0x22FFFFFF)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Symbol badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFFFC107) else Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            if (CurrencyLocaleMapper.isSaudiRiyalSymbol(option.symbol)) {
                Icon(
                    painter = painterResource(R.drawable.ic_saudi_riyal),
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF0D2B1F) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = option.symbol,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color(0xFF0D2B1F) else Color.White
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
            Text(
                text = option.countries,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB0D4C0)
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC107)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFF0D2B1F),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
