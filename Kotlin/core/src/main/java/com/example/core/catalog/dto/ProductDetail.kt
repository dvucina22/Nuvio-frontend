package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetail(
    val id: Long,
    val name: String,
    val description: String?,
    val modelNumber: String?,
    val sku: String?,
    val basePrice: Double,
    val isActive: Boolean,
    val brand: Brand,
    val category: Category,
    val images: List<ProductImage>?,
    val attributes: List<ProductAttribute>?,
    val createdAt: String,
    val updatedAt: String,
    val quantity: Long?,
    val isFavorite: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ProductImage(
    val id: Long,
    val url: String,
    val isPrimary: Boolean
)