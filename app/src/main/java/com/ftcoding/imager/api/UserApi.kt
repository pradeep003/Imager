package com.ftcoding.imager.api

import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.data.response.User
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("/me")
    suspend fun getMyProfile() : CurrentUser

    @GET("/users/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ) : User

    @GET("/users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ) : CurrentUser

    @GET("/users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") username: String,
        @Query("page") page: Int
    ) : List<ImageResponse>

    @GET("/users/{username}/likes")
    suspend fun getUserLikePhotos(
       @Path("username") username: String,
       @Query("page") page: Int
    ) : List<ImageResponse>

    @GET("/photos/{id}")
    suspend fun getPhotoByUsername(
        @Path("id") id: String
    )

    @PUT("/me")
    suspend fun updateMyProfile(
        @Query("username") username: String,
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String,
        @Query("email") email: String,
        @Query("url") portfolioUrl: String,
        @Query("location") location: String,
        @Query("bio") bio: String,
        @Query("instagram_username") instagramUsername: String
    ) : CurrentUser
}