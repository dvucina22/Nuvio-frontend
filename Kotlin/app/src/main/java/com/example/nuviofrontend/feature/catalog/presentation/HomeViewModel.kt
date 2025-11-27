package com.example.nuviofrontend.feature.catalog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.catalog.dto.Product
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val latestProducts: List<Product>,
        val flashDeals: List<Product>,
        val recommendedProducts: List<Product>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

data class HomeState(
    val isLoading: Boolean = false,
    val latestProducts: List<Product> = emptyList(),
    val flashDeals: List<Product> = emptyList(),
    val recommendedProducts: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val latestResult = catalogRepository.getLatestProducts(limit = 5)
                val flashDealsResult = catalogRepository.getFlashDeals(limit = 10)
                val recommendedResult = catalogRepository.getLatestProducts(limit = 8)

                val latestProducts = latestResult.getOrNull() ?: emptyList()
                val flashDeals = flashDealsResult.getOrNull() ?: emptyList()
                val recommendedProducts = recommendedResult.getOrNull() ?: emptyList()

                _state.value = _state.value.copy(
                    isLoading = false,
                    latestProducts = latestProducts,
                    flashDeals = flashDeals,
                    recommendedProducts = recommendedProducts,
                    error = null
                )

                _uiState.value = HomeUiState.Success(
                    latestProducts = latestProducts,
                    flashDeals = flashDeals,
                    recommendedProducts = recommendedProducts
                )
            } catch (e: Exception) {
                val errorMessage = "Failed to load products: ${e.message}"
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                _uiState.value = HomeUiState.Error(errorMessage)
            }
        }
    }

    fun loadCategoryProducts(categoryId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = catalogRepository.getCategoryProducts(categoryId, limit = 20)

            result.onSuccess { products ->
                _state.value = _state.value.copy(isLoading = false)
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load category: ${e.message}"
                )
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = catalogRepository.searchProducts(query, limit = 20)

            result.onSuccess { products ->
                _state.value = _state.value.copy(isLoading = false)
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }

    fun loadBrandProducts(brandId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = catalogRepository.getBrandProducts(brandId, limit = 20)

            result.onSuccess { products ->
                _state.value = _state.value.copy(isLoading = false)
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load brand: ${e.message}"
                )
            }
        }
    }

    fun refreshData() {
        loadHomeData()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}