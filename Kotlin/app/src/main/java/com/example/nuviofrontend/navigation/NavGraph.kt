package com.example.nuviofrontend.navigation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.presentation.AuthViewModel
import com.example.auth.presentation.login.LoginScreen
import com.example.auth.presentation.login.LoginViewModel
import com.example.auth.presentation.register.RegisterScreen
import com.example.core.R
import com.example.nuviofrontend.MainScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditState
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditViewModel
import com.example.nuviofrontend.feature.profile.presentation.UsersScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            val authVm: AuthViewModel = hiltViewModel()
            val ui by authVm.uiState.collectAsState()

            LaunchedEffect(ui.isLoggedIn) {
                navController.navigate(
                    if (ui.isLoggedIn) Screen.MainAppScreen.route else Screen.MainScreen.route
                ) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }
        }

        composable(Screen.MainScreen.route) {
            MainScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onContinueAsGuest = {
                    navController.navigate(Screen.MainAppScreen.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.MainAppScreen.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                viewModel = loginViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.MainAppScreen.route) {
            val authVm: AuthViewModel = hiltViewModel()
            val ui by authVm.uiState.collectAsState()

            MainAppScreen(
                isLoggedIn = ui.isLoggedIn,
                firstName = ui.firstName,
                lastName = ui.lastName,
                email = ui.email,
                gender = ui.gender,
                profilePictureUrl = ui.profilePictureUrl,
                onSignOut = {
                    authVm.logout()
                    navController.navigate(Screen.MainScreen.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToProfileEdit = {
                    navController.navigate(Screen.ProfileEdit.route)
                },
                onNavigateToUsers = {
                    navController.navigate(Screen.Users.route)
                }
            )
        }

        composable(Screen.ProfileEdit.route) {
            val profileEditVm: ProfileEditViewModel = hiltViewModel()
            val uiState by profileEditVm.uiState.collectAsState()
            val profileEditState by profileEditVm.profileEditState.collectAsState()
            val context = LocalContext.current
            val successMessage = stringResource(R.string.profile_update_success)

            LaunchedEffect(profileEditState) {
                when (profileEditState) {
                    is ProfileEditState.Success -> {
                        Toast.makeText(
                            context,
                            successMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        profileEditVm.resetState()
                        navController.popBackStack()
                    }

                    is ProfileEditState.Error -> {
                        Toast.makeText(
                            context,
                            (profileEditState as ProfileEditState.Error).message,
                            Toast.LENGTH_LONG
                        ).show()
                        profileEditVm.resetState()
                    }

                    else -> Unit
                }
            }

            ProfileEditScreen(
                firstName = uiState.firstName,
                lastName = uiState.lastName,
                phoneNumber = uiState.phoneNumber,
                gender = uiState.gender,
                profilePictureUrl = uiState.profilePictureUrl,
                hasProfilePicture = uiState.profilePictureUrl.isNotEmpty(),
                isLoading = uiState.isLoading,
                isUploadingImage = uiState.isUploadingImage,
                firstNameError = uiState.firstNameError,
                lastNameError = uiState.lastNameError,
                phoneNumberError = uiState.phoneNumberError,
                genderError = uiState.genderError,
                onBack = { navController.popBackStack() },
                onSave = { firstName, lastName, phoneNumber, gender ->
                    profileEditVm.updateProfile(firstName, lastName, phoneNumber, gender)
                },
                onProfilePictureSelected = { uri ->
                    profileEditVm.uploadProfilePicture(uri)
                }
            )
        }

        composable(Screen.Users.route) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background_dark),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                UsersScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
