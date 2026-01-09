package com.example.nuviofrontend.feature.catalog.presentation

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.presentation.AuthViewModel
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.ProductCard
import com.example.core.ui.components.banner.BannerData
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.White
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.core.ui.components.IconActionBox
import com.example.core.ui.components.banner.BannerComponent
import com.example.core.ui.components.categories.CategoryButton
import com.example.core.ui.components.categories.CategoryButtonData
import com.example.core.ui.theme.IconDark

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
    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()
    val profile by viewModel.profileFlow.collectAsState(initial = null)

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val isLoggedIn = authState.isLoggedIn
    val isAdmin = authState.isAdmin
    val isSeller = authState.isSeller

    var showDeletePopup by remember { mutableStateOf(false) }
    var productIdToDelete by remember { mutableStateOf<Long?>(null) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(0L) }

    val onDeleteProduct: (Long) -> Unit = { productId ->
        productIdToDelete = productId
        showDeletePopup = true
    }


    val greeting = when (gender?.lowercase()) {
        "male" -> stringResource(R.string.welcome_male, firstName ?: "")
        "female" -> stringResource(R.string.welcome_female, firstName ?: "")
        else -> stringResource(R.string.welcome_neutral, firstName ?: "")
    }

    val categories = listOf(
        CategoryButtonData(id = 0, name = stringResource(R.string.category_all)),
        CategoryButtonData(id = 1, name = stringResource(R.string.category_gaming)),
        CategoryButtonData(id = 2, name = stringResource(R.string.category_multimedia)),
        CategoryButtonData(id = 3, name = stringResource(R.string.category_business))
    )

    val banners = listOf(
        BannerData(
            id = 1,
            imageUrl = "https://res.cloudinary.com/dx6vzaymg/image/upload/v1765468054/Gemini_Generated_Image_xj3bq9xj3bq9xj3b_jlud1h.png",
            title = "",
            subtitle = "",
            buttonText = stringResource(R.string.banner_btn_shop_now),
            onButtonClick = {  }
        ),
        BannerData(
            id = 2,
            imageUrl = "https://res.cloudinary.com/dx6vzaymg/image/upload/v1765468055/Group_108_zwyhez.png",
            title = "",
            subtitle = "",
            buttonText = stringResource(R.string.banner_btn_explore),
            onButtonClick = {  }
        ),
        BannerData(
            id = 3,
            imageUrl = "https://res.cloudinary.com/dx6vzaymg/image/upload/v1765468054/Gemini_Generated_Image_a5us7ma5us7ma5us_crsjza.png",
            title = "",
            subtitle = "",
            buttonText = stringResource(R.string.banner_btn_view_deals),
            onButtonClick = {  }
        )
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
                        text = greeting,
                        color = Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.what_are_you_looking_for),
                        color = Color(0xFF344351),
                        fontSize = 14.sp
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Black
                        )
                    }
                    if (isLoggedIn && (isAdmin || isSeller)) {
                        IconActionBox(
                            onClick = onAddProductClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = "Add",
                                tint = IconDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PromotionalBannerCarousel(
                banners = banners,
                modifier = Modifier
                    .fillMaxWidth(),
                autoScrollInterval = 8000L
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(stringResource(R.string.shop_by_category))
            Spacer(modifier = Modifier.height(12.dp))
            CategoriesButtonRow(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = { categoryId ->
                    selectedCategoryId = categoryId
                    if (categoryId == 0L) {
                        viewModel.loadHomeData()
                    } else {
                        viewModel.loadCategoryProducts(categoryId)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading && state.flashDeals.isEmpty()) {
                LoadingRow()
            } else if (state.flashDeals.isNotEmpty()) {
                FlashDealsRow(
                    products = state.flashDeals.take(6),
                    favoriteIds = state.favoriteProductIds,
                    onToggleFavorite = { id, newValue ->
                        viewModel.setFavorite(id, newValue)
                    },
                    onProductClick = { productId -> onProductClick(productId) },
                    onDeleteProduct = { productId ->
                        productIdToDelete = productId
                        showDeletePopup = true
                    },
                    onEditProduct = { productId ->
                        viewModel.requestRefresh()
                        onEditProductClick(productId)
                    },
                    isAdmin = isAdmin,
                    isSeller = isSeller
                )
            } else {
                EmptyStateRow("No products available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = stringResource(R.string.latest_arrivals),
                actionText = stringResource(R.string.see_all),
                onActionClick = {  }
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
                        productIdToDelete = productId
                        showDeletePopup = true
                    },
                    onEditProduct = { productId ->
                        viewModel.requestRefresh()
                        onEditProductClick(productId)
                    },
                    isAdmin = isAdmin,
                    isSeller = isSeller
                )
            } else {
                EmptyStateRow("No products available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(12.dp))
            PopularBrandsRow(
                onBrandClick = { _ -> }
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                        productIdToDelete = productId
                        showDeletePopup = true
                    },
                    onEditProduct = { productId ->
                        viewModel.requestRefresh()
                        onEditProductClick(productId)
                    },
                    isAdmin = isAdmin,
                    isSeller = isSeller
                )
            }

            Spacer(modifier = Modifier.height(46.dp))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromotionalBannerCarousel(
    banners: List<BannerData>,
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 4000L,
    animationDuration: Int = 1200
) {
    val realPageCount = banners.size
    val infiniteCount = Int.MAX_VALUE

    val pagerState = rememberPagerState(
        initialPage = infiniteCount / 2,
        pageCount = { infiniteCount }
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(autoScrollInterval)
            pagerState.animateScrollToPage(
                pagerState.currentPage + 1,
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearEasing
                )
            )
        }
    }

    Column(modifier = modifier) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 5.dp)
        ) { page ->

            val realIndex = page % realPageCount
            BannerComponent(banner = banners[realIndex])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val current = pagerState.currentPage % realPageCount

            repeat(realPageCount) { index ->
                val isSelected = index == current
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (isSelected) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFF004CBB)
                            else Color.Black
                        )
                )
            }
        }
    }
}





