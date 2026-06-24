package com.expensetracker.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.TextMuted

/**
 * A Sunday-first month calendar grid. Each day with at least one expense gets a small dot;
 * tapping a day reports it via [onDaySelected] so the caller can show that day's expenses
 * below. Rendered eagerly (not lazily) since a month never has more than ~42 cells.
 */
@Composable
fun MonthCalendarView(
    monthKey: String,
    firstWeekdayIndex: Int,
    daysInMonth: Int,
    dailyTotals: Map<String, Double>,
    selectedDay: String?,
    todayIso: String,
    weekdayLabels: List<String>,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        val totalCells = firstWeekdayIndex + daysInMonth
        val totalRows = (totalCells + 6) / 7
        for (rowIndex in 0 until totalRows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = rowIndex * 7 + col
                    val dayNum = cellIndex - firstWeekdayIndex + 1
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayNum in 1..daysInMonth) {
                            val dayIso = "$monthKey-${dayNum.toString().padStart(2, '0')}"
                            val isSelected = dayIso == selectedDay
                            val isToday = dayIso == todayIso
                            val hasExpenses = dailyTotals.containsKey(dayIso)
                            val bgColor by animateColorAsState(
                                targetValue = when {
                                    isSelected -> AccentIndigo
                                    isToday -> AccentIndigoLight
                                    else -> CardWhite
                                },
                                animationSpec = tween(160),
                                label = "calendarDayBg"
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .clickable { onDaySelected(dayIso) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) OnAccent else MaterialTheme.colorScheme.onSurface
                                )
                                if (hasExpenses) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(
                                                if (isSelected) OnAccent else AccentIndigo,
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
