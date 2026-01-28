package com.example.core.auth

import androidx.compose.runtime.Composable


interface IOAuthButtonItem {
    val provider: String
    val order: Int


    @Composable
    fun Content(
        onSuccess: () -> Unit
    )
}