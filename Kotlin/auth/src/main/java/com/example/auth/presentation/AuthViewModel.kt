package com.example.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.data.AuthRepository
import com.example.core.auth.AccessManager
import com.example.core.auth.dto.Role
import com.example.core.network.token.TokenManager
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
    val profilePictureUrl: String = "",
    val roles: List<Role> = emptyList(),
    val isAdmin: Boolean = false,
    val isSeller: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val accessManager: AccessManager,
    private val repo: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<AuthUiState> = combine(
        tokenManager.accessTokenFlow,
        accessManager.profileFlow,
        accessManager.isAdminFlow,
        accessManager.isSellerFlow
    ) { token, profile, isAdmin, isSeller ->

        AuthUiState(
            isLoggedIn = !token.isNullOrEmpty(),
            firstName = profile?.firstName.orEmpty(),
            lastName = profile?.lastName.orEmpty(),
            email = profile?.email.orEmpty(),
            gender = profile?.gender.orEmpty(),
            profilePictureUrl = profile?.profilePictureUrl.orEmpty(),
            roles = profile?.roles ?: emptyList(),
            isAdmin = isAdmin,
            isSeller = isSeller
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AuthUiState()
    )

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
}
