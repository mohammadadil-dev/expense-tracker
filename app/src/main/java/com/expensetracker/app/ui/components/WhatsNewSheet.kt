package com.expensetracker.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.ui.theme.AccentGreen
import kotlinx.coroutines.launch

private data class Feature(
    val emoji: String,
    val title: String,
    val description: String,
    val accentColor: Color
)

private val features = listOf(
    Feature(
        emoji = "📷",
        title = "Scan Receipts",
        description = "Point your camera at any bill or receipt. The app reads the amount and logs the expense instantly — no typing needed.",
        accentColor = Color(0xFF2D6A4F)
    ),
    Feature(
        emoji = "🎤",
        title = "Voice Input",
        description = "Just say \"spent 50 on food\" and it's logged. Hands-free expense tracking for when you're on the go.",
        accentColor = Color(0xFF1565C0)
    ),
    Feature(
        emoji = "👥",
        title = "Split Bills",
        description = "Create groups, add shared expenses, and let the app calculate who owes what. Settle up with one WhatsApp message.",
        accentColor = Color(0xFF6A1B9A)
    ),
    Feature(
        emoji = "📒",
        title = "Khata / Ledger",
        description = "Track who owes you money and what you owe others. Send payment reminders directly on WhatsApp with one tap.",
        accentColor = Color(0xFFB71C1C)
    ),
    Feature(
        emoji = "🎯",
        title = "Goals & Streaks",
        description = "Set savings goals and track your progress. Build a daily logging habit and keep your streak going!",
        accentColor = Color(0xFFE65100)
    ),
)

@Composable
fun WhatsNewSheet(onDismiss: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { features.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == features.size - 1
    val currentFeature = features[pagerState.currentPage]

    // Full-screen scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141428)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Header ───────────────────────────────────────────────────
                Text(
                    text = "✨  What's New",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 22.sp
                )
                Text(
                    text = "Swipe to explore new features",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
                )

                // ── Feature Pager ─────────────────────────────────────────────
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.height(240.dp)
                ) { page ->
                    val feature = features[page]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Emoji circle
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(feature.accentColor.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = feature.emoji,
                                fontSize = 40.sp
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = feature.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = feature.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Dot Indicators ────────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    features.indices.forEach { i ->
                        val isActive = i == pagerState.currentPage
                        val dotWidth by animateDpAsState(
                            targetValue = if (isActive) 20.dp else 6.dp,
                            animationSpec = tween(200),
                            label = "dotWidth"
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (isActive) AccentGreen else Color.White.copy(alpha = 0.25f),
                            animationSpec = tween(200),
                            label = "dotColor"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .height(6.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Buttons ───────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLastPage) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Skip",
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isLastPage) {
                                onDismiss()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isLastPage) "Let's Go! 🚀" else "Next →",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
