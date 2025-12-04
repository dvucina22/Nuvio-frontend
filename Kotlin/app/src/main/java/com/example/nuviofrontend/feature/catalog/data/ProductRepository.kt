package com.example.nuviofrontend.feature.catalog.data

import com.example.core.catalog.dto.AddProductRequest
import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductDetail
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productService: ProductService
) {
    suspend fun fetchProduct(id: Long): Result<ProductDetail> {
        return try {
            val product = productService.getProductById(id)
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(request: AddProductRequest): Result<String> {
        return try {
            val response = productService.addProduct(request)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Product created")
            } else {
                Result.failure(Exception("Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadBrands() = productService.getBrands()
    suspend fun loadCategories() = productService.getCategories()
    suspend fun loadAttributes() = productService.getAttributes()
}