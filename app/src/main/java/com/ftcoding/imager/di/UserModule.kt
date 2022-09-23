package com.ftcoding.imager.di

import com.ftcoding.imager.api.UserApi
import com.ftcoding.imager.repository.UserRepository
import com.ftcoding.imager.repository.repository_impl.UserRepositoryImpl
import com.ftcoding.imager.use_cases.user.*
import com.ftcoding.imager.util.Constants.BASE_URL
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
object UserModule {

    @Provides
    @Singleton
    fun provideUserApi(client: OkHttpClient, moshi: Moshi) : UserApi {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        api : UserApi
    ) : UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserUseCases(repository: UserRepository, api: UserApi): UserUseCases {
        return UserUseCases(
            getMyProfileUseCases = GetMyProfileUseCases(repository),
            getUserByUsernameUseCases = GetUserByUsernameUseCases(repository),
            updateMyProfileUseCases = UpdateMyProfileUseCases(repository),
            getUserLikedPhotoUseCases = GetCurrentUserLikedPhotoUseCases(api),
            getUserPhotoUseCases = GetUserPhotoUseCases(api)
        )
    }

}









