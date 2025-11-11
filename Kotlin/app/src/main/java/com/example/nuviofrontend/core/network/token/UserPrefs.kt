package com.example.nuviofrontend.core.network.token
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.userPrefs by preferencesDataStore("user_prefs")

class UserPrefs(private val context: Context) {
    private val FIRST = stringPreferencesKey("first_name")
    private val LAST = stringPreferencesKey("last_name")
    private val EMAIL = stringPreferencesKey("email")

    val profileFlow: Flow<UserProfile> =
        context.userPrefs.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { prefs ->
                UserProfile(
                    firstName = prefs[FIRST] ?: "",
                    lastName = prefs[LAST] ?: "",
                    email = prefs[EMAIL] ?: ""
                )
            }

    suspend fun saveProfile(p: UserProfile) {
        context.userPrefs.edit { e ->
            e[FIRST] = p.firstName
            e[LAST] = p.lastName
            e[EMAIL] = p.email
        }
    }

    suspend fun clear() {
        context.userPrefs.edit { it.clear() }
    }
}
