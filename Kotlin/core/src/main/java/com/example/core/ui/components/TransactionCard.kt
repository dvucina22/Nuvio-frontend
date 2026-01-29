package com.example.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.core.R
import com.example.core.transactions.dto.TransactionListItem
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.DarkRed
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.Success
import com.example.core.ui.theme.Yellow
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class TransactionStatusUi(
    val raw: String,
    @StringRes val labelRes: Int,
    val pillBg: @Composable () -> Color,
    val pillText: @Composable () -> Color,
    val pillBorder: @Composable () -> Color
) {
    Pending(
        raw = "PENDING",
        labelRes = R.string.status_pending,
        pillBg = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f) },
        pillText = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f) },
        pillBorder = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.14f) }
    ),
    Approved(
        raw = "APPROVED",
        labelRes = R.string.status_approved,
        pillBg = { Success.copy(alpha = 0.14f) },
        pillText = { Success },
        pillBorder = { Success.copy(alpha = 0.22f) }
    ),
    Declined(
        raw = "DECLINED",
        labelRes = R.string.status_declined,
        pillBg = { Error.copy(alpha = 0.14f) },
        pillText = { DarkRed },
        pillBorder = { Error.copy(alpha = 0.22f) }
    ),
    Voided(
        raw = "VOIDED",
        labelRes = R.string.status_voided,
        pillBg = { Yellow.copy(alpha = 0.10f) },
        pillText = { Yellow },
        pillBorder = { Yellow.copy(alpha = 0.14f) }
    ),
    Reversed(
        raw = "REVERSED",
        labelRes = R.string.status_reversed,
        pillBg = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f) },
        pillText = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f) },
        pillBorder = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.14f) }
    ),
    Refunded(
        raw = "REFUNDED",
        labelRes = R.string.status_refunded,
        pillBg = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f) },
        pillText = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f) },
        pillBorder = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.14f) }
    ),
    Unknown(
        raw = "UNKNOWN",
        labelRes = R.string.status_unknown,
        pillBg = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f) },
        pillText = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f) },
        pillBorder = { MaterialTheme.colorScheme.onBackground.copy(alpha = 0.14f) }
    );

    companion object {
        fun from(status: String?): TransactionStatusUi {
            val s = status?.trim()?.uppercase() ?: ""
            return entries.firstOrNull { it.raw == s } ?: Unknown
        }
    }
}

@Composable
fun TransactionCard(
    transaction: TransactionListItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onEdit: (Long) -> Unit = {},
    onDelete: (Long) -> Unit = {},
    showMenu: Boolean = false,
    isAdmin: Boolean = false,
    onVoidClick: (Long) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    val statusUi = TransactionStatusUi.from(transaction.status)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0xFF000000).copy(alpha = 0.08f),
                spotColor = Color(0xFF000000).copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(
                        text = "TRX-${transaction.id}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = formatCreatedAt(transaction.createdAt),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isAdmin && transaction.status != "VOIDED" && transaction.status != "DECLINED") {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(1.dp, AccentColor, shape = CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceDim)
                                .clickable { onVoidClick(transaction.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Void transaction",
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                        }
                    }
                    StatusPill(
                        label = stringResource(statusUi.labelRes),
                        bg = statusUi.pillBg(),
                        textColor = statusUi.pillText(),
                        borderColor = statusUi.pillBorder()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceDim,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🧾",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transaction.panMasked,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${transaction.productCount} ${stringResource(R.string.products)}",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceDim)
            ) {}

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.80f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formatEuro(transaction.amount),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (menuOpen) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { menuOpen = false }
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 10.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceDim,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp)
                        .width(170.dp)
                ) {
                    MenuItem(
                        icon = Icons.Default.Edit,
                        label = stringResource(R.string.edit_button)
                    ) {
                        menuOpen = false
                        onEdit(transaction.id)
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim)
                    )

                    MenuItem(
                        icon = Icons.Default.Delete,
                        label = stringResource(R.string.delete),
                        isDestructive = true
                    ) {
                        menuOpen = false
                        onDelete(transaction.id)
                    }
                }
            }
        }

    }
}

@Composable
private fun StatusPill(
    label: String,
    bg: Color,
    textColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
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
