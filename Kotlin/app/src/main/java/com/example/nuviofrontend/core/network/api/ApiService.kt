package com.example.nuviofrontend.core.network.api

import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest
import com.example.nuviofrontend.feature.auth.data.dto.LoginResponse
import com.example.nuviofrontend.feature.auth.data.dto.RegisterRequest
import com.example.nuviofrontend.feature.auth.data.dto.RegisterResponse
import com.example.nuviofrontend.feature.auth.data.dto.OAuthVerifyRequest
import com.example.nuviofrontend.feature.auth.data.dto.OAuthVerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("oauth/{provider}/verify")
    suspend fun verifyOAuth(@Path("provider") provider: String, @Body request: OAuthVerifyRequest): Response<OAuthVerifyResponse>
}