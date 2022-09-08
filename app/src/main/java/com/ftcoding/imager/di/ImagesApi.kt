package com.ftcoding.imager.di

import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.repository.ImageRepository
import com.ftcoding.imager.repository.repository_impl.ImageRepositoryImpl
import com.ftcoding.imager.use_cases.image.*
import com.ftcoding.imager.util.Constants
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
object ImagesApi {

    @Provides
    @Singleton
    fun provideImagesApi(client: OkHttpClient, moshi: Moshi) : ImagesApi {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
            .create(ImagesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImagesRepository(api: ImagesApi): ImageRepository {
        return ImageRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideImagesUseCases( repository: ImageRepository, api: ImagesApi) : ImageUseCases {
            return ImageUseCases(
                getPhotoByIdUseCases = GetPhotoByIdUseCases(repository),
                likePhotoUseCases = LikePhotoUseCases(repository),
                unLikePhotoUseCases = UnLikePhotoUseCases(repository),
                downloadPhotoUseCases = DownloadPhotoUseCases(repository),
                getAllPagingImageUseCases = GetAllPagingImageUseCases(api),
                getAllSearchedImageUseCases = GetAllSearchedImageUseCases(api)
            )
    }
}