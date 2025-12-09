package com.example.core.network.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.auth.dto.Role
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
    private val PHONE = stringPreferencesKey("phone_number")
    private val GENDER = stringPreferencesKey("gender")
    private val PROFILE_PIC = stringPreferencesKey("profile_picture_url")
    private val USER_ID = stringPreferencesKey("user_id")
    private val ROLES = stringPreferencesKey("roles")

    override val profileFlow: Flow<UserProfile?> =
        context.userPrefs.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { prefs ->
                val rolesCsv = prefs[ROLES] ?: ""
                val rolesList = if (rolesCsv.isNotEmpty()) {
                    rolesCsv.split(",").map { Role(id = 0, name = it) }
                } else emptyList()

                UserProfile(
                    id = prefs[USER_ID] ?: "",
                    firstName = prefs[FIRST] ?: "",
                    lastName = prefs[LAST] ?: "",
                    email = prefs[EMAIL] ?: "",
                    phoneNumber = prefs[PHONE] ?: "",
                    gender = prefs[GENDER] ?: "",
                    profilePictureUrl = prefs[PROFILE_PIC] ?: "",
                    roles = rolesList
                )
            }

    override suspend fun saveProfile(p: UserProfile) {
        val rolesCsv = p.roles?.joinToString(",") { it.name } ?: ""
        context.userPrefs.edit { e ->
            e[USER_ID] = p.id
            e[FIRST] = p.firstName
            e[LAST] = p.lastName
            e[EMAIL] = p.email
            e[PHONE] = p.phoneNumber
            e[GENDER] = p.gender
            e[PROFILE_PIC] = p.profilePictureUrl
            e[ROLES] = rolesCsv
        }
    }

    override suspend fun getProfile(): UserProfile? {
        var profile: UserProfile? = null
        context.userPrefs.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { prefs ->
                val rolesCsv = prefs[ROLES] ?: ""
                val rolesList = if (rolesCsv.isNotEmpty()) {
                    rolesCsv.split(",").map { Role(id = 0, name = it) }
                } else emptyList()

                UserProfile(
                    id = prefs[USER_ID] ?: "",
                    firstName = prefs[FIRST] ?: "",
                    lastName = prefs[LAST] ?: "",
                    email = prefs[EMAIL] ?: "",
                    phoneNumber = prefs[PHONE] ?: "",
                    gender = prefs[GENDER] ?: "",
                    profilePictureUrl = prefs[PROFILE_PIC] ?: "",
                    roles = rolesList
                )
            }
            .collect { profile = it }
        return profile
    }

    override suspend fun clear() {
        context.userPrefs.edit { it.clear() }
    }
}