package com.example.nuviofrontend.feature.catalog.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.catalog.dto.ProductDetail
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import com.example.nuviofrontend.feature.catalog.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val catalogRepository: CatalogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    val productId: Long = savedStateHandle.get<String>("id")?.toLong() ?: 0L

    private val _product = MutableStateFlow<ProductDetail?>(null)
    val product = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.fetchProduct(productId)

            result.onSuccess { p ->
                _product.value = p
            }.onFailure { e ->
                _error.value = e.message
            }

            _isLoading.value = false
        }
    }
    fun toggleFavorite() {
        val currentProduct = _product.value ?: return
        val newFavoriteState = !currentProduct.isFavorite

        _product.value = currentProduct.copy(isFavorite = newFavoriteState)

        viewModelScope.launch {
            try {
                if (newFavoriteState) {
                    catalogRepository.addFavoriteProduct(currentProduct.id)
                } else {
                    catalogRepository.removeFavoriteProduct(currentProduct.id)
                }
            } catch (e: Exception) {
                _product.value = currentProduct
                _error.value = e.message
            }
        }
    }
}