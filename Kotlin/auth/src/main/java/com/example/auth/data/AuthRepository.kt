package com.example.auth.data

import com.example.core.auth.IAuthRepository
import com.example.core.model.UserProfile
import com.example.core.network.token.IUserPrefs
import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.RegisterRequest
import com.example.core.network.token.ITokenStorage

class AuthRepository(
    private val authService: AuthService,
    private val tokenStorage: ITokenStorage,
    private val userPrefs: IUserPrefs
): IAuthRepository {

    override suspend fun register(
        firstName: String?,
        lastName: String?,
        email: String,
        phoneNumber: String?,
        password: String
    ): Boolean {
        val request = RegisterRequest(firstName, lastName, email, phoneNumber, password)
        authService.register(request)
        return true
    }

    override suspend fun login(email: String, password: String): Boolean {
        val request = LoginRequest(email, password)
        val response = authService.login(request)

        tokenStorage.saveAccessToken(response.token)
        userPrefs.saveProfile(
            UserProfile(
                id = "",
                firstName = response.firstName.orEmpty(),
                lastName = response.lastName.orEmpty(),
                email = response.email.orEmpty(),
                phoneNumber = "",
                gender = response.gender.orEmpty(),
                profilePictureUrl = response.profilePictureURL.orEmpty()
            )
        )
        return true
    }

    override suspend fun loginWithGoogle(idToken: String): Boolean {
        val response = authService.verifyOAuth("google", idToken)

        tokenStorage.saveAccessToken(response.token)
        userPrefs.saveProfile(
            UserProfile(
                id = "",
                firstName = response.firstName.orEmpty(),
                lastName = response.lastName.orEmpty(),
                email = response.email.orEmpty(),
                phoneNumber = "",
                gender = response.gender.orEmpty(),
                profilePictureUrl = response.profilePictureURL.orEmpty()
            )
        )
        return true
    }

    override suspend fun loginWithProvider(provider: String, idToken: String): Boolean {
        val response = authService.verifyOAuth(provider, idToken)

        tokenStorage.saveAccessToken(response.token)
        userPrefs.saveProfile(
            UserProfile(
                id = "",
                firstName = response.firstName.orEmpty(),
                lastName = response.lastName.orEmpty(),
                email = response.email.orEmpty(),
                phoneNumber = "",
                gender = response.gender.orEmpty(),
                profilePictureUrl = response.profilePictureURL.orEmpty()
            )
        )
        return true
    }

    override suspend fun logout() {
        tokenStorage.clear()
        userPrefs.clear()
    }
}
