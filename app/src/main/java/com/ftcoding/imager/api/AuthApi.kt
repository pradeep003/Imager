package com.ftcoding.imager.api

import com.ftcoding.imager.data.response.Token
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface AuthApi {

    @POST
    suspend fun getToken(
        @Url url: String,
                         @Query("client_id") clientID: String,
                         @Query("client_secret") clientSecret: String,
                         @Query("redirect_uri") redirectURI: String,
                         @Query("code") code: String,
                         @Query("grant_type") grantType: String
    ): Token


}