package com.example.nuviofrontend.feature.cart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.auth.presentation.AuthViewModel
import com.example.core.cart.dto.CartItemDto
import com.example.core.settings.CurrencyConverter
import com.example.core.ui.components.CartProductCard
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.Error
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onProductClick: (Long) -> Unit,
    onNavigateToCheckout: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val itemToDelete by viewModel.itemToDelete.collectAsState()

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val isLoggedIn = authState.isLoggedIn

    val selectedCurrencyState = settingsViewModel.currencyFlow.collectAsState(initial = 1)
    val selectedCurrency = selectedCurrencyState.value

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.fetchCart()
        }
    }

    if (isLoggedIn) {
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
    }

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
                        start = 20.dp,
                        end = 20.dp,
                        top = 26.dp,
                        bottom = 13.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.cart_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isLoggedIn && cartItems.isNotEmpty()) "${cartItems.size} items" else stringResource(R.string.shopping_cart_without_products),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoggedIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.cart_login_required),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = AccentColor)
                            }
                        }
                        return@LazyColumn
                    }

                    if (!isLoading && cartItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.cart_empty_message),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        return@LazyColumn
                    }

                    if (error != null) {
                        item {
                            Text(
                                text = "Error: $error",
                                color = Error,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    items(cartItems) { item ->
                        CartProductCard(
                            item = item,
                            selectedCurrency = selectedCurrency,
                            onIncrease = { viewModel.increaseQuantity(item.id) },
                            onDecrease = { viewModel.decreaseQuantity(item.id) },
                            onDelete = { viewModel.showDeleteConfirmation(item.id) },
                            onClick = { onProductClick(item.id.toLong()) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SummarySection(cartItems = cartItems, settingsViewModel = settingsViewModel)
                    }

                    item {
                        CheckoutButton(
                            text = stringResource(R.string.checkout_button_text),
                            enabled = cartItems.isNotEmpty()
                        ) {
                            onNavigateToCheckout()
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SummarySection(
    cartItems: List<CartItemDto>,
    deliveryCost: Double = 5.0,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val itemsTotal = cartItems.sumOf { it.basePrice * it.quantity }
    val totalWithDelivery = itemsTotal + deliveryCost

    val selectedCurrency by settingsViewModel.currencyFlow.collectAsState(initial = 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(Modifier.padding(16.dp)) {
            SummaryItem(
                stringResource(R.string.summary_total),
                CurrencyConverter.convertPrice(itemsTotal, selectedCurrency)
            )

            Spacer(modifier = Modifier.height(8.dp))

            SummaryItem(
                stringResource(R.string.summary_delivery),
                CurrencyConverter.convertPrice(deliveryCost, selectedCurrency)
            )

            Spacer(modifier = Modifier.height(8.dp))

            SummaryItem(
                stringResource(R.string.summary_discount),
                CurrencyConverter.convertPrice(0.0, selectedCurrency)
            )

            Divider(
                color = BackgroundNavDark,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SummaryItem(
                stringResource(R.string.summary_total_with_discount),
                CurrencyConverter.convertPrice(totalWithDelivery, selectedCurrency),
                bold = true
            )
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            color = AccentColor,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
fun CheckoutButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentColor,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFCACACA),
            disabledContentColor = Color(0xFF6B7280)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}