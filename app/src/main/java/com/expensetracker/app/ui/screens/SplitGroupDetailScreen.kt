package com.expensetracker.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.R
import com.expensetracker.app.data.SplitExpenseEntity
import com.expensetracker.app.data.SplitGroupEntity
import com.expensetracker.app.data.SplitMemberEntity
import com.expensetracker.app.ui.components.AddSplitExpenseSheet
import com.expensetracker.app.ui.components.AnimatedBlobBackground
import com.expensetracker.app.ui.components.MemberAvatar
import com.expensetracker.app.ui.components.MoneyText
import com.expensetracker.app.ui.components.SettleUpSheet
import com.expensetracker.app.ui.theme.BrandAmber
import com.expensetracker.app.ui.theme.DangerRed
import com.expensetracker.app.ui.theme.SuccessGreen
import com.expensetracker.app.util.Settlement
import com.expensetracker.app.viewmodel.SplitViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

// Unique purple/teal palette for Splits — distinct from Khata's green/amber
private val SplitBlobViolet = Color(0xFF7C4DFF)  // deep violet
private val SplitBlobTeal   = Color(0xFF00BFA5)  // teal
private val SplitBlobCoral  = Color(0xFFFF6D00)  // warm orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitGroupDetailScreen(
    groupId: Long,
    viewModel: SplitViewModel,
    currencySymbol: String,
    myUpiId: String = "",
    ownerDisplayName: String = "",
    onOpenSettings: () -> Unit = {},
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val groups  by viewModel.groups.collectAsState()
    val group   = remember(groups, groupId) { groups.firstOrNull { it.id == groupId } }

    LaunchedEffect(groupId) { viewModel.selectGroup(groupId) }

    val members  by viewModel.selectedGroupMembers.collectAsState()
    val expenses by viewModel.selectedGroupExpenses.collectAsState()

    var settlements by remember { mutableStateOf<List<Settlement>>(emptyList()) }
    var netBalances by remember { mutableStateOf<Map<Long, Double>>(emptyMap()) }

    var showAddExpenseSheet   by remember { mutableStateOf(false) }
    var showSettleUpSheet     by remember { mutableStateOf(false) }
    var showDeleteGroupDialog by remember { mutableStateOf(false) }
    var expenseToDelete       by remember { mutableStateOf<SplitExpenseEntity?>(null) }
    var menuExpanded          by remember { mutableStateOf(false) }

    // Active member filter — null = all, memberId = only that member's expenses
    var filterMemberId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(expenses, members) {
        settlements = viewModel.getSettlement(groupId)
        netBalances = viewModel.getNetBalances(groupId)
    }

    val memberMap = remember(members) { members.associateBy { it.id } }

    if (group == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Total pool (all non-settlement expenses)
    val totalSpent = remember(expenses) {
        expenses.filter { !it.isSettlement }.sumOf { it.amount }
    }
    val meMember = remember(members) { members.firstOrNull { it.isMe } }
    val meBalance = remember(netBalances, meMember) {
        if (meMember != null) netBalances[meMember.id] ?: 0.0 else 0.0
    }

    val listState = rememberLazyListState()

    // Collapsed fraction based on scroll (for topBar shrink effect)
    val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    val firstIndex   = remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val headerAlpha by animateFloatAsState(
        targetValue = if (firstIndex.value > 0) 1f else
            (scrollOffset.value / 400f).coerceIn(0f, 1f),
        animationSpec = tween(150),
        label = "headerAlpha"
    )

    val visibleExpenses = remember(expenses, filterMemberId) {
        val base = expenses.sortedByDescending { it.date }
        if (filterMemberId == null) base else base.filter { it.paidByMemberId == filterMemberId }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Animated blob background ──────────────────────────────────────────
        AnimatedBlobBackground(
            blobColors = listOf(SplitBlobViolet, SplitBlobTeal, SplitBlobCoral),
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Transparent top bar that gets a subtle tint as you scroll
                TopAppBar(
                    title = {
                        // Fade in group name on scroll (hidden when hero is visible)
                        androidx.compose.animation.AnimatedVisibility(
                            visible = headerAlpha > 0.5f,
                            enter = fadeIn(tween(200))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(group.emoji)
                                Text(
                                    group.name,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.onboard_back))
                        }
                    },
                    actions = {
                        TextButton(onClick = { showSettleUpSheet = true }) {
                            Text(
                                stringResource(R.string.split_settle_up),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.split_more_options))
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.split_delete_group)) },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteGroupDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = headerAlpha * 0.9f),
                        scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddExpenseSheet = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.split_add_expense))
                }
            }
        ) { padding ->

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {

                // ── Hero header ─────────────────────────────────────────────
                item(key = "hero") {
                    SplitHeroHeader(
                        group        = group,
                        totalSpent   = totalSpent,
                        meBalance    = meBalance,
                        currencySymbol = currencySymbol,
                        expenseCount = expenses.count { !it.isSettlement }
                    )
                }

                // ── Member avatar filter row ────────────────────────────────
                item(key = "members_row") {
                    MemberFilterRow(
                        members       = members,
                        netBalances   = netBalances,
                        filterMemberId = filterMemberId,
                        onSelect      = { id ->
                            filterMemberId = if (filterMemberId == id) null else id
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // ── Member balances ─────────────────────────────────────────
                item(key = "balances_header") {
                    Text(
                        stringResource(R.string.split_member_balances),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                itemsIndexed(members, key = { _, m -> "member_${m.id}" }) { index, member ->
                    val balance = netBalances[member.id] ?: 0.0
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300, delayMillis = index * 60)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                    ) {
                        MemberBalanceRow(
                            member         = member,
                            balance        = balance,
                            totalAbsDebt   = netBalances.values.sumOf { abs(it) }.coerceAtLeast(0.01),
                            currencySymbol = currencySymbol,
                            modifier       = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                // ── Expenses header ─────────────────────────────────────────
                item(key = "expenses_header") {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.split_expenses_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val label = if (filterMemberId == null)
                            stringResource(R.string.split_expense_count, expenses.count { !it.isSettlement })
                        else
                            "${visibleExpenses.count { !it.isSettlement }}"
                        Text(
                            label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (visibleExpenses.isEmpty()) {
                    item(key = "empty") {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🧾", style = MaterialTheme.typography.displaySmall)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    stringResource(R.string.split_no_expenses),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        visibleExpenses,
                        key = { _, e -> "expense_${e.id}" }
                    ) { index, expense ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(250, delayMillis = index * 50)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 3 },
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    )
                        ) {
                            SplitExpenseRow(
                                expense        = expense,
                                paidByMember   = memberMap[expense.paidByMemberId],
                                currencySymbol = currencySymbol,
                                onDelete       = { expenseToDelete = expense },
                                modifier       = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Sheets & Dialogs ──────────────────────────────────────────────────────

    if (showAddExpenseSheet && members.isNotEmpty()) {
        AddSplitExpenseSheet(
            members = members,
            currencySymbol = currencySymbol,
            onDismiss = { showAddExpenseSheet = false },
            onSave = { description, amount, paidById, splitIds, customShares ->
                viewModel.addExpense(
                    groupId = groupId,
                    description = description,
                    amount = amount,
                    paidByMemberId = paidById,
                    splitAmongIds = splitIds,
                    date = com.expensetracker.app.util.DateUtils.todayIso(),
                    customShares = customShares
                )
                showAddExpenseSheet = false
            }
        )
    }

    if (showSettleUpSheet) {
        SettleUpSheet(
            groupName        = group.name,
            settlements      = settlements,
            currencySymbol   = currencySymbol,
            expenses         = expenses,
            members          = members,
            netBalances      = netBalances,
            meMemberId       = meMember?.id,
            myUpiId          = myUpiId,
            ownerDisplayName = ownerDisplayName.ifBlank { meMember?.name ?: "Me" },
            onSetMemberUpiId = { member, upiId -> viewModel.setMemberUpiId(member, upiId) },
            onOpenSettings   = onOpenSettings,
            onDismiss        = { showSettleUpSheet = false },
            onMarkPaid     = { settlement ->
                viewModel.markSettlementPaid(
                    groupId        = groupId,
                    fromMemberId   = settlement.fromMemberId,
                    toMemberId     = settlement.toMemberId,
                    amount         = settlement.amount
                )
                scope.launch {
                    settlements = viewModel.getSettlement(groupId)
                    netBalances  = viewModel.getNetBalances(groupId)
                }
            }
        )
    }

    expenseToDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text(stringResource(R.string.split_delete_expense)) },
            text  = { Text(stringResource(R.string.split_delete_expense_confirm, expense.description)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(expense)
                    expenseToDelete = null
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showDeleteGroupDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteGroupDialog = false },
            title = { Text(stringResource(R.string.split_delete_group)) },
            text  = { Text(stringResource(R.string.split_delete_group_confirm, group.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGroup(group)
                    showDeleteGroupDialog = false
                    onBack()
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteGroupDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

// ── Hero header ────────────────────────────────────────────────────────────────

@Composable
private fun SplitHeroHeader(
    group: SplitGroupEntity,
    totalSpent: Double,
    meBalance: Double,
    currencySymbol: String,
    expenseCount: Int
) {
    val absBalance   = abs(meBalance)
    val isSettled    = absBalance < 0.01
    val iGotBack     = meBalance > 0

    val gradientStart = when {
        isSettled -> Color(0xFF1A7A4A)
        iGotBack  -> Color(0xFF1A7A4A)
        else      -> Color(0xFF6A1B9A)   // owe → deep purple
    }
    val gradientEnd = when {
        isSettled -> Color(0xFF00BFA5)
        iGotBack  -> Color(0xFF00BFA5)
        else      -> Color(0xFFE91E63)   // owe → magenta
    }

    val shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = gradientStart.copy(alpha = 0.4f),
                spotColor = gradientStart.copy(alpha = 0.5f)
            )
            .clip(shape)
            .drawBehind {
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(gradientStart, gradientEnd),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        )
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 72.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoji in glowing ring
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = Color.White.copy(alpha = 0.3f),
                        spotColor    = Color.White.copy(alpha = 0.3f)
                    )
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(group.emoji, fontSize = 32.sp)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                group.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "$expenseCount ${if (expenseCount == 1) "expense" else "expenses"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(20.dp))

            // Total pool + my balance in a two-column stat row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeroStat(
                    label = stringResource(R.string.split_hero_total_spent),
                    value = "$currencySymbol${"%.2f".format(totalSpent)}",
                    currencySymbol = currencySymbol,
                    valueColor = Color.White
                )

                // Divider
                Box(
                    Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                )

                HeroStat(
                    label = when {
                        isSettled -> stringResource(R.string.split_hero_settled_label)
                        iGotBack  -> stringResource(R.string.split_hero_get_back_label)
                        else      -> stringResource(R.string.split_hero_you_owe_label)
                    },
                    value = if (isSettled) "—"
                    else "$currencySymbol${"%.2f".format(absBalance)}",
                    currencySymbol = currencySymbol,
                    valueColor = when {
                        isSettled -> Color.White.copy(alpha = 0.8f)
                        iGotBack  -> BrandAmber
                        else      -> Color(0xFFFF8A80)
                    }
                )
            }
        }
    }
}

@Composable
private fun HeroStat(
    label: String,
    value: String,
    currencySymbol: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(4.dp))
        MoneyText(
            formatted = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}

// ── Member avatar filter row ───────────────────────────────────────────────────

@Composable
private fun MemberFilterRow(
    members: List<SplitMemberEntity>,
    netBalances: Map<Long, Double>,
    filterMemberId: Long?,
    onSelect: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(members, key = { "filter_${it.id}" }) { member ->
            val isSelected = filterMemberId == member.id
            val balance    = netBalances[member.id] ?: 0.0
            val absBalance = abs(balance)
            val isSettled  = absBalance < 0.01

            val dotColor = when {
                isSettled  -> SuccessGreen
                balance > 0 -> SuccessGreen
                else        -> DangerRed
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(member.id) }
                    .then(
                        if (isSelected) Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Box {
                    MemberAvatar(member = member, size = 44)
                    // Balance dot indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .background(dotColor, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = member.name.take(6),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Member balance row ─────────────────────────────────────────────────────────

@Composable
private fun MemberBalanceRow(
    member: SplitMemberEntity,
    balance: Double,
    totalAbsDebt: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val absBalance = abs(balance)
    val isSettled  = absBalance < 0.01
    val barFraction by animateFloatAsState(
        targetValue = if (isSettled) 0f else (absBalance / totalAbsDebt).toFloat().coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "balanceBar"
    )

    val barColor = when {
        isSettled  -> SuccessGreen.copy(alpha = 0.4f)
        balance > 0 -> SuccessGreen
        else        -> DangerRed
    }
    val textColor = when {
        isSettled  -> MaterialTheme.colorScheme.onSurfaceVariant
        balance > 0 -> Color(0xFF2E7D32)
        else        -> MaterialTheme.colorScheme.error
    }
    val balanceLabel = when {
        isSettled  -> stringResource(R.string.split_settled)
        balance > 0 -> "+$currencySymbol${"%.2f".format(absBalance)}"
        else        -> "-$currencySymbol${"%.2f".format(absBalance)}"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MemberAvatar(member = member, size = 40)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (member.isMe) "${member.name} (${stringResource(R.string.family_me_label)})"
                        else member.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (!isSettled) {
                        Text(
                            if (balance > 0) "gets back" else "owes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                MoneyText(
                    formatted = balanceLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }

            // Animated proportion bar
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(barFraction)
                        .clip(RoundedCornerShape(2.dp))
                        .background(barColor)
                )
            }
        }
    }
}

// ── Expense row ────────────────────────────────────────────────────────────────

@Composable
private fun SplitExpenseRow(
    expense: SplitExpenseEntity,
    paidByMember: SplitMemberEntity?,
    currencySymbol: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSettlement = expense.isSettlement
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSettlement)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (paidByMember != null) {
                MemberAvatar(member = paidByMember, size = 38)
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        expense.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isSettlement) {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                stringResource(R.string.split_settled),
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                if (paidByMember != null) {
                    Text(
                        stringResource(R.string.split_paid_by_member, paidByMember.name),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (expense.note.isNotBlank()) {
                    Text(
                        expense.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                MoneyText(
                    formatted = "$currencySymbol${"%.2f".format(expense.amount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    expense.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
