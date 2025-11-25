package com.example.core.cards.dto

import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class AddCardRequest(
    val cardName: String,
    val cardNumber: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val fullnameOnCard: String,
    val isPrimary: Boolean
)
