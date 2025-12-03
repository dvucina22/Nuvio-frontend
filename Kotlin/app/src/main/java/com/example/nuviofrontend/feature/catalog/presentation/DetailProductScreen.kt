package com.example.nuviofrontend.feature.catalog.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.components.TopBarDetailProducts
import com.example.core.ui.theme.CardItemBackground
import com.example.nuviofrontend.feature.cart.presentation.CartViewModel
import com.example.core.R
import com.example.core.ui.components.ProductImageCarousel
import com.example.core.ui.theme.AppTypography
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.White


@Composable
fun DetailProductScreen(
    viewModel: DetailProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val product by viewModel.product.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

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
                        product?.let { p -> cartViewModel.toggleFavorite(p.id.toInt()) }
                    },
                    onCartClick = {
                        product?.let { p ->
                            cartViewModel.increaseQuantity(p.id.toInt())
                            Toast.makeText(context, "Uspješno dodano u košaricu", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
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
                    Text("Greška: $error", color = Color.Red)
                }
                return@LazyColumn
            }

            product?.let { p ->
                item {
                    ProductImageCarousel(
                        images = p.images
                            ?.map { it.url }
                            .orEmpty()
                            .ifEmpty { listOf("android.resource://com.example.nuviofrontend/${R.drawable.random_laptop}") }
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
                                    text = "${p.basePrice}€",
                                    color = White,
                                    style = AppTypography.displayLarge.copy(fontWeight = FontWeight.Medium, fontSize = 18.sp),
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
                        Column() {
                            Text(
                                stringResource(R.string.product_info),
                                color = White,
                                style = AppTypography.displayLarge.copy(fontSize = 16.sp),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            Divider(
                                color = BackgroundNavDark,
                                thickness = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
                        Column() {
                            Text(
                                stringResource(R.string.product_description),
                                color = White,
                                style = AppTypography.displayLarge.copy(fontSize = 16.sp),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            Divider(
                                color = BackgroundNavDark,
                                thickness = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Text(
                                p.description ?: "Nema opisa",
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }
}

private fun formatProductName(name: String): String {
    return name.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
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
        "display_size", "weight_kg" -> value.replace("_", ",") + if (attribute.lowercase() == "weight_kg") " kg" else ""
        "color" -> when (value.lowercase()) {
            "silver" -> "Srebrna"
            "space_gray" -> "Space Gray"
            "midnight" -> "Midnight"
            "black" -> "Crna"
            "platinum" -> "Platinum"
            else -> value.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
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
}
