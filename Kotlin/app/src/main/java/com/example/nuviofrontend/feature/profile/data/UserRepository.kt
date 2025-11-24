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
            phoneNumber = userDto.phoneNumber ?: "",
            gender = userDto.gender ?: "",
            profilePictureUrl = userDto.profilePictureUrl ?: ""
        )

        userPrefs.saveProfile(profile)

        return profile
    }

    suspend fun updateUserProfile(
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        gender: String?,
        profilePictureUrl: String? = null
    ): UserProfile {
        val request = UpdateUserRequest(
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            gender = gender,
            profilePictureUrl = profilePictureUrl
        )

        val userDto = userService.updateUser(request)

        val profile = UserProfile(
            id = userDto.id,
            firstName = userDto.firstName ?: "",
            lastName = userDto.lastName ?: "",
            email = userDto.email,
            phoneNumber = userDto.phoneNumber ?: "",
            gender = userDto.gender ?: "",
            profilePictureUrl = userDto.profilePictureUrl ?: ""
        )

        userPrefs.saveProfile(profile)

        return profile
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        userService.changePassword(oldPassword, newPassword)
    }
}