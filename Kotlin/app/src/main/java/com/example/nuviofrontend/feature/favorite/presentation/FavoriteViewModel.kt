package com.example.nuviofrontend.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductFilterRequest
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val errorMessageResId: Int? = null,
    val favoriteProductIds: Set<Long> = emptySet()
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state: StateFlow<FavoriteState> = _state.asStateFlow()

    private val pageSize = 12
    private var currentOffset = 0

    init {
        loadFavorites(reset = true)
    }

    private fun loadFavorites(reset: Boolean) {
        val current = _state.value

        if (!reset && (current.isLoading || current.isLoadingMore || current.endReached)) {
            return
        }

        if (reset) {
            currentOffset = 0
            _state.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    endReached = false,
                    errorMessageResId = null,
                    products = emptyList()
                )
            }
        } else {
            _state.update { it.copy(isLoadingMore = true, errorMessageResId = null) }
        }

        viewModelScope.launch {
            val request = ProductFilterRequest(
                search = null,
                brandIds = null,
                categoryIds = null,
                attributes = null,
                priceMin = null,
                priceMax = null,
                sort = "newest",
                limit = pageSize,
                offset = currentOffset,
                isInStock = null,
                isFavorite = true,
                isActive = true
            )

            val result = catalogRepository.filterProducts(request)

            result.onSuccess { products ->
                currentOffset += products.size

                _state.update { state ->
                    val newList =
                        if (reset) products
                        else state.products + products

                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        products = newList,
                        endReached = products.size < pageSize,
                        favoriteProductIds = newList.map { it.id }.toSet(),
                        errorMessageResId = null
                    )
                }
            }.onFailure {
                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessageResId = R.string.favorites_error_load
                    )
                }
            }
        }
    }

    fun loadMore() {
        loadFavorites(reset = false)
    }

    fun toggleFavorite(productId: Long, shouldBeFavorite: Boolean) {
        viewModelScope.launch {
            val previousIds = _state.value.favoriteProductIds
            val previousProducts = _state.value.products

            val updatedIds =
                if (shouldBeFavorite) previousIds + productId
                else previousIds - productId

            val updatedProducts =
                if (shouldBeFavorite) previousProducts
                else previousProducts.filterNot { it.id == productId }

            _state.update {
                it.copy(
                    favoriteProductIds = updatedIds,
                    products = updatedProducts,
                    errorMessageResId = null
                )
            }

            val result = if (shouldBeFavorite) {
                catalogRepository.addFavoriteProduct(productId)
            } else {
                catalogRepository.removeFavoriteProduct(productId)
            }

            result.onFailure {
                _state.update {
                    it.copy(
                        favoriteProductIds = previousIds,
                        products = previousProducts,
                        errorMessageResId = R.string.favorites_error_update
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessageResId = null) }
    }
}
