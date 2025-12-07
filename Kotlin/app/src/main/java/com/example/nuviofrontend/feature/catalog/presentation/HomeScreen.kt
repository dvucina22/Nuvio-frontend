package com.example.nuviofrontend.feature.catalog.presentation

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.ProductCard
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.White
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

data class Category(
    val id: Long,
    val name: String,
    val icon: ImageVector,
    val gradient: Brush
)

data class Banner(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    firstName: String?,
    gender: String? = null,
    viewModel: HomeViewModel = hiltViewModel(),
    productManagementViewModel: ProductManagementViewModel = hiltViewModel(),
    onProductClick: (Long) -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (Long) -> Unit
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showDeletePopup by remember { mutableStateOf(false) }
    var productIdToDelete by remember { mutableStateOf<Long?>(null) }

    val onDeleteProduct: (Long) -> Unit = { productId ->
        productIdToDelete = productId
        showDeletePopup = true
    }
    val successMessage = productManagementViewModel.successMessage
    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrBlank()) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            viewModel.refreshData()
            productManagementViewModel.successMessage = null
        }
    }

    val greeting = when (gender?.lowercase()) {
        "male" -> stringResource(R.string.welcome_male, firstName ?: "")
        "female" -> stringResource(R.string.welcome_female, firstName ?: "")
        else -> stringResource(R.string.welcome_neutral, firstName ?: "")
    }

    val categories = listOf(
        Category(
            id = 1,
            name = "Gaming",
            icon = Icons.Default.SportsEsports,
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
            )
        ),
        Category(
            id = 2,
            name = "Multimedia",
            icon = Icons.Default.Movie,
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFFF093FB), Color(0xFFF5576C))
            )
        ),
        Category(
            id = 3,
            name = "Business",
            icon = Icons.Default.Business,
            gradient = Brush.linearGradient(
                colors = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
            )
        )
    )

    val banners = listOf(
        Banner(1, "", "Black Friday", "Up to 50% OFF"),
        Banner(2, "", "New Arrivals", "Check latest models"),
        Banner(3, "", "Gaming Week", "Special deals")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp, top = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "What are you looking for today?",
                        color = Color(0xFF9AA4A6),
                        fontSize = 14.sp
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* akcija za Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .background(color = BackgroundBehindButton, shape = RoundedCornerShape(5.dp))
                            .clickable { onAddProductClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Add",
                            tint = Black,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PromotionalBanner(banners)

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Shop by Category")
            Spacer(modifier = Modifier.height(12.dp))
            CategoriesRow(
                categories = categories,
                onCategoryClick = { categoryId ->
                    viewModel.loadCategoryProducts(categoryId)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Flash Deals",
                actionText = "See All",
                onActionClick = { }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.flashDeals.isEmpty()) {
                LoadingRow()
            } else if (state.flashDeals.isNotEmpty()) {
                FlashDealsRow(
                    products = state.flashDeals,
                    favoriteIds = state.favoriteProductIds,
                    onToggleFavorite = { id, newValue ->
                        viewModel.setFavorite(id, newValue)
                    },
                    onProductClick = { productId -> onProductClick(productId) },
                    onDeleteProduct = { productId ->
                        onDeleteProduct(productId)
                    },
                    onEditProduct = { productId ->
                        onEditProductClick(productId)
                    }
                )
            } else {
                EmptyStateRow("No deals available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Latest Arrivals",
                actionText = "See All",
                onActionClick = { }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.latestProducts.isEmpty()) {
                LoadingRow()
            } else if (state.latestProducts.isNotEmpty()) {
                LatestProductsRow(
                    products = state.latestProducts,
                    favoriteIds = state.favoriteProductIds,
                    onToggleFavorite = { id, newValue ->
                        viewModel.setFavorite(id, newValue)
                    },
                    onProductClick = { productId -> onProductClick(productId) },
                    onDeleteProduct = { productId ->
                        onDeleteProduct(productId)
                    }
                )
            } else {
                EmptyStateRow("No products available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Popular Brands")
            Spacer(modifier = Modifier.height(12.dp))
            PopularBrandsRow(
                onBrandClick = { _ -> }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Recommended for You",
                actionText = "See All",
                onActionClick = { }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.recommendedProducts.isEmpty()) {
                LoadingGrid()
            } else if (state.recommendedProducts.isNotEmpty()) {
                RecommendedProductsGrid(
                    products = state.recommendedProducts,
                    favoriteIds = state.favoriteProductIds,
                    onToggleFavorite = { id, newValue ->
                        viewModel.setFavorite(id, newValue)
                    },
                    onProductClick = { productId -> onProductClick(productId) },
                    onDeleteProduct = { productId ->
                        onDeleteProduct(productId)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        state.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(error)
            }
        }

        if (state.isLoading && state.latestProducts.isNotEmpty()) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = Color(0xFF667EEA)
            )
        }

        if (showDeletePopup && productIdToDelete != null) {
            CustomPopupWarning(
                title = "Upozorenje",
                message = "Jeste li sigurni da želite obrisati proizvod?",
                confirmText = "Nastavi",
                dismissText = "Odustani",
                onDismiss = {
                    showDeletePopup = false
                    productIdToDelete = null
                },
                onConfirm = {
                    productIdToDelete?.let { id ->
                        productManagementViewModel.deleteProduct(id)
                    }
                    showDeletePopup = false
                    productIdToDelete = null
                }
            )
        }
    }
}

@Composable
fun LoadingRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF667EEA))
    }
}

