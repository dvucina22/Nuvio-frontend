package com.example.core.network.dto

data class ApiResponse<T>(
    val success: Boolean,
    val data: T
)
