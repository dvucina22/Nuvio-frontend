package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.cart.dto.CartItemDto
import com.example.core.ui.theme.CardBorder
import com.example.core.ui.theme.CardItemBackground
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.QuantityBackground
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.joinToString
import kotlin.collections.take
import kotlin.takeIf
import kotlin.text.all
import kotlin.text.isDigit
import kotlin.text.isNotEmpty
import kotlin.text.lowercase
import kotlin.text.replace

@Composable
fun ProductItemCard(
    item: CartItemDto,
    onIncrease: (() -> Unit)? = null,
    onDecrease: (() -> Unit)? = null,
    onFavorite: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .border(
                width = 0.5.dp,
                color = CardBorder,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardItemBackground
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(150.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl?.takeIf { it.isNotEmpty() } ?: R.drawable.random_laptop,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(150.dp)
                        .align(Alignment.CenterStart)
                        .clip(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                bottomStart = 8.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            )
                        ),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.random_laptop),
                    error = painterResource(id = R.drawable.random_laptop)
                )
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(
                            CardItemBackground,
                            androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = item.category,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }

                val colorAttr = item.attributes.find { it.name == "color" }?.value ?: "gray"
                val colorValue = when (colorAttr.lowercase()) {
                    "black" -> Color.Black
                    "white" -> Color.White
                    "silver" -> Color.LightGray
                    "red" -> Color.Red
                    "blue" -> Color.Blue
                    "green" -> Color.Green
                    else -> Color.Gray
                }
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            CardItemBackground,
                            androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("boja: ", color = Color.White, fontSize = 10.sp)
                        Spacer(Modifier.width(2.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                                .background(colorValue)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 150.dp)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.name
                                .replace("_", " ")
                                .split(" ")
                                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                            color = Color.White,
                            fontSize = 16.sp,
                            maxLines = 2,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.SemiBold,
                            overflow = Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                                .background(QuantityBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { onFavorite() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    tint = if (item.isFavorite) Error else Color.White,
                                    contentDescription = "favorite",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val displayAttributes = item.attributes.take(4)
                        val attributeText = displayAttributes
                            .filter { it.name.lowercase() != "color" }
                            .joinToString(separator = " • ") { attr ->
                                formatAttributeValue(attr.name, attr.value)
                            }

                        Text(
                            text = attributeText,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                }

                Spacer(Modifier.height(6.dp))


                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Cijena: ${"%.2f".format(item.basePrice)}€",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        QuantityButton("-") { onDecrease?.invoke() }
                        Spacer(Modifier.width(4.dp))
                        Text("${item.quantity}", color = Color.White)
                        Spacer(Modifier.width(4.dp))
                        QuantityButton("+") { onIncrease?.invoke() }
                    }
                }
            }
        }
    }
        }

@Composable
fun QuantityButton(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
            .background(QuantityBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(2.dp)
                .clickable { onClick() }
        )
    }
}

private fun formatAttributeValue(attrName: String, attrValue: String): String {
    return when(attrName.lowercase()) {
        "display_size" -> "${attrValue.replace("_", ",")} inch"
        "battery_wh" -> "$attrValue Wh"
        "weight_kg" -> "$attrValue kg"
        else -> if (attrValue.all { it.isDigit() || it == '_' }) {
            attrValue.replace("_", ",")
        } else {
            attrValue.replace("_", " ")
        }
    }
}