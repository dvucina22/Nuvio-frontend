package com.example.nuviofrontend.di

import android.content.Context
import com.example.nuviofrontend.core.network.api.ApiClient
import com.example.nuviofrontend.core.network.api.ApiService
import com.example.nuviofrontend.core.network.interceptor.AuthInterceptor
import com.example.nuviofrontend.core.network.token.TokenManager
import com.example.nuviofrontend.feature.auth.data.AuthRepository
import com.example.nuviofrontend.feature.auth.data.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import com.example.nuviofrontend.R
import com.example.nuviofrontend.core.network.token.UserPrefs

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Provides @Singleton
    fun provideUserPrefs(@ApplicationContext context: Context): UserPrefs =
        UserPrefs(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit =
        ApiClient.create(authInterceptor)

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthService(apiService: ApiService): AuthService =
        AuthService(apiService)

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService,
        tokenManager: TokenManager,
        userPrefs: UserPrefs
    ): AuthRepository = AuthRepository(authService, tokenManager, userPrefs)

    @Provides @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val serverClientId = context.getString(R.string.server_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(serverClientId)
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}
