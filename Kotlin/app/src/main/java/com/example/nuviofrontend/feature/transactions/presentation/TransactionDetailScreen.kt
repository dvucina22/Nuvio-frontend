package com.example.nuviofrontend.feature.transactions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.transactions.dto.TransactionDetail
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.WhiteSoft
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    onBack: () -> Unit,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(transactionId) {
        viewModel.load(transactionId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CustomTopBar(
                title = stringResource(R.string.transaction_details_title),
                showBack = true,
                onBack = onBack
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = IconSelectedTintDark)
                    }
                }

                state.data != null -> {
                    TransactionDetailContent(
                        detail = state.data!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp)
                    )
                }

                state.data == null && state.error == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_data),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    }
                }
            }
        }

        if (state.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.clearError() },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = WhiteSoft
                        )
                    ) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                }
            ) {
                Text(text = state.error ?: "")
            }
        }
    }
}

@Composable
private fun TransactionDetailContent(
    detail: TransactionDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailCard {
            DetailRow(
                label = stringResource(R.string.transaction_id),
                value = detail.id.toString()
            )
            DetailRow(
                label = stringResource(R.string.transaction_status),
                value = statusLabel(detail.status)
            )
            DetailRow(
                label = stringResource(R.string.transaction_amount),
                value = formatEuro(detail.amount)
            )
            DetailRow(
                label = stringResource(R.string.transaction_date),
                value = formatCreatedAt(detail.createdAt)
            )
        }

        DetailCard {
            DetailRow(
                label = stringResource(R.string.transaction_pan),
                value = detail.panMasked.ifBlank { stringResource(R.string.no_data) }
            )

            DetailRow(
                label = stringResource(R.string.transaction_expiration),
                value = buildExpiration(detail.cardExpirationMm, detail.cardExpirationYy)
            )

            DetailRow(
                label = stringResource(R.string.transaction_date),
                value = formatTxnDate(detail.transactionDate).ifBlank { stringResource(R.string.no_data) }
            )

            DetailRow(
                label = stringResource(R.string.transaction_time),
                value = formatTxnTime(detail.transactionTime).ifBlank { stringResource(R.string.no_data) }
            )
        }

        DetailCard {
            val productIds = detail.products.map { it.productId }
            DetailRow(
                label = stringResource(R.string.product_ids),
                value = if (productIds.isEmpty()) stringResource(R.string.no_products) else productIds.joinToString(", ")
            )

            DetailRow(
                label = stringResource(R.string.quantity),
                value = detail.products.sumOf { it.quantity }.toString()
            )
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun DetailCard(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun statusLabel(raw: String): String {
    val v = raw.lowercase().trim()
    return when (v) {
        "pending" -> stringResource(R.string.status_pending)
        "approved" -> stringResource(R.string.status_approved)
        "declined" -> stringResource(R.string.status_declined)
        "reversed" -> stringResource(R.string.status_reversed)
        "refunded" -> stringResource(R.string.status_refunded)
        else -> raw
    }
}

private fun buildExpiration(mm: String, yy: String): String {
    val m = mm.trim()
    val y = yy.trim()
    if (m.isBlank() || y.isBlank()) return ""
    return "$m/$y"
}

private fun formatCreatedAt(createdAt: String): String {
    return try {
        val instant = Instant.parse(createdAt)
        val local = instant.atZone(ZoneId.systemDefault())
        val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm", Locale.getDefault())
        fmt.format(local)
    } catch (_: Exception) {
        createdAt
    }
}

private fun formatEuro(amount: Long): String {
    return try {
        val nf = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = true
        }
        val value = amount.toDouble() / 100.0
        "${nf.format(value)}€"
    } catch (_: Exception) {
        "$amount€"
    }
}

private fun formatTxnDate(raw: String): String {
    val v = raw.trim()
    if (v.isBlank()) return ""

    val digits = v.filter { it.isDigit() }
    if (digits.length != 4) return raw

    val mm = digits.substring(0, 2)
    val dd = digits.substring(2, 4)

    return try {
        val year = LocalDate.now().year
        val date = LocalDate.of(year, mm.toInt(), dd.toInt())
        val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
        fmt.format(date)
    } catch (_: Exception) {
        "$dd.$mm"
    }
}

private fun formatTxnTime(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    if (digits.isBlank()) return ""

    val padded = digits.padStart(6, '0').takeLast(6)

    val hh = padded.substring(0, 2)
    val mm = padded.substring(2, 4)
    val ss = padded.substring(4, 6)

    return "$hh:$mm:$ss"
}
