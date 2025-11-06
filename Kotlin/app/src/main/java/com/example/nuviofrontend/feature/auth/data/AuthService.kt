package com.example.nuviofrontend.feature.auth.data

import com.example.nuviofrontend.core.network.api.ApiService
import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest
import com.example.nuviofrontend.feature.auth.data.dto.LoginResponse
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
}