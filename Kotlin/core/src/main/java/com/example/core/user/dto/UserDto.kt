package com.example.core.user.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String,
    val gender: String?,
    val profilePictureUrl: String?
)
