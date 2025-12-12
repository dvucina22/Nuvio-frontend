package com.example.nuviofrontend.feature.sale.data

import com.example.core.network.api.ApiService
import com.example.core.sale.ISaleService
import com.example.core.sale.dto.SaleRequest
import com.example.core.sale.dto.SaleResponse
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SaleService @Inject constructor(
    private val apiService: ApiService
) : ISaleService {

    override suspend fun makeSale(request: SaleRequest): SaleResponse {
        try {
            val response = apiService.makeSale(request)

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            return response.body() ?: throw IOException("Empty response body")
        } catch (e: HttpException) {
            throw IOException("API error: ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}")
        }
    }
}