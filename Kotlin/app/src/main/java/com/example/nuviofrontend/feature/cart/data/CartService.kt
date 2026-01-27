package com.example.nuviofrontend.feature.cart.data

import com.example.core.cart.dto.CartItemDto
import com.example.core.network.api.ApiService

class CartService(private val apiService: ApiService){
    suspend fun getCartItems(): List<CartItemDto> {
        val response = apiService.getCartItems()
        if(response.isSuccessful){
            return response.body() ?: emptyList()
        }
        else {
            throw kotlin.Exception(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    suspend fun addCartItem(productId: Int) {
        val response = apiService.addCartItem(productId)
        if (!response.isSuccessful) {
            val body = response.errorBody()?.string()
            throw kotlin.Exception(body ?: "Failed to add item")
        }
    }
    suspend fun decreaseCartItem(productId: Int) {
        val response = apiService.deleteCartItem(productId)
        if (!response.isSuccessful) {
            val body = response.errorBody()?.string()
            throw kotlin.Exception(body ?: "Failed to remove item")
        }
    }

    suspend fun clearCart() {
        val response = apiService.clearCart()
        if (!response.isSuccessful) throw Exception(response.errorBody()?.string() ?: "Failed to clear cart")
    }
}