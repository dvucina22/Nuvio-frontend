package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val id: Long,
    val name: String,
    val description: String?,
    val modelNumber: String?,
    val sku: String?,
    val basePrice: Double,
    val isActive: Boolean,
    val brand: String,
    val category: String,
    val imageUrl: String,
    val attributes: List<ProductAttribute>?,
    val createdAt: String,
    val updatedAt: String,
    val quantity: Long?
)