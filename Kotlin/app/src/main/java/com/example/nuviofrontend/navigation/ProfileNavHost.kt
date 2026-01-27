package com.example.nuviofrontend.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nuviofrontend.feature.profile.presentation.CardScreen
import com.example.nuviofrontend.feature.profile.presentation.CardViewModel
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordScreen
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordState
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordViewModel
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditState
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditViewModel
import com.example.nuviofrontend.feature.profile.presentation.ProfileScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileViewModel
import com.example.nuviofrontend.feature.profile.presentation.UsersScreen
import com.example.nuviofrontend.feature.settings.presentation.SettingsScreen
import com.example.nuviofrontend.feature.support.presentation.SupportScreen
import com.example.nuviofrontend.feature.transactions.presentation.TransactionDetailScreen
import com.example.nuviofrontend.feature.transactions.presentation.TransactionsScreen

sealed class ProfileRoute(val route: String) {
    object Main : ProfileRoute("profile_main")
    object ChangePassword : ProfileRoute("profile_change_password")
    object EditProfile : ProfileRoute("profile_edit")
    object SavedCards : ProfileRoute("profile_saved_cards")
    object Users : ProfileRoute("users")
    object Support: ProfileRoute("support")
    object Settings: ProfileRoute("settings")
    object Transactions : ProfileRoute("profile_transactions")
    object TransactionDetail : ProfileRoute("profile_transaction_detail/{transactionId}") {
        fun createRoute(id: Long) = "profile_transaction_detail/$id"
    }
}

@Composable
fun ProfileNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    firstName: String?,
    lastName: String?,
    email: String?,
    profilePictureUrl: String?,
    onSignOut: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val context = LocalContext.current
    val changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = ProfileRoute.Main.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(ProfileRoute.Main.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                profileViewModel.loadUserProfileOnce()
            }

            ProfileScreen(
                viewModel = profileViewModel,
                onSignOut = onSignOut,
                onNavigateToLogin = onNavigateToLogin,
                onEdit = { navController.navigate(ProfileRoute.EditProfile.route) },
                onChangePassword = { navController.navigate(ProfileRoute.ChangePassword.route) },
                onNavigateToSavedCards = { navController.navigate(ProfileRoute.SavedCards.route) },
                onNavigateToUsers = { navController.navigate(ProfileRoute.Users.route) },
                onNavigateToSupport = { navController.navigate(ProfileRoute.Support.route) },
                onNavigateToSettings = { navController.navigate(ProfileRoute.Settings.route) },
                onNavigateToTransactions = { navController.navigate(ProfileRoute.Transactions.route) }
            )
        }

        composable(ProfileRoute.ChangePassword.route) {
            val changePasswordState by changePasswordViewModel.changePasswordState.collectAsState()

            LaunchedEffect(changePasswordState) {
                when (changePasswordState) {
                    is ChangePasswordState.Success -> {
                        Toast.makeText(
                            context,
                            "Lozinka uspješno promijenjena",
                            Toast.LENGTH_SHORT
                        ).show()
                        changePasswordViewModel.resetState()
                        navController.popBackStack()
                    }
                    is ChangePasswordState.Error -> {
                        Toast.makeText(
                            context,
                            (changePasswordState as ChangePasswordState.Error).message,
                            Toast.LENGTH_LONG
                        ).show()
                        changePasswordViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            ChangePasswordScreen(
                isLoggedIn = isLoggedIn,
                firstName = firstName,
                lastName = lastName,
                email = email,
                onBack = { navController.popBackStack() }
            )
        }

        composable(ProfileRoute.EditProfile.route) {
            val profileEditViewModel: ProfileEditViewModel = hiltViewModel()
            val profileUiState by profileEditViewModel.uiState.collectAsState()
            val profileEditState by profileEditViewModel.profileEditState.collectAsState()

            LaunchedEffect(profileEditState) {
                when (profileEditState) {
                    is ProfileEditState.Success -> {
                        Toast.makeText(
                            context,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        profileEditViewModel.resetState()
                        navController.popBackStack()
                    }
                    is ProfileEditState.Error -> {
                        Toast.makeText(
                            context,
                            (profileEditState as ProfileEditState.Error).message,
                            Toast.LENGTH_LONG
                        ).show()
                        profileEditViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            ProfileEditScreen(
                firstName = profileUiState.firstName,
                lastName = profileUiState.lastName,
                phoneNumber = profileUiState.phoneNumber,
                gender = profileUiState.gender,
                profilePictureUrl = profileUiState.profilePictureUrl,
                hasProfilePicture = profileUiState.profilePictureUrl.isNotEmpty(),
                isLoading = profileUiState.isLoading,
                isUploadingImage = profileUiState.isUploadingImage,
                firstNameError = profileUiState.firstNameError,
                lastNameError = profileUiState.lastNameError,
                phoneNumberError = profileUiState.phoneNumberError,
                genderError = profileUiState.genderError,
                onBack = { navController.popBackStack() },
                onSave = { fn, ln, phone, gender ->
                    profileEditViewModel.updateProfile(fn, ln, phone, gender)
                },
                onProfilePictureSelected = { uri ->
                    profileEditViewModel.uploadProfilePicture(uri)
                }
            )
        }

        composable(ProfileRoute.SavedCards.route) {
            val viewModel: CardViewModel = hiltViewModel()

            CardScreen(
                viewModel = viewModel,
                onViewTransactions = { _ -> },
                onBack = { navController.popBackStack() }
            )
        }
        composable(ProfileRoute.Users.route) {
            UsersScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(ProfileRoute.Support.route){
            SupportScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(ProfileRoute.Settings.route){
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(ProfileRoute.Transactions.route) {
            TransactionsScreen(
                onTransactionClick = { id ->
                    navController.navigate(ProfileRoute.TransactionDetail.createRoute(id))
                }
            )
        }

        composable(ProfileRoute.TransactionDetail.route) { backStackEntry ->
            val transactionId = backStackEntry.arguments
                ?.getString("transactionId")
                ?.toLongOrNull() ?: 0L

            TransactionDetailScreen(
                transactionId = transactionId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
