package com.expensetracker.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.ExpenseRepository
import com.expensetracker.app.data.SettingsRepository
import com.expensetracker.app.util.LocaleHelper
import com.expensetracker.app.util.ReminderScheduler
import com.expensetracker.app.util.ReminderWorker
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ExpenseApp : Application() {

    lateinit var database: AppDatabase
    lateinit var repository: ExpenseRepository
    lateinit var settings: SettingsRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)
        repository = ExpenseRepository(database)
        settings = SettingsRepository(this)

        // Detect the default currency from the phone's region on every launch,
        // unless the user has already explicitly chosen a symbol from the Settings UI.
        //
        // "Explicitly chosen" means the stored value is one of the picker chips — a proper
        // symbol like "$", "€", "ر.س" etc. Legacy 3-letter ISO codes (e.g. "SAR" from an
        // older build that stored the ISO code instead of the Arabic symbol) are treated as
        // stale auto-detections and replaced with the correct detected symbol.
        val storedCurrency = settings.currencySymbol
        val isLegacyIsoCode = storedCurrency.length == 3
                && storedCurrency.all { it.isLetter() && it.isUpperCase() }
        if (!settings.firstRunDone || isLegacyIsoCode) {
            settings.currencySymbol = CurrencyLocaleMapper.detectFromDevice()
            settings.firstRunDone = true
        }

        // Re-apply the stored language preference on every cold start.
        LocaleHelper.applyLanguagePreference(settings.languagePref)

        // Create the notification channel for daily reminders.
        // NotificationChannel is a no-op below API 26 but required on 26+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderWorker.CHANNEL_ID,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily nudge to log your expenses"
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        // Reschedule the daily reminder on every cold start so WorkManager always
        // has a live chain — handles reboots and app updates automatically.
        if (settings.reminderEnabled) {
            ReminderScheduler.schedule(this, settings.reminderHour)
        }

        // Initialize AdMob SDK. This must be called once before any AdView or InterstitialAd
        // is created. The callback fires when initialization is complete, but ads can often
        // start loading before it returns (the SDK calls it asynchronously).
        MobileAds.initialize(this) {}

        appScope.launch {
            repository.seedDefaultCategoriesIfNeeded()
        }
    }
}
