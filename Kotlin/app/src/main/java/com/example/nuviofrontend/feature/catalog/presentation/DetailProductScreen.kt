package com.example.nuviofrontend.feature.catalog.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.CardItemBackground
import com.example.nuviofrontend.feature.cart.presentation.CartViewModel
import com.example.nuviofrontend.feature.cart.presentation.SummaryItem


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

        Column(modifier = Modifier.fillMaxSize()) {

            CustomTopBar(
                title = stringResource(R.string.product_details_title),
                showBack = true,
                onBack = onBack
            )

            if (loading) {
                Text("Učitavanje...", color = Color.White)
                return
            }
            if (error != null) {
                Text("Greška: $error", color = Color.Red)
                return
            }

            product?.let { p ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    AsyncImage(
                        model = p.imageUrl.takeIf { it.isNotEmpty() } ?: R.drawable.random_laptop,
                        contentDescription = p.name,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.random_laptop),
                        error = painterResource(id = R.drawable.random_laptop)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        Column {
                            Text(
                                text = formatProductName(p.name),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Brand: ${p.brand.name}", color = Color.White)
                            Text("Kategorija: ${p.category.name}", color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Cijena: ${p.basePrice} €",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                cartViewModel.increaseQuantity(p.id.toInt())
                                Toast.makeText(
                                    context,
                                    "Uspješno dodano u košaricu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Dodaj u košaricu",
                                tint = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = CardItemBackground),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.attributes_title),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        p.attributes?.forEach { attr ->
                            SummaryItem(
                                label = mapAttributeName(attr.name),
                                value = mapAttributeValue(attr.name, attr.value),
                                bold = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardItemBackground),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Opis:", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text(p.description ?: "Nema opisa.", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun formatProductName(name: String): String {
    return name.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
}

private fun mapAttributeName(name: String): String {
    return when (name.lowercase()) {
        "display_size" -> "Veličina ekrana"
        "display_resolution" -> "Rezolucija"
        "color" -> "Boja"
        "os" -> "Operativni sustav"
        "build_material" -> "Materijal kućišta"
        "weight_kg" -> "Težina"
        "battery_wh" -> "Baterija"
        else -> name.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

private fun mapAttributeValue(attribute: String, value: String): String {
    return when (attribute.lowercase()) {
        "display_size", "weight_kg" -> value.replace("_", ",") + if (attribute.lowercase() == "weight_kg") " kg" else ""
        "color" -> value.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        "battery_wh" -> "$value Wh"
        else -> value.replace("_", " ")
    }
}
