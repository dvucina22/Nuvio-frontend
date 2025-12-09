package com.example.nuviofrontend.feature.catalog.data

import com.example.core.catalog.dto.AddProductRequest
import com.example.core.catalog.dto.AddProductResponse
import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductDetail
import com.example.core.catalog.dto.SuccessfulDeleteResponse
import com.example.core.catalog.dto.UpdateProductRequest
import com.example.core.catalog.dto.UpdateProductResponse
import com.example.core.network.api.ApiService
import retrofit2.Response
import javax.inject.Inject

class ProductService @Inject constructor(
    private val apiService: ApiService
){
    suspend fun getProductById(id: Long) : ProductDetail?{
        val response = apiService.getProductById(id)
        return if (response.isSuccessful) response.body() else null
    }
    suspend fun addProduct(request: AddProductRequest): Response<AddProductResponse> {
        return apiService.addNewProduct(request)
    }
    suspend fun getBrands() = apiService.getAllBrands()

    suspend fun getCategories() = apiService.getAllCategories()

    suspend fun getAttributes() = apiService.getAttributes()

    suspend fun deleteProduct(productId: Long): SuccessfulDeleteResponse? {
        val response = apiService.deleteProduct(productId)
        return if (response.isSuccessful) response.body() else null
    }
    suspend fun updateProduct(id: Long, request: UpdateProductRequest): Response<UpdateProductResponse>{
        return apiService.updateProduct(id, request)
    }
}