package com.example.auth_oauth.data
import com.example.core.auth.IOAuthService
import com.example.core.auth.dto.OAuthVerifyRequest
import com.example.core.auth.dto.OAuthVerifyResponse
import com.example.core.network.api.ApiService
import retrofit2.HttpException
import java.io.IOException

class OAuthService(private val api: ApiService): IOAuthService {

    override suspend fun verifyOAuth(provider: String, idToken: String): OAuthVerifyResponse {
        val body = OAuthVerifyRequest(idToken)
        val response = api.verifyOAuth(provider, body)

        if (response.isSuccessful) {
            return response.body() ?: throw IOException("Empty response body")
        }

        throw HttpException(response)
    }
}
