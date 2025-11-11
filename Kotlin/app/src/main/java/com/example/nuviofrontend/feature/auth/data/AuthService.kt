package com.example.nuviofrontend.feature.auth.data

import com.example.nuviofrontend.core.network.api.ApiService
import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest
import com.example.nuviofrontend.feature.auth.data.dto.LoginResponse
import com.example.nuviofrontend.feature.auth.data.dto.OAuthVerifyRequest
import com.example.nuviofrontend.feature.auth.data.dto.OAuthVerifyResponse
import retrofit2.HttpException
import java.io.IOException

class AuthService(private val api: ApiService) {
    suspend fun login(request: LoginRequest): LoginResponse {
        try {
            val response = api.login(request)
            if (response.isSuccessful) {
                return response.body() ?: throw IOException("Empty response body")
            }
            else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun verifyOAuth(provider: String, idToken: String): OAuthVerifyResponse {
        val response = api.verifyOAuth(provider, OAuthVerifyRequest(idToken = idToken))
        if (response.isSuccessful) return response.body() ?: throw IOException("Empty response body")
        throw HttpException(response)
    }
}