package com.example.core.sale.dto

import java.util.UUID

data class SaleRequest(
    val userId: String,
    val cardId: Int? = null,
    val cardNumber: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val currencyCode: String,
    val products: List<SaleProduct>,
    val totalAmount: Long
)

