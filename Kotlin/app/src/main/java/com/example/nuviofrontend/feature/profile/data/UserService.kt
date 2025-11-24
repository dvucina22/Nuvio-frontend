package com.example.nuviofrontend.feature.profile.data

import com.example.core.network.api.ApiService
import com.example.core.user.dto.ChangePasswordRequest
import com.example.core.user.dto.UpdateUserRequest
import com.example.core.user.dto.UserDto
import retrofit2.HttpException
import java.io.IOException

class UserService(private val api: ApiService) {

    suspend fun getUser(): UserDto {
        val response = api.getUser()
        if (response.isSuccessful) {
            return response.body() ?: throw IOException("Empty response body")
        }
        throw HttpException(response)
    }

    suspend fun updateUser(request: UpdateUserRequest): UserDto {
        val updateResponse = api.updateUser(request)
        if (!updateResponse.isSuccessful) {
            throw HttpException(updateResponse)
        }

        val getUserResponse = api.getUser()
        if (getUserResponse.isSuccessful) {
            return getUserResponse.body() ?: throw IOException("Empty response body")
        }
        throw HttpException(getUserResponse)
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): String {
        val response = api.changePassword(ChangePasswordRequest(oldPassword, newPassword))

        if (response.isSuccessful) {
            return response.body()?.message ?: "Password updated successfully"
        }

        val errorBody = response.errorBody()?.string() ?: ""

        when {
            errorBody.contains("password invalid", ignoreCase = true) -> {
                throw IllegalArgumentException("password_invalid")
            }
            errorBody.contains("password does not meet security requirements", ignoreCase = true) -> {
                throw IllegalArgumentException("password_complexity")
            }
            errorBody.contains("missing required fields", ignoreCase = true) -> {
                throw IllegalArgumentException("missing_fields")
            }
            else -> {
                throw Exception("server_error")
            }
        }
    }
}