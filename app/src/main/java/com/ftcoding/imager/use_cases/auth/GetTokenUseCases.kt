package com.ftcoding.imager.use_cases.auth

import com.ftcoding.imager.repository.AuthRepository

class GetTokenUseCases(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        clientSecret: String,
        redirectUri: String,
        code: String
    )  {
        repository.getToken(clientSecret, redirectUri, code)
    }
}