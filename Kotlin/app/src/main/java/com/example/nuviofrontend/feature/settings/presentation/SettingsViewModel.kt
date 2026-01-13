package com.example.nuviofrontend.feature.settings.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import com.example.core.settings.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStore: DataStore<Preferences>) : ViewModel(){
    private val CURRENCY_KEY = intPreferencesKey("currency")
    private val LANGUAGE_KEY = intPreferencesKey("language")

    val currencyFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[CURRENCY_KEY] ?: 1
    }

    suspend fun saveCurrency(currencyIndex: Int) {
        dataStore.edit { prefs ->
            prefs[CURRENCY_KEY] = currencyIndex
        }
    }

    val languageFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: 0
    }

    suspend fun saveLanguage(languageIndex: Int) {
        dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = languageIndex
        }
    }

    val themeFlow: Flow<Int> = ThemePreference.themeFlow(dataStore)

    suspend fun saveTheme(index: Int) {
        ThemePreference.saveTheme(dataStore, index)
    }
}