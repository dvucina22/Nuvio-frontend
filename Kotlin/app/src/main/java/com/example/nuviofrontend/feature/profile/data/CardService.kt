package com.example.nuviofrontend.feature.profile.data

import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.CardDto
import com.example.core.network.api.ApiService
import retrofit2.HttpException
import java.io.IOException

class CardService(private val api: ApiService) {
    suspend fun getCards(): List<CardDto> {
        val cardsResponse = api.getCards()
        return cardsResponse.cards
    }

    suspend fun addCard(request: AddCardRequest): CardDto {
        val response = api.addCard(request)
        if (response.isSuccessful) {
            val card = response.body()?.card
            if (card != null) {
                return card
            } else {
                throw IOException("Empty response body")
            }
        }
        val errorBody = response.errorBody()?.string()?.lowercase() ?: ""
        when {
            "invalid token" in errorBody -> throw IllegalArgumentException("invalid_token")
            "invalid card number" in errorBody -> throw IllegalArgumentException("invalid_card")
            "missing required fields" in errorBody -> throw IllegalArgumentException("missing_fields")
            "missing token" in errorBody -> throw IllegalArgumentException("missing_token")
            "card has expired" in errorBody -> throw IllegalArgumentException("expired_card")
            else -> throw HttpException(response)
        }
    }

    suspend fun deleteCard(cardId: Int) {
        val response = api.deleteCard(cardId)
        if (response.isSuccessful) return

        val errorBody = response.errorBody()?.string()?.lowercase() ?: ""
        when {
            "invalid token" in errorBody -> throw IllegalArgumentException("invalid_token")
            "missing token" in errorBody -> throw IllegalArgumentException("missing_token")
            "card not found" in errorBody -> throw IllegalArgumentException("card_not_found")
            "invalid card data" in errorBody -> throw IllegalArgumentException("invalid_card")
            else -> throw Exception("server_error")
        }
    }

    suspend fun setPrimaryCard(cardId: Int): String {
        val response = api.setPrimaryCard(cardId)
        if (response.isSuccessful) {
            return response.body()?.message ?: "Card set as primary"
        } else {
            val errorBody = response.errorBody()?.string()?.lowercase() ?: ""
            when {
                "invalid token" in errorBody -> throw IllegalArgumentException("invalid_token")
                "card not found" in errorBody -> throw IllegalArgumentException("card_not_found")
                else -> throw Exception("server_error")
            }
        }
    }
}