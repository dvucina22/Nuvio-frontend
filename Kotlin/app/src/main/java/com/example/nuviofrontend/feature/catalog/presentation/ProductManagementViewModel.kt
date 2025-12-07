package com.example.nuviofrontend.feature.catalog.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.catalog.dto.AddProductRequest
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.ProductAttributeDto
import com.example.core.catalog.dto.ProductDetail
import com.example.core.catalog.dto.UpdateProductRequest
import com.example.nuviofrontend.feature.catalog.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductManagementViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var brands by mutableStateOf<List<Brand>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var attributes by mutableStateOf<List<AttributeFilter>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var fieldErrors by mutableStateOf<Map<String, String>>(emptyMap())
    var productAdded by mutableStateOf(false)
    var productUpdated by mutableStateOf(false)


    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            brands = repository.loadBrands().body() ?: emptyList()
            categories = repository.loadCategories().body() ?: emptyList()
            attributes = repository.loadAttributes().body() ?: emptyList()
        }
    }

    fun addProduct(
        name: String,
        description: String,
        modelNumber: String,
        sku: String,
        basePrice: Double,
        brandId: Long,
        categoryId: Long,
        quantity: Int,
        selectedAttributes: Map<String, String>
    ) {
        viewModelScope.launch {
            isLoading = true

            val attributeDtos = selectedAttributes.map { (attributeName, value) ->

                val attribute = attributes.first { it.name == attributeName }

                ProductAttributeDto(
                    attributeId = attribute.attributeId!!.toLong(),
                    value = value
                )
            }

            val req = AddProductRequest(
                name = name,
                description = description,
                modelNumber = modelNumber,
                sku = sku,
                basePrice = basePrice,
                brandId = brandId.toInt(),
                categoryId = categoryId.toInt(),
                quantity = quantity,
                attributes = attributeDtos
            )

            val result = repository.createProduct(req)

            isLoading = false

            result.onSuccess {
                successMessage = it
                productAdded = true
            }.onFailure {
                errorMessage = it.message
            }
        }
    }

    fun validateFields(
        productName: String,
        price: String,
        brandId: Long?,
        categoryId: Long?,
        quantity: String,
        modelNumber: String,
        sku: String,
        description: String
    ): Boolean {
        val errors = mutableMapOf<String, String>()

        if (productName.isBlank()) errors["productName"] = "Ovo polje je obavezno"
        if (modelNumber.isBlank()) errors["modelNumber"] = "Ovo polje je obavezno"
        if (sku.isBlank()) errors["sku"] = "Ovo polje je obavezno"
        if (description.isBlank()) errors["description"] = "Ovo polje je obavezno"
        val quantityValue = quantity.toIntOrNull()
        when {
            quantity.isBlank() -> errors["quantity"] = "Ovo polje je obavezno"
            quantityValue == null || quantityValue < 0 -> errors["quantity"] = "Količina ne smije biti negativna"
        }
        val priceValue = price.toDoubleOrNull()
        when {
            price.isBlank() -> errors["price"] = "Ovo polje je obavezno"
            priceValue == null || priceValue < 0 -> errors["price"] = "Cijena ne smije biti negativna"
        }
        if (brandId == null) errors["brand"] = "Ovo polje je obavezno"
        if (categoryId == null) errors["category"] = "Ovo polje je obavezno"

        fieldErrors = errors
        return errors.isEmpty()
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            try {
                val result = repository.removeProduct(productId)
                result.onSuccess { message ->
                    successMessage = "Uspješno obrisan proizvod"
                }.onFailure { e ->
                    errorMessage = e.message ?: "Failed to delete product"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to delete product"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProduct(
        id: Long,
        name: String? = null,
        description: String? = null,
        modelNumber: String? = null,
        sku: String? = null,
        basePrice: Double? = null,
        brandId: Long? = null,
        categoryId: Long? = null,
        quantity: Int? = null,
        selectedAttributes: Map<String, String>? = null
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            val existingProduct = repository.fetchProduct(id).getOrNull()
            if (existingProduct == null) {
                isLoading = false
                errorMessage = "Product not found"
                return@launch
            }

            val attributeDtos: List<ProductAttributeDto> = selectedAttributes?.map { (name, value) ->
                val attr = attributes.firstOrNull { it.name == name }
                ProductAttributeDto(
                    attributeId = attr?.attributeId?.toLong() ?: 0L,
                    value = value
                )
            } ?: existingProduct.attributes?.map { it as ProductAttributeDto } ?: emptyList()

            val req = UpdateProductRequest(
                name = name ?: existingProduct.name,
                description = description ?: existingProduct.description.orEmpty(),
                modelNumber = modelNumber ?: existingProduct.modelNumber.orEmpty(),
                sku = sku ?: existingProduct.sku.orEmpty(),
                basePrice = basePrice ?: existingProduct.basePrice,
                brandId = brandId?.toInt() ?: existingProduct.brand.id!!.toInt(),
                categoryId = categoryId?.toInt() ?: existingProduct.category.id!!.toInt(),
                quantity = quantity ?: existingProduct.quantity?.toInt() ?: 0,
                attributes = attributeDtos
            )

            val result = repository.updateProduct(id, req)
            isLoading = false

            result.onSuccess {
                successMessage = it
                productUpdated = true
            }.onFailure {
                errorMessage = it.message
            }
        }
    }

    suspend fun loadProduct(productId: Long): Result<ProductDetail> {
        return repository.fetchProduct(productId)
    }
}
