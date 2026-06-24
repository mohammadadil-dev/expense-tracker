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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.expensetracker.app.R
import com.expensetracker.app.ui.theme.AccentIndigo
import com.expensetracker.app.ui.theme.AccentIndigoLight
import com.expensetracker.app.ui.theme.CardWhite
import com.expensetracker.app.ui.theme.OnAccent
import com.expensetracker.app.ui.theme.TextMuted
import com.expensetracker.app.util.DateUtils
import java.util.Locale

/**
 * Custom Gregorian/Hijri date-picker dialog, replacing the OS-native (Gregorian-only)
 * Material3 DatePicker. Shows whichever calendar system matches [locale] — Hijri for
 * Arabic, Gregorian for everything else — with no manual toggle. Whatever's picked is
 * converted back to a plain Gregorian "yyyy-MM-dd" via [onConfirm], since that's what's
 * stored everywhere else in the app.
 */
@Composable
fun CalendarDatePickerDialog(
    initialDateIso: String,
    locale: Locale,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val isHijri = locale.language == "ar"
    var selectedDateIso by remember { mutableStateOf(initialDateIso) }

    var gregorianMonthKey by remember { mutableStateOf(DateUtils.monthKeyFromDate(initialDateIso)) }
    val initialHijri = remember(initialDateIso, locale) { DateUtils.toHijri(initialDateIso, locale) }
    var hijriYear by remember { mutableStateOf(initialHijri.year) }
    var hijriMonth by remember { mutableStateOf(initialHijri.month) }

    val todayIso = remember { DateUtils.todayIso() }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.94f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close))
                    }
                    Text(
                        text = stringResource(R.string.calendar_picker_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(Modifier.height(16.dp))

                if (isHijri) {
                    MonthNavHeader(
                        label = DateUtils.hijriMonthLabel(hijriYear, hijriMonth, locale),
                        onPrevious = {
                            val (y, m) = DateUtils.shiftHijriMonth(hijriYear, hijriMonth, -1)
                            hijriYear = y
                            hijriMonth = m
                        },
                        onNext = {
                            val (y, m) = DateUtils.shiftHijriMonth(hijriYear, hijriMonth, 1)
                            hijriYear = y
                            hijriMonth = m
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    val daysInMonth = DateUtils.hijriMonthLength(hijriYear, hijriMonth)
                    CalendarGrid(
                        weekdayLabels = DateUtils.hijriWeekdayLabels(locale),
                        firstWeekdayIndex = DateUtils.hijriFirstWeekdayIndex(hijriYear, hijriMonth),
                        daysInMonth = daysInMonth,
                        dayToIso = { day -> DateUtils.hijriToIso(hijriYear, hijriMonth, day) },
                        selectedDateIso = selectedDateIso,
                        todayIso = todayIso,
                        onDaySelected = { selectedDateIso = it }
                    )
                } else {
                    MonthNavHeader(
                        label = DateUtils.monthLabel(gregorianMonthKey, locale),
                        onPrevious = { gregorianMonthKey = DateUtils.shiftMonthKey(gregorianMonthKey, -1) },
                        onNext = { gregorianMonthKey = DateUtils.shiftMonthKey(gregorianMonthKey, 1) }
                    )
                    Spacer(Modifier.height(12.dp))
                    val daysInMonth = DateUtils.daysInMonth(gregorianMonthKey)
                    CalendarGrid(
                        weekdayLabels = DateUtils.weekdayMediumLabels(locale),
                        firstWeekdayIndex = DateUtils.firstWeekdayIndexOfMonth(gregorianMonthKey),
                        daysInMonth = daysInMonth,
                        dayToIso = { day -> "$gregorianMonthKey-${day.toString().padStart(2, '0')}" },
                        selectedDateIso = selectedDateIso,
                        todayIso = todayIso,
                        onDaySelected = { selectedDateIso = it }
                    )
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = { onConfirm(selectedDateIso) }) { Text(stringResource(R.string.ok)) }
                }
            }
        }
    }
}

/** Prev/next chevrons around the current month/year label, shared by both calendar systems. */
@Composable
private fun MonthNavHeader(label: String, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = null)
        }
        Text(text = label, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null)
        }
    }
}

/** The 7-column day grid itself, shared by both the Gregorian and Hijri branches above. */
@Composable
private fun CalendarGrid(
    weekdayLabels: List<String>,
    firstWeekdayIndex: Int,
    daysInMonth: Int,
    dayToIso: (Int) -> String,
    selectedDateIso: String,
    todayIso: String,
    onDaySelected: (String) -> Unit
) {
    Column {
        Row(Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(6.dp))

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
                            val dayIso = dayToIso(dayNum)
                            val isSelected = dayIso == selectedDateIso
                            val isToday = dayIso == todayIso
                            val bgColor by animateColorAsState(
                                targetValue = when {
                                    isSelected -> AccentIndigo
                                    isToday -> AccentIndigoLight
                                    else -> Color.Transparent
                                },
                                animationSpec = tween(150),
                                label = "pickerDayBg"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .clickable { onDaySelected(dayIso) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) OnAccent else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
