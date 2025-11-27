package com.example.nuviofrontend.feature.catalog.data

import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductFilterRequest
import com.example.core.network.api.ApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CatalogService @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun filterProducts(request: ProductFilterRequest): List<Product> {
        try {
            val response = apiService.filterProducts(request)

            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            return response.body() ?: emptyList()
        } catch (e: HttpException) {
            throw IOException("API error: ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}")
        }
    }

    suspend fun getLatestProducts(limit: Int = 5): List<Product> {
        val request = ProductFilterRequest(
            sort = "newest",
            limit = limit,
            isActive = true,
            isInStock = true
        )
        return filterProducts(request)
    }

    suspend fun getFlashDeals(limit: Int = 10): List<Product> {
        val request = ProductFilterRequest(
            sort = "newest",
            limit = limit,
            isActive = true,
            isInStock = true
        )
        return filterProducts(request)
    }

    suspend fun getCategoryProducts(categoryId: Long, limit: Int = 20, sort: String = "newest"): List<Product> {
        val request = ProductFilterRequest(
            categoryIds = listOf(categoryId),
            sort = sort,
            limit = limit,
            isActive = true,
            isInStock = true
        )
        return filterProducts(request)
    }

    suspend fun searchProducts(query: String, limit: Int = 20, offset: Int = 0, sort: String = "newest"): List<Product> {
        val request = ProductFilterRequest(
            search = query,
            sort = sort,
            limit = limit,
            offset = offset,
            isActive = true
        )
        return filterProducts(request)
    }

    suspend fun getBrandProducts(brandId: Long, limit: Int = 20, sort: String = "newest"): List<Product> {
        val request = ProductFilterRequest(
            brandIds = listOf(brandId),
            sort = sort,
            limit = limit,
            isActive = true,
            isInStock = true
        )
        return filterProducts(request)
    }

    suspend fun getProductsByPriceRange(
        minPrice: Double,
        maxPrice: Double,
        limit: Int = 20,
        sort: String = "price_asc"
    ): List<Product> {
        val request = ProductFilterRequest(
            priceMin = minPrice,
            priceMax = maxPrice,
            sort = sort,
            limit = limit,
            isActive = true,
            isInStock = true
        )
        return filterProducts(request)
    }

    suspend fun getFavoriteProducts(limit: Int = 20): List<Product> {
        val request = ProductFilterRequest(
            isFavorite = true,
            sort = "newest",
            limit = limit
        )
        return filterProducts(request)
    }
}