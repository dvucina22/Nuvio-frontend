package com.example.nuviofrontend.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.network.token.IUserPrefs
import com.example.nuviofrontend.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPrefs: IUserPrefs
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    init {
        viewModelScope.launch {
            userPrefs.profileFlow.collect { profile ->
                if (profile != null) {
                    _profileState.value = ProfileUiState(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email,
                        profilePictureUrl = profile.profilePictureUrl,
                        isLoaded = true
                    )
                }
            }
        }

        loadUserProfileOnce()
    }

    fun loadUserProfileOnce() {
        viewModelScope.launch {
            try {
                val profile = userRepository.getUserProfile()
                _profileState.value = ProfileUiState(
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    email = profile.email,
                    profilePictureUrl = profile.profilePictureUrl,
                    isLoaded = true
                )
            } catch (e: Exception) {
            }
        }
    }
}