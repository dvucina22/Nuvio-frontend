package com.example.core.sale.dto

data class SaleResponse(
    val success: Boolean,
    val data: TransactionData? = null,
    val error: ErrorData? = null
)

data class TransactionData(
    val id: Long,
    val status: String,
    val responseCode: String,
    val authCode: String? = null,
    val createdAt: String
)

data class ErrorData(
    val code: String,
    val message: String
)