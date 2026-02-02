package com.example.nuviofrontend.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.presentation.AuthViewModel
import com.example.core.R
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.Product
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.CustomRangeSlider
import com.example.core.ui.components.IconActionBox
import com.example.core.ui.components.ProductCard
import com.example.core.ui.components.SearchField
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.GrayOne
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.LightOverlay
import com.example.core.ui.theme.SelectedItemBackgroundDark
import com.example.core.ui.theme.White
import com.example.core.ui.theme.WhiteSoft
import com.example.nuviofrontend.feature.catalog.presentation.ProductManagementViewModel
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    productManagementViewModel: ProductManagementViewModel = hiltViewModel(),
    onProductClick: (Long) -> Unit,
    onEditProductClick: (Long) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterSheet by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(FilterState()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(showFilterSheet) {
        if (showFilterSheet) {
            viewModel.ensureFilterDataLoaded()
        }
    }

    val selectedCurrency by settingsViewModel.currencyFlow.collectAsState(initial = 1)

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val isLoggedIn = authState.isLoggedIn
    val isAdmin = authState.isAdmin
    val isSeller = authState.isSeller
    val canManageProducts = isLoggedIn && (isAdmin || isSeller)

    var showDeletePopup by remember { mutableStateOf(false) }
    var productIdToDelete by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp, top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 26.dp,
                        bottom = 13.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.search_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchField(
                        modifier = Modifier.weight(1f),
                        value = state.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = stringResource(R.string.search_products),
                        label = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconActionBox(
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(R.string.filter),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
                ) {
                    if (state.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = IconSelectedTintDark)
                            }
                        }
                    }

                    if (state.results.isEmpty() && state.query.isNotBlank() && state.error == null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_results),
                                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                                )
                            }
                        }
                    }

                    itemsIndexed(items = state.results, key = { _, product -> product.id }) { index, product ->
                        if (index == state.results.lastIndex) {
                            LaunchedEffect(key1 = index) { viewModel.loadMore() }
                        }

                        ProductCard(
                            product = product,
                            selectedCurrency = selectedCurrency,
                            isFavorite = state.favoriteProductIds.contains(product.id),
                            onFavoriteChange = { shouldBeFavorite ->
                                viewModel.setFavorite(product.id, shouldBeFavorite)
                            },
                            onClick = { onProductClick(product.id) },
                            showMenu = canManageProducts,
                            onDelete = { productId ->
                                if (!canManageProducts) return@ProductCard
                                productIdToDelete = productId
                                showDeletePopup = true
                            },
                            onEdit = { productId ->
                                if (!canManageProducts) return@ProductCard
                                onEditProductClick(productId)
                            },
                            isAdmin = isAdmin,
                            isSeller = isSeller,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = IconSelectedTintDark)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }

        if (state.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.clearError() },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = WhiteSoft
                        )
                    ) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                }
            ) {
                Text(text = state.error ?: "")
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                SearchFilterSheetContent(
                    sheetState = sheetState,
                    filterState = filterState,
                    categories = state.categories,
                    brands = state.brands,
                    attributes = state.attributes,
                    isLoadingInitialData = state.isFilterDataLoading,
                    onFilterStateChange = { filterState = it },
                    onReset = {
                        val reset = FilterState()
                        filterState = reset
                        viewModel.applyFilters(reset)
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showFilterSheet = false
                        }
                    },
                    onApply = {
                        viewModel.applyFilters(filterState)
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showFilterSheet = false
                        }
                    }
                )
            }
        }

        if (showDeletePopup && productIdToDelete != null) {
            CustomPopupWarning(
                title = stringResource(R.string.warning),
                message = stringResource(R.string.delete_item_confirm),
                confirmText = stringResource(R.string.next),
                dismissText = stringResource(R.string.cancel),
                onDismiss = {
                    showDeletePopup = false
                    productIdToDelete = null
                },
                onConfirm = {
                    productIdToDelete?.let { id ->
                        productManagementViewModel.deleteProduct(id)
                        viewModel.removeProductFromResults(id)
                    }
                    showDeletePopup = false
                    productIdToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchFilterSheetContent(
    sheetState: SheetState,
    filterState: FilterState,
    categories: List<Category>,
    brands: List<Brand>,
    attributes: List<AttributeFilter>,
    isLoadingInitialData: Boolean,
    onFilterStateChange: (FilterState) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    val scrollState = rememberScrollState()

    val sortOptions = listOf(
        "newest" to stringResource(R.string.sort_newest),
        "price_asc" to stringResource(R.string.sort_price_asc),
        "price_desc" to stringResource(R.string.sort_price_desc)
    )

    val categoryPairs = categories.map { it.id to it.name }
    val brandPairs = brands.map { it.id to it.name }

    val usedAttributes = attributes
        .mapNotNull { attr ->
            val name = attr.name ?: return@mapNotNull null
            attr.copy(name = name)
        }
        .filter { it.name != "brand" && it.name != "category" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(SelectedItemBackgroundDark)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.filter),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoadingInitialData && categoryPairs.isEmpty() && brandPairs.isEmpty() && usedAttributes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IconSelectedTintDark)
            }
        } else {
            FilterSection(title = stringResource(R.string.sort_by)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sortOptions.forEach { (value, label) ->
                        FilterChip(
                            label = label,
                            selected = filterState.sortBy == value,
                            onClick = { onFilterStateChange(filterState.copy(sortBy = value)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = stringResource(id = R.string.category)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryPairs.forEach { (id, name) ->
                        FilterChip(
                            label = name,
                            selected = filterState.selectedCategories.contains(id),
                            onClick = {
                                val newCategories =
                                    if (filterState.selectedCategories.contains(id)) {
                                        filterState.selectedCategories - id
                                    } else {
                                        filterState.selectedCategories + id
                                    }
                                onFilterStateChange(filterState.copy(selectedCategories = newCategories))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = stringResource(R.string.brand)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    brandPairs.forEach { (id, name) ->
                        FilterChip(
                            label = name,
                            selected = filterState.selectedBrands.contains(id),
                            onClick = {
                                val newBrands =
                                    if (filterState.selectedBrands.contains(id)) {
                                        filterState.selectedBrands - id
                                    } else {
                                        filterState.selectedBrands + id
                                    }
                                onFilterStateChange(filterState.copy(selectedBrands = newBrands))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = stringResource(R.string.price_range)) {
                CustomRangeSlider(
                    value = filterState.priceRange,
                    onValueChange = { onFilterStateChange(filterState.copy(priceRange = it)) },
                    valueRange = 0f..5000f
                )
            }

            usedAttributes.forEach { attribute ->
                val attrName = attribute.name
                val selectedValues = filterState.selectedAttributes[attrName] ?: emptySet()
                val valuesList = attribute.items.map { it.value }

                if (valuesList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    FilterSection(
                        title = attributeSectionTitle(attrName)
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            valuesList.forEach { rawValue: String ->
                                val isSelected = selectedValues.contains(rawValue)
                                FilterChip(
                                    label = attributeValueLabel(attrName, rawValue),
                                    selected = isSelected,
                                    onClick = {
                                        val newValues =
                                            if (isSelected) selectedValues - rawValue
                                            else selectedValues + rawValue
                                        val newAttrs = filterState.selectedAttributes.toMutableMap()
                                        if (newValues.isEmpty()) {
                                            newAttrs.remove(attrName)
                                        } else {
                                            newAttrs[attrName] = newValues
                                        }
                                        onFilterStateChange(
                                            filterState.copy(
                                                selectedAttributes = newAttrs
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSwitchRow(
                label = stringResource(R.string.only_in_stock),
                checked = filterState.inStockOnly,
                onCheckedChange = { onFilterStateChange(filterState.copy(inStockOnly = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilterSwitchRow(
                label = stringResource(R.string.only_favorites),
                checked = filterState.favoritesOnly,
                onCheckedChange = { onFilterStateChange(filterState.copy(favoritesOnly = it)) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.reset),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.apply),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) AccentColor
                else MaterialTheme.colorScheme.surfaceContainerLowest
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) White else MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FilterSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = AccentColor,
                uncheckedThumbColor = GrayOne,
                uncheckedTrackColor = LightOverlay
            )
        )
    }
}

@Composable
private fun attributeSectionTitle(name: String): String {
    return when (name) {
        "os" -> stringResource(R.string.attribute_os)
        "display_size" -> stringResource(R.string.attribute_display_size)
        "display_resolution" -> stringResource(R.string.attribute_display_resolution)
        "color" -> stringResource(R.string.attribute_color)
        "build_material" -> stringResource(R.string.attribute_build_material)
        "weight_kg" -> stringResource(R.string.attribute_weight)
        "battery_wh" -> stringResource(R.string.attribute_battery)
        else -> name
    }
}

@Composable
private fun attributeValueLabel(attributeName: String, value: String): String {
    return when (attributeName) {
        "os" -> when (value) {
            "macos" -> stringResource(id = R.string.os_macos)
            "windows_11" -> stringResource(id = R.string.os_windows_11)
            else -> value
        }

        "color" -> when (value) {
            "black" -> stringResource(R.string.color_black)
            "midnight" -> stringResource(R.string.color_midnight)
            "platinum" -> stringResource(R.string.color_platinum)
            "silver" -> stringResource(R.string.color_silver)
            "space_gray" -> stringResource(R.string.color_space_gray)
            else -> value
        }

        "build_material" -> when (value) {
            "aluminum" -> stringResource(R.string.material_aluminum)
            "carbon_fiber" -> stringResource(R.string.material_carbon_fiber)
            "plastic" -> stringResource(R.string.material_plastic)
            else -> value
        }

        "display_size" -> "${normalizeNumericUnderscore(value)}\""
        "weight_kg" -> "${normalizeNumericUnderscore(value)} kg"
        "battery_wh" -> "$value Wh"
        "display_resolution" -> value.replace("x", " x ")
        else -> value
    }
}

private fun normalizeNumericUnderscore(raw: String): String {
    return raw.replace("_", ".")
}
