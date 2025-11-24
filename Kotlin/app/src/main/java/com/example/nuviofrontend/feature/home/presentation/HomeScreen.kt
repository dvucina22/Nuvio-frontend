package com.example.nuviofrontend.feature.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.core.R
import com.example.core.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    firstName: String?,
    gender: String? = null
) {
    val greeting = when (gender?.lowercase()) {
        "male" -> stringResource(R.string.welcome_male, firstName ?: "")
        "female" -> stringResource(R.string.welcome_female, firstName ?: "")
        else -> stringResource(R.string.welcome_neutral, firstName ?: "")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(greeting, color = White)
    }
}