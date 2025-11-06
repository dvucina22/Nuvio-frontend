package com.example.nuviofrontend.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}