package com.example.nuviofrontend.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
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



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = context.getString(R.string.back_button),
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            viewModel.resetAllFields()
                            onBack()
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(
                        text = context.getString(R.string.change_password_title),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logo_dark_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(43.dp)
                            .padding(6.dp)
                            .align(Alignment.TopEnd),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_light_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = displayName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = displayEmail,
                    color = Color(0xFF9AA4A6),
                    style = MaterialTheme.typography.labelSmall,
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))


            CustomTextField(
                value = oldPassword,
                onValueChange = {
                    viewModel.oldPassword.value = it
                    if (viewModel.oldPasswordError.value != null) {
                        viewModel.oldPasswordError.value = null
                    }
                },
                placeholder = context.getString(R.string.old_password_placeholder),
                label = context.getString(R.string.old_password_label),
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
                placeholder = context.getString(R.string.new_password_placeholder),
                label = context.getString(R.string.new_password_label),
                isPassword = true,
                passwordVisible = newPasswordVisible,
                onPasswordVisibilityChange = { newPasswordVisible = !newPasswordVisible },
                isError =  newPasswordError != null,
                errorMessage =  newPasswordError
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = {
                    viewModel.confirmPassword.value = it
                    if (viewModel.confirmPasswordError.value != null) viewModel.confirmPasswordError.value = null
                },
                placeholder = context.getString(R.string.confirm_password_placeholder),
                label = context.getString(R.string.confirm_password_label),
                isPassword = true,
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                text = context.getString(R.string.button_save),
                onClick = { viewModel.changePassword() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
