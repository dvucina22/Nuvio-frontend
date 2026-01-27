package com.example.core.transactions.dto

data class TransactionListResponse(
    val transactions: List<TransactionListItem> = emptyList(),
    val total: Long,
    val page: Int,
    val pageSize: Int
)