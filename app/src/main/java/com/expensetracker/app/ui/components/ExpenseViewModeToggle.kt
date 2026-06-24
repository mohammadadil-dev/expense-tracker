package com.expensetracker.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.TextSecondary

/** The four ways "Expenses This Month" can be laid out. */
enum class ExpenseViewMode { DAY, CATEGORY, GRID, CALENDAR }

@Composable
private fun modeIcon(mode: ExpenseViewMode): ImageVector = when (mode) {
    ExpenseViewMode.DAY -> Icons.Filled.ViewAgenda
    ExpenseViewMode.CATEGORY -> Icons.Filled.Label
    ExpenseViewMode.GRID -> Icons.Filled.Apps
    ExpenseViewMode.CALENDAR -> Icons.Filled.DateRange
}

@Composable
private fun modeLabel(mode: ExpenseViewMode): String = when (mode) {
    ExpenseViewMode.DAY -> stringResource(R.string.view_mode_day)
    ExpenseViewMode.CATEGORY -> stringResource(R.string.view_mode_category)
    ExpenseViewMode.GRID -> stringResource(R.string.view_mode_grid)
    ExpenseViewMode.CALENDAR -> stringResource(R.string.view_mode_calendar)
}

/** A four-way segmented toggle (icon + tiny label) for switching how the month's expenses render. */
@Composable
fun ExpenseViewModeToggle(
    current: ExpenseViewMode,
    onSelect: (ExpenseViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExpenseViewMode.entries.forEach { mode ->
            val selected = mode == current
            val bgColor by animateColorAsState(
                targetValue = if (selected) AccentIndigo else CardWhite,
                animationSpec = tween(180),
                label = "viewModeBg"
            )
            val fgColor by animateColorAsState(
                targetValue = if (selected) OnAccent else TextSecondary,
                animationSpec = tween(180),
                label = "viewModeFg"
            )
            Column(
                modifier = Modifier
                    .background(bgColor, RoundedCornerShape(10.dp))
                    .clickable { onSelect(mode) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(modeIcon(mode), contentDescription = modeLabel(mode), tint = fgColor)
                Text(
                    text = modeLabel(mode),
                    style = MaterialTheme.typography.labelSmall,
                    color = fgColor
                )
            }
        }
    }
}
