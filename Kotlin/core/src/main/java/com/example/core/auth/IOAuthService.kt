package com.example.core.auth

import com.example.core.auth.dto.OAuthVerifyResponse

interface IOAuthService {
    suspend fun verifyOAuth(provider: String, idToken: String): OAuthVerifyResponse
}
