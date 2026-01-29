package com.example.nuviofrontend.feature.profile.presentation

import android.app.Application
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.R
import com.example.nuviofrontend.feature.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

data class ChangePasswordErrors(
    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null
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
    private val _errors = MutableStateFlow(ChangePasswordErrors())
    val errors = MutableStateFlow(ChangePasswordErrors())

    fun clearErrors() {
        oldPasswordError.value = null
        newPasswordError.value = null
        confirmPasswordError.value = null
        generalError.value = null
    }

    private fun validate(): Boolean {
        var valid = true
        errors.value = ChangePasswordErrors()

        if (oldPassword.value.isBlank()) {
            errors.value = errors.value.copy(oldPasswordError = app.getString(R.string.error_old_password_empty))
            valid = false
        }

        if (newPassword.value.isBlank()) {
            errors.value = errors.value.copy(newPasswordError = app.getString(R.string.error_new_password_empty))
            valid = false
        } else if (newPassword.value.length < 8 || !newPassword.value.any { it.isUpperCase() } || !newPassword.value.any { it.isDigit() }) {
            errors.value = errors.value.copy(newPasswordError = app.getString(R.string.error_password_complexity))
            valid = false
        }

        if (confirmPassword.value.isBlank()) {
            errors.value = errors.value.copy(confirmPasswordError = app.getString(R.string.error_confirm_password_empty))
            valid = false
        } else if (newPassword.value != confirmPassword.value) {
            errors.value = errors.value.copy(confirmPasswordError = app.getString(R.string.error_passwords_mismatch))
            valid = false
        }

        return valid
    }

    fun changePassword(userEmail: String) {
        if (!validate()) return

        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            try {
                val transformedOldPassword = transformPassword(oldPassword.value, userEmail)
                val transformedNewPassword = transformPassword(newPassword.value, userEmail)

                userRepository.changePassword(transformedOldPassword, transformedNewPassword)

                _changePasswordState.value = ChangePasswordState.Success
            } catch (e: IllegalArgumentException) {
                when (e.message) {
                    "old_password_incorrect" -> errors.value =
                        errors.value.copy(oldPasswordError = app.getString(R.string.error_old_password_invalid))
                    "password_complexity" -> errors.value =
                        errors.value.copy(newPasswordError = app.getString(R.string.error_password_complexity))
                    "missing_fields" -> errors.value =
                        errors.value.copy(generalError = app.getString(R.string.error_missing_fields))
                    "user_not_found" -> errors.value =
                        errors.value.copy(generalError = app.getString(R.string.error_missing_fields))
                    else -> errors.value =
                        errors.value.copy(generalError = app.getString(R.string.error_unknown))
                }
                _changePasswordState.value = ChangePasswordState.Error(errors.value.generalError ?: "")
            } catch (e: Exception) {
                errors.value =
                    errors.value.copy(generalError = app.getString(R.string.error_server_generic2, e.message ?: ""))
                _changePasswordState.value = ChangePasswordState.Error(errors.value.generalError ?: "")
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

    private fun transformPassword(password: String, email: String): String {
        val normalizedEmail = email.trim().lowercase()

        val salt = MessageDigest
            .getInstance("SHA-256")
            .digest(normalizedEmail.toByteArray(Charsets.UTF_8))

        val iterations = 120_000
        val keyLengthBits = 256

        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits)
        val key = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded

        return Base64.encodeToString(key, Base64.NO_WRAP)
    }

}