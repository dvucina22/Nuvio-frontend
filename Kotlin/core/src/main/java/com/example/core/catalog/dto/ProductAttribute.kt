package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductAttribute(
    val id: Int,
    val name: String,
    val value: String
)
