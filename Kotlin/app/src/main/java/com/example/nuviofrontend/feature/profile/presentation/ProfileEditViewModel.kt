package com.example.nuviofrontend.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuviofrontend.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed class ProfileEditState {
    object Idle : ProfileEditState()
    object Loading : ProfileEditState()
    object Success : ProfileEditState()
    data class Error(val message: String) : ProfileEditState()
}

data class ProfileEditUiState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    private val _profileEditState = MutableStateFlow<ProfileEditState>(ProfileEditState.Idle)
    val profileEditState: StateFlow<ProfileEditState> = _profileEditState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val profile = userRepository.getUserProfile()
                _uiState.value = _uiState.value.copy(
                    id = profile.id,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    email = profile.email,
                    phoneNumber = profile.phoneNumber,
                    isLoading = false
                )
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _profileEditState.value = ProfileEditState.Error("Failed to load profile: ${e.message()}")
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _profileEditState.value = ProfileEditState.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _profileEditState.value = ProfileEditState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ) {
        // Clear previous errors
        _uiState.value = _uiState.value.copy(
            firstNameError = null,
            lastNameError = null,
            emailError = null,
            phoneNumberError = null
        )

        // Validate inputs
        var hasError = false

        if (firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(firstNameError = "First name is required")
            hasError = true
        }

        if (lastName.isBlank()) {
            _uiState.value = _uiState.value.copy(lastNameError = "Last name is required")
            hasError = true
        }

        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(emailError = "Email is required")
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = "Invalid email format")
            hasError = true
        }

        if (hasError) {
            return
        }

        // Make API call
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _profileEditState.value = ProfileEditState.Loading

            try {
                val profile = userRepository.updateUserProfile(
                    id = _uiState.value.id, // Pass the stored id
                    firstName = firstName.ifBlank { null },
                    lastName = lastName.ifBlank { null },
                    email = email.ifBlank { null },
                    phoneNumber = phoneNumber.ifBlank { null }
                )

                _uiState.value = _uiState.value.copy(
                    id = profile.id,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    email = profile.email,
                    phoneNumber = profile.phoneNumber,
                    isLoading = false
                )
                _profileEditState.value = ProfileEditState.Success
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _profileEditState.value = ProfileEditState.Error("Failed to update profile: ${e.message()}")
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _profileEditState.value = ProfileEditState.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _profileEditState.value = ProfileEditState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _profileEditState.value = ProfileEditState.Idle
    }
}

