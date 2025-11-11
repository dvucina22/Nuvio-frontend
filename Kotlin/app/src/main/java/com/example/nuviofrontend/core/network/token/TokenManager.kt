package com.example.nuviofrontend.core.network.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    fun saveAccessToken(token: String) = runBlocking {
        context.dataStore.edit { it[ACCESS_TOKEN_KEY] = token }
    }

    fun getAccessToken(): String? = runBlocking {
        context.dataStore.data.first()[ACCESS_TOKEN_KEY]
    }

    fun clear() = runBlocking {
        context.dataStore.edit { it.clear() }
    }

    val accessTokenFlow: Flow<String?> =
        context.dataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[ACCESS_TOKEN_KEY] }
}