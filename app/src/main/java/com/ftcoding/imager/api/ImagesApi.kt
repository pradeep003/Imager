package com.ftcoding.imager.api

import com.ftcoding.imager.data.response.*
import retrofit2.http.*

interface ImagesApi {

    @GET("/photos")
    suspend fun getList(
        @Query("page") page: Int
    ): List<ImageResponse>

    @GET("/collections")
    suspend fun getCollections(
        @Query("page") page: Int
    ): List<CollectionResponse>

    // get photo by id
    @GET("/photos/{id}")
    suspend fun getPhotoById(
        @Path("id") id: String
    ): ImageResponse

    @GET("/search/photos")
    suspend fun searchPhoto(
        @Query("page") page: Int,
        @Query("query") query: String
    ) : SearchPhoto

    @POST("/photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String
    ) : FavouritePhoto

    @DELETE("/photos/{id}/like")
    suspend fun unLikePhoto(
        @Path("id") id: String
    ) : FavouritePhoto

    @GET("/photos/{id}/download")
    suspend fun downloadPhoto(
        @Path("id") id: String
    ) : DownloadUrl

    @GET("/collections/{id}/photos")
    suspend fun getCollectionPhotosById(
        @Path("id") id: String,
        @Query("page") page: Int
    ) : List<ImageResponse>
}