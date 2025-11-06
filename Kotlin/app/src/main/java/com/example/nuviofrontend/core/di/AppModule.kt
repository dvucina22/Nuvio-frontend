package com.example.nuviofrontend.di

import android.content.Context
import com.example.nuviofrontend.core.network.api.ApiClient
import com.example.nuviofrontend.core.network.api.ApiService
import com.example.nuviofrontend.core.network.interceptor.AuthInterceptor
import com.example.nuviofrontend.core.network.token.TokenManager
import com.example.nuviofrontend.feature.auth.data.AuthRepository
import com.example.nuviofrontend.feature.auth.data.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

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
        tokenManager: TokenManager
    ): AuthRepository = AuthRepository(authService, tokenManager)
}
