package com.example.core.user.dto

import com.example.core.auth.dto.Role

data class UserListItemDto(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val profilePictureUrl: String?,
    val roles: List<Role>?,
    val isActive: Boolean
)
