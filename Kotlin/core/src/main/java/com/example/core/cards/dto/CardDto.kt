package com.example.core.cards.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardDto(
    val id: Int,
    val lastFourDigits: String,
    val cardBrand: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val fullnameOnCard: String,
    val cardName: String,
    val isPrimary: Boolean,
    val createdAt: String,
    val updatedAt: String
)
