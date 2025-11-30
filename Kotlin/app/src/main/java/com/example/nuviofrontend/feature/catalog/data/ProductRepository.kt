package com.example.nuviofrontend.feature.catalog.data

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
}