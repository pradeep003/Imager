package com.ftcoding.imager.use_cases.user

import com.ftcoding.imager.R
import com.ftcoding.imager.data.response.User
import com.ftcoding.imager.repository.UserRepository
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.SimpleResource
import com.ftcoding.imager.util.UiText

class GetUserByUsernameUseCases (
    private val repository: UserRepository
        ) {

    suspend operator fun invoke(username: String) : Resource<User> {
        if (username.isBlank()) {
            return Resource.Error(uiText = UiText.StringResource(R.string.username_is_empty))
        }
        return repository.getUserByUsername(username)
    }
}