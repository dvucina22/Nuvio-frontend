package com.example.nuviofrontend.feature.transactions.data

import com.example.core.transactions.ITransactionRepository
import com.example.core.transactions.dto.TransactionDetail
import com.example.core.transactions.dto.TransactionFilterRequest
import com.example.core.transactions.dto.TransactionListResponse
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionService: TransactionService
) : ITransactionRepository {

    override suspend fun getTransactionHistory(request: TransactionFilterRequest): Result<TransactionListResponse> {
        return try {
            val result = transactionService.getTransactionHistory(request)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactionDetail(transactionId: Long): Result<TransactionDetail> {
        return try {
            val result = transactionService.getTransactionDetail(transactionId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun voidTransaction(transactionId: Long): Result<Unit> {
        return transactionService.voidTransaction(transactionId)
    }
}