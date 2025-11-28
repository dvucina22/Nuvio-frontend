package com.example.nuviofrontend.feature.favorite.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.components.ProductCard
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.White

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp, bottom = 80.dp, start = 20.dp, end = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.favorites_title),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

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
                            text = stringResource(id = R.string.favorites_empty_message),
                            color = White
                        )
                    }
                }

                else -> {
                    FavoriteResultsGrid(
                        products = state.products,
                        favoriteProductIds = state.favoriteProductIds,
                        isLoadingMore = state.isLoadingMore,
                        onToggleFavorite = { productId, shouldBeFavorite ->
                            viewModel.toggleFavorite(productId, shouldBeFavorite)
                        },
                        onLoadMore = {
                            viewModel.loadMore()
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
                        Text(text = stringResource(id = R.string.favorites_dismiss))
                    }
                }
            ) {
                Text(text = stringResource(id = resId))
            }
        }
    }
}

@Composable
private fun FavoriteResultsGrid(
    products: List<Product>,
    favoriteProductIds: Set<Long>,
    isLoadingMore: Boolean,
    onToggleFavorite: (productId: Long, shouldBeFavorite: Boolean) -> Unit,
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
                    val isFavorite = favoriteProductIds.contains(product.id)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 4.dp)
                    ) {
                        ProductCard(
                            product = product,
                            isFavorite = isFavorite,
                            onFavoriteChange = { shouldBeFavorite ->
                                onToggleFavorite(product.id, shouldBeFavorite)
                            }
                        )
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

@Preview
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreen()
}
