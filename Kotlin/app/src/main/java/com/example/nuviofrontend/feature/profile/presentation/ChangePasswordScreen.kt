package com.example.nuviofrontend.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.ui.components.CustomButton
import com.example.core.ui.components.CustomTextField
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.BackgroundNavDark

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
    val errors by viewModel.errors.collectAsState()

    val oldPassword by viewModel.oldPassword.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

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
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            CustomTopBar(
                title = stringResource(R.string.change_password_title),
                showBack = true,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(150.dp))

            Divider(color = BackgroundNavDark, modifier = Modifier.padding(vertical = 16.dp))

            CustomTextField(
                value = oldPassword,
                onValueChange = {
                    viewModel.oldPassword.value = it
                    if (viewModel.errors.value.oldPasswordError != null) {
                        viewModel.errors.value =
                            viewModel.errors.value.copy(oldPasswordError = null)
                    }
                },
                placeholder = stringResource(R.string.old_password_placeholder),
                label = stringResource(R.string.old_password_label),
                isPassword = true,
                passwordVisible = oldPasswordVisible,
                onPasswordVisibilityChange = { oldPasswordVisible = !oldPasswordVisible },
                isError = viewModel.errors.value.oldPasswordError != null,
                errorMessage = viewModel.errors.value.oldPasswordError
            )

            CustomTextField(
                value = newPassword,
                onValueChange = {
                    viewModel.newPassword.value = it
                    if (viewModel.errors.value.newPasswordError != null) {
                        viewModel.errors.value =
                            viewModel.errors.value.copy(newPasswordError = null)
                    }
                },
                placeholder = stringResource(R.string.new_password_placeholder),
                label = stringResource(R.string.new_password_label),
                isPassword = true,
                passwordVisible = newPasswordVisible,
                onPasswordVisibilityChange = { newPasswordVisible = !newPasswordVisible },
                isError = errors.newPasswordError != null,
                errorMessage = errors.newPasswordError
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = {
                    viewModel.confirmPassword.value = it
                    if (viewModel.errors.value.confirmPasswordError != null) {
                        viewModel.errors.value =
                            viewModel.errors.value.copy(confirmPasswordError = null)
                    }
                },
                placeholder = stringResource(R.string.confirm_password_placeholder),
                label = stringResource(R.string.confirm_password_label),
                isPassword = true,
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                isError = errors.confirmPasswordError != null,
                errorMessage = errors.confirmPasswordError
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
}
