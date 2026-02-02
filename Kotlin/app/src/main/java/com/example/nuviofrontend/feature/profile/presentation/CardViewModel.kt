package com.example.nuviofrontend.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.CardDto
import com.example.nuviofrontend.feature.profile.data.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardErrors(
    val cardNumberError: String? = null,
    val expiryError: String? = null
)
@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _cards = MutableStateFlow<List<CardDto>>(emptyList())
    val cards: StateFlow<List<CardDto>> = _cards

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<CardErrors?>(null)
    val error: StateFlow<CardErrors?> = _error
    private val _addingCard = MutableStateFlow(false)

    init { fetchCards() }

    fun fetchCards() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _cards.value = cardRepository.getUserCards()
                _error.value = null
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun addCard(
        cardName: String,
        cardNumber: String,
        expirationMonth: Int,
        expirationYear: Int,
        fullName: String,
        isPrimary: Boolean = true,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                _addingCard.value = true
                _error.value = null
                val request = AddCardRequest(
                    cardName = cardName,
                    cardNumber = cardNumber,
                    expirationMonth = expirationMonth,
                    expirationYear = 2000 + expirationYear,
                    fullnameOnCard = fullName,
                    isPrimary = isPrimary
                )
                val newCard = cardRepository.addCard(request)

                _cards.value = if (isPrimary) {
                    _cards.value.map { it.copy(isPrimary = false) } + newCard
                } else {
                    _cards.value + newCard
                }

                onSuccess?.invoke()
            } catch (e: Exception) {
                val msg = e.message?.lowercase() ?: ""
                _error.value = CardErrors(
                    cardNumberError = if ("invalid_card" in msg) "Broj kartice nije ispravan" else null,
                    expiryError = if ("expired_card" in msg) "Kartica je istekla" else null
                )
            } finally {
                _addingCard.value = false
            }
        }
    }
    fun clearError() {
        _error.value = null
    }

    fun deleteCard(
        cardId: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                cardRepository.deleteUserCard(cardId.toInt())
                _cards.value = _cards.value.filterNot { it.id.toString() == cardId }
                onSuccess?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke(e.message ?: "Greška prilikom brisanja kartice")
            } finally {
                _loading.value = false
            }
        }
    }

    fun setPrimaryCard(cardId: String) {
        viewModelScope.launch {
            try {
                cardRepository.setPrimaryCard(cardId.toInt())
                val selectedId = cardId.toInt()
                _cards.value = _cards.value.map { it.copy(isPrimary = it.id == selectedId) }
            } catch (e: Exception) {
                _error.value = CardErrors(cardNumberError = e.localizedMessage ?: "Greška")
            } finally {
                _loading.value = false
            }
        }
    }

}

