package com.ftcoding.imager.repository.repository_impl

import com.ftcoding.imager.api.UserApi
import com.ftcoding.imager.components.UnsplashResponseHandler
import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.data.response.User
import com.ftcoding.imager.repository.UserRepository
import com.ftcoding.imager.util.Resource

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {

    override suspend fun getMyProfile(): Resource<CurrentUser> {
        return try {
            val user = api.getMyProfile()
            UnsplashResponseHandler.handleSuccess(user)
        } catch (e: Exception) {
            UnsplashResponseHandler.handleException(e)
        }
    }

    override suspend fun getUserByUsername(username: String): Resource<User> {
        return try {
            val user = api.getUserByUsername(username)
            UnsplashResponseHandler.handleSuccess(user)
        } catch (e: Exception) {
            UnsplashResponseHandler.handleException(e)
        }
    }

    override suspend fun updateMyProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        portfolioUrl: String,
        location: String,
        bio: String,
        instagramUsername: String
    ): Resource<CurrentUser> {

        return try {
            val updatedUser = api.updateMyProfile(username, firstName, lastName, email, portfolioUrl, location, bio, instagramUsername)
            UnsplashResponseHandler.handleSuccess(updatedUser)
        } catch (e: Exception) {
            UnsplashResponseHandler.handleException(e)
        }
    }
}