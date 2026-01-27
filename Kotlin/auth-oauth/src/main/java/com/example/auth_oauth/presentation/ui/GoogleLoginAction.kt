package com.example.auth_oauth.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth_oauth.R
import com.example.auth_oauth.presentation.GoogleLoginState
import com.example.auth_oauth.presentation.GoogleLoginViewModel
import com.example.core.ui.components.CustomButton
import com.example.core.R as CoreR

@Composable
fun GoogleLoginAction(
    onSuccess: () -> Unit
) {
    val viewModel: GoogleLoginViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is GoogleLoginState.Success) onSuccess()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> viewModel.handleResult(result) }

    CustomButton(
        text = stringResource(id = R.string.button_login_google),
        onClick = { launcher.launch(viewModel.signInIntent()) },
        iconRes = CoreR.drawable.google_icon,
        iconSize = 40
    )
}
