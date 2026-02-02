package com.example.nuviofrontend.feature.favorite.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.components.ProductCard
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onProductClick: (Long) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedCurrency by settingsViewModel.currencyFlow.collectAsState(initial = 1)

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
                        start = 15.dp,
                        end = 10.dp,
                        top = 26.dp,
                        bottom = 13.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.favorites_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.your_saved_products),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading && state.products.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = IconSelectedTintDark)
                    }
                }

                state.products.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.favorites_empty_message),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    FavoriteResultsList(
                        products = state.products,
                        selectedCurrency = selectedCurrency,
                        favoriteProductIds = state.favoriteProductIds,
                        isLoadingMore = state.isLoadingMore,
                        onToggleFavorite = { productId, shouldBeFavorite ->
                            viewModel.toggleFavorite(productId, shouldBeFavorite)
                        },
                        onLoadMore = {
                            viewModel.loadMore()
                        },
                        onProductClick = { productId ->
                            onProductClick(productId)
                        }
                    )
                }
            }
        }

        state.errorMessageResId?.let { resId ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text(text = stringResource(R.string.favorites_dismiss))
                    }
                }
            ) {
                Text(text = stringResource(id = resId))
            }
        }
    }
}

@Composable
private fun FavoriteResultsList(
    products: List<Product>,
    favoriteProductIds: Set<Long>,
    isLoadingMore: Boolean,
    onToggleFavorite: (productId: Long, shouldBeFavorite: Boolean) -> Unit,
    onLoadMore: () -> Unit,
    onProductClick: (Long) -> Unit,
    selectedCurrency: Int
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
    ) {
        itemsIndexed(products) { index, product ->

            if (index == products.lastIndex) {
                LaunchedEffect(key1 = index) {
                    onLoadMore()
                }
            }

            val isFavorite = favoriteProductIds.contains(product.id)

            ProductCard(
                product = product,
                selectedCurrency = selectedCurrency,
                isFavorite = isFavorite,
                onFavoriteChange = { shouldBeFavorite ->
                    onToggleFavorite(product.id, shouldBeFavorite)
                },
                onClick = { onProductClick(product.id) },
                showMenu = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (isLoadingMore) {
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
    }
}