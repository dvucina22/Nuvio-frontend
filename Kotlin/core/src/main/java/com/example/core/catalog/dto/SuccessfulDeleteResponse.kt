package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SuccessfulDeleteResponse(
    val message: String
)
