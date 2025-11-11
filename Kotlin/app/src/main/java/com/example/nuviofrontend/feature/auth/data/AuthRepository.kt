package com.example.nuviofrontend.feature.auth.data

import com.example.nuviofrontend.core.network.token.TokenManager
import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest
import com.example.nuviofrontend.feature.auth.data.dto.RegisterRequest

class AuthRepository(private val authService: AuthService, private val tokenManager: TokenManager) {
    suspend fun register(firstName: String?, lastName: String?, email: String, phoneNumber: String?, password: String
    ): Boolean {
        val request = RegisterRequest(firstName, lastName, email, phoneNumber, password)
        val response = authService.register(request)
        return true
    }

    suspend fun login(email: String, password: String): Boolean {
        val request = LoginRequest(email, password)
        val response = authService.login(request)
        tokenManager.saveAccessToken(response.token)
        return true
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        val res = authService.verifyOAuth("google", idToken)
        tokenManager.saveAccessToken(res.token)
        return true
    }

    suspend fun loginWithProvider(provider: String, idToken: String): Boolean {
        val res = authService.verifyOAuth(provider, idToken)
        tokenManager.saveAccessToken(res.token)
        return true
    }
}