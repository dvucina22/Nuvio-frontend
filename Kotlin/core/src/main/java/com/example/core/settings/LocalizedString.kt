package com.example.core.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun localizedString(resId: Int): String {
    val context = LocalContext.current
    val locale = LocaleManager.currentLocale

    val configuration = context.resources.configuration
    configuration.setLocale(locale)

    return context.createConfigurationContext(configuration)
        .resources
        .getString(resId)
}