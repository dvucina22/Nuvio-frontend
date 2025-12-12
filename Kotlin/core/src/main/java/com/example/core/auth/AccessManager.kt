package com.example.core.auth

import com.example.core.model.UserProfile
import com.example.core.network.token.UserPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessManager @Inject constructor(
    private val userPrefs: UserPrefs
) {
    val profileFlow: Flow<UserProfile?> = userPrefs.profileFlow

    val isAdminFlow: Flow<Boolean> = profileFlow.map { profile ->
        profile?.hasRole(RoleType.ADMIN) == true
    }

    val isSellerFlow: Flow<Boolean> = profileFlow.map { profile ->
        profile?.hasRole(RoleType.SELLER) == true
    }

    fun isAdmin(profile: UserProfile?): Boolean =
        profile?.hasRole(RoleType.ADMIN) == true

    fun isSeller(profile: UserProfile?): Boolean =
        profile?.hasRole(RoleType.SELLER) == true
}
