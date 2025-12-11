package com.example.core.cards

import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.CardDto

interface ICardRepository {
    suspend fun getUserCards(): List<CardDto>
    suspend fun addCard(request: AddCardRequest): CardDto
    suspend fun deleteUserCard(cardId: Int)
    suspend fun setPrimaryCard(cardId: Int): String
}