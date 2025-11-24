package com.example.nuviofrontend.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.ProfileHeader
import com.example.core.ui.theme.BackgroundNavDark
import com.example.nuviofrontend.core.ui.components.CustomButton
import com.example.nuviofrontend.core.ui.components.CustomTextField

@Composable
fun ChangePasswordScreen(
    isLoggedIn: Boolean,
    firstName: String? = null,
    lastName: String? = null,
    email: String? = null,
    onBack: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val displayName = if (isLoggedIn && !firstName.isNullOrBlank()) {
        if (!lastName.isNullOrBlank()) "$firstName $lastName" else firstName
    } else {
        context.getString(R.string.guest)
    }

    val displayEmail = if (isLoggedIn && !email.isNullOrBlank()) {
        email
    } else {
        context.getString(R.string.not_logged_in)
    }

    val oldPassword by viewModel.oldPassword.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val oldPasswordError by viewModel.oldPasswordError.collectAsState()
    val newPasswordError by viewModel.newPasswordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val generalError by viewModel.generalError.collectAsState()

    val changePasswordState by viewModel.changePasswordState.collectAsState()

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is ChangePasswordState.Success -> {
                Toast.makeText(context, context.getString(R.string.toast_password_changed), Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                onBack()
            }
            is ChangePasswordState.Error -> {
                Toast.makeText(context, generalError ?: context.getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        CustomTopBar(
            title = stringResource(R.string.change_password_title),
            showBack = true,
            onBack = onBack
        )

        Spacer(modifier = Modifier.height(20.dp))

        ProfileHeader(displayName = displayName, displayEmail = displayEmail)

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = BackgroundNavDark, modifier = Modifier.padding(vertical = 16.dp))

        CustomTextField(
            value = oldPassword,
            onValueChange = {
                viewModel.oldPassword.value = it
                if (viewModel.oldPasswordError.value != null) {
                    viewModel.oldPasswordError.value = null
                }
            },
            placeholder = stringResource(R.string.old_password_placeholder),
            label = stringResource(R.string.old_password_label),
            isPassword = true,
            passwordVisible = oldPasswordVisible,
            onPasswordVisibilityChange = { oldPasswordVisible = !oldPasswordVisible },
            isError =  oldPasswordError != null,
            errorMessage = oldPasswordError
        )

        CustomTextField(
            value = newPassword,
            onValueChange = {
                viewModel.newPassword.value = it
                if (viewModel.newPasswordError.value != null) viewModel.newPasswordError.value = null
            },
            placeholder = stringResource(R.string.new_password_placeholder),
            label = stringResource(R.string.new_password_label),
            isPassword = true,
            passwordVisible = newPasswordVisible,
            onPasswordVisibilityChange = { newPasswordVisible = !newPasswordVisible },
            isError =  newPasswordError != null,
            errorMessage = newPasswordError
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                viewModel.confirmPassword.value = it
                if (viewModel.confirmPasswordError.value != null) viewModel.confirmPasswordError.value = null
            },
            placeholder = stringResource(R.string.confirm_password_placeholder),
            label = stringResource(R.string.confirm_password_label),
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        Divider(color = BackgroundNavDark, modifier = Modifier.padding(vertical = 16.dp))

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            text = stringResource(R.string.button_save),
            onClick = { viewModel.changePassword() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
