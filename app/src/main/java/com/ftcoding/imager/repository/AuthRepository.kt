package com.ftcoding.imager.repository

import com.ftcoding.imager.data.response.Token
import com.ftcoding.imager.util.Resource


interface AuthRepository {

    suspend fun getToken(
        clientSecret: String,
        redirectUri: String,
        code: String
    ) : Resource<Token>
}