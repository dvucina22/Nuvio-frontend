package com.example.core.transactions.dto

data class TransactionDetail(
    val id: Long,
    val status: String,
    val amount: Long,
    val currencyCode: String,
    val panMasked: String,
    val cardExpirationYy: String,
    val cardExpirationMm: String,
    val transactionDate: String,
    val transactionTime: String,
    val createdAt: String,
    val products: List<TransactionProductDetail> = emptyList()
)