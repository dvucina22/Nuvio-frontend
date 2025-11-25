package com.example.core.cards.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PrimaryCardResponse(
    val message: String
)