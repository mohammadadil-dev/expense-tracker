package com.expensetracker.app

import android.app.Application
import com.expensetracker.app.data.AppDatabase
import com.expensetracker.app.data.CurrencyLocaleMapper
import com.expensetracker.app.data.ExpenseRepository
import com.expensetracker.app.data.SettingsRepository
import com.expensetracker.app.util.LocaleHelper
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

        // First launch only: pick a default currency from the phone's region setting.
        // This never calls the network — it's a free, fully offline lookup.
        if (!settings.firstRunDone) {
            settings.currencySymbol = CurrencyLocaleMapper.detectFromDevice()
            settings.firstRunDone = true
        }

        // Re-apply the stored language preference on every cold start.
        LocaleHelper.applyLanguagePreference(settings.languagePref)

        appScope.launch {
            repository.seedDefaultCategoriesIfNeeded()
        }
    }
}
