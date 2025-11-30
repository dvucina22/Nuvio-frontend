package com.example.nuviofrontend.feature.catalog.data

import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductDetail
import com.example.core.network.api.ApiService
import javax.inject.Inject

class ProductService @Inject constructor(
    private val apiService: ApiService
){
    suspend fun getProductById(id: Long) : ProductDetail?{
        val response = apiService.getProductById(id)
        return if (response.isSuccessful) response.body() else null
    }
}