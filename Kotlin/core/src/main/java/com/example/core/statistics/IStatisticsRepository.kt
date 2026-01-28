package com.example.core.statistics

import com.example.core.statistics.dto.TransactionStatisticsResponse

interface IStatisticsRepository {
    suspend fun fetchStatistics(): TransactionStatisticsResponse
}