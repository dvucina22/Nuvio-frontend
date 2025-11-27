package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductFilterRequest(
    val search: String? = null,
    val brandIds: List<Long>? = null,
    val categoryIds: List<Long>? = null,
    val isActive: Boolean? = null,
    val attributes: List<AttributeFilter>? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val sort: String? = null, // "price_asc", "price_desc", "newest"
    val limit: Int? = null,
    val offset: Int? = null,
    val isInStock: Boolean? = null,
    val isFavorite: Boolean? = null
)
