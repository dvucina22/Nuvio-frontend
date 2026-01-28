package com.example.auth.presentation.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OAuthButtons(onSuccess: () -> Unit) {
    val vm: OAuthButtonsViewModel = hiltViewModel()
    val items = vm.items

    if (items.isEmpty()) return

    items.forEachIndexed { index, item ->
        if (index != 0) Spacer(modifier = Modifier.height(8.dp))
        item.Content(
            onSuccess = onSuccess
        )
    }
}