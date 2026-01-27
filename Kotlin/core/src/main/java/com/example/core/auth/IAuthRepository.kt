package com.example.core.auth

interface IAuthRepository {

    suspend fun register(
        firstName: String?,
        lastName: String?,
        email: String,
        phoneNumber: String?,
        password: String,
        gender: String?,
    ): Boolean

    suspend fun login(email: String, password: String): Boolean

    suspend fun logout()
}
