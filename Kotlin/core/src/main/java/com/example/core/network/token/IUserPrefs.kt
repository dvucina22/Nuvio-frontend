package com.example.core.network.token

import com.example.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IUserPrefs {
    val profileFlow: Flow<UserProfile?>
    suspend fun saveProfile(p: UserProfile)
    suspend fun clear()
}
