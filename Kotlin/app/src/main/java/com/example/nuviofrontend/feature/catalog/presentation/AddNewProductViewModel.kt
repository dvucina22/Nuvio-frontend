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
import com.example.nuviofrontend.feature.catalog.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    var brands by mutableStateOf<List<Brand>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var attributes by mutableStateOf<List<AttributeFilter>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var fieldErrors by mutableStateOf<Map<String, String>>(emptyMap())

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
}
