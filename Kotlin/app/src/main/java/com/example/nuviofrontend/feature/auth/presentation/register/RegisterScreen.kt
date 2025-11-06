package com.example.nuviofrontend.feature.auth.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nuviofrontend.R
import com.example.nuviofrontend.core.ui.components.CustomTextField
import com.example.nuviofrontend.core.ui.theme.White
import com.example.nuviofrontend.screens.components.CustomButton

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    val scrollState = rememberScrollState()
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 55.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_dark_full),
                contentDescription = "logo_dark_full",
                modifier = Modifier.size(200.dp)
            )
            RegisterForm()
        }
    }
}

@Composable
fun RegisterForm() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    )  {
        Text(
            text = "Registracija",
            color = White,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        )

        CustomTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "Ime",
            placeholder = "Ime",
            textStyle = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        CustomTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Prezime",
            placeholder = "Prezime",
            textStyle = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "Email",
            textStyle = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        CustomTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Broj telefona",
            placeholder = "Broj telefona",
            textStyle = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Lozinka",
            placeholder = "Lozinka",
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
            textStyle = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Potvrdite lozinku",
            placeholder = "Potvrda lozinke",
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
            textStyle = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth()
        )
        CustomButton(
            text = "Registracija",
            onClick = { /*todo logika*/ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(onNavigateToLogin = {})
    }
}