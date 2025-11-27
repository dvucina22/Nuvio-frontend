package com.example.core.auth

interface IAuthRepository {
    suspend fun register(
        firstName: String?,
        lastName: String?,
        email: String,
        phoneNumber: String?,
        password: String,
        gender: String?,
        profilePictureUrl: String?
    ): Boolean

    suspend fun login(email: String, password: String): Boolean

    suspend fun loginWithGoogle(idToken: String): Boolean

    suspend fun loginWithProvider(provider: String, idToken: String): Boolean

    suspend fun logout()
}