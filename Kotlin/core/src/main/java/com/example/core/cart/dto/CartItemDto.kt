package com.example.core.cart.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartItemDto(
    val id: Int,
    val name: String,
    val basePrice: Double,
    val brand: String,
    val category: String,
    val attributes: List<CartItemAttribute>,
    val imageUrl: String?,
    val quantity: Int,
    val isFavorite: Boolean
)

@JsonClass(generateAdapter = true)
data class CartItemAttribute(
    val id: Int,
    val name: String,
    val value: String
)