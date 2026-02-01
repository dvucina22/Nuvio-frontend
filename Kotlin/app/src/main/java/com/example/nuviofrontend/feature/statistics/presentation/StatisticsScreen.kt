package com.example.nuviofrontend.feature.statistics.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.settings.CurrencyConverter
import com.example.core.statistics.dto.RecentTransaction
import com.example.core.statistics.dto.TransactionStatisticsData
import com.example.core.statistics.dto.TransactionStatusBreakdown
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.Success
import com.example.core.ui.theme.Yellow
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    settingsViewModel: SettingsViewModel = hiltViewModel()
){
    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val state by viewModel.state.collectAsState()

        val selectedCurrencyState = settingsViewModel.currencyFlow.collectAsState(initial = 1)
        val selectedCurrency = selectedCurrencyState.value

        val context = LocalContext.current

        LaunchedEffect(state.pdfUri) {
            val uri = state.pdfUri ?: return@LaunchedEffect

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(Intent.createChooser(intent, "Open PDF"))
            } catch (e: Exception) {
                Toast.makeText(context, "No PDF viewer installed.", Toast.LENGTH_LONG).show()
            }

            viewModel.clearPdfResult()
        }

        LaunchedEffect(state.pdfError) {
            val err = state.pdfError ?: return@LaunchedEffect
            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            viewModel.clearPdfResult()
        }

        CustomTopBar(
            title = stringResource(R.string.statistics),
            showBack = true,
            onBack = onBack
        )
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Text("Error: ${state.error}")
            }
            state.statistics != null -> {
                val stats = state.statistics!!
                StatisticsContent(
                    data = stats.data,
                    currencyIndex = selectedCurrency,
                    isGeneratingPdf = state.isGeneratingPdf,
                    onGeneratePdf = { viewModel.generatePdfReport(currencyIndex = selectedCurrency) }
                )
            }
        }
    }
}

@Composable
fun StatisticsContent(
    data: TransactionStatisticsData,
    currencyIndex: Int,
    isGeneratingPdf: Boolean,
    onGeneratePdf: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryRow(
                totalRevenue = data.totalRevenue,
                totalTransactions = data.totalTransactions,
                currencyIndex = currencyIndex
            )
        }

        item {
            TransactionStatusSection(data.statusBreakdown)
        }

        item {
            AverageTransactionCard(data.averageTransactionValue,  currencyIndex = currencyIndex)
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PdfReportSection(
                    isGenerating = isGeneratingPdf,
                    onGenerate = onGeneratePdf
                )
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun PdfReportSection(
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    CustomButton(
        text = stringResource(R.string.generate_pdf_report),
        onClick = onGenerate
    )
}

@Composable
fun SummaryRow(totalRevenue: Double, totalTransactions: Int, currencyIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AttachMoney,
            iconTint = Success,
            value = CurrencyConverter.convertPrice(totalRevenue, currencyIndex),
            label = stringResource(R.string.total_revenue)
        )

        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.SyncAlt,
            iconTint = AccentColor,
            value = totalTransactions.toString(),
            label = stringResource(R.string.total_transactions)
        )
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    StatCard(modifier = modifier) {

        Icon(icon, null, tint = iconTint)

        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TransactionStatusSection(statusList: List<TransactionStatusBreakdown>) {
    StatCard(
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = stringResource(R.string.transaction_status),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        val rows = statusList.chunked(2)
        rows.forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { status ->
                    val color = when(status.status) {
                        "APPROVED" -> Success
                        "DECLINED" -> Error
                        "PENDING" -> AccentColor
                        "VOIDED" -> Yellow
                        else -> MaterialTheme.colorScheme.onBackground
                    }
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        value = status.count.toString(),
                        icon = when(status.status) {
                            "APPROVED" -> Icons.Outlined.CheckCircle
                            "DECLINED" -> Icons.Outlined.Cancel
                            "PENDING" -> Icons.Outlined.HourglassEmpty
                            "VOIDED" -> Icons.Outlined.Refresh
                            else -> Icons.Outlined.HourglassEmpty
                        },
                        label = statusToString(status.status),
                        percent = "%.2f%%".format(status.percentage),
                        color = color
                    )
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    percent: String,
    color: Color
) {
    StatCard(modifier = modifier) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                percent,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun AverageTransactionCard(avg: Double, currencyIndex: Int) {
    StatCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = stringResource(R.string.average_transaction),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = CurrencyConverter.convertPrice(avg, currencyIndex),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceDim
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun statusToString(status: String): String {
    return when(status.uppercase()) {
        "APPROVED" -> stringResource(R.string.status_approved)
        "DECLINED" -> stringResource(R.string.status_declined)
        "VOIDED" -> stringResource(R.string.status_voided)
        "PENDING" -> stringResource(R.string.status_pending)
        else -> status
    }
}
