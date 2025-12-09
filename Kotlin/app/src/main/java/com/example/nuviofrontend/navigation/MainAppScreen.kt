package com.example.nuviofrontend.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nuviofrontend.feature.cart.presentation.CartScreen
import com.example.nuviofrontend.feature.search.presentation.SearchScreen
import com.example.core.R
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.IconUnselectedTintDark
import com.example.core.ui.theme.SelectedItemBackgroundDark
import com.example.nuviofrontend.feature.catalog.presentation.AddNewProductScreen
import com.example.nuviofrontend.feature.catalog.presentation.DetailProductScreen
import com.example.nuviofrontend.feature.catalog.presentation.EditProductScreen
import com.example.nuviofrontend.feature.catalog.presentation.HomeScreen
import com.example.nuviofrontend.feature.favorite.presentation.FavoriteScreen


@Composable
fun MainAppScreen(
    isLoggedIn: Boolean,
    firstName: String?,
    lastName: String?,
    email: String?,
    gender: String?,
    profilePictureUrl: String?,
    onSignOut: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileEdit: () -> Unit
) {
    val navController = rememberNavController()
    val tabs = HomeTab.values()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_dark),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        NavHost(
            navController = navController,
            startDestination = HomeTab.HOME.name,
            modifier = Modifier.fillMaxSize()
        ) {
            tabs.forEach { tab ->
                composable(tab.name) {
                    when (tab) {
                        HomeTab.HOME -> HomeScreen(
                            firstName = firstName,
                            gender = gender,
                            onProductClick = { productId ->
                                navController.navigate("product/$productId")
                            },
                            onAddProductClick = {
                                navController.navigate("add_product")
                            },
                            onEditProductClick = { productId ->
                                navController.navigate("edit_product/$productId")
                            }
                        )
                        HomeTab.SEARCH -> SearchScreen(
                            onProductClick = { productId ->
                                navController.navigate("product/$productId")
                            }
                        )
                        HomeTab.CART -> CartScreen(
                            onProductClick = { productId ->
                                navController.navigate("product/$productId")
                            }
                        )
                        HomeTab.FAVORITES -> FavoriteScreen(
                            onProductClick = { productId ->
                                navController.navigate("product/$productId")
                            }
                        )
                        HomeTab.PROFILE -> ProfileNavHost(
                            navController = rememberNavController(),
                            isLoggedIn = isLoggedIn,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            profilePictureUrl = profilePictureUrl,
                            onSignOut = onSignOut,
                            onNavigateToLogin = onNavigateToLogin,
                            onNavigateToProfileEdit = onNavigateToProfileEdit
                        )
                    }
                }
            }
            composable("product/{id}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("id")?.toLong() ?: 0L
                DetailProductScreen(
                    onBack = { navController.popBackStack() },
                    onEditNavigate = { productId ->
                        navController.navigate("edit_product/$productId")
                    }
                )
            }
            composable("add_product") {
                AddNewProductScreen(
                    navController = navController,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("edit_product/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toLong() ?: 0L

                EditProductScreen(
                    navController = navController,
                    productId = id,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        CustomBottomNavBar(
            selectedIndex = tabs.indexOfFirst { it.name == navController.currentBackStackEntryAsState().value?.destination?.route },
            onItemSelected = { index ->
                navController.navigate(tabs[index].name) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
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
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundNavDark),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selectedIndex == index) SelectedItemBackgroundDark else Color.Transparent
                        )
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedIndex == index) IconSelectedTintDark else IconUnselectedTintDark
                    )
                }
            }
        }
    }
}