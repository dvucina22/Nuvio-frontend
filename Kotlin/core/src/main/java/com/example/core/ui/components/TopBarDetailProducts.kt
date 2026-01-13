package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.IconDark
import com.example.core.ui.theme.White
import com.example.core.ui.theme.WhiteSoft

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
    showFavorite: Boolean = true,
    showCart: Boolean = true,
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
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBack() }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showEdit) {
                IconActionBox(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Edit"
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            if (showDelete) {
                IconActionBox(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        tint = Error,
                        contentDescription = "Delete"
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            if (showFavorite) {
                IconActionBox(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Error else MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            if (showCart) {
                IconActionBox(onClick = onCartClick) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Cart"
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
            onBack = {},
            onFavoriteClick = {},
            onCartClick = {},
            showFavorite = true,
            showCart = true
        )
    }
}
