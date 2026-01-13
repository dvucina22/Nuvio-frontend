package com.example.core.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

object LocaleManager {
    var currentLocale by mutableStateOf(Locale.getDefault())
        private set

    fun setLocale(context: Context, locale: Locale) {
        currentLocale = locale
        updateResources(context, locale)
    }

    private fun updateResources(context: Context, locale: Locale) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}