package com.example.nuviofrontend.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nuviofrontend.MainScreen
import com.example.nuviofrontend.feature.auth.presentation.login.LoginScreen
import com.example.nuviofrontend.feature.auth.presentation.login.LoginViewModel
import com.example.nuviofrontend.feature.auth.presentation.register.RegisterScreen
import com.example.nuviofrontend.feature.home.presentation.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {

        composable(Screen.MainScreen.route) {
            MainScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()

            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                viewModel = loginViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
