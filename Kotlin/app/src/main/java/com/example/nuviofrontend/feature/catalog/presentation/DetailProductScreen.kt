package com.example.nuviofrontend.feature.catalog.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.presentation.AuthViewModel
import com.example.core.R
import com.example.core.settings.CurrencyConverter
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.ProductImageCarousel
import com.example.core.ui.components.TopBarDetailProducts
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.BackgroundNavDark
import com.example.nuviofrontend.feature.cart.presentation.CartViewModel
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailProductScreen(
    viewModel: DetailProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    managementViewModel: ProductManagementViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEditNavigate: (Long) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
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

    val selectedCurrencyState = settingsViewModel.currencyFlow.collectAsState(initial = 1)
    val selectedCurrency = selectedCurrencyState.value

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
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 105.dp)
                .padding(horizontal = 20.dp)
                .padding(bottom = 55.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )

                        Card(
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = CurrencyConverter.convertPrice(p.basePrice, selectedCurrency),
                                    color = AccentColor,
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
                    InfoCardContainer {
                        Text(
                            text = stringResource(R.string.product_info),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = AppTypography.displayLarge.copy(fontSize = 16.sp)
                        )

                        Divider(
                            color = BackgroundNavDark,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        InfoAboutProd(
                            label = stringResource(R.string.brand),
                            value = p.brand.name
                        )

                        InfoAboutProd(
                            label = stringResource(R.string.category),
                            value = p.category.name
                        )

                        p.attributes?.forEach { attr ->
                            InfoAboutProd(
                                label = mapAttributeName(attr.name),
                                value = mapAttributeValue(attr.name, attr.value)
                            )
                        }
                    }
                }

                item {
                    InfoCardContainer {
                        Text(
                            stringResource(R.string.product_description),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = AppTypography.displayLarge.copy(fontSize = 16.sp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Divider(
                            color = BackgroundNavDark,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            p.description ?: stringResource(R.string.no_description),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(55.dp)) }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = AppTypography.titleSmall.copy(fontSize = 15.sp),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            style = AppTypography.titleSmall.copy(fontSize = 15.sp),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun InfoCardContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}


