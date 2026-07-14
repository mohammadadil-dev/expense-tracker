package com.expensetracker.app.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.components.CoachmarkOverlay
import com.expensetracker.app.ui.components.CoachmarkStep
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.expensetracker.app.ui.ads.BannerAdView
import com.expensetracker.app.R
import com.expensetracker.app.ui.screens.CurrencySetupScreen
import com.expensetracker.app.ui.screens.OnboardingScreen
import com.expensetracker.app.ui.screens.DashboardScreen
import com.expensetracker.app.ui.screens.DebtsScreen
import com.expensetracker.app.ui.screens.KhataDetailScreen
import com.expensetracker.app.ui.screens.KhataScreen
import com.expensetracker.app.ui.screens.SettingsScreen
import com.expensetracker.app.ui.screens.SplashScreen
import com.expensetracker.app.ui.screens.SplitsScreen
import com.expensetracker.app.ui.screens.SplitGroupDetailScreen
import com.expensetracker.app.ui.screens.SubscriptionsScreen
import com.expensetracker.app.util.LocaleHelper
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.expensetracker.app.viewmodel.KhataViewModel
import com.expensetracker.app.viewmodel.SplitViewModel

private object Routes {
    const val SPLASH          = "splash"
    const val ONBOARDING      = "onboarding"
    const val CURRENCY_SETUP  = "currency_setup"
    const val DASHBOARD       = "dashboard"
    const val KHATA           = "khata"
    const val KHATA_DETAIL    = "khata_detail/{partyId}"
    const val DEBTS           = "debts"
    const val SPLITS          = "splits"
    const val SPLIT_DETAIL    = "split_detail/{groupId}"
    const val SETTINGS        = "settings"
    const val SUBSCRIPTIONS   = "subscriptions"

    fun khataDetail(partyId: Long) = "khata_detail/$partyId"
    fun splitDetail(groupId: Long) = "split_detail/$groupId"
}

private const val TRANSITION_MS = 260

private data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD, R.string.nav_dashboard,   Icons.Filled.Home),
    BottomNavItem(Routes.KHATA,     R.string.khata_tab_label, Icons.Filled.MenuBook),
    BottomNavItem(Routes.DEBTS,     R.string.nav_debts,       Icons.Filled.AccountBalance),
    BottomNavItem(Routes.SPLITS,    R.string.nav_splits,      Icons.Filled.Groups),
    BottomNavItem(Routes.SETTINGS,  R.string.nav_settings,    Icons.Filled.Settings),
)

