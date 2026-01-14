package com.example.core.transactions.dto

data class TransactionProductDetail(
    val id: Long,
    val productId: Long,
    val unitPrice: Long,
    val quantity: Int,
    val lineTotal: Long,
    val productName: String? = null,
    val productSku: String? = null
)