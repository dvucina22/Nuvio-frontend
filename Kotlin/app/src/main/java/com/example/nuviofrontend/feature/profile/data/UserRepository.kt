package com.example.nuviofrontend.feature.profile.data

import com.example.core.auth.dto.Role
import com.example.core.model.UserProfile
import com.example.core.network.token.IUserPrefs
import com.example.core.user.dto.UpdateUserRequest
import com.example.core.user.dto.UserListItemDto

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
            profilePictureUrl = userDto.profilePictureUrl ?: "",
            roles = userDto.roles ?: emptyList()
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
            profilePictureUrl = userDto.profilePictureUrl ?: "",
            roles = userDto.roles ?: emptyList()
        )

        userPrefs.saveProfile(profile)

        return profile
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        userService.changePassword(oldPassword, newPassword)
    }

    suspend fun loadAllUsers(): List<UserListItemDto> =
        userService.filterUsersByName("")

    suspend fun filterUsersByName(query: String): List<UserListItemDto> =
        userService.filterUsersByName(query.trim())

    suspend fun deactivateUser(userId: String) {
        userService.deactivateUser(userId)
    }

    suspend fun updateUserRole(userId: String, newRole: Role) {
        val allRoles = userService.getAllRoles()
        val currentUsers = userService.filterUsersByName("")
        val user = currentUsers.firstOrNull { it.id == userId }
        val currentRole = user?.roles?.firstOrNull()

        if (currentRole != null && currentRole.id != newRole.id) {
            userService.removeUserRole(userId, currentRole.id)
        }
        if (currentRole == null || currentRole.id != newRole.id) {
            userService.addUserRole(userId, newRole.id)
        }
    }

    suspend fun getAllRoles(): List<Role> = userService.getAllRoles()

    suspend fun setUserPrimaryRole(userId: String, newRoleId: Int, currentRoles: List<Role>) {
        val currentIds = currentRoles.map { it.id }.toSet()

        currentIds.filter { it != newRoleId }.forEach { roleId ->
            userService.removeUserRole(userId, roleId)
        }

        if (!currentIds.contains(newRoleId)) {
            userService.addUserRole(userId, newRoleId)
        }
    }

    private fun com.example.core.user.dto.UserDto.toUserProfile(): UserProfile {
        return UserProfile(
            id = this.id,
            firstName = this.firstName ?: "",
            lastName = this.lastName ?: "",
            email = this.email,
            phoneNumber = this.phoneNumber ?: "",
            gender = this.gender ?: "",
            profilePictureUrl = this.profilePictureUrl ?: "",
            roles = this.roles ?: emptyList()
        )
    }
}
