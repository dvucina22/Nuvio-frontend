package com.example.core.network.token

interface ITokenStorage {
    suspend fun saveAccessToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun clear()

    val cachedToken: String?
}