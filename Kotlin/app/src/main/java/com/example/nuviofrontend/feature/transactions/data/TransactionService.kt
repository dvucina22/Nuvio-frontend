package com.example.nuviofrontend.feature.transactions.data

import com.example.core.transactions.dto.TransactionDetail
import com.example.core.transactions.dto.TransactionFilterRequest
import com.example.core.transactions.dto.TransactionListResponse
import com.example.core.network.api.ApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TransactionService @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getTransactionHistory(request: TransactionFilterRequest): TransactionListResponse {
        try {
            val response = apiService.getTransactionHistory(request)

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            val body = response.body() ?: throw IOException("Empty response body")
            return body.data
        } catch (e: HttpException) {
            throw IOException("API error: ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}")
        }
    }

    suspend fun getTransactionDetail(transactionId: Long): TransactionDetail {
        try {
            val result = apiService.getTransactionDetail(transactionId)
            return result.data
        } catch (e: HttpException) {
            throw IOException("API error: ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}")
        }
    }

    suspend fun voidTransaction(transactionId: Long): Result<Unit> {
        return try {
            val response = apiService.voidTransaction(transactionId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Void failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
