package com.example.nuviofrontend.feature.sale.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.R
import androidx.compose.ui.unit.sp
import com.example.core.sale.dto.SaleResponse

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionResultScreen(
    isSuccess: Boolean,
    saleResponse: SaleResponse? = null,
    errorMessage: String? = null,
    maxRetriesReached: Boolean = false,
    onContinueShopping: () -> Unit,
    onViewOrders: () -> Unit = {},
    onGoToHome: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSuccess) Color(0xFFE8F5E9)
                        else Color(0xFFFFEBEE)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.size(80.dp)
                )
            }

            Text(
                text = if (isSuccess)
                    stringResource(R.string.payment_success_title)
                else
                    stringResource(R.string.payment_failed_title),
                color = Color(0xFF1C1C1C),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (isSuccess && saleResponse != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD8D9D9))
                        .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = stringResource(R.string.transaction_details_title),
                        color = Color(0xFF1C1C1C),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    InfoRow(
                        label = stringResource(R.string.transaction_id),
                        value = "#${saleResponse.id}"
                    )
                    InfoRow(
                        label = stringResource(R.string.transaction_status),
                        value = saleResponse.status
                    )
                    InfoRow(
                        label = stringResource(R.string.transaction_amount),
                        value = "€${String.format("%.2f", saleResponse.amount / 100.0)}"
                    )
                    InfoRow(
                        label = stringResource(R.string.transaction_date),
                        formatDate(saleResponse.createdAt)
                    )
                }
            }

            Text(
                text = when {
                    isSuccess -> stringResource(R.string.payment_success_message)
                    maxRetriesReached -> stringResource(R.string.payment_failed_max_retries)
                    errorMessage != null -> errorMessage
                    else -> stringResource(R.string.payment_failed_retry_message)
                },
                color = Color(0xFF6B7280),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                if (isSuccess) {

                    Button(
                        onClick = onGoToHome,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF004CBB),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.go_to_home),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onViewOrders,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF004CBB)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF004CBB)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.view_orders),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {

                    if (maxRetriesReached) {

                        Button(
                            onClick = onGoToHome,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF004CBB),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.go_to_home),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    } else {

                        Button(
                            onClick = onContinueShopping,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF004CBB),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.try_again),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onGoToHome,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF6B7280)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.go_to_home),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
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
            color = Color(0xFF6B7280),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color(0xFF1C1C1C),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateString: String): String {
    return try {
        val instant = java.time.Instant.parse(dateString)
        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("MMM dd, yyyy HH:mm")
            .withZone(java.time.ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateString
    }
}