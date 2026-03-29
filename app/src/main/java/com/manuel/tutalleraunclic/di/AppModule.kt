package com.manuel.tutalleraunclic.di

import com.manuel.tutalleraunclic.data.local.SessionManager
import com.manuel.tutalleraunclic.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSessionManager(tokenManager: TokenManager): SessionManager {
        return SessionManager(tokenManager)
    }
}