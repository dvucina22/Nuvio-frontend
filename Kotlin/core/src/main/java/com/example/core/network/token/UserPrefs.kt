package com.example.core.network.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.userPrefs by preferencesDataStore("user_prefs")

class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) : IUserPrefs {

    private val FIRST = stringPreferencesKey("first_name")
    private val LAST = stringPreferencesKey("last_name")
    private val EMAIL = stringPreferencesKey("email")

    override val profileFlow: Flow<UserProfile?> =
        context.userPrefs.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { prefs ->
                UserProfile(
                    firstName = prefs[FIRST] ?: "",
                    lastName = prefs[LAST] ?: "",
                    email = prefs[EMAIL] ?: ""
                )
            }

    override suspend fun saveProfile(p: UserProfile) {
        context.userPrefs.edit { e ->
            e[FIRST] = p.firstName
            e[LAST] = p.lastName
            e[EMAIL] = p.email
        }
    }

    override suspend fun clear() {
        context.userPrefs.edit { it.clear() }
    }
}
