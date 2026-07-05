package com.expensetracker.app.ui.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

// ─── Ad Unit IDs ─────────────────────────────────────────────────────────────
//
// TEST IDs — safe to use during development, never generate real revenue.
// BEFORE PUBLISHING: replace with your production Ad Unit IDs from admob.google.com
// Also replace APPLICATION_ID in AndroidManifest.xml.
//
private const val BANNER_UNIT_ID       = "ca-app-pub-8890346685665889/8936478861"
private const val INTERSTITIAL_UNIT_ID = "ca-app-pub-8890346685665889/4726982565"

// ─────────────────────────────────────────────────────────────────────────────
// Banner Ad Composable  — with proper lifecycle management
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Standard 320×50 banner ad embedded via [AndroidView].
 *
 * [AdView] is a legacy View that must be paused/resumed/destroyed in step with the
 * Activity lifecycle — skipping these calls is the #1 cause of banner ad crashes on
 * Compose screens. We attach a [DefaultLifecycleObserver] via [DisposableEffect] to
 * forward every relevant event, then destroy the view when the composable leaves.
 */
@Composable
fun BannerAdView(modifier: Modifier = Modifier, show: Boolean = true) {
    if (!show) return   // screenshot mode — render nothing, no gap
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create the AdView once and remember it across recompositions.
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BANNER_UNIT_ID
        }
    }

    // Wire AdView lifecycle events to the host lifecycle so it pauses/resumes
    // correctly and is destroyed when the composable is removed.
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner)  { adView.resume()  }
            override fun onPause(owner: LifecycleOwner)   { adView.pause()   }
            override fun onDestroy(owner: LifecycleOwner) { adView.destroy() }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // Load the first ad only after attaching the observer, so lifecycle events
        // are already hooked when the network response arrives.
        adView.loadAd(AdRequest.Builder().build())
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adView.destroy()
        }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Interstitial Ad Manager
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Singleton that keeps one interstitial loaded and ready to show.
 *
 * Usage:
 *  1. Call [preload] early (LaunchedEffect on screen entry) so the ad is ready.
 *  2. Call [showThen] at the trigger point (e.g. PDF export tap).
 *     - Ad ready → shows it, then calls [onAfterAd] when user dismisses it.
 *     - Ad not ready → calls [onAfterAd] immediately; user is never blocked.
 *  3. After each show or failure, [showThen] automatically reloads the next ad.
 */
object InterstitialAdManager {

    private var loadedAd: InterstitialAd? = null
    private var isLoading = false

    /** Starts loading in the background; no-op if one is already loaded or loading. */
    fun preload(context: Context) {
        if (loadedAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            context.applicationContext,
            INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loadedAd = ad
                    isLoading = false
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadedAd = null
                    isLoading = false
                }
            }
        )
    }

    /**
     * Shows the interstitial if ready, then calls [onAfterAd] on dismiss.
     * If no ad is available, [onAfterAd] fires immediately so the user's action
     * still goes through without any delay.
     */
    fun showThen(activity: Activity, onAfterAd: () -> Unit) {
        val ad = loadedAd
        if (ad == null) {
            onAfterAd()
            preload(activity)
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                loadedAd = null
                onAfterAd()
                preload(activity)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                loadedAd = null
                onAfterAd()
                preload(activity)
            }
        }
        ad.show(activity)
    }
}
