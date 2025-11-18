package com.example.core.network.api

import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.LoginResponse
import com.example.core.auth.dto.OAuthVerifyRequest
import com.example.core.auth.dto.OAuthVerifyResponse
import com.example.core.auth.dto.RegisterRequest
import com.example.core.auth.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("accounts/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("accounts/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("accounts/oauth/{provider}/verify")
    suspend fun verifyOAuth(@Path("provider") provider: String, @Body request: OAuthVerifyRequest): Response<OAuthVerifyResponse>
}