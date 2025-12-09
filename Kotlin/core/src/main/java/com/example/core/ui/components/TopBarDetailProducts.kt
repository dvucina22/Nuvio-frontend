package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopBarDetailProducts(
    isFavorite: Boolean = false,
    onBack: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
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
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                tint = if (isFavorite) Color.Red else Color.White,
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onFavoriteClick() },

            )

            Spacer(modifier = Modifier.width(15.dp))

            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onCartClick() }
            )
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
