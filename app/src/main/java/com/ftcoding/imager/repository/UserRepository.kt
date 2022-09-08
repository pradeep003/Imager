package com.ftcoding.imager.repository

import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.data.response.User
import com.ftcoding.imager.util.Resource

interface UserRepository {

    suspend fun getMyProfile() : Resource<CurrentUser>

    suspend fun getUserByUsername(username: String) : Resource<User>

    suspend fun updateMyProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        portfolioUrl: String,
        location: String,
        bio: String,
        instagramUsername: String
    ) : Resource<CurrentUser>
}