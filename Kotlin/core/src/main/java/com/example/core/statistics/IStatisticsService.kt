package com.example.core.statistics

import com.example.core.statistics.dto.TransactionStatisticsResponse

interface IStatisticsService {
    suspend fun getStatistics(): TransactionStatisticsResponse
}