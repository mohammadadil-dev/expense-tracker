package com.expensetracker.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.expensetracker.app.ui.navigation.AppNav
import com.expensetracker.app.ui.theme.BgApp
import com.expensetracker.app.ui.theme.ExpenseTrackerTheme
import com.expensetracker.app.util.SmsConsentManager
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever

/**
 * The only Activity in the app. Everything else is composables hosted by a single
 * NavHost — no other screens, no external intents, no network permissions needed.
 *
 * Extends AppCompatActivity (not plain ComponentActivity) so AppCompat's per-app
 * language backport can reliably recreate this activity when the user switches
 * language at runtime via LocaleHelper / AppCompatDelegate.setApplicationLocales().
 *
 * Also hosts the SMS User Consent listener: showing the system consent prompt requires an
 * Activity, so this is where the [ActivityResultLauncher] and [SmsConsentManager] live. The
 * `by viewModels()` delegate resolves to the same ExpenseViewModel instance Compose's
 * `viewModel()` call inside AppNav() gets (both are scoped to this Activity), so a consented
 * SMS's text can be forwarded straight into the existing data flow with no extra plumbing.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var smsConsentManager: SmsConsentManager
    private lateinit var smsResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        smsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)?.let { message ->
                    viewModel.handleIncomingSms(message)
                }
            }
            // Re-arm immediately (while still in the foreground) so a second follow-up SMS for
            // the same payment — e.g. a UPI debit alert followed by the bank's own debit alert —
            // can still be caught without the user having to reopen the app.
            if (viewModel.smsDetectionEnabled.value) smsConsentManager.startListening()
        }
        smsConsentManager = SmsConsentManager(this, smsResultLauncher)

        setContent {
            ExpenseTrackerApp()
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.smsDetectionEnabled.value) smsConsentManager.startListening()
    }

    override fun onStop() {
        super.onStop()
        smsConsentManager.stopListening()
    }
}

@Composable
private fun ExpenseTrackerApp() {
    ExpenseTrackerTheme {
        // No shared background mounted here anymore — Dashboard and Settings each mount their
        // own AnimatedBlobBackground instance with a distinct color trio, so every screen reads
        // as its own "place" instead of sharing one global backdrop.
        Surface(modifier = Modifier.fillMaxSize(), color = BgApp) {
            AppNav()
        }
    }
}
