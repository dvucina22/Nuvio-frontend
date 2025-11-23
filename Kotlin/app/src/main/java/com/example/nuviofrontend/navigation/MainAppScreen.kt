package com.example.nuviofrontend.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.core.R
import com.example.nuviofrontend.feature.cart.presentation.CartScreen
import com.example.nuviofrontend.feature.favorite.presentation.FavoriteScreen
import com.example.nuviofrontend.feature.home.presentation.HomeScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileScreen
import com.example.nuviofrontend.feature.search.presentation.SearchScreen

@Composable
fun MainAppScreen(
    isLoggedIn: Boolean,
    firstName: String?,
    lastName: String? = null,
    email: String? = null,
    onSignOut: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileEdit: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_dark),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                HomeTab.HOME -> HomeScreen(firstName)
                HomeTab.SEARCH -> SearchScreen()
                HomeTab.CART -> CartScreen()
                HomeTab.FAVORITES -> FavoriteScreen()
                HomeTab.PROFILE -> ProfileScreen(
                    isLoggedIn = isLoggedIn,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    onSignOut = onSignOut,
                    onEdit = onNavigateToProfileEdit,
                    onNavigateToLogin = onNavigateToLogin,
                )

            }
        }

        CustomBottomNavBar(
            selectedIndex = selectedTab.ordinal,
            onItemSelected = { selectedTab = HomeTab.values()[it] },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CustomBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Icons.Default.Home,
        Icons.Default.Search,
        Icons.Default.FavoriteBorder,
        Icons.Default.ShoppingCart,
        Icons.Default.Person
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .height(68.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5C6B73)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selectedIndex == index) Color(0x331E2F23) else Color.Transparent
                        )
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedIndex == index) Color(0xFFEDF2F4) else Color(0xFF818E96)
                    )
                }
            }
        }
    }
}
