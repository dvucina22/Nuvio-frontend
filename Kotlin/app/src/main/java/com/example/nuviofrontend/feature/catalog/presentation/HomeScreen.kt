package com.example.nuviofrontend.feature.home.presentation

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.theme.White
import com.example.nuviofrontend.feature.catalog.presentation.HomeViewModel
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()

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
                IconButton(onClick = { /* TODO: Notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = White
                    )
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
                onActionClick = { /* TODO: Navigate to deals */ }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.flashDeals.isEmpty()) {
                LoadingRow()
            } else if (state.flashDeals.isNotEmpty()) {
                FlashDealsRow(products = state.flashDeals)
            } else {
                EmptyStateRow("No deals available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Latest Arrivals",
                actionText = "See All",
                onActionClick = { /* TODO: Navigate to latest */ }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.latestProducts.isEmpty()) {
                LoadingRow()
            } else if (state.latestProducts.isNotEmpty()) {
                LatestProductsRow(products = state.latestProducts)
            } else {
                EmptyStateRow("No products available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Popular Brands")
            Spacer(modifier = Modifier.height(12.dp))
            PopularBrandsRow(
                onBrandClick = { brandName ->
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Recommended for You",
                actionText = "See All",
                onActionClick = { /* TODO: Navigate to recommended */ }
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.recommendedProducts.isEmpty()) {
                LoadingGrid()
            } else if (state.recommendedProducts.isNotEmpty()) {
                RecommendedProductsGrid(products = state.recommendedProducts)
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
                        .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
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
            .clickable { /* TODO: Navigate to banner offer */ }
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
                onClick = { /* TODO */ },
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
fun FlashDealsRow(products: List<Product>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            FlashDealCard(product = product)
        }
    }
}

@Composable
fun FlashDealCard(product: Product) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E2A38))
            .clickable { /* TODO: Navigate to product details */ }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF2D3E4A))
            ) {
                AsyncImage(
                    model = product.imageUrl.ifEmpty { R.drawable.logo_light_icon },
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (product.quantity != null && product.quantity!! < 10) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = Color(0xFFFF4757),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Only ${product.quantity} left",
                            color = White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.brand,
                    color = Color(0xFF9AA4A6),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${product.basePrice}",
                    color = Color(0xFF667EEA),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LatestProductsRow(products: List<Product>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(product = product)
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E2A38))
            .clickable { /* TODO: Navigate to product details */ }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF2D3E4A))
            ) {
                AsyncImage(
                    model = product.imageUrl.ifEmpty { R.drawable.logo_light_icon },
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { /* TODO: Toggle favorite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = White
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.brand,
                    color = Color(0xFF9AA4A6),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${product.basePrice}",
                    color = Color(0xFF667EEA),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
fun RecommendedProductsGrid(products: List<Product>) {
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
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(product = product)
                    }
                }
                if (rowProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}