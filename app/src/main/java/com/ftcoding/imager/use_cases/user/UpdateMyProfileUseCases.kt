package com.ftcoding.imager.use_cases.user

import com.ftcoding.imager.R
import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.repository.UserRepository
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText

class UpdateMyProfileUseCases(
    private val repository: UserRepository
) {

    suspend operator fun invoke(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        portfolioUrl: String,
        location: String,
        bio: String,
        instagramUsername: String
    ): Resource<CurrentUser> {
        if (username.isBlank()) {
            return Resource.Error(UiText.StringResource(R.string.username_is_empty))
        }
        return repository.updateMyProfile(
            username,
            firstName,
            lastName,
            email,
            portfolioUrl,
            location,
            bio,
            instagramUsername
        )
    }
}