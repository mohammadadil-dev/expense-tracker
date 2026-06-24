package com.expensetracker.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expensetracker.app.ui.screens.DashboardScreen
import com.expensetracker.app.ui.screens.SettingsScreen
import com.expensetracker.app.ui.screens.SplashScreen
import com.expensetracker.app.viewmodel.ExpenseViewModel

private object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
}

private const val TRANSITION_MS = 260

/**
 * Splash, then two screens, one shared ViewModel — everything is backed by the local Room
 * database, so there's no server/account state to keep in sync across screens.
 *
 * The splash screen is removed from the back stack as soon as it hands off to the
 * dashboard (`popUpTo(SPLASH) { inclusive = true }`), so the back button never returns to it.
 *
 * Settings slides in from the trailing edge and slides back out on the way home,
 * instead of the default instant cut, for a more tactile feel.
 */
@Composable
fun AppNav() {
    val navController = rememberNavController()
    val viewModel: ExpenseViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Routes.DASHBOARD,
            enterTransition = { fadeIn(tween(TRANSITION_MS)) },
            exitTransition = {
                fadeOut(tween(TRANSITION_MS)) + slideOutHorizontally(tween(TRANSITION_MS)) { -it / 4 }
            },
            popEnterTransition = {
                fadeIn(tween(TRANSITION_MS)) + slideInHorizontally(tween(TRANSITION_MS)) { -it / 4 }
            }
        ) {
            DashboardScreen(
                viewModel = viewModel,
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(
            Routes.SETTINGS,
            enterTransition = {
                fadeIn(tween(TRANSITION_MS)) + slideInHorizontally(tween(TRANSITION_MS)) { it }
            },
            popExitTransition = {
                fadeOut(tween(TRANSITION_MS)) + slideOutHorizontally(tween(TRANSITION_MS)) { it }
            }
        ) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
