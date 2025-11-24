package com.example.core.auth.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OAuthVerifyResponse(
    val token: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val gender: String?,
    val profilePictureURL: String?
)
