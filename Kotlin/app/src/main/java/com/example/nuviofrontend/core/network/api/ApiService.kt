package com.example.nuviofrontend.core.network.api

import com.example.nuviofrontend.feature.auth.data.dto.LoginRequest
import com.example.nuviofrontend.feature.auth.data.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}