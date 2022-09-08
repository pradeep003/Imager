package com.ftcoding.imager.use_cases.user

import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.repository.UserRepository
import com.ftcoding.imager.util.Resource

class GetMyProfileUseCases(
    private val repository: UserRepository
) {

    suspend operator fun invoke(): Resource<CurrentUser> {
        return repository.getMyProfile()
    }
}