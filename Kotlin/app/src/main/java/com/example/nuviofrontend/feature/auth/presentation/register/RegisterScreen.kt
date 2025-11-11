package com.example.nuviofrontend.feature.auth.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nuviofrontend.R
import com.example.nuviofrontend.core.ui.components.CustomTextField
import com.example.nuviofrontend.core.ui.theme.Error
import com.example.nuviofrontend.core.ui.theme.White
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nuviofrontend.core.ui.components.CustomButton

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val registerState by viewModel.registerState.collectAsState()

    val context = LocalContext.current

    val registerSuccessful = context.getString(R.string.register_successful)


    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            Toast.makeText(context, registerSuccessful, Toast.LENGTH_LONG).show()
            onRegisterSuccess()
        }
        if (registerState is RegisterState.Error) {
            (registerState as? RegisterState.Error)?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

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
                .padding(horizontal = 32.dp, vertical = 55.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_dark_full),
                contentDescription = "logo_dark_full",
                modifier = Modifier.size(200.dp)
            )
            RegisterForm(viewModel = viewModel)
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

    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val generalError by viewModel.generalError.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(id = R.string.registration_title),
            color = White,
            style = MaterialTheme.typography.displayLarge)

        if (generalError != null) Text(generalError!!, color = Error)

        CustomTextField(
            value = firstName,
            onValueChange = { viewModel.firstName.value = it; viewModel.clearErrors() },
            label = stringResource(id = R.string.label_first_name),
            placeholder = stringResource(id = R.string.label_first_name)
        )

        CustomTextField(
            value = lastName,
            onValueChange = { viewModel.lastName.value = it; viewModel.clearErrors() },
            label = stringResource(id = R.string.label_last_name),
            placeholder = stringResource(id = R.string.label_last_name),
        )

        CustomTextField(
            value = email,
            onValueChange = { viewModel.email.value = it; viewModel.clearErrors() },
            label = stringResource(id = R.string.label_email),
            placeholder = stringResource(id = R.string.label_email),
            isError = emailError != null,
            errorMessage = emailError
        )

        CustomTextField(
            value = phoneNumber,
            onValueChange = { viewModel.phoneNumber.value = it; viewModel.clearErrors() },
            label = stringResource(id = R.string.label_phone_number),
            placeholder = stringResource(id = R.string.label_phone_number)
        )

        CustomTextField(
            value = password,
            onValueChange = { viewModel.password.value = it; viewModel.clearErrors() },
            label = stringResource(id = R.string.label_password),
            placeholder = stringResource(id = R.string.label_password),
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
            isError = passwordError != null,
            errorMessage = passwordError
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = { viewModel.confirmPassword.value = it; viewModel.clearErrors() },
            label = stringResource(id = R.string.label_confirm_password),
            placeholder = stringResource(id = R.string.label_confirm_password),
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(15.dp))

        CustomButton(
            text = stringResource(id = R.string.registration_title),
            onClick = { viewModel.register() }
        )
    }
}
