package com.example.nuviofrontend.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.catalog.dto.AttributeFilter
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
    val error: String? = null
)

// TODO: Replace with actual IDs from DB
private val ATTRIBUTE_NAME_TO_ID = mapOf(
    "RAM" to 1L,
    "CPU" to 2L,
    "Storage" to 3L
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
            queryFlow
                .debounce(1000)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val trimmed = query.trim()
                    if (trimmed.isEmpty()) {
                        currentOffset = 0
                        _state.value = SearchState(query = query)
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
        if (query.isNotEmpty()) {
            performSearch(query, filterState)
        }
    }

    private fun performSearch(query: String, filterState: FilterState?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isLoadingMore = false, endReached = false, error = null, results = emptyList()) }

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
        if (trimmed.isEmpty() || current.isLoading || current.isLoadingMore || current.endReached) return

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
        val attributes = filterState?.selectedAttributes?.mapNotNull { (attrName, values) ->
            ATTRIBUTE_NAME_TO_ID[attrName]?.let { attributeId ->
                AttributeFilter(
                    attributeId = attributeId,
                    values = values.toList()
                )
            }
        } ?: emptyList()

        return ProductFilterRequest(
            search = query,
            brandIds = filterState?.selectedBrands?.toList(),
            categoryIds = filterState?.selectedCategories?.toList(),
            attributes = attributes.ifEmpty { null },
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

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}