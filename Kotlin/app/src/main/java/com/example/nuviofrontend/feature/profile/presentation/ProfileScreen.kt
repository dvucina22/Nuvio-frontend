package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.core.ui.theme.White

@Composable
fun ProfileScreen(){
    Text("Profile Screen", color = White)
}

@Preview
@Composable
fun ProfileScreenPreview(){
    ProfileScreen()
}