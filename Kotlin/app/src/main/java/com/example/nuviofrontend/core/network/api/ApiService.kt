package com.example.nuviofrontend.core.network.api

import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest
import com.example.nuviofrontend.feature.auth.data.dto.LoginResponse
import com.example.nuviofrontend.feature.auth.data.dto.RegisterRequest
import com.example.nuviofrontend.feature.auth.data.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}