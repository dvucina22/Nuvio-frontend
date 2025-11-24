package com.example.nuviofrontend.feature.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuviofrontend.feature.profile.data.ProfilePictureRepository
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
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val profilePictureUrl: String = "",
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
    val genderError: String? = null
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val profilePictureRepository: ProfilePictureRepository
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
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    phoneNumber = profile.phoneNumber,
                    gender = profile.gender,
                    profilePictureUrl = profile.profilePictureUrl,
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

    fun uploadProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingImage = true)

            try {
                val imageUrl = profilePictureRepository.uploadProfilePicture(imageUri)

                _uiState.value = _uiState.value.copy(
                    profilePictureUrl = imageUrl,
                    isUploadingImage = false
                )

                loadUserProfile()
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(isUploadingImage = false)
                _profileEditState.value = ProfileEditState.Error("Failed to upload image: ${e.message()}")
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(isUploadingImage = false)
                _profileEditState.value = ProfileEditState.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isUploadingImage = false)
                _profileEditState.value = ProfileEditState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        gender: String
    ) {
        _uiState.value = _uiState.value.copy(
            firstNameError = null,
            lastNameError = null,
            phoneNumberError = null,
            genderError = null
        )

        var hasError = false

        if (firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(firstNameError = "First name is required")
            hasError = true
        }

        if (lastName.isBlank()) {
            _uiState.value = _uiState.value.copy(lastNameError = "Last name is required")
            hasError = true
        }

        if (hasError) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _profileEditState.value = ProfileEditState.Loading

            try {
                val profile = userRepository.updateUserProfile(
                    firstName = firstName.ifBlank { null },
                    lastName = lastName.ifBlank { null },
                    phoneNumber = phoneNumber.ifBlank { null },
                    gender = gender.ifBlank { null }
                )

                _uiState.value = _uiState.value.copy(
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    phoneNumber = profile.phoneNumber,
                    gender = profile.gender,
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