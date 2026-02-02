package com.example.nuviofrontend.feature.statistics.data

import com.example.core.network.api.ApiService
import com.example.core.statistics.IStatisticsService
import com.example.core.statistics.dto.TransactionStatisticsResponse
import javax.inject.Inject


class StatisticsService @Inject constructor(
    private val apiService: ApiService
) : IStatisticsService {
    override suspend fun getStatistics(): TransactionStatisticsResponse {
        val response = apiService.getTransactionStatistics()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception(response.errorBody()?.string() ?: "Failed to fetch statistics")
        }
    }
}