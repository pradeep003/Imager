package com.ftcoding.imager.di

import com.ftcoding.imager.api.AuthApi
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.AuthRepository
import com.ftcoding.imager.repository.repository_impl.AuthRepositoryImpl
import com.ftcoding.imager.use_cases.auth.GetTokenUseCases
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(client: OkHttpClient, moshi: Moshi): AuthApi{
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://unsplash.com/oauth/token/")
            .client(client)
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        prefsStore: PrefsStore
    ) : AuthRepository {
        return AuthRepositoryImpl(api, prefsStore)
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(repository: AuthRepository) : GetTokenUseCases {
        return GetTokenUseCases(repository)
    }
}