package com.example.core.auth.dto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OAuthVerifyRequest(
    @Json(name = "id_token") val idToken: String
)
