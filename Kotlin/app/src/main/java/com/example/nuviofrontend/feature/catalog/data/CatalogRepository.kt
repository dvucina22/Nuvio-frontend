package com.example.nuviofrontend.feature.catalog.data

import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductFilterRequest
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val catalogService: CatalogService
) {

    suspend fun getLatestProducts(limit: Int = 5): Result<List<Product>> {
        return try {
            val products = catalogService.getLatestProducts(limit)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFlashDeals(limit: Int = 10): Result<List<Product>> {
        return try {
            val products = catalogService.getFlashDeals(limit)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryProducts(categoryId: Long, limit: Int = 20, sort: String = "newest"): Result<List<Product>> {
        return try {
            val products = catalogService.getCategoryProducts(categoryId, limit, sort)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String, limit: Int = 20, offset: Int = 0, sort: String = "newest"): Result<List<Product>> {
        return try {
            val products = catalogService.searchProducts(query, limit, offset, sort)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBrandProducts(brandId: Long, limit: Int = 20, sort: String = "newest"): Result<List<Product>> {
        return try {
            val products = catalogService.getBrandProducts(brandId, limit, sort)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByPriceRange(
        minPrice: Double,
        maxPrice: Double,
        limit: Int = 20,
        sort: String = "price_asc"
    ): Result<List<Product>> {
        return try {
            val products = catalogService.getProductsByPriceRange(minPrice, maxPrice, limit, sort)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteProducts(limit: Int = 20): Result<List<Product>> {
        return try {
            val products = catalogService.getFavoriteProducts(limit)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterProducts(request: ProductFilterRequest): Result<List<Product>> {
        return try {
            val products = catalogService.filterProducts(request)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}