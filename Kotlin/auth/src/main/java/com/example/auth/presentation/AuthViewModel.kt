// feature/auth/presentation/AuthViewModel.kt
package com.example.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.data.AuthRepository
import com.example.core.network.token.TokenManager
import com.example.core.network.token.UserPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val gender: String = "",
    val profilePictureUrl: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val tokenManager: TokenManager, private val userPrefs: UserPrefs, private val repo: AuthRepository) : ViewModel() {

    val uiState: StateFlow<AuthUiState> = combine(
        tokenManager.accessTokenFlow,
        userPrefs.profileFlow
    ) { token, profile ->
        val firstName = profile?.firstName ?: ""
        val lastName = profile?.lastName ?: ""
        val email = profile?.email ?: ""
        val gender = profile?.gender ?: ""
        val profilePictureUrl = profile?.profilePictureUrl ?: ""

        AuthUiState(
            isLoggedIn = !token.isNullOrEmpty(),
            firstName = firstName,
            lastName = lastName,
            email = email,
            gender = gender,
            profilePictureUrl = profilePictureUrl
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthUiState())

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
}
