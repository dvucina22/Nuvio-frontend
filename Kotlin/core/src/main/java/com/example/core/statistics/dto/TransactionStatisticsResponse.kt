package com.example.core.statistics.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionStatisticsResponse(
    val success: Boolean,
    val data: TransactionStatisticsData
)