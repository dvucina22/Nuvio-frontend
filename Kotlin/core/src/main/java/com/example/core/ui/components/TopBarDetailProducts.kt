package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.BackgroundBehindButton
import com.example.core.ui.theme.White
import com.example.core.ui.theme.Error

@Composable
fun TopBarDetailProducts(
    isFavorite: Boolean = false,
    onBack: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    showDelete: Boolean = false,
    showEdit: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBack() }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(color = BackgroundBehindButton, shape = RoundedCornerShape(5.dp))
                    .clickable { onFavoriteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.Favorite,
                    tint = if (isFavorite) Error else White,
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(23.dp)
                        .clickable { onFavoriteClick() },

                    )
            }

            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(color = BackgroundBehindButton, shape = RoundedCornerShape(5.dp))
                    .clickable { onCartClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = White,
                    modifier = Modifier
                        .size(23.dp)
                        .clickable { onCartClick() }
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
            if (showEdit) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(color = BackgroundBehindButton, shape = RoundedCornerShape(5.dp))
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = White,
                        modifier = Modifier.size(23.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            if (showDelete) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(color = BackgroundBehindButton, shape = RoundedCornerShape(5.dp))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = White,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarDetailProductsPreview() {
    Box(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
    ) {
        TopBarDetailProducts(
            onBack = { /* preview back action */ },
            onFavoriteClick = { /* preview favorite */ },
            onCartClick = { /* preview cart */ }
        )
    }
}
