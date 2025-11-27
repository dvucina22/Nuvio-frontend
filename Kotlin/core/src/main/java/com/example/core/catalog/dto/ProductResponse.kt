package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductResponse(
    val products: List<Product>,
    val total: Int,
    val limit: Int,
    val offset: Int
)