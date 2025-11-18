package com.example.nuviofrontend.di
import com.example.auth.data.AuthRepository
import com.example.auth.data.AuthService
import com.example.core.network.token.IUserPrefs
import com.example.core.network.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.core.network.token.ITokenStorage

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideAuthService(api: ApiService): AuthService =
        AuthService(api)

    @Provides
    fun provideAuthRepository(service: AuthService, tokenStorage: ITokenStorage, userPrefs: IUserPrefs): AuthRepository =
        AuthRepository(service, tokenStorage, userPrefs)
}
