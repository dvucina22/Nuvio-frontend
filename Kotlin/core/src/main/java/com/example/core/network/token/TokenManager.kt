package com.example.core.network.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("auth_prefs")

class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ITokenStorage {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    }

    @Volatile
    private var _cachedToken: String? = null

    override val cachedToken: String?
        get() = _cachedToken

    override suspend fun saveAccessToken(token: String) {
        _cachedToken = token
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
        }
    }

    override suspend fun getAccessToken(): String? {
        if (_cachedToken != null) return _cachedToken

        val token = context.dataStore.data.first()[ACCESS_TOKEN_KEY]
        _cachedToken = token
        return token
    }

    override suspend fun clear() {
        _cachedToken = null
        context.dataStore.edit { it.clear() }
    }

    val accessTokenFlow: Flow<String?> =
        context.dataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { prefs ->
                val token = prefs[ACCESS_TOKEN_KEY]
                _cachedToken = token
                token
            }
}
