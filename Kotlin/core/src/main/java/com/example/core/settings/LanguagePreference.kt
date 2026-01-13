package com.example.core.settings

import android.content.Context

object LanguagePreference {
    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_LANGUAGE = "key_language"

    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "hr") ?: "hr"
    }
}