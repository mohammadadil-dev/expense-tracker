package com.expensetracker.app.util

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Wraps Google Play's In-App Review API — shows the native star-rating dialog right inside
 * the app, without sending the user to the Play Store. This has a much higher completion rate
 * than deep-linking to the store listing (which the Settings screen's "Rate Us" button still
 * does as a manual fallback), because the user never has to leave the app or come back.
 *
 * Important nuance: Google's API intentionally never tells the calling app whether the user
 * actually left a rating, or even whether the dialog was shown at all — Play Console enforces
 * its own quota on how often a given user can be shown this dialog across ALL apps, to prevent
 * review-prompt fatigue. So calling [requestReview] does not guarantee a dialog appears; it's a
 * "request," not a "show." That's why the call site (see [SettingsRepository.hasRequestedReview]
 * in ExpenseViewModel.saveExpense) only ever calls this once per install — there's no feedback
 * signal to retry on, and repeatedly asking would violate Google's own Play Core review policy.
 */
object InAppReviewManager {

    fun requestReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        val requestFlow = manager.requestReviewFlow()
        requestFlow.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                // launchReviewFlow's own task also completes regardless of whether a dialog was
                // actually shown or a rating submitted — intentionally not observed further.
                manager.launchReviewFlow(activity, reviewInfo)
            }
            // If the request itself failed (e.g. no Play Store on device, emulator without Play
            // Services), fail silently — this must never surface an error or block the user.
        }
    }
}
