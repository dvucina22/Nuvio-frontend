package com.example.nuviofrontend.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object MainScreen : Screen("main")
    object Login : Screen("login")
    object MainAppScreen : Screen("mainAppScreen")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProfileEdit : Screen("profileEdit")
    object Checkout : Screen("checkout")
    object TransactionSuccess : Screen("transaction_success")
    object TransactionFailure : Screen("transaction_failure")
    object Users : Screen("users")
}