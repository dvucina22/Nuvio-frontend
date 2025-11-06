package com.example.nuviofrontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    AppNavGraph(navController)
}