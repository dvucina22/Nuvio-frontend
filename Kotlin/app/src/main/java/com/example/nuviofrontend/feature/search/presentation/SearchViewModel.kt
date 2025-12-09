package com.example.nuviofrontend.feature.search.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductFilterRequest
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val results: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val brands: List<Brand> = emptyList(),
    val categories: List<Category> = emptyList(),
    val attributes: List<AttributeFilter> = emptyList(),
    val isFilterDataLoading: Boolean = false,
    val favoriteProductIds: Set<Long> = emptySet()
)

data class FilterState(
    val sortBy: String = "newest",
    val selectedCategories: Set<Long> = emptySet(),
    val selectedBrands: Set<Long> = emptySet(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..5000f,
    val inStockOnly: Boolean = false,
    val favoritesOnly: Boolean = false,
    val selectedAttributes: Map<String, Set<String>> = emptyMap()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private var currentFilterState: FilterState? = null
    private val pageSize = 12
    private var currentOffset = 0

    init {
        viewModelScope.launch {
            loadFavoriteProductIds()
        }

        viewModelScope.launch {
            queryFlow
                .debounce(1000)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val trimmed = query.trim()
                    if (trimmed.isEmpty()) {
                        currentOffset = 0
                        _state.update {
                            it.copy(
                                query = query,
                                results = emptyList(),
                                isLoading = false,
                                isLoadingMore = false,
                                endReached = false,
                                error = null
                            )
                        }
                        return@collectLatest
                    }
                    currentOffset = 0
                    performSearch(trimmed, currentFilterState)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    fun applyFilters(filterState: FilterState) {
        currentFilterState = filterState
        currentOffset = 0
        val query = _state.value.query.trim()
        performSearch(query, filterState)
    }

    private fun performSearch(query: String, filterState: FilterState?) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    endReached = false,
                    error = null,
                    results = emptyList()
                )
            }

            val request = buildFilterRequest(query, filterState, pageSize, currentOffset)
            val result = catalogRepository.filterProducts(request)

            result.onSuccess { products ->
                currentOffset += products.size
                _state.update {
                    it.copy(
                        isLoading = false,
                        results = products,
                        endReached = products.size < pageSize,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        results = emptyList(),
                        endReached = true,
                        error = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    fun loadMore() {
        val current = _state.value
        val trimmed = current.query.trim()
        if (current.isLoading || current.isLoadingMore || current.endReached) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }

            val request = buildFilterRequest(trimmed, currentFilterState, pageSize, currentOffset)
            val result = catalogRepository.filterProducts(request)

            result.onSuccess { products ->
                currentOffset += products.size
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        results = it.results + products,
                        endReached = products.size < pageSize,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        isLoadingMore = false,
                        error = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    private fun buildFilterRequest(
        query: String,
        filterState: FilterState?,
        limit: Int,
        offset: Int
    ): ProductFilterRequest {
        val attributes: List<AttributeFilter>? = filterState
            ?.selectedAttributes
            ?.mapNotNull { (name, selectedValues) ->
                val attr = this.state.value.attributes.firstOrNull { it.name == name } ?: return@mapNotNull null
                val selectedItems = attr.items.filter { it.value in selectedValues }
                if (selectedItems.isEmpty()) null
                else AttributeFilter(name = name, items = selectedItems)
            }
            ?.takeIf { it.isNotEmpty() }

        return ProductFilterRequest(
            search = query.takeIf { it.isNotBlank() },
            brandIds = filterState?.selectedBrands?.toList(),
            categoryIds = filterState?.selectedCategories?.toList(),
            attributes = attributes,
            priceMin = filterState?.priceRange?.start?.toDouble(),
            priceMax = filterState?.priceRange?.endInclusive?.toDouble(),
            sort = filterState?.sortBy ?: "newest",
            limit = limit,
            offset = offset,
            isInStock = if (filterState?.inStockOnly == true) true else null,
            isFavorite = if (filterState?.favoritesOnly == true) true else null,
            isActive = true
        )
    }

    private suspend fun loadFavoriteProductIds() {
        val request = ProductFilterRequest(
            search = null,
            brandIds = null,
            categoryIds = null,
            attributes = null,
            priceMin = null,
            priceMax = null,
            sort = "newest",
            limit = 200,
            offset = 0,
            isInStock = null,
            isFavorite = true,
            isActive = true
        )

        val result = catalogRepository.filterProducts(request)

        result.onSuccess { products ->
            _state.update {
                it.copy(
                    favoriteProductIds = products
                        .map { p -> p.id }
                        .toSet()
                )
            }
        }.onFailure { e ->
            _state.update {
                it.copy(error = e.message ?: "Failed to load favorites")
            }
        }
    }

    fun setFavorite(productId: Long, shouldBeFavorite: Boolean) {
        viewModelScope.launch {
            val previous = _state.value.favoriteProductIds
            val updated =
                if (shouldBeFavorite) previous + productId else previous - productId

            _state.update { it.copy(favoriteProductIds = updated) }

            val result = if (shouldBeFavorite) {
                catalogRepository.addFavoriteProduct(productId)
            } else {
                catalogRepository.removeFavoriteProduct(productId)
            }

            result.onFailure { e ->
                _state.update {
                    it.copy(
                        favoriteProductIds = previous,
                        error = e.message ?: "Failed to update favorites"
                    )
                }
            }
        }
    }

    fun ensureFilterDataLoaded() {
        val current = _state.value
        if (
            (current.brands.isNotEmpty() || current.categories.isNotEmpty() || current.attributes.isNotEmpty()) ||
            current.isFilterDataLoading
        ) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isFilterDataLoading = true) }

            val brandsResult = catalogRepository.getAllBrands()
            val categoriesResult = catalogRepository.getAllCategories()
            val attributesResult = catalogRepository.getAttributes()

            val brands = brandsResult.getOrDefault(emptyList())
            val categories = categoriesResult.getOrDefault(emptyList())
            val attributes = attributesResult.getOrDefault(emptyList())
            attributes.forEach { attr ->
                attr.items.forEach { item ->
                    Log.d("ATTR_DEBUG123", "Attribute loaded SEARCH: name=${attr.name} value=${item.value} id=${item.id}")
                }
            }

            val errorMessage = when {
                brandsResult.isFailure && categoriesResult.isFailure && attributesResult.isFailure ->
                    brandsResult.exceptionOrNull()?.message
                        ?: categoriesResult.exceptionOrNull()?.message
                        ?: attributesResult.exceptionOrNull()?.message

                brandsResult.isFailure ->
                    brandsResult.exceptionOrNull()?.message

                categoriesResult.isFailure ->
                    categoriesResult.exceptionOrNull()?.message

                attributesResult.isFailure ->
                    attributesResult.exceptionOrNull()?.message

                else -> null
            }

            _state.update {
                it.copy(
                    brands = brands,
                    categories = categories,
                    attributes = attributes,
                    isFilterDataLoading = false,
                    error = errorMessage ?: it.error
                )
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
