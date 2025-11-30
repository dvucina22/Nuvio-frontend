package com.example.nuviofrontend.feature.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.cart.dto.CartItemDto
import com.example.nuviofrontend.feature.cart.data.CartRepository
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.map

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItemDto>>(emptyList())
    val cartItems: StateFlow<List<CartItemDto>> = _cartItems

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _itemToDelete = MutableStateFlow<CartItemDto?>(null)
    val itemToDelete: StateFlow<CartItemDto?> = _itemToDelete

    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = cartRepository.getCartItems()
                _cartItems.value = items
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun increaseQuantity(id: Int) {
        viewModelScope.launch {
            try {
                cartRepository.addCartItem(id)
                _cartItems.value = _cartItems.value.map {
                    if (it.id == id) it.copy(quantity = it.quantity + 1) else it
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun decreaseQuantity(id: Int) {
        val item = _cartItems.value.find { it.id == id } ?: return

        if (item.quantity > 1) {
            viewModelScope.launch {
                try {
                    cartRepository.decreaseCartItem(id)
                    _cartItems.value = _cartItems.value.map {
                        if (it.id == id) it.copy(quantity = it.quantity - 1) else it
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        } else {
            showDeleteConfirmation(id)
        }
    }

    fun deleteItemFromCart(id: Int) {
        viewModelScope.launch {
            try {
                cartRepository.decreaseCartItem(id)
                _cartItems.value = _cartItems.value.filter { it.id != id }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun showDeleteConfirmation(id: Int) {
        val item = _cartItems.value.find { it.id == id }
        _itemToDelete.value = item
    }

    fun dismissDeletePopup() {
        _itemToDelete.value = null
    }

    fun toggleFavorite(id: Int) {
        val item = _cartItems.value.find { it.id == id } ?: return
        val newFavoriteState = !item.isFavorite

        viewModelScope.launch {
            try {
                if (newFavoriteState) {
                    catalogRepository.addFavoriteProduct(id.toLong())
                } else {
                    catalogRepository.removeFavoriteProduct(id.toLong())
                }

                _cartItems.value = _cartItems.value.map {
                    if (it.id == id) it.copy(isFavorite = newFavoriteState) else it
                }

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
