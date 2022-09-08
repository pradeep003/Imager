package com.ftcoding.imager.repository.repository_impl

import android.util.Log
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.components.UnsplashResponseHandler
import com.ftcoding.imager.data.response.*
import com.ftcoding.imager.repository.ImageRepository
import com.ftcoding.imager.util.Resource

class ImageRepositoryImpl (
    private val api: ImagesApi
        ) : ImageRepository {

    override suspend fun getPhotoById(id: String): Resource<ImageResponse> {
        return try {
            val photo = api.getPhotoById(id)
            UnsplashResponseHandler.handleSuccess(photo)
        } catch (e: Exception) {
            UnsplashResponseHandler.handleException(e)
        }
    }

    override suspend fun likePhoto(id: String) : Resource<FavouritePhoto> {
        return try {
            val photo = api.likePhoto(id)
            Log.e("repository impl success", photo.toString())
            UnsplashResponseHandler.handleSuccess(photo)
        } catch (e: Exception) {
            Log.e("repository impl error", e.toString())
            UnsplashResponseHandler.handleException(e)
        }
    }

    override suspend fun unLikePhoto(id: String) : Resource<FavouritePhoto> {
        return try {
            Log.e("id is", id)
            val photo = api.unLikePhoto(id)
            Log.e("repository impl success", photo.toString())
            UnsplashResponseHandler.handleSuccess(photo)
        } catch (e: Exception) {
            Log.e("repository impl error", e.toString())
            UnsplashResponseHandler.handleException(e)
        }
    }

    override suspend fun downloadPhoto(id: String): Resource<DownloadUrl> {
        return try {
            val photo = api.downloadPhoto(id)
            UnsplashResponseHandler.handleSuccess(photo)
        } catch (e: Exception) {
            UnsplashResponseHandler.handleException(e)
        }
    }

//    override suspend fun searchPhoto(query: String): Resource<SearchPhoto> {
//        return try {
//            val searchPhoto = api.searchPhoto(query)
//            UnsplashResponseHandler.handleSuccess(searchPhoto)
//        } catch (e: Exception) {
//            UnsplashResponseHandler.handleException(e)
//        }
//    }


}