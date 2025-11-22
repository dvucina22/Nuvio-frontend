package com.example.nuviofrontend.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object MainScreen : Screen("main")
    object Login : Screen("login")
    object MainAppScreen : Screen("mainAppScreen")
    object Register : Screen("register")
    object Home : Screen("home")
}