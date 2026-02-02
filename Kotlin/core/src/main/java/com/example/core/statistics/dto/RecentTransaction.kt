package com.example.core.statistics.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecentTransaction(
    val id: Int,
    val userId: String,
    val type: String,
    val status: String,
    val amount: Int,
    val currencyCode: String,
    val createdAt: String
)