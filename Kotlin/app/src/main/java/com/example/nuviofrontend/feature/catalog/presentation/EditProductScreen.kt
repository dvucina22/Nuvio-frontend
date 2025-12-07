package com.example.nuviofrontend.feature.catalog.presentation

import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.core.R
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomTextField
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.BackgroundColorInput
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.CardItemBackground
import com.example.core.ui.theme.ColorInput
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White
import kotlin.collections.contains
import kotlin.collections.forEach
import kotlin.collections.set

@Composable
fun EditProductScreen(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    productId: Long,
    viewModel: ProductManagementViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
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
    var attributeValuesMap by remember { mutableStateOf(mutableMapOf<String, String?>()) }

    val fieldErrors by viewModel::fieldErrors

    LaunchedEffect(productId, viewModel.categories) {
        if (viewModel.categories.isEmpty()) return@LaunchedEffect

        val result = viewModel.loadProduct(productId)

        result.onSuccess { product ->
            productName = product.name
            description = product.description ?: ""
            modelNumber = product.modelNumber ?: ""
            sku = product.sku ?: ""
            price = product.basePrice.toString()
            quantity = product.quantity?.toString() ?: "0"

            selectedBrand = viewModel.brands.find { it.id == product.brand.id }
            selectedCategory = viewModel.categories.find { it.id == product.category.id }

            val allAttrs = viewModel.attributes
            val productAttrNames = product.attributes?.map { it.name } ?: emptyList()

            addedAttributes = allAttrs.filter { productAttrNames.contains(it.name) }
            attributeValuesMap = mutableMapOf<String, String?>().apply {
                product.attributes?.forEach { attr ->
                    this[attr.name] = attr.value
                }
            }
        }
    }

    LaunchedEffect(viewModel.productUpdated) {
        if (viewModel.productUpdated) {
            Toast.makeText(context, "Proizvod ažuriran!", Toast.LENGTH_SHORT).show()
            viewModel.productUpdated = false
            homeViewModel.refreshData()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ){
        CustomTopBar(
            title = stringResource(R.string.edit_product_title),
            showBack = true,
            onBack = onBackClick
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background((BackgroundBehindButton), RoundedCornerShape(12.dp))
                        .clickable { /* TODO: odabir slike */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.add_image_text),
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = CardItemBackground,
                                shape = RoundedCornerShape(6.dp)
                            )
                    ) {
                        Text(
                            text = stringResource(R.string.basic_information_product),
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(start = 22.dp, top = 10.dp)
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Divider(color = BackgroundNavDark)
                        Spacer(modifier = Modifier.height(7.dp))

                        CustomTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = stringResource(R.string.label_product_name),
                            placeholder = "",
                            isError = fieldErrors.containsKey("productName"),
                            errorMessage = fieldErrors["productName"]
                        )

                        CustomTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = stringResource(R.string.label_price),
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

                        CustomTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = stringResource(R.string.label_quantity),
                            placeholder = "",
                            isError = fieldErrors.containsKey("quantity"),
                            errorMessage = fieldErrors["quantity"]
                        )

                        CustomTextField(
                            value = modelNumber,
                            onValueChange = { modelNumber = it },
                            label = stringResource(R.string.label_model_number),
                            placeholder = "",
                            isError = fieldErrors.containsKey("modelNumber"),
                            errorMessage = fieldErrors["modelNumber"]
                        )

                        CustomTextField(
                            value = sku,
                            onValueChange = { sku = it },
                            label = stringResource(R.string.label_sku),
                            placeholder = "",
                            isError = fieldErrors.containsKey("sku"),
                            errorMessage = fieldErrors["sku"]
                        )

                        CustomTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = stringResource(R.string.label_description),
                            placeholder = "",
                            isError = fieldErrors.containsKey("description"),
                            errorMessage = fieldErrors["description"]
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AdditionalSpecificationsCard(
                    allAttributes = viewModel.attributes,
                    addedAttributes = addedAttributes,
                    onAddAttribute = { addedAttributes = addedAttributes + it },
                    onRemoveAttribute = { attr ->
                        addedAttributes = addedAttributes - attr
                        attributeValuesMap.remove(attr.name)
                    },
                    selectedAttribute = selectedAttribute,
                    onSelectedAttributeChange = { selectedAttribute = it },
                    attributeValuesMap = attributeValuesMap,
                    onAttributeValueChange = { name, value ->
                        attributeValuesMap[name] = value
                    }
                )
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

                            viewModel.updateProduct(
                                id = productId,
                                name = productName,
                                description = description,
                                modelNumber = modelNumber,
                                sku = sku,
                                basePrice = price.toDoubleOrNull(),
                                brandId = selectedBrand?.id,
                                categoryId = selectedCategory?.id,
                                quantity = quantity.toIntOrNull(),
                                selectedAttributes = attributeValuesMap.filterValues { it != null }.mapValues { it.value!! }
                            )
                        },
                        width = 304
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun <T> CustomDropdownEditProduct(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: T?,
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
                color = White,
                style = textStyle,
                modifier = Modifier
                    .width(304.dp)
                    .padding(bottom = 4.dp)
            )
        }

        Box(modifier = Modifier.width(304.dp)) {
            Row(
                modifier = Modifier
                    .width(304.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundColorInput.copy(alpha = 0.3f))
                    .border(
                        width = if (isError) 1.dp else 0.dp,
                        color = if (isError) Error else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        expanded = !expanded
                        if (isError) showErrorDropdown = false
                    }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value?.let { itemLabel(it) } ?: placeholder,
                    style = textStyle.copy(
                        color = if (value == null)
                            ColorInput.copy(alpha = 0.7f)
                        else White
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotation)
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(304.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.85f))
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = itemLabel(item), color = Color.Black, style = textStyle) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                            showErrorDropdown = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 4.dp, vertical = 0.dp)
                    )
                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Black.copy(alpha = 0.1f))
                        )
                    }
                }
            }

            if (isError && errorMessage != null && showErrorDropdown) {
                DropdownMenu(
                    expanded = showErrorDropdown,
                    onDismissRequest = { showErrorDropdown = false },
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .background(Color(0xFF1A1F16))
                        .shadow(elevation = 8.dp, clip = true),
                    offset = DpOffset(x = (130).dp, y = (-60).dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
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
            if (formatted.lowercase().endsWith("kg")) formatted else "$formatted kg"
        }
        "display_size" -> {
            val formatted = value.replace("_", ",")
            if (formatted.lowercase().endsWith("inch")) formatted else "$formatted inch"
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


