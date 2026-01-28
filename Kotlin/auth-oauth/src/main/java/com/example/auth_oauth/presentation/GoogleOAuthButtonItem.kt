package com.example.auth_oauth.presentation

import androidx.compose.runtime.Composable
import com.example.auth_oauth.presentation.ui.GoogleLoginAction
import javax.inject.Inject
import com.example.core.auth.IOAuthButtonItem

class GoogleOAuthButtonEntry @Inject constructor(): IOAuthButtonItem {
    override val provider: String = "google"
    override val order: Int = 10

    @Composable
    override fun Content(onSuccess: () -> Unit) {
        GoogleLoginAction(
            onSuccess = onSuccess
        )
    }
}