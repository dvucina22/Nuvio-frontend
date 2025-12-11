package com.example.nuviofrontend.feature.catalog.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.presentation.AuthViewModel
import com.example.core.R
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.ProductImageCarousel
import com.example.core.ui.components.TopBarDetailProducts
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.CardItemBackground
import com.example.core.ui.theme.White
import com.example.nuviofrontend.feature.cart.presentation.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailProductScreen(
    viewModel: DetailProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    managementViewModel: ProductManagementViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEditNavigate: (Long) -> Unit
) {
    val product by viewModel.product.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val isLoggedIn = authState.isLoggedIn
    val isAdmin = authState.isAdmin
    val isSeller = authState.isSeller

    var showDeletePopup by remember { mutableStateOf(false) }
    var productIdToDelete by remember { mutableStateOf<Long?>(null) }

    val isScreenActive by remember { mutableStateOf(true) }

    LaunchedEffect(isScreenActive) {
        if (isScreenActive) {
            viewModel.loadProduct(viewModel.productId)
        }
    }

    LaunchedEffect(Unit) {
        managementViewModel.productDeleted.collectLatest {
            homeViewModel.requestRefresh()
            Toast.makeText(
                context,
                context.getString(R.string.toast_product_deleted),
                Toast.LENGTH_SHORT
            ).show()
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 55.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopBarDetailProducts(
                    onBack = { onBack() },
                    onFavoriteClick = {
                        viewModel.toggleFavorite()
                    },
                    onCartClick = {
                        product?.let { p ->
                            cartViewModel.increaseQuantity(p.id.toInt())
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_added_to_cart),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    isFavorite = product?.isFavorite ?: false,
                    showDelete = isAdmin || isSeller,
                    showEdit = isAdmin || isSeller,
                    onDeleteClick = {
                        product?.let { p ->
                            productIdToDelete = p.id
                            showDeletePopup = true
                            homeViewModel.requestRefresh()
                        }
                    },
                    onEditClick = {
                        product?.let { p ->
                            onEditNavigate(p.id)
                        }
                    },
                    showFavorite = isLoggedIn,
                    showCart = isLoggedIn
                )
            }

            if (loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                return@LazyColumn
            }

            if (error != null) {
                item {
                    Text(
                        text = stringResource(R.string.error_label, error ?: ""),
                        color = Color.Red
                    )
                }
                return@LazyColumn
            }

            product?.let { p ->
                item {
                    ProductImageCarousel(
                        images = p.images
                            ?.map { it.url }
                            .orEmpty()
                            .ifEmpty {
                                listOf(
                                    "android.resource://com.example.nuviofrontend/${R.drawable.random_laptop}"
                                )
                            }
                    )
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatProductName(p.name),
                            style = AppTypography.displayLarge,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        Card(
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = CardItemBackground),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${"%.2f".format(p.basePrice)}€",
                                    color = White,
                                    style = AppTypography.displayLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp
                                    ),
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardItemBackground)
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.product_info),
                                color = White,
                                style = AppTypography.displayLarge.copy(fontSize = 16.sp),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            Divider(
                                color = BackgroundNavDark,
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                InfoAboutProd(
                                    label = stringResource(R.string.brand),
                                    value = p.brand.name.split(" ")
                                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                                )

                                InfoAboutProd(
                                    label = stringResource(R.string.category),
                                    value = p.category.name
                                        .replace("_", " ")
                                        .split(" ")
                                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                                )

                                p.attributes?.forEach { attr ->
                                    InfoAboutProd(
                                        label = mapAttributeName(attr.name),
                                        value = mapAttributeValue(attr.name, attr.value),
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardItemBackground)
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.product_description),
                                color = White,
                                style = AppTypography.displayLarge.copy(fontSize = 16.sp),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            Divider(
                                color = BackgroundNavDark,
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                p.description ?: stringResource(R.string.no_description),
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }

        if (showDeletePopup && productIdToDelete != null) {
            CustomPopupWarning(
                title = stringResource(R.string.warning),
                message = stringResource(R.string.delete_item_confirm),
                confirmText = stringResource(R.string.delete),
                dismissText = stringResource(R.string.cancel),
                onDismiss = {
                    showDeletePopup = false
                    productIdToDelete = null
                },
                onConfirm = {
                    productIdToDelete?.let { id ->
                        managementViewModel.deleteProduct(id)
                    }
                    showDeletePopup = false
                    productIdToDelete = null
                    homeViewModel.requestRefresh()
                }
            )
        }
    }
}

private fun formatProductName(name: String): String {
    return name.replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}

@Composable
private fun mapAttributeName(name: String): String {
    return when (name.lowercase()) {
        "display_size" -> stringResource(R.string.attribute_display_size)
        "display_resolution" -> stringResource(R.string.attribute_display_resolution)
        "color" -> stringResource(R.string.attribute_color)
        "os" -> stringResource(R.string.attribute_os)
        "build_material" -> stringResource(R.string.attribute_build_material)
        "weight_kg" -> stringResource(R.string.attribute_weight)
        "battery_wh" -> stringResource(R.string.attribute_battery)
        else -> name.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

private fun mapAttributeValue(attribute: String, value: String): String {
    return when (attribute.lowercase()) {
        "display_size", "weight_kg" -> value.replace("_", ",") +
                if (attribute.lowercase() == "weight_kg") " kg" else ""
        "color" -> when (value.lowercase()) {
            "silver" -> "Srebrna"
            "space_gray" -> "Space Gray"
            "midnight" -> "Midnight"
            "black" -> "Crna"
            "platinum" -> "Platinum"
            else -> value.replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
        "build_material" -> when (value.lowercase()) {
            "aluminium" -> "Aluminij"
            "carbon_fiber" -> "Karbon"
            "plastic" -> "Plastika"
            else -> value.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
        "battery_wh" -> "$value Wh"
        "os" -> when (value.lowercase()) {
            "windows_11" -> "Windows 11"
            "macos" -> "macOS"
            else -> value.replace("_", " ").replaceFirstChar { it.uppercase() }
        }
        else -> value.replace("_", " ")
    }
}

@Composable
fun InfoAboutProd(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.White,
            style = AppTypography.titleSmall.copy(fontSize = 16.sp)
        )
        Text(
            value,
            color = Color.White,
            style = AppTypography.titleSmall.copy(fontSize = 16.sp)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}
