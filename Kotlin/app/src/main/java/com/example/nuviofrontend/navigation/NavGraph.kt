package com.example.nuviofrontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.presentation.AuthViewModel
import com.example.auth.presentation.login.LoginScreen
import com.example.auth.presentation.login.LoginViewModel
import com.example.auth.presentation.register.RegisterScreen
import com.example.nuviofrontend.MainScreen

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
                firstName = ui.firstName
            )
        }

    }
}
