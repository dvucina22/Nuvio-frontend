package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateProductResponse(
    val message: String
)
