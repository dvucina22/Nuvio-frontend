package com.example.nuviofrontend.feature.profile.data

import com.example.core.network.api.ApiService
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
}