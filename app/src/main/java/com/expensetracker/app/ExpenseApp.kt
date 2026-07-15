package com.expensetracker.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
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

    /**
     * Installs a targeted safety net for a well-known Android/Compose platform race:
     *
     *   java.lang.IllegalArgumentException: parameter must be a descendant of this view
     *       at android.view.ViewGroup.offsetRectBetweenParentAndChild
     *       at android.view.ViewRootImpl.scrollToRectOrFocus
     *       at android.view.ViewRootImpl.draw
     *
     * This happens when a focused interop View (e.g. the EditText backing a Compose
     * TextField) has a pending "scroll me into view" request queued, but gets detached
     * from its parent hierarchy (a ModalBottomSheet/Dialog dismissing, or a dynamically
     * removed list row) before the next draw pass runs. There are zero app frames in the
     * stack trace — it originates entirely inside the platform's View system — so this is
     * not fixable from a specific screen's code. It's also harmless: it happens during a
     * draw traversal after the relevant UI is already gone, so no data is lost or corrupted.
     *
     * We only swallow this *exact* signature (message text + the ViewRootImpl.scrollToRectOrFocus
     * frame) and delegate every other Throwable to the previously-installed default handler
     * so real crashes still surface normally (Play Console vitals, crash reporters, etc.).
     */
    private fun installBenignRenderRaceGuard() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val isBenignScrollFocusRace = throwable is IllegalArgumentException &&
                throwable.message?.contains("must be a descendant of this view") == true &&
                throwable.stackTrace.any { it.className == "android.view.ViewRootImpl" && it.methodName == "scrollToRectOrFocus" }

            if (isBenignScrollFocusRace) {
                Log.w(
                    "ExpenseApp",
                    "Swallowed benign Android platform race (focused view detached during " +
                        "scroll-into-view on draw). Not app-fixable; see installBenignRenderRaceGuard doc.",
                    throwable
                )
            } else {
                if (previousHandler != null) {
                    previousHandler.uncaughtException(thread, throwable)
                } else {
                    Log.e("ExpenseApp", "Uncaught exception, no previous handler installed", throwable)
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        installBenignRenderRaceGuard()

        database = AppDatabase.getInstance(this)
        repository = ExpenseRepository(database)
        khataRepository = KhataRepository(database)
        familyRepository = FamilyRepository(database.familyMemberDao())
        splitRepository = SplitRepository(
            database.splitGroupDao(),
            database.splitMemberDao(),
            database.splitExpenseDao(),
            database.splitExpenseShareDao(),
            database
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

        // Ensure the daily reminder AlarmManager alarm(s) are set if the user has them enabled.
        // Note: this does NOT request POST_NOTIFICATIONS (Application has no Activity to show a
        // permission dialog from) — that happens in AppNav's onboarding completion and the
        // one-time Dashboard catch-up check for pre-existing installs. Scheduling the alarm here
        // regardless of permission status is harmless; the notification just won't display until
        // permission is actually granted.
        if (settings.reminderEnabled) {
            ReminderScheduler.schedule(this, settings.reminderHour, slot = 1)
            if (settings.reminder2Enabled) {
                ReminderScheduler.schedule(this, settings.reminderHour2, slot = 2)
            }
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
            // Fresh installs skip MIGRATION_16_17 entirely (Room creates the latest schema
            // directly), so this is what actually seeds Cash/Bank Account/Card for new users.
            repository.seedDefaultAccountsIfNeeded()
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
