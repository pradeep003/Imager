package com.ftcoding.imager.repository

import com.ftcoding.imager.data.response.*
import com.ftcoding.imager.util.Resource

interface ImageRepository {

    suspend fun getPhotoById(id: String): Resource<ImageResponse>

    suspend fun likePhoto(id: String) : Resource<FavouritePhoto>

    suspend fun unLikePhoto(id: String) : Resource<FavouritePhoto>

//    suspend fun searchPhoto(query: String) : Resource<SearchPhoto>

    suspend fun downloadPhoto(id: String) : Resource<DownloadUrl>
}