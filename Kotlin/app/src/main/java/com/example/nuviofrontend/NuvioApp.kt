package com.example.nuviofrontend

import android.app.Application
import com.example.core.settings.CurrencyPreference
import com.example.core.settings.LanguagePreference
import com.example.core.settings.LocaleManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import java.util.Locale

@HiltAndroidApp
class NuvioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
        applySavedCurrency()
    }

    private fun applySavedLanguage() {
        val languageCode = runBlocking {
            LanguagePreference.getSavedLanguage(this@NuvioApp)
        } ?: "hr"
        LocaleManager.setLocale(this, Locale(languageCode))
    }

    private fun applySavedCurrency() {
        val currencyCode = runBlocking {
            CurrencyPreference.getSavedCurrency(this@NuvioApp)
        } ?: "€"
        LocaleManager.setLocale(this, Locale(currencyCode))
    }
}
