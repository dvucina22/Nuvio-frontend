package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.cart.dto.CartItemDto
import com.example.core.settings.CurrencyConverter
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.Error

@Composable
fun CartProductCard(
    item: CartItemDto,
    selectedCurrency: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF000000).copy(alpha = 0.03f),
                spotColor = Color(0xFF000000).copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .wrapContentHeight()
            ) {
                AsyncImage(
                    model = item.imageUrl?.takeIf { it.isNotEmpty() } ?: R.drawable.random_laptop,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = item.category,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(top = 14.dp, bottom = 8.dp, start = 14.dp, end = 14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.name
                                .replace("_", " ")
                                .split(" ")
                                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val orderedAttributes = getOrderedAttributes(
                        item.attributes.map { CartItemAttribute(it.name, it.value) }
                    ).take(3)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        orderedAttributes.forEachIndexed { index, attr ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AttributeIcon(attr.name)

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = formatAttributeValue(attr.name, attr.value),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (index != orderedAttributes.lastIndex) {
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = CurrencyConverter.convertPrice(
                                item.basePrice * item.quantity,
                                selectedCurrency
                            ),
                            color = AccentColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onDecrease() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_remove),
                                    contentDescription = "Decrease",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Text(
                                text = "${item.quantity}",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(24.dp),
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(AccentColor)
                                    .clickable { onIncrease() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

interface Attribute {
    val name: String
    val value: String
}

data class CartItemAttribute(
    override val name: String,
    override val value: String
) : Attribute

@Composable
private fun AttributeIcon(name: String) {
    val iconRes = when (name.lowercase()) {
        "battery_wh" -> R.drawable.ic_battery
        "display_size" -> R.drawable.ic_display
        "weight_kg" -> R.drawable.ic_weight
        "ram" -> R.drawable.ic_ram
        "storage" -> R.drawable.ic_storage
        "processor" -> R.drawable.ic_processor
        "gpu" -> R.drawable.ic_gpu
        "os" -> R.drawable.ic_os
        else -> R.drawable.ic_info
    }

    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier.size(14.dp),
        tint = MaterialTheme.colorScheme.onBackground
    )
}

private fun <T : Attribute> getOrderedAttributes(attributes: List<T>): List<T> {
    val attributeOrder = mapOf(
        "display_size" to 1,
        "battery_wh" to 2,
        "weight_kg" to 3,
        "ram" to 4,
        "storage" to 5,
        "processor" to 6,
        "gpu" to 7,
        "os" to 8
    )

    return attributes
        .filter { it.name.lowercase() != "color" }
        .sortedBy { attributeOrder[it.name.lowercase()] ?: 999 }
}

private fun formatAttributeValue(attrName: String, attrValue: String): String {
    return when(attrName.lowercase()) {
        "display_size" -> "${attrValue.replace("_", ",")}″"
        "battery_wh" -> "${attrValue.replace("_", ",")} Wh"
        "weight_kg" -> "${attrValue.replace("_", ",")} kg"
        "ram" -> "$attrValue GB"
        "storage" -> "$attrValue GB"
        "processor" -> "${attrValue.replace("_", " ")}"
        "gpu" -> "${attrValue.replace("_", " ")}"
        "os" -> "${attrValue.replace("_", " ")}"
        else -> "${attrName.replace("_", " ").replaceFirstChar { it.uppercase() }}: ${attrValue.replace("_", " ")}"
    }
}