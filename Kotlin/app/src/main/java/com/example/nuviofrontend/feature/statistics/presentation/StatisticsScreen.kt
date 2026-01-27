package com.example.nuviofrontend.feature.statistics.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.Success
import com.example.core.ui.theme.Yellow

@Composable
fun StatisticsScreen(
    onBack: () -> Unit = {},
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomTopBar(
            title = stringResource(R.string.statistics),
            showBack = true,
            onBack = onBack
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                SummaryRow()
            }

            item {
                TransactionStatusSection()
            }

            item {
                AverageTransactionCard()
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SummaryRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AttachMoney,
            iconTint = Success,
            value = "€32,5k",
            label = stringResource(R.string.total_revenue)
        )

        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.SyncAlt,
            iconTint = AccentColor,
            value = "1 588",
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
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TransactionStatusSection() {
    StatCard(
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = stringResource(R.string.transaction_status),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusCard(
                modifier = Modifier.weight(1f),
                value = "1 247",
                icon = Icons.Outlined.CheckCircle,
                label = stringResource(R.string.successful),
                percent = "78,5%",
                color = Success
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                value = "198",
                icon = Icons.Outlined.Cancel,
                label = stringResource(R.string.unsuccessful),
                percent = "12,5%",
                color = Error
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusCard(
                modifier = Modifier.weight(1f),
                value = "89",
                icon = Icons.Outlined.Refresh,
                label = stringResource(R.string.cancelled),
                percent = "5,6%",
                color = Yellow
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                value = "54",
                label = stringResource(R.string.pending),
                icon = Icons.Outlined.HourglassEmpty,
                percent = "3,4%",
                color = AccentColor
            )
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
fun AverageTransactionCard() {
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
            text = "34,15€",
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


