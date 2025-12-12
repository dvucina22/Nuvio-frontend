package com.example.auth.presentation.register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomGenderField
import com.example.core.ui.components.CustomTextField
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White
import com.example.auth.R as AuthR
import com.example.core.R as CoreR

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val registerState by viewModel.registerState.collectAsState()
    val context = LocalContext.current
    val registerSuccessful = stringResource(id = AuthR.string.register_successful)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = CoreR.drawable.background_light),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = CoreR.drawable.logo_dark_full),
                    contentDescription = "logo_dark_full",
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = stringResource(id = AuthR.string.registration_title),
                    color = White,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.offset(y = (-50).dp)
                )
            }

            item {
                RegisterForm(viewModel)
            }
        }
    }
}


@Composable
fun RegisterForm(viewModel: RegisterViewModel) {
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
    val generalError by viewModel.generalError.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth().offset(y = (-30).dp)
    ) {
        generalError?.let {
            Text(text = it, color = Error)
        }

        CustomTextField(
            value = firstName,
            onValueChange = {
                viewModel.firstName.value = it
            },
            label = stringResource(id = AuthR.string.label_first_name),
            placeholder = stringResource(id = AuthR.string.label_first_name)
        )

        CustomTextField(
            value = lastName,
            onValueChange = {
                viewModel.lastName.value = it
            },
            label = stringResource(id = AuthR.string.label_last_name),
            placeholder = stringResource(id = AuthR.string.label_last_name)
        )

        CustomTextField(
            value = email,
            onValueChange = {
                viewModel.email.value = it
                viewModel.clearEmailError()
            },
            label = stringResource(id = AuthR.string.label_email),
            placeholder = stringResource(id = AuthR.string.label_email),
            isError = emailError != null,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        CustomTextField(
            value = phoneNumber,
            onValueChange = {
                viewModel.phoneNumber.value = it
            },
            label = stringResource(id = AuthR.string.label_phone_number),
            placeholder = stringResource(id = AuthR.string.label_phone_number)
        )

        CustomTextField(
            value = password,
            onValueChange = {
                viewModel.password.value = it
                viewModel.clearPasswordError()
            },
            label = stringResource(id = AuthR.string.label_password),
            placeholder = stringResource(id = AuthR.string.label_password),
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
            label = stringResource(id = AuthR.string.label_confirm_password),
            placeholder = stringResource(id = AuthR.string.label_confirm_password),
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        CustomGenderField(
            label = stringResource(id = AuthR.string.label_gender),
            gender = gender,
            onGenderSelected = {
                Log.d("Register", "Gender clicked: $it")
                viewModel.gender.value = it
            }
        )


        CustomButton(
            text = stringResource(id = AuthR.string.registration_title),
            onClick = { viewModel.register() }
        )
    }
}
