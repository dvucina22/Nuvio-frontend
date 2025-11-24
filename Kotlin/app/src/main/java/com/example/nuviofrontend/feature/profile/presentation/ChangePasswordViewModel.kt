package com.example.nuviofrontend.feature.profile.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.R
import com.example.nuviofrontend.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

data class ChangePasswordUiState(
    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val app: Application
) : ViewModel() {

    val oldPassword = MutableStateFlow("")
    val newPassword = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    val oldPasswordError = MutableStateFlow<String?>(null)
    val newPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = MutableStateFlow<String?>(null)
    val generalError = MutableStateFlow<String?>(null)

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState = _changePasswordState.asStateFlow()

    fun clearErrors() {
        oldPasswordError.value = null
        newPasswordError.value = null
        confirmPasswordError.value = null
        generalError.value = null
    }

    private fun validate(): Boolean {
        var valid = true
        clearErrors()

        if (oldPassword.value.isBlank()) {
            oldPasswordError.value = app.getString(R.string.error_old_password_empty)
            valid = false
        }

        if (newPassword.value.isBlank()) {
            newPasswordError.value = app.getString(R.string.error_new_password_empty)
            valid = false
        } else if (newPassword.value.length < 8 || !newPassword.value.any { it.isUpperCase() } || !newPassword.value.any { it.isDigit() }) {
            newPasswordError.value = app.getString(R.string.error_password_complexity)
            valid = false
        }

        if (confirmPassword.value.isBlank()) {
            confirmPasswordError.value = app.getString(R.string.error_confirm_password_empty)
            valid = false
        } else if (newPassword.value != confirmPassword.value) {
            confirmPasswordError.value = app.getString(R.string.error_passwords_mismatch)
            valid = false
        }

        return valid
    }

    fun changePassword() {
        if (!validate()) return

        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading

            try {
                userRepository.changePassword(oldPassword.value, newPassword.value)
                _changePasswordState.value = ChangePasswordState.Success
            } catch (e: IllegalArgumentException) {
                when (e.message) {
                    "password_invalid" -> oldPasswordError.value = app.getString(R.string.error_old_password_invalid)
                    "password_complexity" -> newPasswordError.value = app.getString(R.string.error_password_complexity)
                    "missing_fields" -> generalError.value = app.getString(R.string.error_missing_fields)
                    else -> generalError.value = app.getString(R.string.error_unknown)
                }
                _changePasswordState.value = ChangePasswordState.Error(generalError.value ?: "")
            } catch (e: Exception) {
                generalError.value = app.getString(R.string.error_server_generic2, e.message ?: "")
                _changePasswordState.value = ChangePasswordState.Error(generalError.value ?: "")
            }
        }
    }



    fun resetState() {
        clearErrors()
        _changePasswordState.value = ChangePasswordState.Idle
    }

    fun resetAllFields() {
        oldPassword.value = ""
        newPassword.value = ""
        confirmPassword.value = ""

        oldPasswordError.value = null
        newPasswordError.value = null
        confirmPasswordError.value = null
        generalError.value = null

        _changePasswordState.value = ChangePasswordState.Idle
    }


}