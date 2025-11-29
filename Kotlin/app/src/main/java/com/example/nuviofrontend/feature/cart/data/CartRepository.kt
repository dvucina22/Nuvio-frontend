package com.example.nuviofrontend.feature.cart.data

import com.example.core.cart.dto.CartItemDto

class CartRepository(private val cartService: CartService) {
    suspend fun getCartItems(): List<CartItemDto> {
        return cartService.getCartItems()
    }

    suspend fun addCartItem(productId: Int) = cartService.addCartItem(productId)
    suspend fun decreaseCartItem(productId: Int) = cartService.decreaseCartItem(productId)
}
