package com.example.core.auth.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String,
    val phoneNumber: String? = null,
    val password: String
)
