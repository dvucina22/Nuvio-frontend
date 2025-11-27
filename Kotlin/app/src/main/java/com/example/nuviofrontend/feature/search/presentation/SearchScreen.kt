package com.example.nuviofrontend.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.components.CustomRangeSlider
import com.example.core.ui.components.SearchField
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.SelectedItemBackgroundDark
import com.example.core.ui.theme.White
import com.example.nuviofrontend.feature.home.presentation.ProductCard
import kotlinx.coroutines.launch

data class FilterState(
    val sortBy: String = "newest",
    val selectedCategories: Set<Long> = emptySet(),
    val selectedBrands: Set<Long> = emptySet(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..5000f,
    val inStockOnly: Boolean = false,
    val favoritesOnly: Boolean = false,
    val selectedAttributes: Map<String, Set<String>> = emptyMap()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterSheet by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(FilterState()) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp, top = 36.dp, start = 20.dp, end = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchField(
                    modifier = Modifier.weight(1f),
                    value = state.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = stringResource(id = R.string.search_products),
                    label = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SelectedItemBackgroundDark)
                        .border(1.dp, BackgroundNavDark, RoundedCornerShape(12.dp))
                ) {
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(id = R.string.filter),
                            tint = IconSelectedTintDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = IconSelectedTintDark)
                    }
                }

                state.results.isEmpty() && state.query.isNotBlank() && state.error == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_results),
                            color = White
                        )
                    }
                }

                state.results.isNotEmpty() -> {
                    SearchResultsGrid(
                        products = state.results,
                        isLoadingMore = state.isLoadingMore,
                        onLoadMore = { viewModel.loadMore() }
                    )
                }
            }
        }

        if (state.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text(text = stringResource(id = R.string.dismiss))
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
                containerColor = BackgroundNavDark
            ) {
                SearchFilterSheetContent(
                    sheetState = sheetState,
                    filterState = filterState,
                    onFilterStateChange = { filterState = it },
                    onReset = {
                        filterState = FilterState()
                        viewModel.applyFilters(FilterState())
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
    }
}

@Composable
private fun SearchResultsGrid(
    products: List<Product>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val rows = products.chunked(2)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(rows) { index, rowProducts ->
            if (index == rows.lastIndex) {
                LaunchedEffect(key1 = index, key2 = rowProducts.size) {
                    onLoadMore()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowProducts.forEach { product ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp)
                    ) {
                        ProductCard(product = product)
                    }
                }
                if (rowProducts.size == 1) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .width(0.dp)
                    )
                }
            }
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = IconSelectedTintDark)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchFilterSheetContent(
    sheetState: SheetState,
    filterState: FilterState,
    onFilterStateChange: (FilterState) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    val scrollState = rememberScrollState()

    val sortOptions = listOf(
        "newest" to stringResource(id = R.string.sort_newest),
        "price_asc" to stringResource(id = R.string.sort_price_asc),
        "price_desc" to stringResource(id = R.string.sort_price_desc)
    )

    val categories = listOf(
        1L to "Gaming",
        2L to "Multimedia",
        3L to "Business"
    )

    val brands = listOf(
        1L to "Apple",
        2L to "Dell",
        3L to "HP",
        4L to "Lenovo",
        5L to "Asus",
        6L to "MSI"
    )

    val ramOptions = listOf("8 GB", "16 GB", "32 GB", "64 GB")
    val cpuOptions = listOf("i5", "i7", "i9", "Ryzen 5", "Ryzen 7", "Ryzen 9")
    val storageOptions = listOf("256 GB", "512 GB", "1 TB", "2 TB")

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
            text = stringResource(id = R.string.filter),
            color = Color(0xFF1A1A1A),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilterSection(title = stringResource(id = R.string.sort_by)) {
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
                categories.forEach { (id, name) ->
                    FilterChip(
                        label = name,
                        selected = filterState.selectedCategories.contains(id),
                        onClick = {
                            val newCategories = if (filterState.selectedCategories.contains(id)) {
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

        FilterSection(title = stringResource(id = R.string.brand)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                brands.forEach { (id, name) ->
                    FilterChip(
                        label = name,
                        selected = filterState.selectedBrands.contains(id),
                        onClick = {
                            val newBrands = if (filterState.selectedBrands.contains(id)) {
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

        FilterSection(title = stringResource(id = R.string.price_range)) {
            CustomRangeSlider(
                value = filterState.priceRange,
                onValueChange = { onFilterStateChange(filterState.copy(priceRange = it)) },
                valueRange = 0f..5000f
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilterSection(title = stringResource(id = R.string.ram)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ramOptions.forEach { ram ->
                    val ramAttrs = filterState.selectedAttributes["RAM"] ?: emptySet()
                    FilterChip(
                        label = ram,
                        selected = ramAttrs.contains(ram),
                        onClick = {
                            val newRam = if (ramAttrs.contains(ram)) {
                                ramAttrs - ram
                            } else {
                                ramAttrs + ram
                            }
                            val newAttrs = filterState.selectedAttributes.toMutableMap()
                            if (newRam.isEmpty()) {
                                newAttrs.remove("RAM")
                            } else {
                                newAttrs["RAM"] = newRam
                            }
                            onFilterStateChange(filterState.copy(selectedAttributes = newAttrs))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilterSection(title = stringResource(id = R.string.processor)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cpuOptions.forEach { cpu ->
                    val cpuAttrs = filterState.selectedAttributes["CPU"] ?: emptySet()
                    FilterChip(
                        label = cpu,
                        selected = cpuAttrs.contains(cpu),
                        onClick = {
                            val newCpu = if (cpuAttrs.contains(cpu)) {
                                cpuAttrs - cpu
                            } else {
                                cpuAttrs + cpu
                            }
                            val newAttrs = filterState.selectedAttributes.toMutableMap()
                            if (newCpu.isEmpty()) {
                                newAttrs.remove("CPU")
                            } else {
                                newAttrs["CPU"] = newCpu
                            }
                            onFilterStateChange(filterState.copy(selectedAttributes = newAttrs))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilterSection(title = stringResource(id = R.string.storage)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storageOptions.forEach { storage ->
                    val storageAttrs = filterState.selectedAttributes["Storage"] ?: emptySet()
                    FilterChip(
                        label = storage,
                        selected = storageAttrs.contains(storage),
                        onClick = {
                            val newStorage = if (storageAttrs.contains(storage)) {
                                storageAttrs - storage
                            } else {
                                storageAttrs + storage
                            }
                            val newAttrs = filterState.selectedAttributes.toMutableMap()
                            if (newStorage.isEmpty()) {
                                newAttrs.remove("Storage")
                            } else {
                                newAttrs["Storage"] = newStorage
                            }
                            onFilterStateChange(filterState.copy(selectedAttributes = newAttrs))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FilterSwitchRow(
            label = stringResource(id = R.string.only_in_stock),
            checked = filterState.inStockOnly,
            onCheckedChange = { onFilterStateChange(filterState.copy(inStockOnly = it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilterSwitchRow(
            label = stringResource(id = R.string.only_favorites),
            checked = filterState.favoritesOnly,
            onCheckedChange = { onFilterStateChange(filterState.copy(favoritesOnly = it)) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF8A9499),
                    contentColor = Color(0xFF1A1A1A)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6A7479)),
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
                    containerColor = Color(0xFF5A656A),
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
            color = Color(0xFF2A2A2A),
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
                if (selected) Color(0xFF5A656A)
                else Color(0xFFD1D5D7)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) White else Color(0xFF2A2A2A),
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
            .background(Color(0xFFD1D5D7))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF2A2A2A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = Color(0xFF5A656A),
                uncheckedThumbColor = Color(0xFF8A9499),
                uncheckedTrackColor = Color(0xFFB1B5B7)
            )
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}