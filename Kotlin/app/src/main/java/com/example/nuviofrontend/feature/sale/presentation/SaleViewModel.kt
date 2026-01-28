package com.example.nuviofrontend.feature.sale.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.cards.dto.CardDto
import com.example.core.cart.dto.CartItemDto
import com.example.core.model.UserProfile
import com.example.core.sale.dto.SaleProduct
import com.example.core.sale.dto.SaleRequest
import com.example.core.sale.dto.SaleResponse
import com.example.nuviofrontend.feature.cart.data.CartRepository
import com.example.nuviofrontend.feature.profile.data.CardRepository
import com.example.nuviofrontend.feature.sale.data.SaleRepository
import com.example.nuviofrontend.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutState(
    val isLoading: Boolean = false,
    val cartItems: List<CartItemDto> = emptyList(),
    val user: UserProfile? = null,
    val cards: List<CardDto> = emptyList(),
    val selectedCard: CardDto? = null,
    val manualCardNumber: String = "",
    val manualExpiryMonth: String = "",
    val manualExpiryYear: String = "",
    val manualFullName: String = "",
    val error: String? = null,
    val isProcessing: Boolean = false,
    val retryCount: Int = 0
)

sealed class CheckoutResult {
    data class Success(val response: SaleResponse) : CheckoutResult()
    data class Error(val message: String, val maxRetriesReached: Boolean = false) : CheckoutResult()
}

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val cartRepository: CartRepository,
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state

    private val _checkoutResult = MutableStateFlow<CheckoutResult?>(null)
    val checkoutResult: StateFlow<CheckoutResult?> = _checkoutResult

    private val maxRetries = 3

    init {
        loadCheckoutData()
    }

    fun loadCheckoutData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val cartItems = cartRepository.getCartItems()
                val user = userRepository.getUserProfile()
                val cards = cardRepository.getUserCards()
                val primaryCard = cards.firstOrNull { it.isPrimary }

                _state.value = _state.value.copy(
                    isLoading = false,
                    cartItems = cartItems,
                    user = user,
                    cards = cards,
                    selectedCard = primaryCard ?: cards.firstOrNull(),
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load checkout data"
                )
            }
        }
    }

    fun selectCard(card: CardDto) {
        _state.value = _state.value.copy(selectedCard = card)
    }

    fun updateManualCardNumber(value: String) {
        val filtered = value.filter { it.isDigit() }.take(16)
        _state.value = _state.value.copy(manualCardNumber = filtered)
    }

    fun updateManualExpiryMonth(value: String) {
        val filtered = value.filter { it.isDigit() }.take(2)
        _state.value = _state.value.copy(manualExpiryMonth = filtered)
    }

    fun updateManualExpiryYear(value: String) {
        val filtered = value.filter { it.isDigit() }.take(2)
        _state.value = _state.value.copy(manualExpiryYear = filtered)
    }

    fun updateManualFullName(value: String) {
        _state.value = _state.value.copy(manualFullName = value)
    }

    fun processSale() {
        val currentState = _state.value

        if (currentState.cartItems.isEmpty()) {
            _state.value = currentState.copy(error = "Cart is empty")
            return
        }

        if (currentState.user == null) {
            _state.value = currentState.copy(error = "User data not loaded")
            return
        }

        val usingSavedCard = currentState.manualCardNumber.isEmpty() && currentState.selectedCard != null

        if (!usingSavedCard) {
            if (currentState.manualCardNumber.length != 16) {
                _state.value = currentState.copy(error = "Card number must be 16 digits")
                return
            }

            val month = currentState.manualExpiryMonth.toIntOrNull()
            if (month == null || month !in 1..12) {
                _state.value = currentState.copy(error = "Invalid expiry month (1-12)")
                return
            }

            val year = currentState.manualExpiryYear.toIntOrNull()
            if (year == null || year < 0 || year > 99) {
                _state.value = currentState.copy(error = "Invalid expiry year")
                return
            }

            if (currentState.manualFullName.isBlank()) {
                _state.value = currentState.copy(error = "Full name is required")
                return
            }
        }

        attemptPayment()
    }

    private fun attemptPayment() {
        val currentState = _state.value

        viewModelScope.launch {
            _state.value = currentState.copy(isProcessing = true, error = null)

            try {
                val products = currentState.cartItems.map { item ->
                    SaleProduct(
                        productId = item.id.toLong(),
                        quantity = item.quantity,
                        unitPrice = (item.basePrice * 100).toLong(),
                        name = item.name,
                        sku = null
                    )
                }

                val totalAmount = currentState.cartItems.sumOf {
                    (it.basePrice * it.quantity * 100).toLong()
                }

                val usingSavedCard = currentState.manualCardNumber.isEmpty() && currentState.selectedCard != null

                val request = if (usingSavedCard) {
                    SaleRequest(
                        userId = currentState.user!!.id.toString(),
                        cardId = currentState.selectedCard!!.id,
                        cardNumber = null,
                        expiryMonth = null,
                        expiryYear = null,
                        currencyCode = "978",
                        products = products,
                        totalAmount = totalAmount
                    )
                } else {
                    val year = currentState.manualExpiryYear.toInt()
                    SaleRequest(
                        userId = currentState.user!!.id.toString(),
                        cardId = null,
                        cardNumber = currentState.manualCardNumber,
                        expiryMonth = currentState.manualExpiryMonth.toInt(),
                        expiryYear = 2000 + year,
                        currencyCode = "978",
                        products = products,
                        totalAmount = totalAmount
                    )
                }

                val result = saleRepository.makeSale(request)

                result.onSuccess { response ->
                    if (!response.success) {
                        val errorMsg = response.error?.message ?: "Unknown error"
                        handlePaymentFailure(errorMsg)
                    } else if (response.data?.status == "APPROVED") {
                        try {
                            cartRepository.clearCart(_state.value.cartItems)
                            _state.value = _state.value.copy(cartItems = emptyList())
                        } catch (e: Exception) {
                            _state.value = _state.value.copy(error = "Payment successful but failed to clear cart")
                        }

                        _checkoutResult.value = CheckoutResult.Success(response)
                        _state.value = _state.value.copy(isProcessing = false, retryCount = 0)
                    } else {
                        val status = response.data?.status?.lowercase() ?: "declined"
                        val responseCode = response.data?.responseCode ?: ""
                        handlePaymentFailure("Payment $status (Code: $responseCode)")
                    }
                }.onFailure { error ->
                    handlePaymentFailure(error.message ?: "Payment failed")
                }
            } catch (e: Exception) {
                handlePaymentFailure(e.message ?: "Payment failed")
            }
        }
    }

    private fun handlePaymentFailure(errorMessage: String) {
        val currentState = _state.value
        val newRetryCount = currentState.retryCount + 1

        if (newRetryCount < maxRetries) {
            _state.value = currentState.copy(
                retryCount = newRetryCount,
                isProcessing = false,
                error = "$errorMessage (Attempt $newRetryCount of $maxRetries)"
            )
            attemptPayment()
        } else {
            _checkoutResult.value = CheckoutResult.Error(
                "Payment failed after $maxRetries attempts. Please try again later.",
                maxRetriesReached = true
            )
            _state.value = currentState.copy(
                isProcessing = false,
                retryCount = 0,
                error = null
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearResult() {
        _checkoutResult.value = null
    }

    fun resetRetryCount() {
        _state.value = _state.value.copy(retryCount = 0)
    }
}