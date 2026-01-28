package com.example.auth.presentation.login

import androidx.lifecycle.ViewModel
import com.example.core.auth.IOAuthButtonItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OAuthButtonsViewModel @Inject constructor(items: Set<@JvmSuppressWildcards IOAuthButtonItem>) : ViewModel() {
    val items = items.toList().sortedBy { it.order }
}