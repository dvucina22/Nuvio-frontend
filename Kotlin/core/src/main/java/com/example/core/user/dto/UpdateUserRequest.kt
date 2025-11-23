package com.example.core.user.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserRequest(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?
)
