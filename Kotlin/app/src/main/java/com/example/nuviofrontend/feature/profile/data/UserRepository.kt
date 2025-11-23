package com.example.nuviofrontend.feature.profile.data

import com.example.core.model.UserProfile
import com.example.core.network.token.IUserPrefs
import com.example.core.user.dto.UpdateUserRequest

class UserRepository(
    private val userService: UserService,
    private val userPrefs: IUserPrefs
) {

    suspend fun getUserProfile(): UserProfile {
        val userDto = userService.getUser()

        val profile = UserProfile(
            id = userDto.id,
            firstName = userDto.firstName ?: "",
            lastName = userDto.lastName ?: "",
            email = userDto.email,
            phoneNumber = "",
            gender = userDto.gender ?: "",
            profilePictureUrl = userDto.profilePictureUrl ?: ""
        )

        userPrefs.saveProfile(profile)

        return profile
    }

    suspend fun updateUserProfile(
        id: String,
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?
    ): UserProfile {
        val request = UpdateUserRequest(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber
        )

        val userDto = userService.updateUser(request)

        val profile = UserProfile(
            id = userDto.id,
            firstName = userDto.firstName ?: "",
            lastName = userDto.lastName ?: "",
            email = userDto.email,
            phoneNumber = phoneNumber ?: "",
            gender = userDto.gender ?: "",
            profilePictureUrl = userDto.profilePictureUrl ?: ""
        )

        userPrefs.saveProfile(profile)

        return profile
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): String {
        return userService.changePassword(oldPassword, newPassword)
    }
}