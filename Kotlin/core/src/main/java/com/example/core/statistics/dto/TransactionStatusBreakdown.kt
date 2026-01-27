package com.example.core.statistics.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionStatusBreakdown(
    val status: String,
    val count: Int,
    val percentage: Double
)
