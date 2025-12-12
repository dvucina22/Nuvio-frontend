package com.example.core.sale

import com.example.core.sale.dto.SaleRequest
import com.example.core.sale.dto.SaleResponse

interface ISaleRepository {
    suspend fun makeSale(request: SaleRequest): Result<SaleResponse>
}