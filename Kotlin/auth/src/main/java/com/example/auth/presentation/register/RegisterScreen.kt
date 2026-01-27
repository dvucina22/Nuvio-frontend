package com.example.auth.presentation.register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomGenderField
import com.example.core.ui.components.CustomTextField
import com.example.auth.R as AuthR
import com.example.core.R as CoreR

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    themeIndex: Int,
    onNavigateToLogin: () -> Unit
) {
    val registerState by viewModel.registerState.collectAsState()
    val context = LocalContext.current
    val registerSuccessful = stringResource(AuthR.string.register_successful)

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, registerSuccessful, Toast.LENGTH_LONG).show()
                onRegisterSuccess()
            }
            is RegisterState.Error -> {
                Toast.makeText(
                    context,
                    (registerState as RegisterState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> Unit
        }
    }
    val backgroundRes = if (themeIndex == 1) CoreR.drawable.background_dark else CoreR.drawable.background_light
    val logoRes = if (themeIndex == 1) CoreR.drawable.logo_dark_full else CoreR.drawable.logo_light_full

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = stringResource(AuthR.string.registration_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displayLarge.copy(lineHeight = 40.sp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    RegisterForm(
                        viewModel = viewModel,
                        onNavigateToLogin = onNavigateToLogin
                    )
                }
            }
        }
    }
}


@Composable
fun RegisterForm(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit
) {
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val gender by viewModel.gender.collectAsState()

    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val phoneNumberError by viewModel.phoneNumberError.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTextField(
            value = firstName,
            onValueChange = {
                viewModel.firstName.value = it
            },
            label = stringResource( AuthR.string.label_first_name),
            placeholder = stringResource(AuthR.string.label_first_name)
        )

        CustomTextField(
            value = lastName,
            onValueChange = {
                viewModel.lastName.value = it
            },
            label = stringResource(AuthR.string.label_last_name),
            placeholder = stringResource(AuthR.string.label_last_name)
        )

        CustomTextField(
            value = email,
            onValueChange = {
                viewModel.email.value = it
                viewModel.clearEmailError()
            },
            label = stringResource(AuthR.string.label_email),
            placeholder = stringResource(AuthR.string.label_email),
            isError = emailError != null,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        CustomTextField(
            value = phoneNumber,
            onValueChange = {
                viewModel.phoneNumber.value = it
                viewModel.clearPhoneNumberError()
            },
            label = stringResource( AuthR.string.label_phone_number),
            placeholder = stringResource(AuthR.string.label_phone_number),
            isError = phoneNumberError != null,
            errorMessage = phoneNumberError
        )

        CustomTextField(
            value = password,
            onValueChange = {
                viewModel.password.value = it
                viewModel.clearPasswordError()
            },
            label = stringResource(AuthR.string.label_password),
            placeholder = stringResource(AuthR.string.label_password),
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
            isError = passwordError != null,
            errorMessage = passwordError
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                viewModel.confirmPassword.value = it
                viewModel.clearConfirmPasswordError()
            },
            label = stringResource(AuthR.string.label_confirm_password),
            placeholder = stringResource(AuthR.string.label_confirm_password),
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        CustomGenderField(
            label = stringResource( AuthR.string.label_gender),
            gender = gender,
            onGenderSelected = {
                Log.d("Register", "Gender clicked: $it")
                viewModel.gender.value = it
            }
        )


        CustomButton(
            text = stringResource(AuthR.string.registration_title),
            onClick = { viewModel.register() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = AuthR.string.text_login_prompt),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .padding(top = 24.dp)
                .clickable { onNavigateToLogin() }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
