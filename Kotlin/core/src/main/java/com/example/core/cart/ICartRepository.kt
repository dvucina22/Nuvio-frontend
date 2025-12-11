package com.example.core.cart

import com.example.core.cart.dto.CartItemDto

interface ICartRepository {
    suspend fun getCartItems(): List<CartItemDto>
    suspend fun addCartItem(productId: Int)
    suspend fun decreaseCartItem(productId: Int)
}