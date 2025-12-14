package com.example.core.ui.components

import android.R.attr.fontWeight
import android.R.attr.maxLines
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.catalog.dto.Product
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.White

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    onEdit: (Long) -> Unit = {},
    onDelete: (Long) -> Unit = {},
    showMenu: Boolean = false,
    isAdmin: Boolean = false,
    isSeller: Boolean = false
) {
    var menuOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(140.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF000000).copy(alpha = 0.03f),
                spotColor = Color(0xFF000000).copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xD8D9D9D9))
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = product.imageUrl.ifEmpty { R.drawable.logo_light_icon },
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(32.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(Color(0xDD1A1F2E))
                        .clickable { onFavoriteChange(!isFavorite) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFF6B6B) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                val quantity = product.quantity ?: 0
                if (quantity <= 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Out of Stock",
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (quantity < 5) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentColor)
                            .padding(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Low Stock",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = product.brand.uppercase(),
                            color = AccentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        if (product.modelNumber?.isNotEmpty() == true) {
                            Text(
                                text = product.modelNumber!!,
                                color = Color(0xFF1D1D1D),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (showMenu && (isAdmin || isSeller)) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(BackgroundBehindButton)
                                        .clickable { menuOpen = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (menuOpen) {
                                    Popup(
                                        alignment = Alignment.TopEnd,
                                        onDismissRequest = { menuOpen = false }
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .shadow(
                                                    elevation = 8.dp,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .background(Color(0xFF252D3D), RoundedCornerShape(10.dp))
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFF353D4D),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .padding(4.dp)
                                                .width(IntrinsicSize.Max)
                                        ) {
                                            MenuItem(
                                                icon = Icons.Default.Edit,
                                                label = "Edit"
                                            ) {
                                                menuOpen = false
                                                onEdit(product.id)
                                            }

                                            Spacer(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .padding(horizontal = 8.dp)
                                                    .background(Color(0xFF353D4D))
                                            )

                                            MenuItem(
                                                icon = Icons.Default.Delete,
                                                label = "Delete",
                                                isDestructive = true
                                            ) {
                                                menuOpen = false
                                                onDelete(product.id)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = product.name,
                        color = Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    if (product.category.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.category,
                            color = Color(0xFF1D1D1D),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = "€${String.format("%.2f", product.basePrice)}",
                    color = AccentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    label: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isDestructive) Color(0xFFFF6B6B) else Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = if (isDestructive) Color(0xFFFF6B6B) else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}