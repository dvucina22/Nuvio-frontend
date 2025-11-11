package com.example.nuviofrontend.feature.auth.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OAuthVerifyResponse(
    val token: String
)
