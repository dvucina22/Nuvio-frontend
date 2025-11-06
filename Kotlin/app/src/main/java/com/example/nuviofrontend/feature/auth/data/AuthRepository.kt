package com.example.nuviofrontend.feature.auth.data

import com.example.nuviofrontend.core.network.token.TokenManager
import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest

class AuthRepository(private val authService: AuthService, private val tokenManager: TokenManager) {
    suspend fun login(email: String, password: String): Boolean {
        val request = LoginRequest(email, password)
        val response = authService.login(request)
        tokenManager.saveAccessToken(response.token)
        return true
    }
}