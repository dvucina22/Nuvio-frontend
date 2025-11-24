package com.example.core.user.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserRequest(
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val gender: String?,
    val profilePictureUrl: String?
)
