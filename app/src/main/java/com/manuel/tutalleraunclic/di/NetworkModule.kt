package com.manuel.tutalleraunclic.di

import com.manuel.tutalleraunclic.BuildConfig
import com.manuel.tutalleraunclic.data.network.ApiService
import com.manuel.tutalleraunclic.data.network.AuthApiService
import com.manuel.tutalleraunclic.data.network.AuthInterceptor
import com.manuel.tutalleraunclic.data.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * URL base del backend Django.
     * Se configura en build.gradle.kts > defaultConfig > buildConfigField.
     * - Emulador:        http://10.0.2.2:8000/
     * - Dispositivo:     http://192.168.X.X:8000/
     * - ngrok:           https://xxxx.ngrok-free.app/
     * - Producción:      https://tu-dominio.com/
     */
    private val BASE_URL = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

    /**
     * Retrofit sin autenticación, usado solo para el endpoint de refresh
     * dentro del TokenAuthenticator (evita dependencia circular con OkHttpClient principal).
     */
    @Provides
    @Singleton
    fun provideAuthApiService(loggingInterceptor: HttpLoggingInterceptor): AuthApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("ngrok-skip-browser-warning", "true")
                        .header("Accept", "application/json")
                        .build()
                )
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("ngrok-skip-browser-warning", "true")
                        .header("Accept", "application/json")
                        .build()
                )
            }
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
