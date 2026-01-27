package com.example.core.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ThemePreference {
    private val THEME_KEY = intPreferencesKey("theme")

    fun themeFlow(dataStore: DataStore<Preferences>): Flow<Int> =
        dataStore.data.map { it[THEME_KEY] ?: 0 }

    suspend fun saveTheme(dataStore: DataStore<Preferences>, value: Int) {
        dataStore.edit { it[THEME_KEY] = value }
    }
}