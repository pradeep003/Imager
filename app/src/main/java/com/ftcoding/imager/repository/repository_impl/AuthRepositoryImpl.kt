package com.ftcoding.imager.repository.repository_impl

import android.util.Log
import com.ftcoding.imager.api.AuthApi
import com.ftcoding.imager.components.UnsplashResponseHandler
import com.ftcoding.imager.data.response.Token
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.AuthRepository
import com.ftcoding.imager.util.Constants.API_KEY
import com.ftcoding.imager.util.Resource

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefsStore: PrefsStore
) : AuthRepository {
    override suspend fun getToken(
        clientSecret: String,
        redirectUri: String,
        code: String
    ): Resource<Token> {

        return try {
            val token = api.getToken(
                "https://unsplash.com/oauth/token",
                API_KEY,
                clientSecret,
                redirectUri,
                code,
                "authorization_code"
            )
            Log.e("access token", token.accessToken)
            // saving access token in prefsStore
            prefsStore.saveToken(token.accessToken)
            UnsplashResponseHandler.handleSuccess(token)
        } catch (e: Exception) {
            UnsplashResponseHandler.handleException(e)
        }

    }


}