package com.example.nuviofrontend.feature.auth.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val token: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)