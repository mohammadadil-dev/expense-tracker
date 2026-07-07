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
import com.expensetracker.app.ui.components.ForceUpdateDialog
import com.expensetracker.app.ui.navigation.AppNav
import com.expensetracker.app.ui.theme.BgApp
import com.expensetracker.app.ui.theme.ExpenseTrackerTheme
import com.expensetracker.app.util.ForceUpdateManager
import com.expensetracker.app.util.BiometricHelper
import com.expensetracker.app.util.SmsConsentManager
import com.expensetracker.app.viewmodel.ExpenseViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
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

    // Tracks whether the currently active update flow is IMMEDIATE (blocking).
    // Used in updateResultLauncher so we only finish() the app when the user
    // cancels/rejects a mandatory immediate update — not a flexible one.
    private var immediateUpdateStarted = false

    // Flexible-update listener: fires when a background download completes.
    // We call completeUpdate() immediately so the app restarts and installs
    // without asking the user — this makes every release a forced update even
    // when Play Console priority is not set to 5.
    private val flexibleInstallListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

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

        // Register the update flow launcher.
        // For IMMEDIATE updates: if user cancels/rejects → finish() so they can't bypass.
        // For FLEXIBLE updates: cancelling is allowed — the download continues in background
        // and installs automatically when done (via flexibleInstallListener).
        appUpdateManager = AppUpdateManagerFactory.create(this)
        updateResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (immediateUpdateStarted && result.resultCode != RESULT_OK) finish()
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
            if (info.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return@addOnSuccessListener

            when {
                // Layer 1: IMMEDIATE (blocking full-screen) — triggered when Play Console
                // update priority is 4 or 5, OR after staleness threshold passes.
                // The user cannot dismiss this; cancelling finishes the app.
                info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                    immediateUpdateStarted = true
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }

                // Layer 2: FLEXIBLE fallback — works even when Play Console priority is 0.
                // The update downloads silently in the background. When it finishes,
                // flexibleInstallListener calls completeUpdate() which restarts the app
                // automatically. No user action needed — effectively a silent force update.
                info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    immediateUpdateStarted = false
                    appUpdateManager.registerListener(flexibleInstallListener)
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Always unregister to avoid memory leaks on Activity recreation.
        appUpdateManager.unregisterListener(flexibleInstallListener)
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
        Surface(modifier = Modifier.fillMaxSize(), color = BgApp) {
            AppNav()
        }

        // Layer 3: in-app custom dialog — shown when the installed versionCode is below
        // ForceUpdateManager.MIN_VERSION_CODE. This fires even if the Play In-App Updates
        // API fails (e.g. no network at install time, sideloaded APK, Play Store cache lag).
        // The dialog is non-dismissable; the only action is "Update Now" → Play Store.
        val context = androidx.compose.ui.platform.LocalContext.current
        if (ForceUpdateManager.isUpdateRequired(context)) {
            ForceUpdateDialog()
        }
    }
}
