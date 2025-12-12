package com.example.core.sale.dto

data class SaleResponse(
    val id: Long,
    val status: String,
    val amount: Long,
    val currencyCode: String,
    val createdAt: String,
    val products: List<SaleResponseProduct>
)
