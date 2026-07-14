package com.expensetracker.app.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.R
import com.expensetracker.app.util.Formatters
import com.expensetracker.app.util.UpiPaymentHelper

/**
 * "Request via UPI" bottom sheet — shown from a Khata party's row (list or detail screen)
 * when they owe the app user money. Builds a standard `upi://pay` link/QR for the exact
 * outstanding balance, payable to the app user's own UPI ID.
 *
 * Deliberately has no UPI-app-detection or "copy link" fallback (see
 * FEATURE_SPEC_KHATA_UPI_PAYMENTS.md §10.3): the QR is generated and shown on-device
 * regardless of what's installed on this device, and sharing always goes through the
 * existing WhatsApp reminder flow — whether the *recipient* has a UPI app is their own
 * concern, same as it already is for the plain-text reminder today.
 *
 * Sharing sends the actual QR *image* (not just the raw link) because WhatsApp only
 * auto-linkifies http(s) URLs — a `upi://pay` link shows up as inert plain text in a chat
 * bubble, so the image is what makes this actually scannable/usable for the recipient.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestUpiPaymentSheet(
    myUpiId: String,
    payeeDisplayName: String,
    partyName: String,
    amount: Double,
    currencySymbol: String,
    onShareQr: (qrImageUri: android.net.Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val note = "Khata: $partyName"
    val upiUri = remember(myUpiId, payeeDisplayName, amount, note) {
        UpiPaymentHelper.buildUpiUri(myUpiId, payeeDisplayName, amount, note)
    }
    val qrBitmapRaw: Bitmap = remember(upiUri) {
        UpiPaymentHelper.generateQrBitmap(upiUri.toString(), 640)
    }
    val qrBitmap = remember(qrBitmapRaw) { qrBitmapRaw.asImageBitmap() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.khata_request_via_upi),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = Formatters.money(amount, currencySymbol),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    bitmap = qrBitmap,
                    contentDescription = stringResource(R.string.khata_request_via_upi),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(220.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.khata_upi_sheet_caption),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val qrUri = UpiPaymentHelper.saveQrToCache(context, qrBitmapRaw)
                    onShareQr(qrUri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.khata_upi_share_link))
            }
        }
    }
}
