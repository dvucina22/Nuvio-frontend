package com.example.nuviofrontend.feature.sale.presentation

import androidx.compose.foundation.Image
import kotlin.collections.isNotEmpty
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.cards.dto.CardDto
import com.example.core.model.UserProfile
import com.example.core.sale.dto.SaleResponse
import com.example.nuviofrontend.feature.profile.presentation.CardNumberVisualTransformation
import androidx.compose.ui.res.stringResource
import com.example.core.ui.components.CustomTextField
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.AccentColor

@Composable
fun CheckoutScreen(
    viewModel: SaleViewModel = hiltViewModel(),
    onPaymentSuccess: (SaleResponse) -> Unit,
    onPaymentFailure: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val checkoutResult by viewModel.checkoutResult.collectAsState()

    LaunchedEffect(checkoutResult) {
        when (val result = checkoutResult) {
            is CheckoutResult.Success -> onPaymentSuccess(result.response)
            is CheckoutResult.Error -> onPaymentFailure(result.message)
            null -> {}
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomTopBar(
                title = stringResource(R.string.checkout_title),
                showBack = true,
                onBack = onBackClick
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp)
            ) {
                Text(
                    text = stringResource(R.string.checkout_subtitle),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF004CBB))
                        }
                    }
                    return@LazyColumn
                }

                item {
                    CartPreviewSection(cartItems = state.cartItems)
                }

                item {
                    UserInfoSection(user = state.user)
                }

                item {
                    PaymentMethodSection(
                        cards = state.cards,
                        selectedCard = state.selectedCard,
                        manualCardNumber = state.manualCardNumber,
                        manualExpiryMonth = state.manualExpiryMonth,
                        manualExpiryYear = state.manualExpiryYear,
                        manualFullName = state.manualFullName,
                        cardNumberError = state.cardNumberError,
                        expiryMonthError = state.expiryMonthError,
                        expiryYearError = state.expiryYearError,
                        fullNameError = state.fullNameError,
                        useNewCard = state.useNewCard,
                        onCardSelected = { viewModel.selectCard(it) },
                        onManualCardNumberChanged = { viewModel.updateManualCardNumber(it) },
                        onManualExpiryMonthChanged = { viewModel.updateManualExpiryMonth(it) },
                        onManualExpiryYearChanged = { viewModel.updateManualExpiryYear(it) },
                        onManualFullNameChanged = { viewModel.updateManualFullName(it) },
                        onToggleUseNewCard = { viewModel.setUseNewCard(!state.useNewCard) }
                    )
                }

                item {
                    val total = state.cartItems.sumOf { it.basePrice * it.quantity }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.total),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "€${String.format("%.2f", total)}",
                                color = AccentColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.processSale() },
                            enabled = !state.isProcessing && state.cartItems.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentColor,
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFFCACACA),
                                disabledContentColor = Color(0xFF6B7280)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            if (state.isProcessing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.pay_button),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
        state.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text(stringResource(id = R.string.dismiss))
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}


@Composable
fun CartPreviewSection(cartItems: List<com.example.core.cart.dto.CartItemDto>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, color = MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.order_summary, cartItems.size),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = item.imageUrl?.takeIf { it.isNotEmpty() } ?: R.drawable.random_laptop,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (item.quantity > 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF004CBB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${item.quantity}",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoSection(user: UserProfile?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, color = MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.billing_information),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (user != null) {
            InfoRow(stringResource(R.string.info_name), "${user.firstName} ${user.lastName}")
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(stringResource(R.string.info_email), user.email)
            if (user.phoneNumber.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(stringResource(R.string.info_phone), user.phoneNumber)
            }
        } else {
            Text(
                text = stringResource(R.string.user_info_not_available),
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PaymentMethodSection(
    cards: List<CardDto>,
    selectedCard: CardDto?,
    manualCardNumber: String,
    manualExpiryMonth: String,
    manualExpiryYear: String,
    manualFullName: String,
    cardNumberError: String?,
    expiryMonthError: String?,
    expiryYearError: String?,
    fullNameError: String?,
    useNewCard: Boolean,
    onToggleUseNewCard: () -> Unit,
    onCardSelected: (CardDto) -> Unit,
    onManualCardNumberChanged: (String) -> Unit,
    onManualExpiryMonthChanged: (String) -> Unit,
    onManualExpiryYearChanged: (String) -> Unit,
    onManualFullNameChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, color = MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.payment_method),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (cards.isNotEmpty()) {
                TextButton(
                    onClick = onToggleUseNewCard,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AccentColor
                    )
                ) {
                    Icon(
                        imageVector = if (useNewCard) Icons.Default.CreditCard else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (useNewCard) stringResource(R.string.payment_use_saved_card) else stringResource(R.string.payment_new_card),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (!useNewCard && cards.isNotEmpty()) {
            Text(
                text = stringResource(R.string.payment_select_saved_card),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                cards.forEach { card ->
                    SavedCardItem(
                        card = card,
                        isSelected = card == selectedCard,
                        onClick = { onCardSelected(card) }
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.payment_enter_card_details),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            CustomTextField(
                value = manualCardNumber,
                onValueChange = onManualCardNumberChanged,
                placeholder = stringResource(R.string.card_number),
                label = stringResource(R.string.card_number),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CardNumberVisualTransformation(),
                isError = cardNumberError != null,
                errorMessage = cardNumberError
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                CustomTextField(
                    value = manualExpiryMonth,
                    onValueChange = onManualExpiryMonthChanged,
                    placeholder = stringResource(R.string.expiry_mm),
                    label = stringResource(R.string.expiry_mm),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = expiryMonthError != null,
                    errorMessage = expiryMonthError,
                    modifier = Modifier.weight(1f)
                )

                CustomTextField(
                    value = manualExpiryYear,
                    onValueChange = onManualExpiryYearChanged,
                    placeholder = stringResource(R.string.expiry_yy),
                    label = stringResource(R.string.expiry_yy),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = expiryYearError != null,
                    errorMessage = expiryYearError,
                    modifier = Modifier.weight(1f)
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                value = manualFullName,
                onValueChange = onManualFullNameChanged,
                placeholder = stringResource(R.string.full_name_on_card),
                label = stringResource(R.string.full_name_on_card),
                isError = fullNameError != null,
                errorMessage = fullNameError
            )

        }
    }
}

@Composable
fun SavedCardItem(
    card: CardDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentColor else MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val cardIcon = when (card.cardBrand.uppercase()) {
                "VISA" -> R.drawable.visa_logo
                "MASTERCARD" -> R.drawable.mastercard_logo
                else -> R.drawable.add_new_card
            }

            Image(
                painter = painterResource(id = cardIcon),
                contentDescription = card.cardBrand,
                modifier = Modifier.size(40.dp)
            )

            Column {
                Text(
                    text = card.cardName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "**** **** **** ${card.lastFourDigits}",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp
                )
                Text(
                    text = stringResource(
                        id = R.string.card_expires,
                        card.expirationMonth,
                        card.expirationYear
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 11.sp
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = AccentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}