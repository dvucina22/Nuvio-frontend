package com.example.nuviofrontend.core.network.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    fun saveAccessToken(token: String) {
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[ACCESS_TOKEN_KEY] = token
            }
        }
    }

    fun getAccessToken(): String? = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[ACCESS_TOKEN_KEY]
    }

    fun clear() {
        runBlocking {
            context.dataStore.edit { it.clear() }
        }
    }
}