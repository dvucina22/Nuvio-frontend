package com.example.core.user.dto
import com.example.core.auth.dto.Role
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val gender: String?,
    val profilePictureUrl: String?,
    val phoneNumber: String?,
    val roles: List<Role>?
)
