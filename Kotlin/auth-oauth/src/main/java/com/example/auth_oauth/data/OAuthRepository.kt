package com.example.auth_oauth.data
import com.example.core.auth.IOAuthRepository
import com.example.core.model.UserProfile
import com.example.core.network.token.IUserPrefs
import com.example.core.network.token.ITokenStorage

class OAuthRepository(
    private val oauthService: OAuthService,
    private val tokenStorage: ITokenStorage,
    private val userPrefs: IUserPrefs
): IOAuthRepository {

    override suspend fun loginWithGoogle(idToken: String): Boolean {
        return loginWithProvider("google", idToken)
    }

    override suspend fun loginWithProvider(provider: String, idToken: String): Boolean {
        val response = oauthService.verifyOAuth(provider, idToken)

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
}
