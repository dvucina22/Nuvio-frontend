package com.example.nuviofrontend.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.presentation.AuthViewModel
import com.example.auth.presentation.login.LoginScreen
import com.example.auth.presentation.login.LoginViewModel
import com.example.auth.presentation.register.RegisterScreen
import com.example.nuviofrontend.MainScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditScreen
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditState
import com.example.nuviofrontend.feature.profile.presentation.ProfileEditViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            val authVm: com.example.auth.presentation.AuthViewModel = hiltViewModel()
            val ui by authVm.uiState.collectAsState()

            LaunchedEffect(ui.isLoggedIn) {
                navController.navigate(if (ui.isLoggedIn) Screen.MainAppScreen.route else Screen.MainScreen.route) {
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
                onSignOut = {
                    authVm.logout()
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToProfileEdit = {
                    navController.navigate(Screen.ProfileEdit.route)
                }
            )
        }

        composable(Screen.ProfileEdit.route) {
            val profileEditVm: ProfileEditViewModel = hiltViewModel()
            val uiState by profileEditVm.uiState.collectAsState()
            val profileEditState by profileEditVm.profileEditState.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(profileEditState) {
                when (profileEditState) {
                    is ProfileEditState.Success -> {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
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
                email = uiState.email,
                phoneNumber = uiState.phoneNumber,
                hasProfilePicture = false,
                isLoading = uiState.isLoading,
                firstNameError = uiState.firstNameError,
                lastNameError = uiState.lastNameError,
                emailError = uiState.emailError,
                phoneNumberError = uiState.phoneNumberError,
                onBack = {
                    navController.popBackStack()
                },
                onSave = { firstName, lastName, email, phoneNumber ->
                    profileEditVm.updateProfile(firstName, lastName, email, phoneNumber)
                },
                onProfilePictureClick = {
                    // TODO: Implement profile picture selection
                    Toast.makeText(context, "Profile picture selection coming soon", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}