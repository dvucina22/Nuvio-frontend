package com.example.auth.presentation.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuviofrontend.core.ui.components.CustomButton
import com.example.nuviofrontend.core.ui.components.CustomTextField
import com.example.auth.R as AuthR
import com.example.core.R as CoreR
import com.example.core.ui.theme.White

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onNavigateToHome: () -> Unit, viewModel: LoginViewModel) {
    val scrollState = rememberScrollState()
    val loginState by viewModel.loginState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val invalidCredentialsMsg = context.getString(AuthR.string.toast_invalid_credentials)

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> onNavigateToHome()
            is LoginState.Error -> {
                Toast.makeText(
                    context,
                    invalidCredentialsMsg,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> Unit
        }
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> viewModel.handleGoogleResult(result) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = CoreR.drawable.background_dark),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 55.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = CoreR.drawable.logo_dark_full),
                contentDescription = "logo_dark_full",
                modifier = Modifier.size(200.dp)
            )

            LoginForm(
                state = loginState,
                email = email,
                password = password,
                emailError = emailError,
                passwordError = passwordError,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onLogin = { viewModel.login(email, password) },
                onNavigateToRegister = onNavigateToRegister,
                onGoogleLoginClick = { googleLauncher.launch(viewModel.googleSignInIntent()) }
            )
        }
    }
}

@Composable
fun LoginForm(
    state: LoginState,
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onGoogleLoginClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = AuthR.string.login_title),
            color = White,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(id = AuthR.string.label_email),
            placeholder = stringResource(id = AuthR.string.placeholder_email),
            textStyle = MaterialTheme.typography.labelSmall,
            isError = emailError != null,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(id = AuthR.string.label_password),
            placeholder = stringResource(id = AuthR.string.placeholder_password),
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
            textStyle = MaterialTheme.typography.labelSmall,
            isError = passwordError != null,
            errorMessage = passwordError
        )

        if (state is LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        }

        CustomButton(
            text = stringResource(id = AuthR.string.button_login),
            onClick = onLogin
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton(
            text = stringResource(id = AuthR.string.button_login_google),
            onClick = onGoogleLoginClick,
            iconRes = CoreR.drawable.google_icon,
            iconSize = 40
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = AuthR.string.text_register_prompt),
            color = Color(0xFF9DA39F),
            fontSize = 14.sp,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 24.dp)
                .clickable { onNavigateToRegister() }
        )
    }
}