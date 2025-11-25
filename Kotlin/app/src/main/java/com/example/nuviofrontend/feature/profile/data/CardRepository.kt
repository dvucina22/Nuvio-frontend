package com.example.nuviofrontend.feature.profile.data

import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.CardDto
import com.example.core.network.token.IUserPrefs

class CardRepository(
    private val cardService: CardService,
    private val userPrefs: IUserPrefs
) {
    suspend fun getUserCards(): List<CardDto> {
        return cardService.getCards()
    }
    suspend fun addCard(request: AddCardRequest): CardDto {
        return cardService.addCard(request)
    }

    suspend fun deleteUserCard(cardId: Int) {
        cardService.deleteCard(cardId)
    }

    suspend fun setPrimaryCard(cardId: Int): String {
        return cardService.setPrimaryCard(cardId)
    }
}