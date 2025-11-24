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
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordScreen
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordState
import com.example.nuviofrontend.feature.profile.presentation.ChangePasswordViewModel
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditState
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditViewModel
import com.example.nuviofrontend.feature.profile.presentation.ProfileScreen

sealed class ProfileRoute(val route: String) {
    object Main : ProfileRoute("profile_main")
    object ChangePassword : ProfileRoute("profile_change_password")
    object EditProfile : ProfileRoute("profile_edit")
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
    onNavigateToProfileEdit: () -> Unit
) {
    val context = LocalContext.current
    val changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = ProfileRoute.Main.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(ProfileRoute.Main.route) {
            ProfileScreen(
                isLoggedIn = isLoggedIn,
                firstName = firstName,
                lastName = lastName,
                email = email,
                profilePictureUrl = profilePictureUrl,
                onSignOut = onSignOut,
                onNavigateToLogin = onNavigateToLogin,
                onEdit = { navController.navigate(ProfileRoute.EditProfile.route) },
                onChangePassword = { navController.navigate(ProfileRoute.ChangePassword.route) }
            )
        }

        composable(ProfileRoute.ChangePassword.route) {
            val changePasswordState by changePasswordViewModel.changePasswordState.collectAsState()

            LaunchedEffect(changePasswordState) {
                when (changePasswordState) {
                    is ChangePasswordState.Success -> {
                        Toast.makeText(context, "Lozinka uspješno promijenjena", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
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
                onSave = { firstName, lastName, phoneNumber, gender ->
                    profileEditViewModel.updateProfile(firstName, lastName, phoneNumber, gender)
                },
                onProfilePictureSelected = { uri ->
                    profileEditViewModel.uploadProfilePicture(uri)
                }
            )
        }
    }
}