// Routes where the bottom nav should be hidden
private val routesWithoutBottomNav = setOf(Routes.SPLASH, Routes.ONBOARDING, Routes.CURRENCY_SETUP, "khata_detail/", "split_detail/", Routes.SUBSCRIPTIONS)

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val viewModel: ExpenseViewModel = viewModel()
    val khataViewModel: KhataViewModel = viewModel()
    val splitViewModel: SplitViewModel = viewModel()

    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val displayName    by viewModel.displayName.collectAsState()

    // POST_NOTIFICATIONS (Android 13+) request for the daily reminder — shared by onboarding
    // completion and the Dashboard catch-up check below. Mirrors Settings screen's own toggle
    // logic exactly: only actually enable the reminder if the user grants the permission.
    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.setReminderEnabled(granted) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute != null &&
        routesWithoutBottomNav.none { currentRoute.startsWith(it) }

    // Root Box lets CoachmarkOverlay render over the full window (including bottom nav bar)
    // so steps 4 and 5 can spotlight nav tab items.
    val coachDensity = LocalDensity.current
    val coachmarkSteps = remember(
        viewModel.fabBounds, viewModel.budgetCardBounds,
        viewModel.incomeTilesBounds, viewModel.debtsNavBounds, viewModel.ledgerNavBounds,
        viewModel.receiptScanBounds, viewModel.micButtonBounds, viewModel.splitsNavBounds
    ) {
        // Step 2: spotlight only the ring (upper 38%, fixed 88dp radius) not the whole card
        val budgetRingSpot = viewModel.budgetCardBounds?.let { bounds ->
            val cx = bounds.center.x
            val cy = bounds.top + bounds.height * 0.38f
            val r = with(coachDensity) { 88.dp.toPx() }
            Rect(cx - r, cy - r, cx + r, cy + r)
        }
        // Step 3: spotlight the income/salary tiles row (circle based on tile height)
        val incomeSpot = viewModel.incomeTilesBounds?.let { bounds ->
            val r = bounds.height / 2f + with(coachDensity) { 16.dp.toPx() }
            Rect(bounds.center.x - r, bounds.center.y - r, bounds.center.x + r, bounds.center.y + r)
        }
        listOf(
            CoachmarkStep(R.string.tour_step1_title, R.string.tour_step1_body, viewModel.fabBounds),
            CoachmarkStep(R.string.tour_step2_title, R.string.tour_step2_body, budgetRingSpot),
            CoachmarkStep(R.string.tour_step3_title, R.string.tour_step3_body, incomeSpot),
            CoachmarkStep(R.string.tour_step4_title, R.string.tour_step4_body, viewModel.debtsNavBounds),
            CoachmarkStep(R.string.tour_step5_title, R.string.tour_step5_body, viewModel.ledgerNavBounds),
            // Steps 6–9: new feature introductions (spotlight the actual button/tab)
            CoachmarkStep(R.string.tour_step6_title, R.string.tour_step6_body, viewModel.receiptScanBounds),
            CoachmarkStep(R.string.tour_step7_title, R.string.tour_step7_body, viewModel.micButtonBounds),
            CoachmarkStep(R.string.tour_step8_title, R.string.tour_step8_body, viewModel.splitsNavBounds),
            // step9 (Khata/Ledger) removed — already covered by step 5
            CoachmarkStep(R.string.tour_step10_title, R.string.tour_step10_body, null),
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                Column {
                    // Banner ad shown on all tab screens, flush above the nav bar.
                    // Hidden when screenshot mode is active (long-press version in Settings).
                    BannerAdView(show = !viewModel.screenshotMode)

                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route

                            // Spring-bounce scale: icon pops up when selected
                            val iconScale by animateFloatAsState(
                                targetValue = if (selected) 1.22f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness    = Spring.StiffnessMedium
                                ),
                                label = "navIconScale_${item.route}"
                            )

                            NavigationBarItem(
                                modifier = when (item.route) {
                                    Routes.DEBTS -> Modifier.onGloballyPositioned { coords ->
                                        viewModel.debtsNavBounds = coords.boundsInWindow()
                                    }
                                    Routes.KHATA -> Modifier.onGloballyPositioned { coords ->
                                        viewModel.ledgerNavBounds = coords.boundsInWindow()
                                    }
                                    Routes.SPLITS -> Modifier.onGloballyPositioned { coords ->
                                        viewModel.splitsNavBounds = coords.boundsInWindow()
                                    }
                                    else -> Modifier
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = stringResource(item.labelRes),
                                        modifier = Modifier.graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                    )
                                },
                                label = { Text(stringResource(item.labelRes)) },
                                selected = selected,
                                onClick = {
                                    if (!selected) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = Routes.SPLASH) {

                composable(Routes.SPLASH) {
                    SplashScreen(
                        onFinished = {
                            // New installs → onboarding wizard.
                            // Returning users (onboardingDone=true) go straight to dashboard.
                            // Legacy users who completed currency setup before onboarding
                            // existed also get onboardingDone=true via the default in
                            // SettingsRepository, so they're never interrupted.
                            val next = if (viewModel.isOnboardingDone)
                                Routes.DASHBOARD else Routes.ONBOARDING
                            navController.navigate(next) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    Routes.ONBOARDING,
                    enterTransition = { fadeIn(tween(TRANSITION_MS)) },
                    exitTransition  = { fadeOut(tween(TRANSITION_MS)) }
                ) {
                    OnboardingScreen(
                        // Resume mid-wizard after Activity recreation caused by locale change
                        initialStep         = viewModel.onboardingResumeStep,
                        initialName         = viewModel.onboardingPendingName,
                        initialLanguageCode = viewModel.languagePref.value,
                        onApplyLanguage     = { name, langCode ->
                            // Save progress before recreation so we restore it afterwards
                            viewModel.saveOnboardingProgress(name, langCode)
                            LocaleHelper.applyLanguagePreference(langCode)
                            // Activity recreates — OnboardingScreen reopens at step 2
                        },
                        onDone = { name, languageCode, symbol, salary, budget, reminderEnabled, reminderHour ->
                            viewModel.resetOnboardingResume()
                            if (name.isNotBlank()) viewModel.setDisplayName(name)
                            LocaleHelper.applyLanguagePreference(languageCode)
                            viewModel.setCurrencySymbol(symbol)
                            viewModel.markCurrencySetupDone()
                            viewModel.markOnboardingDone()
                            if (salary > 0) viewModel.setMonthlySalary(salary)
                            if (budget > 0) viewModel.setBudget(null, budget)
                            if (reminderEnabled) viewModel.setReminderHour(reminderHour)
                            // This is the actual fix for the reminder silently never firing:
                            // previously this called setReminderEnabled(reminderEnabled) directly,
                            // scheduling the alarm without ever asking for POST_NOTIFICATIONS
                            // (Android 13+) — the notification would then be silently dropped by
                            // the OS forever, with no error anywhere. Now we request the
                            // permission first and only actually enable the reminder if granted
                            // (notifPermLauncher's callback calls setReminderEnabled(granted)),
                            // matching exactly what the Settings screen's own toggle already does.
                            viewModel.markNotifPermissionRequested()
                            if (reminderEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.setReminderEnabled(reminderEnabled)
                            }
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    Routes.CURRENCY_SETUP,
                    enterTransition = { fadeIn(tween(TRANSITION_MS)) },
                    exitTransition  = { fadeOut(tween(TRANSITION_MS)) }
                ) {
                    CurrencySetupScreen(
                        onCurrencyChosen = { symbol ->
                            viewModel.setCurrencySymbol(symbol)
                            viewModel.markCurrencySetupDone()
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.CURRENCY_SETUP) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    Routes.DASHBOARD,
                    enterTransition    = { fadeIn(tween(TRANSITION_MS)) },
                    exitTransition     = { fadeOut(tween(TRANSITION_MS)) },
                    popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
                    popExitTransition  = { fadeOut(tween(TRANSITION_MS)) }
                ) {
                    // One-time catch-up for installs that predate the onboarding permission fix
                    // above: reminderEnabled defaults to true for everyone, so any existing user
                    // who installed before this fix has the alarm scheduled but was never actually
                    // asked for POST_NOTIFICATIONS — their reminder has been silently doing
                    // nothing. Ask exactly once (guarded by notifPermissionRequested) so we don't
                    // nag every launch after a denial.
                    val dashboardContext = LocalContext.current
                    LaunchedEffect(Unit) {
                        val alreadyGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                            ContextCompat.checkSelfPermission(
                                dashboardContext, Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        if (!viewModel.notifPermissionRequested &&
                            viewModel.reminderEnabled.value &&
                            !alreadyGranted
                        ) {
                            viewModel.markNotifPermissionRequested()
                            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    DashboardScreen(
                        viewModel = viewModel,
                        onOpenSettings = {
                            navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                        },
                        onOpenDebts = {
                            navController.navigate(Routes.DEBTS) { launchSingleTop = true }
                        },
                        onOpenSubscriptions = {
                            navController.navigate(Routes.SUBSCRIPTIONS) { launchSingleTop = true }
                        }
                    )
                }

                composable(
                    Routes.SUBSCRIPTIONS,
                    enterTransition   = {
                        fadeIn(tween(TRANSITION_MS)) +
                            slideInHorizontally(tween(TRANSITION_MS)) { it }
                    },
                    popExitTransition = {
                        fadeOut(tween(TRANSITION_MS)) +
                            slideOutHorizontally(tween(TRANSITION_MS)) { it }
                    }
                ) {
                    SubscriptionsScreen(
                        viewModel = viewModel,
                        onBack    = { navController.popBackStack() }
                    )
                }

                composable(
                    Routes.KHATA,
                    enterTransition    = { fadeIn(tween(TRANSITION_MS)) },
                    exitTransition     = { fadeOut(tween(TRANSITION_MS)) },
                    popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
                    popExitTransition  = { fadeOut(tween(TRANSITION_MS)) }
                ) {
                    KhataScreen(
                        khataViewModel   = khataViewModel,
                        expenseViewModel = viewModel,
                        onOpenDetail     = { partyId ->
                            navController.navigate(Routes.khataDetail(partyId))
                        }
                    )
                }

                composable(
                    Routes.KHATA_DETAIL,
                    enterTransition   = {
                        fadeIn(tween(TRANSITION_MS)) +
                            slideInHorizontally(tween(TRANSITION_MS)) { it }
                    },
                    popExitTransition = {
                        fadeOut(tween(TRANSITION_MS)) +
                            slideOutHorizontally(tween(TRANSITION_MS)) { it }
                    }
                ) { backStackEntry ->
                    val partyId = backStackEntry.arguments
                        ?.getString("partyId")?.toLongOrNull() ?: return@composable
                    KhataDetailScreen(
                        partyId          = partyId,
                        khataViewModel   = khataViewModel,
                        expenseViewModel = viewModel,
                        onBack           = { navController.popBackStack() },
                        onOpenSettings   = {
                            navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                        }
                    )
                }

                composable(
                    Routes.DEBTS,
                    enterTransition    = { fadeIn(tween(TRANSITION_MS)) },
                    exitTransition     = { fadeOut(tween(TRANSITION_MS)) },
                    popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
                    popExitTransition  = { fadeOut(tween(TRANSITION_MS)) }
                ) {
                    DebtsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    Routes.SPLITS,
                    enterTransition    = { fadeIn(tween(TRANSITION_MS)) },
                    exitTransition     = { fadeOut(tween(TRANSITION_MS)) },
                    popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
                    popExitTransition  = { fadeOut(tween(TRANSITION_MS)) }
                ) {
                    SplitsScreen(
                        viewModel       = splitViewModel,
                        ownerName       = displayName.ifBlank { "Me" },
                        currencySymbol  = currencySymbol,
                        onOpenGroup     = { groupId ->
                            navController.navigate(Routes.splitDetail(groupId))
                        }
                    )
                }

                composable(
                    Routes.SPLIT_DETAIL,
                    enterTransition   = {
                        fadeIn(tween(TRANSITION_MS)) +
                            slideInHorizontally(tween(TRANSITION_MS)) { it }
                    },
                    popExitTransition = {
                        fadeOut(tween(TRANSITION_MS)) +
                            slideOutHorizontally(tween(TRANSITION_MS)) { it }
                    }
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments
                        ?.getString("groupId")?.toLongOrNull() ?: return@composable
                    SplitGroupDetailScreen(
                        groupId        = groupId,
                        viewModel      = splitViewModel,
                        currencySymbol = currencySymbol,
                        onBack         = { navController.popBackStack() }
                    )
                }

                composable(
                    Routes.SETTINGS,
                    enterTransition   = {
                        fadeIn(tween(TRANSITION_MS)) +
                            slideInHorizontally(tween(TRANSITION_MS)) { it }
                    },
                    popExitTransition = {
                        fadeOut(tween(TRANSITION_MS)) +
                            slideOutHorizontally(tween(TRANSITION_MS)) { it }
                    }
                ) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack    = { navController.popBackStack() }
                    )
                }
            }
        }
    } // end Scaffold

    // CoachmarkOverlay renders at root Box level so it covers the full window
    // including the bottom nav bar (steps 4 & 5 spotlight nav tab items).
    // Steps 6–10 introduce new features with null spotlight (full-screen dim, centered card).
    // Only new installs see the full 10-step tour; returning users who already have
    // hasSeenCoachmarks=true are unaffected.
    if (viewModel.coachmarkStep < coachmarkSteps.size &&
        currentRoute == Routes.DASHBOARD) {
        CoachmarkOverlay(
            steps         = coachmarkSteps,
            currentStep   = viewModel.coachmarkStep,
            bottomPadding = 140.dp,
            onNext = {
                if (viewModel.coachmarkStep < coachmarkSteps.size - 1) {
                    viewModel.coachmarkStep++
                } else {
                    viewModel.markCoachmarksSeen()
                    viewModel.coachmarkStep = Int.MAX_VALUE
                }
            },
            onSkip = {
                viewModel.markCoachmarksSeen()
                viewModel.coachmarkStep = Int.MAX_VALUE
            }
        )
    }

    } // end root Box
}
