package com.example.core.user.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserResponse(
    val message: String
)