package com.example.auth.presentation.login

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.R
import com.example.auth.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.java
import androidx.activity.result.ActivityResult

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String, val isServerError: Boolean = false) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val app: Application,
    private val googleClient: GoogleSignInClient
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    fun login(email: String, password: String) {
        _emailError.value = null
        _passwordError.value = null

        if (email.isBlank() || password.isBlank()) {
            if (email.isBlank()) _emailError.value = app.getString(R.string.error_email_empty)
            if (password.isBlank()) _passwordError.value = app.getString(R.string.error_password_empty)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = app.getString(R.string.error_email_invalid)
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                repository.login(email, password)
                _loginState.value = LoginState.Success
            } catch (e: HttpException) {
                _loginState.value = when (e.code()) {
                    401 -> LoginState.Error(app.getString(R.string.error_login_invalid_credentials))
                    403 -> LoginState.Error(app.getString(R.string.error_login_forbidden))
                    else -> LoginState.Error(app.getString(R.string.error_server_generic, e.code()), isServerError = true)
                }
            } catch (e: IOException) {
                _loginState.value = LoginState.Error(app.getString(R.string.error_network))
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(app.getString(R.string.error_server_generic, -1), isServerError = true)
            }
        }
    }

    fun googleSignInIntent(): Intent = googleClient.signInIntent

    fun handleGoogleResult(result: ActivityResult) {
        val data = result.data
        if (result.resultCode != Activity.RESULT_OK || data == null) {
            _loginState.value = LoginState.Error(app.getString(R.string.error_google_signin_cancelled))
            return
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken.isNullOrBlank()) {
                    _loginState.value = LoginState.Error(app.getString(R.string.error_google_no_idtoken))
                    return@launch
                }

                repository.loginWithGoogle(idToken)
                _loginState.value = LoginState.Success

            } catch (e: ApiException) {
                _loginState.value = LoginState.Error(app.getString(R.string.error_google_signin_cancelled))
            } catch (e: HttpException) {
                _loginState.value = when (e.code()) {
                    401 -> LoginState.Error(app.getString(R.string.error_login_invalid_credentials))
                    403 -> LoginState.Error(app.getString(R.string.error_login_forbidden))
                    else -> LoginState.Error(app.getString(R.string.error_server_generic, e.code()), isServerError = true)
                }
            } catch (e: IOException) {
                _loginState.value = LoginState.Error(app.getString(R.string.error_network))
            } catch (e: Throwable) {
                _loginState.value = LoginState.Error(app.getString(R.string.error_server_generic, -1), isServerError = true)
            }
        }
    }
}
