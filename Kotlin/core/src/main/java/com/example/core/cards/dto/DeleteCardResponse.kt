package com.example.core.cards.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteCardResponse(
    val message: String
)
