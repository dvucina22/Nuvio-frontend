package com.example.core.transactions.dto

data class TransactionListItem(
    val id: Long,
    val status: String,
    val amount: Long,
    val currencyCode: String,
    val panMasked: String,
    val productCount: Int,
    val productIds: List<Long> = emptyList(),
    val createdAt: String
)