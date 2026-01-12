package com.example.auth_oauth.presentation

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth_oauth.R
import com.example.core.auth.IOAuthRepository
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

sealed class GoogleLoginState {
    object Idle : GoogleLoginState()
    object Loading : GoogleLoginState()
    object Success : GoogleLoginState()
    data class Error(val message: String) : GoogleLoginState()
}

@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val repo: IOAuthRepository,
    private val app: Application,
    private val googleClient: GoogleSignInClient
) : ViewModel() {

    private val _state = MutableStateFlow<GoogleLoginState>(GoogleLoginState.Idle)
    val state = _state.asStateFlow()

    fun signInIntent(): Intent = googleClient.signInIntent

    fun handleResult(result: ActivityResult) {
        val data = result.data
        if (result.resultCode != Activity.RESULT_OK || data == null) {
            _state.value = GoogleLoginState.Error(app.getString(R.string.error_google_signin_cancelled))
            return
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        viewModelScope.launch {
            _state.value = GoogleLoginState.Loading
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken.isNullOrBlank()) {
                    _state.value = GoogleLoginState.Error(app.getString(R.string.error_google_no_idtoken))
                    return@launch
                }

                repo.loginWithGoogle(idToken)
                _state.value = GoogleLoginState.Success

            } catch (e: ApiException) {
                _state.value = GoogleLoginState.Error(app.getString(R.string.error_google_signin_cancelled))
            } catch (e: HttpException) {
                _state.value = GoogleLoginState.Error(app.getString(R.string.error_server_generic, e.code()))
            } catch (e: IOException) {
                _state.value = GoogleLoginState.Error(app.getString(R.string.error_network))
            } catch (e: Throwable) {
                _state.value = GoogleLoginState.Error(app.getString(R.string.error_server_generic, -1))
            }
        }
    }
}
