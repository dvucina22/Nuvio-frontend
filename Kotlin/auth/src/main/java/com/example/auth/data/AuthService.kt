package com.example.auth.data

import com.example.core.auth.IAuthService
import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.LoginResponse
import com.example.core.auth.dto.RegisterRequest
import com.example.core.auth.dto.RegisterResponse
import com.example.core.network.api.ApiService
import retrofit2.HttpException
import java.io.IOException

class AuthService(private val api: ApiService): IAuthService {

    override suspend fun register(request: RegisterRequest): RegisterResponse {
        val response = api.register(request)
        if (response.isSuccessful) {
            return response.body() ?: throw IOException("Empty response body")
        }
        throw HttpException(response)
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(request)
        if (response.isSuccessful) {
            return response.body() ?: throw IOException("Empty response body")
        }
        throw HttpException(response)
    }
}
