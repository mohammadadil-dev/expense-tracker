package com.expensetracker.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
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
import com.expensetracker.app.util.BiometricHelper
import com.expensetracker.app.util.SmsConsentManager
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

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
 *
 * Biometric lock: if the user has enabled the app lock in Settings, onResume shows a
 * fingerprint/device-credential prompt. The app content is set in onCreate so the NavHost
 * is always initialised — the lock just blocks the Activity window visually until auth passes.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var smsConsentManager: SmsConsentManager
    private lateinit var smsResultLauncher: ActivityResultLauncher<Intent>

    // Force update via Play In-App Updates API.
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var updateResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    // True while biometric prompt is showing — prevents double-firing on quick resume/pause.
    private var biometricPromptShowing = false

    companion object {
        // Kept in companion so it survives Activity recreation (e.g. language switch).
        // Without this, a locale change recreates the Activity and biometric fires again.
        // Reset to false only when the app goes to background (onStop).
        var sessionAuthenticated = false
    }

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

        // Register the update flow launcher. If the user somehow cancels (e.g. backs out of
        // the Play Store overlay), finish the app — they cannot use it without updating.
        appUpdateManager = AppUpdateManagerFactory.create(this)
        updateResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != RESULT_OK) finish()
        }
        // Only check for updates on a true first launch, not on Activity recreations
        // (language switch, rotation) — the Play Store network call adds unnecessary
        // latency every time the user changes language.
        if (savedInstanceState == null) {
            checkForAppUpdate()
        }

        setContent {
            ExpenseTrackerApp()
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.smsDetectionEnabled.value) smsConsentManager.startListening()

        // If an immediate update was already in progress (e.g. user backgrounded the app
        // mid-download), resume it — don't let them into the app until it's installed.
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }

        // Show biometric prompt if the user has enabled app lock and one isn't already showing.
        if (viewModel.biometricEnabled && !biometricPromptShowing &&
            !sessionAuthenticated && BiometricHelper.isAvailable(this)
        ) {
            biometricPromptShowing = true
            BiometricHelper.authenticate(
                activity  = this,
                title     = getString(R.string.biometric_lock_title),
                subtitle  = getString(R.string.biometric_lock_subtitle),
                onSuccess = {
                    biometricPromptShowing = false
                    sessionAuthenticated   = true
                },
                onError   = {
                    // User cancelled or failed — finish so they can't bypass the lock.
                    biometricPromptShowing = false
                    sessionAuthenticated   = false
                    finish()
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.smsDetectionEnabled.value) smsConsentManager.startListening()
    }

    override fun onStop() {
        super.onStop()
        smsConsentManager.stopListening()
        // App went fully to background — require re-auth next time it's foregrounded.
        sessionAuthenticated = false
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
