package com.example.nuviofrontend.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nuviofrontend.feature.cart.presentation.CartScreen
import com.example.nuviofrontend.feature.search.presentation.SearchScreen
import com.example.core.R
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.LightOverlay
import com.example.core.ui.theme.IconUnselectedTintDark
import com.example.nuviofrontend.feature.catalog.presentation.AddNewProductScreen
import com.example.nuviofrontend.feature.catalog.presentation.DetailProductScreen
import com.example.nuviofrontend.feature.catalog.presentation.EditProductScreen
import com.example.nuviofrontend.feature.catalog.presentation.HomeScreen
import com.example.nuviofrontend.feature.favorite.presentation.FavoriteScreen
import com.example.nuviofrontend.feature.sale.presentation.CheckoutResult
import com.example.nuviofrontend.feature.sale.presentation.CheckoutScreen
import com.example.nuviofrontend.feature.sale.presentation.SaleViewModel
import com.example.nuviofrontend.feature.sale.presentation.TransactionResultScreen


@RequiresApi(Build.VERSION_CODES.O)
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
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToUsers: () -> Unit
) {
    val navController = rememberNavController()
    val tabs = HomeTab.values()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val hideBottomBarRoutes = listOf(
        "checkout",
        "transaction_success",
        "transaction_failure"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_light),
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
                            },
                            onNavigateToCheckout = {
                                navController.navigate("checkout")
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
                            onNavigateToProfileEdit = onNavigateToProfileEdit,
                            onNavigateToUsers = onNavigateToUsers
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

            composable("checkout") {
                CheckoutScreen(
                    onPaymentSuccess = { saleResponse ->
                        navController.navigate("transaction_success") {
                            popUpTo(HomeTab.CART.name) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onPaymentFailure = { errorMessage ->
                        navController.navigate("transaction_failure") {
                            popUpTo("checkout") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("transaction_success") {
                TransactionResultScreen(
                    isSuccess = true,
                    onContinueShopping = {
                        navController.navigate(HomeTab.HOME.name) {
                            popUpTo(HomeTab.HOME.name) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onViewOrders = {
                        navController.navigate(HomeTab.PROFILE.name) {
                            popUpTo(HomeTab.HOME.name) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onGoToHome = {
                        navController.navigate(HomeTab.HOME.name) {
                            popUpTo(HomeTab.HOME.name) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("transaction_failure") {
                val viewModel: SaleViewModel = hiltViewModel()
                val checkoutResult by viewModel.checkoutResult.collectAsState()

                val maxRetriesReached = (checkoutResult as? CheckoutResult.Error)?.maxRetriesReached ?: false

                TransactionResultScreen(
                    isSuccess = false,
                    errorMessage = (checkoutResult as? CheckoutResult.Error)?.message,
                    maxRetriesReached = maxRetriesReached,
                    onContinueShopping = {
                        if (maxRetriesReached) {
                            navController.navigate(HomeTab.HOME.name) {
                                popUpTo(HomeTab.HOME.name) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onViewOrders = {},
                    onGoToHome = {
                        navController.navigate(HomeTab.HOME.name) {
                            popUpTo(HomeTab.HOME.name) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        if (currentRoute !in hideBottomBarRoutes && !currentRoute.orEmpty().startsWith("product/") && !currentRoute.orEmpty().startsWith("edit_product/")) {
            CustomBottomNavBar(
                selectedIndex = tabs.indexOfFirst { it.name == currentRoute },
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
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(LightOverlay),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .then(
                            if (selectedIndex == index) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = AccentColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .background(
                            if (selectedIndex == index) AccentColor else Color.Transparent
                        )
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selectedIndex == index) LightOverlay else IconUnselectedTintDark
                    )
                }
            }
        }
    }
}