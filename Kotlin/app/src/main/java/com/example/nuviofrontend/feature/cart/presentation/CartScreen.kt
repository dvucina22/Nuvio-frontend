package com.example.nuviofrontend.feature.cart.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.core.ui.theme.White

@Composable
fun CartScreen(){
    Text("Cart Screen", color = White)
}

@Preview
@Composable
fun CartScreenPreview(){
    CartScreen()
}