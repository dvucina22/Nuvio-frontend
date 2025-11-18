package com.example.core.network.di

import com.example.core.network.token.ITokenStorage
import com.example.core.network.token.IUserPrefs
import com.example.core.network.token.TokenManager
import com.example.core.network.token.UserPrefs
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreBindingsModule {

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        impl: TokenManager
    ): ITokenStorage

    @Binds
    @Singleton
    abstract fun bindUserPrefs(
        impl: UserPrefs
    ): IUserPrefs
}
