package com.example.core.sale

import com.example.core.sale.dto.SaleRequest
import com.example.core.sale.dto.SaleResponse

interface ISaleService {
    suspend fun makeSale(request: SaleRequest): SaleResponse
}