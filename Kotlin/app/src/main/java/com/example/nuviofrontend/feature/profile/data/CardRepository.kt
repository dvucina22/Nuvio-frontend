package com.example.nuviofrontend.feature.profile.data

import com.example.core.cards.ICardRepository
import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.CardDto
import com.example.core.network.token.IUserPrefs
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val cardService: CardService,
    private val userPrefs: IUserPrefs
) : ICardRepository {
    override suspend fun getUserCards(): List<CardDto> {
        return cardService.getCards()
    }
    override suspend fun addCard(request: AddCardRequest): CardDto {
        return cardService.addCard(request)
    }

    override suspend fun deleteUserCard(cardId: Int) {
        cardService.deleteCard(cardId)
    }

    override suspend fun setPrimaryCard(cardId: Int): String {
        return cardService.setPrimaryCard(cardId)
    }
}