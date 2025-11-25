package com.example.core.cards.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddCardResponse(
    @Json(name = "data") val card: CardDto
)
