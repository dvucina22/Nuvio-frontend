package com.example.nuviofrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuviofrontend.core.ui.theme.NuvioFrontendTheme
import com.example.nuviofrontend.navigation.NavigationHost
import com.example.nuviofrontend.screens.components.CustomButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NuvioFrontendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    NavigationHost()
                }
            }
        }
    }
}


@Composable
fun MainScreen(onNavigateToRegister: () -> Unit, onNavigateToLogin: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_dark),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_dark_full),
                contentDescription = "logo_dark_full",
                modifier = Modifier.size(230.dp),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CustomButton(
                    text = "Prijava",
                    onClick = onNavigateToLogin
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomButton(
                    text = "Registracija",
                    onClick = onNavigateToRegister
                )
                Text(
                    text = "Nastavi kao gost",
                    color = Color(0xFF9DA39F),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .clickable { /* TODO: guest flow */ }
                )
            }
        }
    }
}

