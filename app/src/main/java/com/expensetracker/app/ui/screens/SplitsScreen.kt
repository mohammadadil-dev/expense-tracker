package com.expensetracker.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.data.SplitGroupEntity
import com.expensetracker.app.ui.components.AddEditSplitGroupSheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.BackgroundScrollSignal
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.ui.theme.TextPrimary
import com.expensetracker.app.ui.theme.TextSecondary
import com.expensetracker.app.viewmodel.SplitViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

// Shared palette with SplitGroupDetailScreen so the whole Splits section
// feels like one cohesive space (different from Debt's red/green/indigo)
private val SplitBlobViolet = Color(0xFF7C4DFF)
private val SplitBlobTeal   = Color(0xFF00BFA5)
private val SplitBlobCoral  = Color(0xFFFF6D00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitsScreen(
    viewModel: SplitViewModel,
    ownerName: String,
    currencySymbol: String,
    onOpenGroup: (Long) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    var showNewGroupSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Per-group balance summary: groupId → meBalance
    val groupSummaries = remember { mutableStateMapOf<Long, Pair<Double, Int>>() }

    LaunchedEffect(groups) {
        groups.forEach { group ->
            scope.launch {
                val netBalances = viewModel.getNetBalances(group.id)
                val members = viewModel.getMembersSnapshot(group.id)
                val meMember = members.firstOrNull { it.isMe }
                val meBalance = if (meMember != null) netBalances[meMember.id] ?: 0.0 else 0.0
                groupSummaries[group.id] = meBalance to members.size
            }
        }
    }

    // Aggregate stats across all groups
    val totalYouOwe = remember(groupSummaries, groups) {
        groups.sumOf { g -> (groupSummaries[g.id]?.first ?: 0.0).coerceAtMost(0.0) }.let { abs(it) }
    }
    val totalYouGetBack = remember(groupSummaries, groups) {
        groups.sumOf { g -> (groupSummaries[g.id]?.first ?: 0.0).coerceAtLeast(0.0) }
    }
    val net = totalYouGetBack - totalYouOwe

    // Hero slide-in trigger (same LaunchedEffect(Unit) pattern as DebtsScreen)
    var heroVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { heroVisible = true }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.firstVisibleItemScrollOffset) {
        BackgroundScrollSignal.pixels.floatValue = listState.firstVisibleItemScrollOffset.toFloat()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Animated blob background — splits palette ──────────────────────
        AnimatedBlobBackground(
            blobColors = listOf(SplitBlobViolet, SplitBlobTeal, SplitBlobCoral),
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showNewGroupSheet = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.split_new_group))
                }
            }
        ) { padding ->

            if (groups.isEmpty()) {
                // ── Empty state ───────────────────────────────────────────
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        // Still show the hero even when empty
                        AnimatedVisibility(
                            visible = heroVisible,
                            enter = slideInVertically(tween(500, easing = FastOutSlowInEasing)) { -it / 2 } +
                                    fadeIn(tween(500))
                        ) {
                            SplitsHeroHeader(
                                groupCount      = 0,
                                totalYouOwe     = 0.0,
                                totalYouGetBack = 0.0,
                                net             = 0.0,
                                currencySymbol  = currencySymbol
                            )
                        }
                    }
                    item {
                        SplitsEmptyState(
                            onCreateGroup = { showNewGroupSheet = true },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    // ── Animated hero header ───────────────────────────────
                    item(key = "hero") {
                        AnimatedVisibility(
                            visible = heroVisible,
                            enter = slideInVertically(tween(500, easing = FastOutSlowInEasing)) { -it / 2 } +
                                    fadeIn(tween(500))
                        ) {
                            SplitsHeroHeader(
                                groupCount      = groups.size,
                                totalYouOwe     = totalYouOwe,
                                totalYouGetBack = totalYouGetBack,
                                net             = net,
                                currencySymbol  = currencySymbol
                            )
                        }
                    }

                    // ── Section label ──────────────────────────────────────
                    item(key = "section_label") {
                        Text(
                            stringResource(R.string.split_screen_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // ── Group cards with staggered animation ───────────────
                    itemsIndexed(groups, key = { _, g -> g.id }) { index, group ->
                        val (meBalance, memberCount) = groupSummaries[group.id] ?: (0.0 to 0)
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeIn(tween(300, delayMillis = index * 60))
                        ) {
                            SplitGroupCard(
                                group = group,
                                memberCount = memberCount,
                                meBalance = meBalance,
                                currencySymbol = currencySymbol,
                                onClick = { onOpenGroup(group.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNewGroupSheet) {
        AddEditSplitGroupSheet(
            ownerName = ownerName,
            onDismiss = { showNewGroupSheet = false },
            onSave = { name, emoji, extraMembers ->
                viewModel.createGroup(
                    name = name,
                    emoji = emoji,
                    ownerName = ownerName,
                    ownerColor = com.expensetracker.app.data.SplitRepository.PALETTE[0],
                    extraMembers = extraMembers
                )
                showNewGroupSheet = false
                scope.launch {
                    kotlinx.coroutines.delay(300)
                    viewModel.selectedGroupId.value?.let { onOpenGroup(it) }
                }
            }
        )
    }
}

// ── Splits hero header ─────────────────────────────────────────────────────────

@Composable
private fun SplitsHeroHeader(
    groupCount: Int,
    totalYouOwe: Double,
    totalYouGetBack: Double,
    net: Double,
    currencySymbol: String
) {
    val allSettled  = totalYouOwe < 0.01 && totalYouGetBack < 0.01
    val netPositive = net >= 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.split_screen_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.split_screen_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(Modifier.height(16.dp))

        // Two identical stat cards side by side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SplitStatCard(
                label = "You owe",
                amount = totalYouOwe,
                currencySymbol = currencySymbol,
                amountColor = if (totalYouOwe < 0.01) TextMuted else DangerRed,
                modifier = Modifier.weight(1f)
            )
            SplitStatCard(
                label = "You get back",
                amount = totalYouGetBack,
                currencySymbol = currencySymbol,
                amountColor = if (totalYouGetBack < 0.01) TextMuted else SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Net position pill (same pattern as DebtsScreen)
        Surface(
            shape = RoundedCornerShape(50),
            color = when {
                allSettled   -> SuccessGreen.copy(alpha = 0.10f)
                netPositive  -> SuccessGreen.copy(alpha = 0.10f)
                else         -> DangerRed.copy(alpha = 0.10f)
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (allSettled || netPositive) Icons.Filled.TrendingUp
                                  else Icons.Filled.TrendingDown,
                    contentDescription = null,
                    tint = if (allSettled || netPositive) SuccessGreen else DangerRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                val netLabel = when {
                    allSettled  -> stringResource(R.string.split_settled)
                    netPositive -> "Net: +$currencySymbol${"%.2f".format(net)} in your favour"
                    else        -> "Net: you owe $currencySymbol${"%.2f".format(abs(net))}"
                }
                MoneyText(
                    formatted = netLabel,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (allSettled || netPositive) SuccessGreen else DangerRed
                )
            }
        }

        if (groupCount > 0) {
            Spacer(Modifier.height(12.dp))

            // Gradient net card
            SplitsNetCard(
                net = net,
                groupCount = groupCount,
                currencySymbol = currencySymbol,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SplitStatCard(
    label: String,
    amount: Double,
    currencySymbol: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "statCardScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(120),
        label = "statCardElevation"
    )

    Card(
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier  = modifier
            .fillMaxHeight()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interactionSource, indication = null) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(6.dp))
            MoneyText(
                formatted = "$currencySymbol${"%.2f".format(amount)}",
                style     = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color     = amountColor
            )
        }
    }
}

@Composable
private fun SplitsNetCard(
    net: Double,
    groupCount: Int,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val accentColor = when {
        net > 0.01  -> SuccessGreen
        net < -0.01 -> DangerRed
        else        -> SplitBlobViolet
    }
    val shape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = accentColor.copy(alpha = 0.25f),
                spotColor    = accentColor.copy(alpha = 0.35f)
            )
            .clip(shape)
            .drawBehind {
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.65f)),
                            start = Offset.Zero,
                            end   = Offset(size.width, size.height)
                        )
                    )
                }
            }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = when {
                        net > 0.01  -> "Overall you're owed"
                        net < -0.01 -> "Overall you owe"
                        else        -> "All settled up ✓"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = OnAccent.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(4.dp))
                MoneyText(
                    formatted = "$currencySymbol${"%.2f".format(abs(net))}",
                    style     = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color     = OnAccent
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "across $groupCount ${if (groupCount == 1) "group" else "groups"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnAccent.copy(alpha = 0.75f)
                )
            }
            // Big emoji icon
            Text(
                text  = if (net > 0.01) "💰" else if (net < -0.01) "💸" else "🎉",
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}

// ── Group card ─────────────────────────────────────────────────────────────────

@Composable
private fun SplitGroupCard(
    group: SplitGroupEntity,
    memberCount: Int,
    meBalance: Double,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val absBalance = abs(meBalance)
    val isSettled  = absBalance < 0.01
    val iGotBack   = meBalance > 0

    val chipBg = when {
        isSettled -> MaterialTheme.colorScheme.surfaceVariant
        iGotBack  -> SuccessGreen.copy(alpha = 0.15f)
        else      -> DangerRed.copy(alpha = 0.12f)
    }
    val chipTextColor = when {
        isSettled -> MaterialTheme.colorScheme.onSurfaceVariant
        iGotBack  -> SuccessGreen
        else      -> DangerRed
    }
    val chipLabel = when {
        isSettled -> stringResource(R.string.split_settled)
        iGotBack  -> stringResource(R.string.split_you_get, currencySymbol, absBalance)
        else      -> stringResource(R.string.split_you_owe, currencySymbol, absBalance)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Emoji bubble with gradient background
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .drawBehind {
                        drawRect(
                            brush = Brush.linearGradient(
                                listOf(
                                    SplitBlobViolet.copy(alpha = 0.15f),
                                    SplitBlobTeal.copy(alpha = 0.15f)
                                ),
                                start = Offset.Zero,
                                end   = Offset(size.width, size.height)
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(group.emoji, style = MaterialTheme.typography.titleLarge)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    stringResource(R.string.split_member_count, memberCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Balance chip — MoneyText so SAR icon renders correctly
            Surface(
                color = chipBg,
                shape = RoundedCornerShape(10.dp)
            ) {
                MoneyText(
                    formatted = chipLabel,
                    modifier  = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style     = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color     = chipTextColor
                )
            }
        }
    }
}

// ── Empty state ────────────────────────────────────────────────────────────────

@Composable
private fun SplitsEmptyState(
    onCreateGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Groups,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(56.dp)
        )
        Text(
            stringResource(R.string.split_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            stringResource(R.string.split_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onCreateGroup,
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.split_create_group))
        }
    }
}
