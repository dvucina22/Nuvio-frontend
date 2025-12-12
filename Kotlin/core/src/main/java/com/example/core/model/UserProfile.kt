package com.example.core.model

import com.example.core.auth.RoleType
import com.example.core.auth.toRoleType
import com.example.core.auth.dto.Role

data class UserProfile(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val profilePictureUrl: String = "",
    val roles: List<Role> = emptyList()
) {
    fun hasRole(roleType: RoleType): Boolean = roles.any { it.toRoleType() == roleType }
}
