package com.example.auth_oauth

import android.content.Context
import com.example.core.auth.IOAuthRepository
import com.example.core.network.api.ApiService
import com.example.core.network.token.IUserPrefs
import com.example.core.network.token.ITokenStorage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.auth_oauth.R
import com.example.auth_oauth.data.OAuthRepository
import com.example.auth_oauth.data.OAuthService

@Module
@InstallIn(SingletonComponent::class)
object AuthOAuthModule {

    @Provides
    @Singleton
    fun provideGoogleClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.server_client_id))
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideOAuthService(api: ApiService): OAuthService {
        return OAuthService(api)
    }

    @Provides
    @Singleton
    fun provideOAuthRepository(
        oauthService: OAuthService,
        tokenStorage: ITokenStorage,
        userPrefs: IUserPrefs
    ): IOAuthRepository {
        return OAuthRepository(oauthService, tokenStorage, userPrefs)
    }
}