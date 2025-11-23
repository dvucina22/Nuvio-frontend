package com.example.nuviofrontend.navigation

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nuviofrontend.feature.cart.presentation.CartScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileScreen
import com.example.nuviofrontend.feature.search.presentation.SearchScreen
import com.example.nuviofrontend.feature.home.presentation.HomeScreen
import com.example.core.R
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.IconUnselectedTintDark
import com.example.core.ui.theme.SelectedItemBackgroundDark
import com.example.nuviofrontend.feature.favorite.presentation.FavoriteScreen
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordScreen
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordState
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordViewModel

enum class ProfileSubScreen {
    ProfileView,
    ChangePassword
}
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
    var profileSubScreen by remember { mutableStateOf<ProfileSubScreen>(ProfileSubScreen.ProfileView) }

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
                HomeTab.PROFILE -> {
                    when(profileSubScreen) {
                        ProfileSubScreen.ProfileView -> ProfileScreen(
                            isLoggedIn = isLoggedIn,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            onSignOut = onSignOut,
                            onNavigateToLogin = onNavigateToLogin,
                            onEdit = onNavigateToProfileEdit,
                            onChangePassword = {
                                profileSubScreen = ProfileSubScreen.ChangePassword
                            }
                        )
                        ProfileSubScreen.ChangePassword -> {
                            val viewModel: ChangePasswordViewModel = hiltViewModel()
                            val changePasswordState by viewModel.changePasswordState.collectAsState()
                            val context = LocalContext.current

                            LaunchedEffect(changePasswordState) {
                                when (changePasswordState) {
                                    is ChangePasswordState.Success -> {
                                        Toast.makeText(context, "Lozinka uspješno promijenjena", Toast.LENGTH_SHORT).show()
                                        viewModel.resetState()
                                        profileSubScreen = ProfileSubScreen.ProfileView
                                    }
                                    is ChangePasswordState.Error -> {
                                        Toast.makeText(context, (changePasswordState as ChangePasswordState.Error).message, Toast.LENGTH_LONG).show()
                                        viewModel.resetState()
                                    }
                                    else -> Unit
                                }
                            }
                            ChangePasswordScreen(
                                isLoggedIn = isLoggedIn,
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                onBack = { profileSubScreen = ProfileSubScreen.ProfileView }
                            )
                        }
                    }
                }
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
                .background(BackgroundNavDark),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
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
