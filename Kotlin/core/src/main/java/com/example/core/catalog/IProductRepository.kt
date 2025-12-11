package com.example.core.catalog

import com.example.core.catalog.dto.AddProductRequest
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.ProductDetail
import com.example.core.catalog.dto.UpdateProductRequest

interface IProductRepository {
    suspend fun fetchProduct(id: Long): Result<ProductDetail>
    suspend fun createProduct(request: AddProductRequest): Result<String>
    suspend fun loadBrands(): List<Brand>
    suspend fun loadCategories(): List<Category>
    suspend fun loadAttributes(): List<AttributeFilter>
    suspend fun removeProduct(productId: Long): Result<String>
    suspend fun updateProduct(id: Long, request: UpdateProductRequest): Result<String>
}