@Composable
fun CategoriesButtonRow(
    categories: List<CategoryButtonData>,
    selectedCategoryId: Long?,
    onCategoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            CategoryButton(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

///

@Composable
fun LoadingRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
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
            .padding(horizontal = 10.dp),
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
            .padding(horizontal = 10.dp, vertical = 32.dp),
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
                .padding(horizontal = 10.dp)
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
                color = Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = banner.subtitle,
                color = Black.copy(alpha = 0.9f),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Black,
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
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (actionText != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = Color(0xFF004CBB),
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
        contentPadding = PaddingValues(horizontal = 10.dp),
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
    onEditProduct: (Long) -> Unit,
    isAdmin: Boolean,
    isSeller: Boolean
) {
    LazyRow(
        contentPadding = PaddingValues(end = 16.dp),
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
                onEdit = { productId ->
                    onEditProduct(productId)
                },
                isAdmin = isAdmin,
                isSeller = isSeller,
                modifier = Modifier.width(360.dp)
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
    onDeleteProduct: (Long) -> Unit,
    onEditProduct: (Long) -> Unit,
    isAdmin: Boolean,
    isSeller: Boolean
) {
    LazyRow(
        contentPadding = PaddingValues(end = 16.dp),
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
                onEdit = { productId ->
                    onEditProduct(productId)
                },
                isAdmin = isAdmin,
                isSeller = isSeller,
                modifier = Modifier.width(360.dp)
            )
        }
    }
}

@Composable
fun PopularBrandsRow(onBrandClick: (String) -> Unit) {

    val brandLogos = listOf(
        "Apple" to "https://1000logos.net/wp-content/uploads/2016/10/Apple-Logo.png",
        "Dell" to "https://1000logos.net/wp-content/uploads/2017/07/Dell-Logo.png",
        "HP" to "https://1000logos.net/wp-content/uploads/2017/02/HP-Log%D0%BE.png",
        "Lenovo" to "https://res.cloudinary.com/dx6vzaymg/image/upload/v1765473224/pngegg_xkpnok.png",
        "Asus" to "https://1000logos.net/wp-content/uploads/2016/10/Asus-Logo.png",
        "MSI" to "https://1000logos.net/wp-content/uploads/2018/10/MSI-Logo.png"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(brandLogos) { (brand, logoUrl) ->
            BrandCard(
                logoUrl = logoUrl,
                onClick = { onBrandClick(brand) }
            )
        }
    }
}


@Composable
fun BrandCard(
    logoUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp, 50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E2A38))
            .border(1.dp, Color(0xFF3A4A5A), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(logoUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.7f),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}


@Composable
fun RecommendedProductsGrid(
    products: List<Product>,
    favoriteIds: Set<Long>,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onProductClick: (Long) -> Unit,
    onDeleteProduct: (Long) -> Unit,
    onEditProduct: (Long) -> Unit,
    isAdmin: Boolean,
    isSeller: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.forEach { product ->
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
                onEdit = { productId ->
                    onEditProduct(productId)
                },
                isAdmin = isAdmin,
                isSeller = isSeller
            )
        }
    }
}
