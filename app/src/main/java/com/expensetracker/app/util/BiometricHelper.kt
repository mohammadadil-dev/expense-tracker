package com.expensetracker.app.util

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Thin wrapper around AndroidX BiometricPrompt.
 *
 * Supports BIOMETRIC_STRONG (fingerprint, face, iris) with DEVICE_CREDENTIAL (PIN/pattern/password)
 * as a fallback — so even users without enrolled biometrics can still use app lock via their screen
 * lock.
 *
 * [authenticate] shows the prompt. [onSuccess] is called on the main thread after a successful
 * authentication. [onError] is called with a human-readable message if the user cancels or the
 * device can't authenticate.
 */
object BiometricHelper {

    /** Returns true if the device can authenticate with biometrics or device credential. */
    fun isAvailable(activity: FragmentActivity): Boolean {
        val mgr = BiometricManager.from(activity)
        return mgr.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // ERROR_NEGATIVE_BUTTON / ERROR_USER_CANCELED — user dismissed; treat as error.
                    onError(errString.toString())
                }
                override fun onAuthenticationFailed() {
                    // A single failed attempt — the prompt stays visible, do nothing here.
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()
        prompt.authenticate(info)
    }
}
