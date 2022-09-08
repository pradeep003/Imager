package com.ftcoding.imager.di

import android.content.Context
import com.ftcoding.imager.api.components.HeaderInterceptor
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.util.Constants.ACCEPT_VERSION
import com.ftcoding.imager.util.Constants.APPLICATION_JSON
import com.ftcoding.imager.util.Constants.CONTENT_TYPE
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {

        val headerInterceptor = HeaderInterceptor(context)
        return OkHttpClient.Builder()
            .addInterceptor {
                val newRequest = it.request().newBuilder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(ACCEPT_VERSION, "v1")
                    .build()
                it.proceed(newRequest)
            }
            .addInterceptor(headerInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build()

    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun prefsStore(@ApplicationContext context: Context): PrefsStore {
        return PrefsStoreImpl(context)
    }
}