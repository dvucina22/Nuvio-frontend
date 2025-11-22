package com.example.nuviofrontend.feature.favorite.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.core.ui.theme.White

@Composable
fun FavoriteScreen(){
    Text("Favorite Screen", color = White)
}

@Preview
@Composable
fun FavoriteScreenPreview(){
    FavoriteScreen()
}