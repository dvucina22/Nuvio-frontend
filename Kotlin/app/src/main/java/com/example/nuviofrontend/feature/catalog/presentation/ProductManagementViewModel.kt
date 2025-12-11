package com.example.nuviofrontend.feature.catalog.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.catalog.dto.AddProductRequest
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.ProductDetail
import com.example.core.catalog.dto.UpdateProductRequest
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import com.example.nuviofrontend.feature.catalog.data.ProductImageRepository
import com.example.nuviofrontend.feature.catalog.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductManagementViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val catalogRepository: CatalogRepository,
    private val imageRepository: ProductImageRepository
) : ViewModel() {

    var brands by mutableStateOf<List<Brand>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var attributes by mutableStateOf<List<AttributeFilter>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var fieldErrors by mutableStateOf<Map<String, String>>(emptyMap())
    private val _productUpdated = MutableStateFlow(false)
    val productUpdated = _productUpdated.asStateFlow()
    var productImages by mutableStateOf<List<String>>(emptyList())
    var isUploadingImages by mutableStateOf(false)
    private val _productChanged = MutableSharedFlow<Unit>(replay = 1)
    val productChanged = _productChanged.asSharedFlow()

    private val _productDeleted = MutableSharedFlow<Unit>(replay = 1)
    val productDeleted = _productDeleted.asSharedFlow()
    private val _productUpdatedFlow = MutableSharedFlow<Boolean>()
    val productUpdatedFlow = _productUpdatedFlow.asSharedFlow()

    private val _productAddedFlow = MutableSharedFlow<Unit>()
    val productAddedFlow = _productAddedFlow.asSharedFlow()

    fun markProductUpdated() {
        _productUpdated.value = true
    }

    fun resetProductUpdatedFlag() {
        _productUpdated.value = false
    }


    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            brands = repository.loadBrands()
            categories = repository.loadCategories()
            val attributesResult = catalogRepository.getAttributes()
            attributesResult.onSuccess { list ->
                attributes = list
                attributes.forEach { attr ->
                    attr.items.forEach { item ->
                        Log.d(
                            "ATTR_DEBUG123",
                            "Attribute loaded ADD PRODUCT: name=${attr.name} value=${item.value} id=${item.id}"
                        )
                    }
                }
            }.onFailure { e ->
                errorMessage = e.message
            }
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
        selectedAttributes: List<AttributeFilter>,
        attributeValuesMap: Map<String, String?>,
        imageUrls: List<String>
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            val attributeIds: List<Long> = selectedAttributes.mapNotNull { attr ->
                val selectedValue = attributeValuesMap[attr.name]
                attr.items.firstOrNull { it.value == selectedValue }?.id
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
                attributeIds = attributeIds,
                imageUrls = imageUrls
            )

            val result = repository.createProduct(req)

            isLoading = false

            result.onSuccess {
                successMessage = it
                _productUpdatedFlow.emit(true)
                _productAddedFlow.emit(Unit)
                markProductUpdated()
            }.onFailure { throwable ->
                val message = throwable.message
                val errors = mutableMapOf<String, String>()
                if (message?.contains("products_sku_key") == true) {
                    errors["sku"] = "SKU već postoji, molimo upišite drugi."
                }
                if (errors.isEmpty()) {
                    errorMessage = message
                }

                fieldErrors = errors
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
            quantityValue == null || quantityValue < 0 -> errors["quantity"] =
                "Količina ne smije biti negativna"
        }
        val priceValue = price.toDoubleOrNull()
        when {
            price.isBlank() -> errors["price"] = "Ovo polje je obavezno"
            priceValue == null || priceValue < 0 -> errors["price"] =
                "Cijena ne smije biti negativna"
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
                    _productDeleted.emit(Unit)
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
        selectedAttributes: List<AttributeFilter>? = null,
        attributeValuesMap: Map<String, String?> = emptyMap(),
        imageUrls: List<String>? = null
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

            val attributeIds: List<Long> = selectedAttributes?.mapNotNull { attr ->
                val selectedValue = attributeValuesMap[attr.name]
                attr.items.firstOrNull { it.value == selectedValue }?.id
            } ?: existingProduct.attributes?.map { it.id.toLong() } ?: emptyList()

            val existingImages = existingProduct.images?.map { it.toString() } ?: emptyList()

            val req = UpdateProductRequest(
                name = name ?: existingProduct.name,
                description = description ?: existingProduct.description.orEmpty(),
                modelNumber = modelNumber ?: existingProduct.modelNumber.orEmpty(),
                sku = sku ?: existingProduct.sku.orEmpty(),
                basePrice = basePrice ?: existingProduct.basePrice,
                brandId = brandId?.toInt() ?: existingProduct.brand.id!!.toInt(),
                categoryId = categoryId?.toInt() ?: existingProduct.category.id!!.toInt(),
                quantity = quantity ?: existingProduct.quantity?.toInt() ?: 0,
                attributeIds = attributeIds,
                imageUrls = imageUrls ?: existingImages
            )

            val result = repository.updateProduct(id, req)
            isLoading = false

            result.onSuccess {
                successMessage = it
                _productChanged.emit(Unit)
                markProductUpdated()
            }.onFailure {
                errorMessage = it.message
            }
        }

    }

    suspend fun loadProduct(productId: Long): Result<ProductDetail> {
        return repository.fetchProduct(productId)
    }

    fun uploadProductImage(
        uri: Uri,
        onSuccess: ((String) -> Unit)? = null,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val url = imageRepository.uploadProductPicture(uri)
                productImages = productImages + url
                onSuccess?.invoke(url)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

}
