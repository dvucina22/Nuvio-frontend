package com.example.nuviofrontend.feature.sale.data

import com.example.core.sale.ISaleRepository
import com.example.core.sale.ISaleService
import com.example.core.sale.dto.SaleRequest
import com.example.core.sale.dto.SaleResponse
import javax.inject.Inject

class SaleRepository @Inject constructor(
    private val saleService: ISaleService
) : ISaleRepository {

    override suspend fun makeSale(request: SaleRequest): Result<SaleResponse> {
        return try {
            val response = saleService.makeSale(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}