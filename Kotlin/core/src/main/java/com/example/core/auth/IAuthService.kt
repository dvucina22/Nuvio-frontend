package com.example.core.auth

import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.LoginResponse
import com.example.core.auth.dto.RegisterRequest
import com.example.core.auth.dto.RegisterResponse

interface IAuthService {
    suspend fun register(request: RegisterRequest): RegisterResponse

    suspend fun login(request: LoginRequest): LoginResponse
}
