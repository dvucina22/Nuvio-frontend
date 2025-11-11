// feature/auth/presentation/AuthViewModel.kt
package com.example.nuviofrontend.feature.auth.presentation

import android.R.attr.name
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuviofrontend.core.network.token.TokenManager
import com.example.nuviofrontend.core.network.token.UserPrefs
import com.example.nuviofrontend.core.network.token.UserProfile
import com.example.nuviofrontend.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val firstName: String = "",
    val lastName: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val tokenManager: TokenManager, private val userPrefs: UserPrefs, private val repo: AuthRepository) : ViewModel() {

    val uiState: StateFlow<AuthUiState> = combine(tokenManager.accessTokenFlow, userPrefs.profileFlow) { token, profile ->
            val firstName = profile?.firstName ?: ""
            val lastName = profile?.lastName ?: ""
            AuthUiState(isLoggedIn = !token.isNullOrEmpty(), firstName = firstName, lastName = lastName)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthUiState())

    fun logout() {
        viewModelScope.launch { repo.logout() }
    }
}
