package com.example.nuviofrontend.feature.auth.presentation.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuviofrontend.R
import com.example.nuviofrontend.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val app: Application
) : ViewModel() {

    // UI state
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val phoneNumber = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    val emailError = MutableStateFlow<String?>(null)
    val passwordError = MutableStateFlow<String?>(null)
    val confirmPasswordError = MutableStateFlow<String?>(null)
    val generalError = MutableStateFlow<String?>(null)

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState = _registerState.asStateFlow()

    fun clearErrors() {
        emailError.value = null
        passwordError.value = null
        confirmPasswordError.value = null
        generalError.value = null
    }

    private fun validate(): Boolean {
        var valid = true
        clearErrors()

        if (email.value.isBlank()) {
            emailError.value = app.getString(R.string.error_email_empty)
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            emailError.value = app.getString(R.string.error_email_invalid)
            valid = false
        }

        if (password.value.isBlank()) {
            passwordError.value = app.getString(R.string.error_password_required)
            valid = false
        } else if (password.value.length < 8 || !password.value.any { it.isUpperCase() } || !password.value.any { it.isDigit() }) {
            passwordError.value = app.getString(R.string.error_password_complexity)
            valid = false
        }

        if (confirmPassword.value.isBlank()) {
            confirmPasswordError.value = app.getString(R.string.error_confirm_password_empty)
            valid = false
        } else if (password.value != confirmPassword.value) {
            confirmPasswordError.value = app.getString(R.string.error_passwords_mismatch)
            valid = false
        }

        return valid
    }

    fun register() {
        if (!validate()) return

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                repository.register(
                    firstName = firstName.value.ifEmpty { null },
                    lastName = lastName.value.ifEmpty { null },
                    email = email.value,
                    phoneNumber = phoneNumber.value.ifEmpty { null },
                    password = password.value
                )
                _registerState.value = RegisterState.Success
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                when {
                    errorBody.contains("email already exists", ignoreCase = true) -> emailError.value = app.getString(R.string.error_email_exists)
                    errorBody.contains("password", ignoreCase = true) -> passwordError.value = app.getString(R.string.error_password_complexity)
                    else -> generalError.value = "Server greška: ${e.code()}"
                }
            }
        }
    }
}