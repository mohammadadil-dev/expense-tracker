package com.expensetracker.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.NeonCyan
import com.expensetracker.app.ui.theme.NeonViolet
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.ZakatMath
import com.expensetracker.app.util.ZakatMath.NisabBasis
import com.expensetracker.app.util.ZakatMath.ZakatableAssets
import com.expensetracker.app.viewmodel.ExpenseViewModel

/**
 * Offline zakat calculator. Reuses [ExpenseViewModel] purely to read the currency symbol and to
 * prefill the Savings field from the user's active savings-goal balances — everything else is
 * entered by hand, and the current gold/silver price per gram is user-supplied so the whole
 * thing stays offline and neutral (no fatwa API, no price feed). Results recompute live via the
 * pure [ZakatMath] module.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()
    val savingsPrefill = remember(activeGoals) { activeGoals.sumOf { it.savedAmount } }

    var basis by remember { mutableStateOf(NisabBasis.SILVER) }
    var hawl by remember { mutableStateOf(true) }

    var cash by remember { mutableStateOf("") }
    var bank by remember { mutableStateOf("") }
    // Prefilled from savings goals; re-seeds once the goals flow first emits a non-zero total.
    var savings by remember(savingsPrefill) { mutableStateOf(if (savingsPrefill > 0.0) plain(savingsPrefill) else "") }
    // Gold/silver are entered as grams + price-per-gram; the value is computed (grams × price)
    // and the SAME price feeds the nisab threshold — one number serves both, which removes the
    // old "price per gram" vs "gold value" double-entry confusion.
    var goldGrams by remember { mutableStateOf("") }
    var goldPriceText by remember { mutableStateOf("") }
    var silverGrams by remember { mutableStateOf("") }
    var silverPriceText by remember { mutableStateOf("") }
    var investments by remember { mutableStateOf("") }
    var business by remember { mutableStateOf("") }
    var receivables by remember { mutableStateOf("") }
    var liabilities by remember { mutableStateOf("") }

    val goldPrice = num(goldPriceText)
    val silverPrice = num(silverPriceText)
    val goldValue = num(goldGrams) * goldPrice
    val silverValue = num(silverGrams) * silverPrice
    val assets = ZakatableAssets(
        cash = num(cash),
        bankBalances = num(bank),
        savings = num(savings),
        goldValue = goldValue,
        silverValue = silverValue,
        investments = num(investments),
        businessInventory = num(business),
        receivables = num(receivables),
        shortTermLiabilities = num(liabilities)
    )
    val net = ZakatMath.netZakatableWealth(assets)
    val nisab = ZakatMath.nisabValue(basis, goldPrice, silverPrice)
    val eligible = ZakatMath.isEligible(net, nisab)
    val due = ZakatMath.zakatDue(assets, basis, goldPrice, silverPrice, hawl)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBlobBackground(
            blobColors = listOf(NeonViolet, NeonCyan, AccentIndigo),
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.zakat_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.close))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ResultCard(
                        net = net, nisab = nisab, due = due, eligible = eligible,
                        hawl = hawl, currencySymbol = currencySymbol
                    )
                }

                item {
                    Text(
                        stringResource(R.string.zakat_intro),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                item { SectionLabel(stringResource(R.string.zakat_section_nisab)) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = basis == NisabBasis.SILVER,
                            onClick = { basis = NisabBasis.SILVER },
                            label = { Text(stringResource(R.string.zakat_basis_silver)) }
                        )
                        FilterChip(
                            selected = basis == NisabBasis.GOLD,
                            onClick = { basis = NisabBasis.GOLD },
                            label = { Text(stringResource(R.string.zakat_basis_gold)) }
                        )
                    }
                }
                item {
                    Text(
                        stringResource(R.string.zakat_nisab_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                item { SectionLabel(stringResource(R.string.zakat_section_assets)) }
                item { MoneyInput(stringResource(R.string.zakat_asset_cash), cash) { cash = it } }
                item { MoneyInput(stringResource(R.string.zakat_asset_bank), bank) { bank = it } }
                item {
                    Column {
                        MoneyInput(stringResource(R.string.zakat_asset_savings), savings) { savings = it }
                        if (savingsPrefill > 0.0) {
                            Text(
                                stringResource(R.string.zakat_savings_prefill_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }
                // Gold: enter grams + price per gram → value computed live (and price feeds nisab).
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        MoneyInput(stringResource(R.string.zakat_gold_grams), goldGrams) { goldGrams = it }
                        MoneyInput(stringResource(R.string.zakat_price_gold), goldPriceText) { goldPriceText = it }
                        if (goldValue > 0.0) {
                            ResultRow(stringResource(R.string.zakat_asset_gold), Formatters.money(goldValue, currencySymbol))
                        }
                    }
                }
                // Silver: enter grams + price per gram → value computed live.
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        MoneyInput(stringResource(R.string.zakat_silver_grams), silverGrams) { silverGrams = it }
                        MoneyInput(stringResource(R.string.zakat_price_silver), silverPriceText) { silverPriceText = it }
                        if (silverValue > 0.0) {
                            ResultRow(stringResource(R.string.zakat_asset_silver), Formatters.money(silverValue, currencySymbol))
                        }
                    }
                }
                item { MoneyInput(stringResource(R.string.zakat_asset_investments), investments) { investments = it } }
                item { MoneyInput(stringResource(R.string.zakat_asset_business), business) { business = it } }
                item { MoneyInput(stringResource(R.string.zakat_asset_receivables), receivables) { receivables = it } }
                item { MoneyInput(stringResource(R.string.zakat_liabilities), liabilities) { liabilities = it } }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = hawl, onCheckedChange = { hawl = it })
                        Text(stringResource(R.string.zakat_hawl), color = TextPrimary)
                    }
                }
                item {
                    Text(
                        stringResource(R.string.zakat_disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                item { Spacer(Modifier.height(96.dp)) }
            }
        }
    }
}

@Composable
private fun ResultCard(
    net: Double,
    nisab: Double,
    due: Double,
    eligible: Boolean,
    hawl: Boolean,
    currencySymbol: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.zakat_due_label),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )
            MoneyText(
                formatted = Formatters.money(due, currencySymbol),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = if (due > 0.0) SuccessGreen else TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            ResultRow(stringResource(R.string.zakat_net_wealth), Formatters.money(net, currencySymbol))
            ResultRow(stringResource(R.string.zakat_nisab_threshold), Formatters.money(nisab, currencySymbol))
            Spacer(Modifier.height(6.dp))
            val statusText = when {
                !hawl -> stringResource(R.string.zakat_hawl_note)
                eligible -> stringResource(R.string.zakat_eligible_yes)
                else -> stringResource(R.string.zakat_eligible_no)
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = if (eligible && hawl) SuccessGreen else DangerRed
            )
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        MoneyText(
            formatted = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = TextPrimary
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoneyInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

/** Parses a user-entered money string to a non-negative Double (commas stripped). */
private fun num(text: String): Double = text.replace(",", "").trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0

/** Formats a Double for prefilling a field: drops the decimals when it's a whole number. */
private fun plain(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
