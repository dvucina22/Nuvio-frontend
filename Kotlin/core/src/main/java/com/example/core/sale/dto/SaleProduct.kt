package com.example.core.sale.dto

data class SaleProduct(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Long,
    val name: String?,
    val sku: String?
)
