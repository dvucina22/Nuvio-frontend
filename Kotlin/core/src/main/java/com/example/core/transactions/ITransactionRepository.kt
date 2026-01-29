package com.example.core.transactions

import com.example.core.transactions.dto.TransactionDetail
import com.example.core.transactions.dto.TransactionFilterRequest
import com.example.core.transactions.dto.TransactionListResponse

interface ITransactionRepository {
    suspend fun getTransactionHistory(request: TransactionFilterRequest): Result<TransactionListResponse>
    suspend fun getTransactionDetail(transactionId: Long): Result<TransactionDetail>
    suspend fun voidTransaction(transactionId: Long): Result<Unit>
}