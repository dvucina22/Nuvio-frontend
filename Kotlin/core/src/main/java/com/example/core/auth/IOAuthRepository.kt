package com.example.core.auth

interface IOAuthRepository {

    suspend fun loginWithGoogle(idToken: String): Boolean

    suspend fun loginWithProvider(provider: String, idToken: String): Boolean
}
