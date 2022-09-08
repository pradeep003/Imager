package com.ftcoding.imager.api.components

import android.content.Context
import android.util.Log
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.util.Constants.API_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    @ApplicationContext context: Context
) : Interceptor {

    private val prefsStore: PrefsStore = PrefsStoreImpl(context)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var token: String?
        runBlocking {
            token = prefsStore.getToken.first()
        }
        var auth = "Client-ID $API_KEY"
        if (token != null) {
            auth += ",Bearer $token"
        }
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Authorization", auth)
            .build()
        return chain.proceed(request)
    }
}