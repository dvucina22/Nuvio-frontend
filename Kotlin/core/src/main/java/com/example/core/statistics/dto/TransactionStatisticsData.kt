package com.example.core.statistics.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionStatisticsData(
    val totalRevenue: Double,
    val totalTransactions: Int,
    val statusBreakdown: List<TransactionStatusBreakdown>,
    val averageTransactionValue: Double,
    val recentTransactions: List<RecentTransaction>
)