package com.example.nuviofrontend.core.network.util

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> NetworkResult.Error("Network Error")
            is HttpException -> NetworkResult.Error(
                message = throwable.response()?.message(),
                code = throwable.code()
            )
            else -> NetworkResult.Error("Unexpected Error")
        }
    }
}