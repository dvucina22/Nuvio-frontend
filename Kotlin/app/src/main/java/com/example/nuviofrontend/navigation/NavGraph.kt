package com.example.nuviofrontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nuviofrontend.MainScreen
import com.example.nuviofrontend.feature.auth.presentation.login.LoginScreen
import com.example.nuviofrontend.feature.auth.presentation.register.RegisterScreen

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
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(onNavigateToLogin = { navController.navigate(Screen.Login.route) })
        }
    }
}
