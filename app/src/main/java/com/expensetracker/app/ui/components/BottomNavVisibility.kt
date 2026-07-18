package com.expensetracker.app.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Shared, cross-screen signal for whether the bottom navigation bar should currently be
 * visible. Each of the app's 5 main bottom-nav screens (Dashboard, Khata, Debts, Splits,
 * Settings) drives this from its own scroll position — scrolling down hides the nav bar (and
 * that screen's own FAB, animated locally against the same signal), scrolling up or coming to
 * rest brings both back.
 *
 * A plain object (not a ViewModel) because this is transient UI state, not app data, and it
 * needs to be written by whichever single screen is currently composed while being read by
 * AppNav's outer Scaffold, which sits above all of them — mirrors the existing
 * BackgroundScrollSignal object already used in this codebase for the blob-background parallax.
 */
object BottomNavVisibility {
    var visible by mutableStateOf(true)
}

/**
 * True while the user is scrolling up on this [LazyListState], or not scrolling at all; false
 * while actively scrolling down. Standard "hide on scroll down, show on scroll up" recipe —
 * compares the current first-visible-item position against the last-seen one on every change,
 * so a single downward flick hides the target and the very next upward movement reveals it.
 */
@Composable
fun LazyListState.rememberIsScrollingUp(): State<Boolean> {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            val scrollingUp = if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }
            previousIndex = firstVisibleItemIndex
            previousScrollOffset = firstVisibleItemScrollOffset
            scrollingUp
        }
    }
}

/**
 * Same idea as [LazyListState.rememberIsScrollingUp], for screens that scroll a plain
 * [ScrollState]-backed Column instead of a LazyColumn (Debts, Settings).
 */
@Composable
fun ScrollState.rememberIsScrollingUp(): State<Boolean> {
    var previousValue by remember(this) { mutableStateOf(value) }
    return remember(this) {
        derivedStateOf {
            val scrollingUp = previousValue >= value
            previousValue = value
            scrollingUp
        }
    }
}
