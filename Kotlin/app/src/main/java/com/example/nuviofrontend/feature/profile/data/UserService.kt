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
        val errorBody = response.errorBody()?.string()?.lowercase() ?: ""
        when {
            "old password incorrect" in errorBody -> throw IllegalArgumentException("old_password_incorrect")
            "password does not meet security requirements" in errorBody -> throw IllegalArgumentException("password_complexity")
            "missing required fields" in errorBody -> throw IllegalArgumentException("missing_fields")
            "user not found" in errorBody -> throw IllegalArgumentException("user_not_found")
            else -> throw Exception("server_error")
        }

    }
}