package com.example.nuviofrontend.feature.statistics.data

import com.example.core.statistics.IStatisticsRepository
import com.example.core.statistics.dto.TransactionStatisticsResponse
import javax.inject.Inject

class StatisticsRepository @Inject constructor(
    private val service: StatisticsService
) : IStatisticsRepository {
    override suspend fun fetchStatistics(): TransactionStatisticsResponse {
        return service.getStatistics()
    }
}