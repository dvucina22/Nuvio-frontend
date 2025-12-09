package com.example.nuviofrontend.feature.cart.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.cart.dto.CartItemDto
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.ProductItemCard
import com.example.core.ui.theme.ButtonColorDark
import com.example.core.ui.theme.CardBorder
import com.example.core.ui.theme.CardItemBackground
import com.example.core.R
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onProductClick: (Long) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    val itemToDelete by viewModel.itemToDelete.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCart()
    }

    itemToDelete?.let { item ->
        CustomPopupWarning(
            title = stringResource(R.string.warning),
            message = stringResource(R.string.delete_item_confirm),
            onDismiss = { viewModel.dismissDeletePopup() },
            onConfirm = {
                viewModel.deleteItemFromCart(item.id)
                viewModel.dismissDeletePopup()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            CustomTopBar(
                title = stringResource(R.string.cart_title)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp)
            ) {


                if (isLoading) {
                    item {
                        Text(
                            stringResource(R.string.loading_message),
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    return@LazyColumn
                }

                if (!isLoading && cartItems.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.cart_empty_message),
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    return@LazyColumn
                }

                if (error != null) {
                    item {
                        Text(
                            "Greška: $error",
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }



                items(cartItems) { item ->
                    ProductItemCard(
                        item = item,
                        onIncrease = { viewModel.increaseQuantity(item.id) },
                        onDecrease = { viewModel.decreaseQuantity(item.id) },
                        onFavorite = { viewModel.toggleFavorite(item.id) },
                        onClick = { onProductClick(item.id.toLong()) }
                    )
                }


                item {
                    SummarySection(cartItems)
                }
                item {
                    CheckoutButton(
                        text = stringResource(R.string.checkout_button_text)
                    ) {
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
fun SummarySection(
    cartItems: List<CartItemDto>,
    deliveryCost: Double = 5.0
) {
    val itemsTotal = cartItems.sumOf { it.basePrice * it.quantity }
    val totalWithDelivery = itemsTotal + deliveryCost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .border(
                width = 0.5.dp,
                color = CardBorder,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardItemBackground)
    ) {
        Column(Modifier.padding(12.dp)) {

            SummaryItem(stringResource(R.string.summary_total), "%.2f €".format(itemsTotal))
            SummaryItem(stringResource(R.string.summary_delivery), "%.2f €".format(deliveryCost))
            SummaryItem(stringResource(R.string.summary_discount), "%.2f €".format(0.0))

            Divider(color = Color.Gray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

            SummaryItem(stringResource(R.string.summary_total_with_discount), "%.2f €".format(totalWithDelivery), bold = true)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White)
        Text(
            value,
            color = Color.White,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CheckoutButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(50.dp),
        shape = RoundedCornerShape(4.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = ButtonColorDark,
            contentColor = Color.White
        ),
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
