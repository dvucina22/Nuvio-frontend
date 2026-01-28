package com.example.nuviofrontend.feature.cart.data

import com.example.core.cart.ICartRepository
import com.example.core.cart.dto.CartItemDto
import javax.inject.Inject

class CartRepository @Inject constructor (
    private val cartService: CartService
) : ICartRepository {
    override suspend fun getCartItems(): List<CartItemDto> {
        return cartService.getCartItems()
    }

    override suspend fun addCartItem(productId: Int) = cartService.addCartItem(productId)
    override suspend fun decreaseCartItem(productId: Int) = cartService.decreaseCartItem(productId)

    override suspend fun clearCart(cartItems: List<CartItemDto>) {
        cartService.clearCart()
    }

}
