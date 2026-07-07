package com.expensetracker.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.ExpenseRepository
import com.expensetracker.app.data.FamilyRepository
import com.expensetracker.app.data.KhataRepository
import com.expensetracker.app.data.SettingsRepository
import com.expensetracker.app.data.SplitRepository
import com.expensetracker.app.util.LocaleHelper
import com.expensetracker.app.util.ReminderReceiver
import com.expensetracker.app.util.ReminderScheduler
import com.expensetracker.app.util.ReminderWorker
import com.expensetracker.app.util.WeeklyDigestScheduler
import com.expensetracker.app.util.WeeklyDigestWorker
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ExpenseApp : Application() {

    lateinit var database: AppDatabase
    lateinit var repository: ExpenseRepository
    lateinit var khataRepository: KhataRepository
    lateinit var familyRepository: FamilyRepository
    lateinit var splitRepository: SplitRepository
    lateinit var settings: SettingsRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)
        repository = ExpenseRepository(database)
        khataRepository = KhataRepository(database)
        familyRepository = FamilyRepository(database.familyMemberDao())
        splitRepository = SplitRepository(
            database.splitGroupDao(),
            database.splitMemberDao(),
            database.splitExpenseDao(),
            database.splitExpenseShareDao()
        )
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

        // Create notification channels.
        // NotificationChannel is a no-op below API 26 but required on 26+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            // IMPORTANCE_HIGH → heads-up popup + sound + vibration on lock screen.
            nm?.createNotificationChannel(
                NotificationChannel(
                    ReminderReceiver.CHANNEL_ID,
                    "Daily Reminder",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Daily nudge to log your expenses"
                    enableVibration(true)
                    enableLights(true)
                }
            )
            nm?.createNotificationChannel(
                NotificationChannel(
                    WeeklyDigestWorker.CHANNEL_ID,
                    "Weekly Digest",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Weekly spending summary every Sunday" }
            )
        }

        // Ensure the daily reminder AlarmManager alarm is set if the user has it enabled.
        if (settings.reminderEnabled) {
            ReminderScheduler.schedule(this, settings.reminderHour)
        }

        // Schedule the weekly digest — always on, fires Sunday evenings.
        WeeklyDigestScheduler.schedule(this)

        // Initialize AdMob SDK. This must be called once before any AdView or InterstitialAd
        // is created. The callback fires when initialization is complete, but ads can often
        // start loading before it returns (the SDK calls it asynchronously).
        MobileAds.initialize(this) {}

        appScope.launch {
            repository.seedDefaultCategoriesIfNeeded()
            repository.ensureNewBuiltinCategories()
            // Auto-create this month's entries for any recurring expense templates.
            // Runs every startup; idempotent — won't duplicate entries already created.
            repository.createRecurringExpensesForCurrentMonth()
            // Same for recurring income templates.
            repository.createRecurringIncomeForCurrentMonth()
            // Push fresh data to any pinned home-screen widgets.
            com.expensetracker.app.widget.ExpenseWidget.refresh(this@ExpenseApp)
        }
    }
}
