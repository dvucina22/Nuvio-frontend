package com.example.core.auth.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String,
    val createdAt: String
)
