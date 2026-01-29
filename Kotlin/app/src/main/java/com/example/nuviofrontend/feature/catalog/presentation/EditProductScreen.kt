package com.example.nuviofrontend.feature.catalog.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.core.R
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.settings.CurrencyConverter
import com.example.core.settings.CurrencyConverter.format
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomDescriptionField
import com.example.core.ui.components.CustomTextFieldAligned
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.SelectedImagesRow
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel

@Composable
fun EditProductScreen(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    productId: Long,
    viewModel: ProductManagementViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
){
    val context = LocalContext.current

    var productName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var modelNumber by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    var selectedBrand by remember { mutableStateOf<Brand??>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    var addedAttributes by remember { mutableStateOf<List<AttributeFilter>>(emptyList()) }
    var selectedAttribute by remember { mutableStateOf<AttributeFilter?>(null) }
    val attributeValuesMap = remember { mutableStateMapOf<String, String?>() }

    var productImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var initialImages by remember { mutableStateOf<List<String>>(emptyList()) }
    val allImages = (initialImages + productImages).distinct()

    val fieldErrors by viewModel::fieldErrors
    val allAttributes = viewModel.attributes
    val urlsToSend = initialImages + productImages

    val selectedCurrency by settingsViewModel.currencyFlow.collectAsState(initial = 1)

    LaunchedEffect(productId, viewModel.categories) {
        if (viewModel.categories.isEmpty()) return@LaunchedEffect

        val result = viewModel.loadProduct(productId)

        result.onSuccess { product ->
            productName = product.name
            description = product.description ?: ""
            modelNumber = product.modelNumber ?: ""
            sku = product.sku ?: ""
            price = CurrencyConverter.fromEuro(
                amountInEuro = product.basePrice,
                currencyIndex = selectedCurrency
            ).format(2)
            quantity = product.quantity?.toString() ?: "0"

            selectedBrand = viewModel.brands.find { it.id == product.brand.id }
            selectedCategory = viewModel.categories.find { it.id == product.category.id }

            val allAttrs = viewModel.attributes
            val productAttrNames = product.attributes?.map { it.name } ?: emptyList()

            addedAttributes = allAttrs.filter { productAttrNames.contains(it.name) }
            attributeValuesMap.clear()
            product.attributes?.forEach { attr ->
                attributeValuesMap[attr.name] = attr.value
            }
            selectedAttribute = null

            productImages = product.images?.map { it.url } ?: emptyList()
        }
    }

    val productUpdated by viewModel.productUpdated.collectAsState(initial = false)

    LaunchedEffect(productUpdated) {
        if (productUpdated) {
            Toast.makeText(context, "Proizvod ažuriran!", Toast.LENGTH_SHORT).show()
            viewModel.resetProductUpdatedFlag()
            homeViewModel.refreshData()
            navController.popBackStack()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProductImage(
                uri = it,
                onSuccess = { uploadedUrl ->
                    productImages = productImages + uploadedUrl
                },
                onError = { msg ->
                    Toast.makeText(context, "Upload failed: $msg", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            CustomTopBar(
                title = stringResource(R.string.edit_product_title),
                showBack = true,
                onBack = onBackClick
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, start = 10.dp, end = 10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background((BackgroundBehindButton), RoundedCornerShape(12.dp))
                            .clickable {
                                imagePickerLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.add_image_text),
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        )
                    }
                    if (allImages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))

                        SelectedImagesRow(
                            images = allImages,
                            onRemoveImage = { imageUrl ->
                                if (productImages.contains(imageUrl)) {
                                    productImages = productImages.filter { it != imageUrl }
                                } else {
                                    initialImages = initialImages.filter { it != imageUrl }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    InfoCardContainer {
                        Text(
                            text = stringResource(R.string.basic_information_product),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Divider(color = BackgroundNavDark)
                        Spacer(modifier = Modifier.height(7.dp))

                        CustomTextFieldAligned(
                            value = productName,
                            onValueChange = { productName = it },
                            label = stringResource(R.string.label_product_name),
                            placeholder = "",
                            isError = fieldErrors.containsKey("productName"),
                            errorMessage = fieldErrors["productName"]
                        )

                        val priceLabel = if (selectedCurrency == 0) {
                            stringResource(R.string.label_price_dolar)
                        } else {
                            stringResource(R.string.label_price_euro)
                        }

                        CustomTextFieldAligned(
                            value = price,
                            onValueChange = { price = it },
                            label = priceLabel,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = "",
                            isError = fieldErrors.containsKey("price"),
                            errorMessage = fieldErrors["price"]
                        )

                        CustomDropdownEditProduct(
                            label = stringResource(R.string.brand),
                            value = selectedBrand,
                            items = viewModel.brands,
                            itemLabel = { mapAttributeValue("brand", it.name) },
                            placeholder = stringResource(R.string.placeholder_brand),
                            onItemSelected = { selectedBrand = it },
                            isError = fieldErrors.containsKey("brand"),
                            errorMessage = fieldErrors["brand"]
                        )


                        CustomDropdownEditProduct(
                            label = stringResource(R.string.category),
                            value = selectedCategory,
                            items = viewModel.categories,
                            itemLabel = { mapAttributeValue("category", it.name) },
                            placeholder = stringResource(R.string.placeholder_category),
                            onItemSelected = { selectedCategory = it },
                            isError = fieldErrors.containsKey("category"),
                            errorMessage = fieldErrors["category"]
                        )

                        CustomTextFieldAligned(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = stringResource(R.string.label_quantity),
                            placeholder = "",
                            isError = fieldErrors.containsKey("quantity"),
                            errorMessage = fieldErrors["quantity"]
                        )

                        CustomTextFieldAligned(
                            value = modelNumber,
                            onValueChange = { modelNumber = it },
                            label = stringResource(R.string.label_model_number),
                            placeholder = "",
                            isError = fieldErrors.containsKey("modelNumber"),
                            errorMessage = fieldErrors["modelNumber"]
                        )

                        CustomTextFieldAligned(
                            value = sku,
                            onValueChange = { sku = it },
                            label = stringResource(R.string.label_sku),
                            placeholder = "",
                            isError = fieldErrors.containsKey("sku"),
                            errorMessage = fieldErrors["sku"]
                        )

                        CustomDescriptionField(
                            value = description,
                            onValueChange = { description = it },
                            label = stringResource(R.string.label_description),
                            placeholder = "",
                            isError = fieldErrors.containsKey("description"),
                            errorMessage = fieldErrors["description"]
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    InfoCardContainer {
                        AdditionalSpecificationsCard(
                            allAttributes = allAttributes,
                            addedAttributes = addedAttributes,
                            onAddAttribute = { attr ->
                                addedAttributes = addedAttributes + attr
                                selectedAttribute = null
                            },
                            selectedAttribute = selectedAttribute,
                            onSelectedAttributeChange = { selectedAttribute = it },
                            attributeValuesMap = attributeValuesMap,
                            onAttributeValueChange = { attr, value ->
                                attributeValuesMap[attr] = value
                            },
                            onRemoveAttribute = { attr ->
                                addedAttributes = addedAttributes - attr
                                attributeValuesMap.remove(attr.name)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomButton(
                            text = stringResource(R.string.save_button),
                            onClick = {
                                val valid = viewModel.validateFields(
                                    productName = productName,
                                    price = price,
                                    brandId = selectedBrand?.id,
                                    categoryId = selectedCategory?.id,
                                    quantity = quantity,
                                    modelNumber = modelNumber,
                                    sku = sku,
                                    description = description
                                )

                                if (!valid) return@CustomButton
                                val basePrice = CurrencyConverter.toEuro(price.toDouble(), selectedCurrency)
                                viewModel.updateProduct(
                                    id = productId,
                                    name = productName,
                                    description = description,
                                    modelNumber = modelNumber,
                                    sku = sku,
                                    basePrice = basePrice,
                                    brandId = selectedBrand?.id,
                                    categoryId = selectedCategory?.id,
                                    quantity = quantity.toIntOrNull(),
                                    selectedAttributes = addedAttributes,
                                    attributeValuesMap = attributeValuesMap,
                                    imageUrls = urlsToSend
                                )
                            },
                            width = 304
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun <T> CustomDropdownEditProduct(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: T? = null,
    items: List<T>,
    itemLabel: (T) -> String,
    placeholder: String,
    onItemSelected: (T) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var showErrorDropdown by remember { mutableStateOf(false) }
    var triggeredWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "arrow_rotation"
    )

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onBackground,
                style = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { layoutCoordinates ->
                    triggeredWidth = with(density) {
                        layoutCoordinates.size.width.toDp()
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.7f))
                    .border(
                        width = if (isError) 1.dp else 0.dp,
                        color = if (isError) Error else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        expanded = true
                        if (isError) showErrorDropdown = false
                    }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value?.let { itemLabel(it) } ?: placeholder,
                    style = textStyle.copy(
                        color = if (value == null)
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotation)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            expanded = !expanded
                            if (!expanded) showErrorDropdown = false
                        }
                )

                if (isError && errorMessage != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { showErrorDropdown = !showErrorDropdown },
                        modifier = Modifier.size(15.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error_hint),
                            contentDescription = "",
                            tint = Error
                        )
                    }
                }
            }

            if (expanded) {
                val popupYOffsetPx = with(density) { 45.dp.roundToPx() }
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(0, popupYOffsetPx),
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Column(
                        modifier = Modifier
                            .width(triggeredWidth)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        items.forEachIndexed { index, item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onItemSelected(item)
                                        expanded = false
                                        showErrorDropdown = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = itemLabel(item),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = textStyle
                                )
                            }

                            if (index < items.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(BackgroundNavDark.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }
            }

            if (isError && errorMessage != null && showErrorDropdown) {
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(0, with(density) { (-60).dp.roundToPx() }),
                    onDismissRequest = { showErrorDropdown = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color(0xFF1A1F16))
                            .shadow(8.dp, clip = true)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



private fun formatProductName(name: String): String {
    return name.replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}

private fun mapAttributeValue(attribute: String, value: String): String {
    return when (attribute.lowercase()) {
        "weight_kg" -> {
            val formatted = value.replace("_", ",").replace(",", ",")
            if (value.lowercase().endsWith("kg")) formatted else "$formatted kg"
        }
        "display_size" -> {
            val formatted = value.replace("_", ",")
            if (value.lowercase().endsWith("inch")) formatted else "$formatted inch"
        }
        "color" -> value.replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        "battery_wh" -> "$value Wh"
        "category" -> when(value.lowercase()) {
            "gaming_laptops" -> "Gaming laptop"
            "laptops" -> "Laptop"
            "ultrabooks" -> "Ultrabook"
            else -> formatProductName(value)
        }
        "brand" -> value.split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        else -> formatProductName(value)
    }
}


