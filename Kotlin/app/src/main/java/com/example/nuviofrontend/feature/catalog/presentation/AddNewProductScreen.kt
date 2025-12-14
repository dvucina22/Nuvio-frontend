package com.example.nuviofrontend.feature.catalog.presentation

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.core.R
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomDescriptionField
import com.example.core.ui.components.CustomTextField
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.SelectedImagesRow
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.BackgroundColorInput
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.ButtonColorSelected
import com.example.core.ui.theme.CardItemBackground
import com.example.core.ui.theme.ColorInput
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun AddNewProductScreen(
    navController: NavHostController,
    onBackClick: () -> Unit = {},
    viewModel: ProductManagementViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
){
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var modelNumber by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedBrand by remember { mutableStateOf<String?>(null) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val allAttributes = viewModel.attributes
    var addedAttributes by remember { mutableStateOf(listOf<AttributeFilter>()) }
    var selectedAttribute by remember { mutableStateOf<AttributeFilter?>(null) }
    val attributeValuesMap = remember { mutableStateMapOf<String, String?>() }

    val brands = viewModel.brands
    val categories = viewModel.categories

    val context = LocalContext.current

    val productAdded by viewModel.productUpdated.collectAsState(initial = false)

    LaunchedEffect(viewModel.productAddedFlow) {
        viewModel.productAddedFlow.collect {
            Toast.makeText(context, "Uspješno dodan novi proizvod", Toast.LENGTH_SHORT).show()
            homeViewModel.loadHomeData()
            navController.popBackStack()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProductImage(
                it,
                onError = { msg ->
                    Toast.makeText(context, "Upload failed: $msg", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    fun removeAttribute(attribute: AttributeFilter) {
        addedAttributes = addedAttributes.filter { it.name != attribute.name }
        attributeValuesMap.remove(attribute.name)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ){
        CustomTopBar(
            title = stringResource(R.string.add_new_product_title),
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
                if (viewModel.productImages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    SelectedImagesRow(
                        images = viewModel.productImages,
                        onRemoveImage = { removedUrl ->
                            viewModel.productImages =
                                viewModel.productImages.filter { it != removedUrl }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
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
                            modifier = Modifier.padding(start = 32.dp, top = 10.dp)
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Divider(color = BackgroundNavDark)
                        Spacer(modifier = Modifier.height(7.dp))

                        CustomTextField(
                            value = productName,
                            onValueChange = {
                                productName = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "productName"
                            },
                            placeholder = stringResource(R.string.placeholder_product_name),
                            label = stringResource(R.string.label_product_name),
                            isError = viewModel.fieldErrors.containsKey("productName"),
                            errorMessage = viewModel.fieldErrors["productName"],
                            labelColor = White
                        )

                        CustomTextField(
                            value = price,
                            onValueChange = {
                                price = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "price"
                            },
                            placeholder = stringResource(R.string.placeholder_price),
                            label = stringResource(R.string.label_price),
                            isError = viewModel.fieldErrors.containsKey("price"),
                            errorMessage = viewModel.fieldErrors["price"],
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            labelColor = White
                        )

                        CustomDropdownAddProduct(
                            label = stringResource(R.string.brand),
                            value = selectedBrand?.let { mapAttributeValue("brand", it) },
                            items = brands.map { it.name },
                            itemLabel = { mapAttributeValue("brand", it) },
                            placeholder = stringResource(R.string.placeholder_brand),
                            onItemSelected = {
                                selectedBrand = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "brand"
                            },
                            isError = viewModel.fieldErrors.containsKey("brand"),
                            errorMessage = viewModel.fieldErrors["brand"]
                        )

                        CustomDropdownAddProduct(
                            label = stringResource(R.string.category),
                            value = selectedCategory?.let { mapAttributeValue("category", it) },
                            items = categories.map { it.name },
                            itemLabel = { mapAttributeValue("category", it) },
                            placeholder = stringResource(R.string.placeholder_category),
                            onItemSelected = {
                                selectedCategory = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "category"
                            },
                            isError = viewModel.fieldErrors.containsKey("category"),
                            errorMessage = viewModel.fieldErrors["category"]
                        )

                        CustomTextField(
                            value = quantity,
                            onValueChange = {
                                quantity = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "quantity"
                            },
                            placeholder = stringResource(R.string.placeholder_quantity),
                            label = stringResource(R.string.label_quantity),
                            isError = viewModel.fieldErrors.containsKey("quantity"),
                            errorMessage = viewModel.fieldErrors["quantity"],
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            labelColor = White
                        )

                        CustomTextField(
                            value = modelNumber,
                            onValueChange = {
                                modelNumber = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "modelNumber"
                            },
                            placeholder = stringResource(R.string.placeholder_model_number),
                            label = stringResource(R.string.label_model_number),
                            isError = viewModel.fieldErrors.containsKey("modelNumber"),
                            errorMessage = viewModel.fieldErrors["modelNumber"],
                            labelColor = White
                        )

                        CustomTextField(
                            value = sku,
                            onValueChange = {
                                sku = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "sku"
                            },
                            placeholder = stringResource(R.string.placeholder_sku),
                            label = stringResource(R.string.label_sku),
                            isError = viewModel.fieldErrors.containsKey("sku"),
                            errorMessage = viewModel.fieldErrors["sku"],
                            labelColor = White
                        )

                        CustomDescriptionField(
                            value = description,
                            onValueChange = {
                                description = it
                                viewModel.fieldErrors = viewModel.fieldErrors - "description"
                            },
                            placeholder = stringResource(R.string.placeholder_description),
                            label = stringResource(R.string.label_description),
                            isError = viewModel.fieldErrors.containsKey("description"),
                            errorMessage = viewModel.fieldErrors["description"]
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
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
                    onAttributeValueChange = { attr, value -> attributeValuesMap[attr] = value },
                    onRemoveAttribute = { attr -> removeAttribute(attr) },
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
                            val brandId = brands.find { it.name == selectedBrand }?.id
                            val categoryId = categories.find { it.name == selectedCategory }?.id

                            val isValid = viewModel.validateFields(
                                productName = productName,
                                price = price,
                                brandId = brandId,
                                categoryId = categoryId,
                                quantity = quantity,
                                modelNumber = modelNumber,
                                sku = sku,
                                description = description
                            )

                            if (isValid) {
                                val basePrice = price.toDouble()
                                val qty = quantity.toInt()
                                val filteredAttributes = addedAttributes
                                viewModel.addProduct(
                                    name = productName,
                                    description = description,
                                    modelNumber = modelNumber,
                                    sku = sku,
                                    basePrice = basePrice,
                                    brandId = brandId!!,
                                    categoryId = categoryId!!,
                                    quantity = qty,
                                    selectedAttributes = filteredAttributes,
                                    attributeValuesMap = attributeValuesMap,
                                    imageUrls = viewModel.productImages
                                )
                            }
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
fun AdditionalSpecificationsCard(
    allAttributes: List<AttributeFilter>,
    addedAttributes: List<AttributeFilter>,
    onAddAttribute: (AttributeFilter) -> Unit,
    onRemoveAttribute: (AttributeFilter) -> Unit,
    selectedAttribute: AttributeFilter?,
    onSelectedAttributeChange: (AttributeFilter?) -> Unit,
    attributeValuesMap: SnapshotStateMap<String, String?>,
    onAttributeValueChange: (String, String?) -> Unit
) {
    val basicAttributes = listOf("category", "brand")

    val remainingAttributes = allAttributes
        .filter { attr ->
            addedAttributes.none { it.name == attr.name } &&
                    !basicAttributes.contains(attr.name?.lowercase())
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardItemBackground, RoundedCornerShape(6.dp))
    ) {
        Text(
            text = stringResource(R.string.additional_specifications_title),
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 32.dp, bottom = 10.dp, top = 10.dp)
        )
        Divider(color = BackgroundNavDark)
        Spacer(modifier = Modifier.height(8.dp))

        addedAttributes.forEach { attribute ->
            val values = attribute.items.map { it.value }
            val selectedValue = attributeValuesMap[attribute.name]

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 40.dp)
            ) {
                Box(modifier = Modifier.weight(0.7f)) {
                    CustomDropdownAddProduct(
                        label = mapAttributeName(attribute.name ?: ""),
                        value = attributeValuesMap[attribute.name],
                        items = values,
                        itemLabel = { mapAttributeValue(attribute.name ?: "", it) },
                        placeholder = stringResource(R.string.placeholder_select_attribute),
                        onItemSelected = { value ->
                            attributeValuesMap[attribute.name ?: ""] = value
                        },
                        modifier = Modifier.width(250.dp)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier.offset(y = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .background(BackgroundBehindButton, RoundedCornerShape(5.dp))
                            .clickable { onRemoveAttribute(attribute) },
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "",
                            tint = Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (remainingAttributes.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 40.dp)
            ) {
                Box(modifier = Modifier.weight(0.7f)) {
                    CustomDropdownAddProduct(
                        label = stringResource(R.string.placeholder_add_attribute),
                        value = selectedAttribute?.name?.let { mapAttributeName(it) },
                        items = remainingAttributes.map { it.name ?: "" },
                        itemLabel = { mapAttributeName(it) },
                        placeholder = stringResource(R.string.placeholder_add_attribute),
                        onItemSelected = { selectedName ->
                            val attr = allAttributes.find { it.name == selectedName }
                            if (attr != null) onSelectedAttributeChange(attr)
                        },
                        modifier = Modifier.width(250.dp)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier.offset(y = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(BackgroundBehindButton, RoundedCornerShape(5.dp))
                            .clickable {
                                selectedAttribute?.let {
                                    onAddAttribute(it)
                                    onSelectedAttributeChange(null)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "",
                            tint = Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun <T> CustomDropdownAddProduct(
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
                color = White,
                style = textStyle,
                modifier = Modifier
                    .width(304.dp)
                    .padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier.width(304.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    triggeredWidth = with(density) {
                        layoutCoordinates.size.width.toDp()
                    }
                }

        ) {
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
                            .background(ButtonColorSelected)
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
                                    color = White,
                                    style = textStyle
                                )
                            }

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

private fun mapAttributeName(name: String): String {
    return when (name.lowercase()) {
        "display_size" -> "Veličina ekrana"
        "display_resolution" -> "Rezolucija"
        "color" -> "Boja"
        "os" -> "Operativni sustav"
        "build_material" -> "Materijal kućišta"
        "weight_kg" -> "Težina"
        "battery_wh" -> "Baterija"
        else -> name.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
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
