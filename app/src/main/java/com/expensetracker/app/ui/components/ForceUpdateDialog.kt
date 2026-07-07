package com.expensetracker.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.expensetracker.app.ui.theme.AccentGreen
import com.expensetracker.app.ui.theme.AccentGreenMid
import com.expensetracker.app.util.ForceUpdateManager

/**
 * A non-dismissable full-screen dialog shown when the installed app is below the
 * minimum required version ([ForceUpdateManager.MIN_VERSION_CODE]).
 *
 * The user cannot dismiss this dialog via the back button or tapping outside —
 * the only escape is tapping "Update Now", which opens the Play Store listing.
 *
 * The dialog stays on screen between app sessions: it is shown from the root
 * composable in [ExpenseApp], before the NavHost, so the user literally cannot
 * reach any screen without updating.
 */
@Composable
fun ForceUpdateDialog() {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { /* intentionally blocked — user cannot dismiss */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            val gradientStart = AccentGreen
            val gradientEnd   = AccentGreenMid
            val shape = RoundedCornerShape(28.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation    = 24.dp,
                        shape        = shape,
                        ambientColor = gradientStart.copy(alpha = 0.4f),
                        spotColor    = gradientStart.copy(alpha = 0.5f)
                    )
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Icon in gradient circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .drawBehind {
                            drawRect(
                                brush = Brush.linearGradient(
                                    listOf(gradientStart, gradientEnd),
                                    start = Offset.Zero,
                                    end   = Offset(size.width, size.height)
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SystemUpdate,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    "Update Required",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    "A newer version of the app is available with important improvements and fixes. " +
                    "Please update to continue using the app.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                // Update Now button — full width, gradient bg
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(ForceUpdateManager.PLAY_STORE_URL)
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            setPackage("com.android.vending") // open in Play Store app
                        }
                        // If Play Store app not installed, fall back to browser
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(ForceUpdateManager.PLAY_STORE_URL)
                                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    )
                ) {
                    Icon(
                        Icons.Filled.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Update Now",
                        style    = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "You must update to continue",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
