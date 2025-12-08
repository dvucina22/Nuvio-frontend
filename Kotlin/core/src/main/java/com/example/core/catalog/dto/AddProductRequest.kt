package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddProductRequest(
    val name: String,
    val description: String,
    val modelNumber: String,
    val sku: String,
    val basePrice: Double,
    val brandId: Int,
    val categoryId: Int,
    val quantity: Int,
    val attributeIds: List<Long>,
    val imageUrls: List<String> = emptyList()
)
