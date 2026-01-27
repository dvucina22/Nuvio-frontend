package com.example.core.transactions.dto

data class TransactionFilterRequest(
    val search: String? = null,

    val dateFrom: String? = null,
    val dateTo: String? = null,

    val statuses: List<String> = emptyList(),
    val types: List<String> = emptyList(),

    val amountMin: Long? = null,
    val amountMax: Long? = null,

    val productCountMin: Int? = null,
    val productCountMax: Int? = null,

    val page: Int = 1,
    val pageSize: Int = 20,

    val userId: String? = null
)