@Composable
fun LoadingGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Color(0xFF667EEA))
    }
}

@Composable
fun EmptyStateRow(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color(0xFF9AA4A6),
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PromotionalBanner(banners: List<Banner>) {
    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(3000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % banners.size,
                animationSpec = tween(600)
            )
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 20.dp)
        ) {
            HorizontalPager(
                count = banners.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                BannerCard(banners[page])
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            if (pagerState.currentPage == index) 24.dp else 8.dp,
                            8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Color(0xFF667EEA)
                            else Color(0xFF3A4A5A)
                        )
                )
            }
        }
    }
}

@Composable
fun BannerCard(banner: Banner) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                )
            )
            .clickable { }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = banner.title,
                color = White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = banner.subtitle,
                color = White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Color(0xFF667EEA)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Shop Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (actionText != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = Color(0xFF667EEA),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CategoriesRow(
    categories: List<Category>,
    onCategoryClick: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(category.gradient)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.name,
            tint = White,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FlashDealsRow(
    products: List<Product>,
    favoriteIds: Set<Long>,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onProductClick: (Long) -> Unit,
    onDeleteProduct: (Long) -> Unit,
    onEditProduct: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            val isFavorite = favoriteIds.contains(product.id)
            ProductCard(
                product = product,
                isFavorite = isFavorite,
                onFavoriteChange = { newValue ->
                    onToggleFavorite(product.id, newValue)
                },
                onClick = { onProductClick(product.id) },
                showMenu = true,
                onDelete = { productId ->
                    onDeleteProduct(productId)
                },
                onEdit = {productId ->
                    onEditProduct(productId)
                }
            )
        }
    }
}

@Composable
fun LatestProductsRow(
    products: List<Product>,
    favoriteIds: Set<Long>,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onProductClick: (Long) -> Unit,
    onDeleteProduct: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            val isFavorite = favoriteIds.contains(product.id)
            ProductCard(
                product = product,
                isFavorite = isFavorite,
                onFavoriteChange = { newValue ->
                    onToggleFavorite(product.id, newValue)
                },
                onClick = { onProductClick(product.id) },
                showMenu = true,
                onDelete = { productId ->
                    onDeleteProduct(productId)
                }
            )
        }
    }
}

@Composable
fun PopularBrandsRow(onBrandClick: (String) -> Unit) {
    val brands = listOf("Apple", "Dell", "HP", "Lenovo", "Asus", "MSI")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(brands) { brand ->
            BrandCard(
                brandName = brand,
                onClick = { onBrandClick(brand) }
            )
        }
    }
}

@Composable
fun BrandCard(
    brandName: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp, 60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E2A38))
            .border(1.dp, Color(0xFF3A4A5A), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = brandName,
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RecommendedProductsGrid(
    products: List<Product>,
    favoriteIds: Set<Long>,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onProductClick: (Long) -> Unit,
    onDeleteProduct: (Long) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.chunked(2).forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowProducts.forEach { product ->
                    val isFavorite = favoriteIds.contains(product.id)
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = product,
                            isFavorite = isFavorite,
                            onFavoriteChange = { newValue ->
                                onToggleFavorite(product.id, newValue)
                            },
                            onClick = { onProductClick(product.id) },
                            showMenu = true,
                            onDelete = { productId ->
                                onDeleteProduct(productId)
                            }
                        )
                    }
                }
                if (rowProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
