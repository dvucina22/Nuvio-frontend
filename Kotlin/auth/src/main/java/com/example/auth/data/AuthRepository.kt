package com.example.auth.data

import com.example.core.auth.IAuthRepository
import com.example.core.model.UserProfile
import com.example.core.network.token.IUserPrefs
import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.RegisterRequest
import com.example.core.network.token.ITokenStorage
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64

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
        password: String,
        gender: String?,
    ): Boolean {
        val request = RegisterRequest(
            firstName,
            lastName,
            email,
            phoneNumber,
            transformPassword(password, email),
            gender
        )

        authService.register(request)
        return true
    }

    override suspend fun login(email: String, password: String): Boolean {
        val request = LoginRequest(email, transformPassword(password, email))
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
                profilePictureUrl = response.profilePictureURL.orEmpty(),
                roles = response.roles ?: emptyList()
            )
        )

        return true
    }

    override suspend fun logout() {
        tokenStorage.clear()
        userPrefs.clear()
    }

    private fun transformPassword(password: String, email: String): String {
        val normalizedEmail = email.trim().lowercase()

        val salt = MessageDigest
            .getInstance("SHA-256")
            .digest(normalizedEmail.toByteArray(Charsets.UTF_8))

        val iterations = 120_000
        val keyLengthBits = 256

        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits)
        val key = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded

        return Base64.encodeToString(key, Base64.NO_WRAP)
    }
}
