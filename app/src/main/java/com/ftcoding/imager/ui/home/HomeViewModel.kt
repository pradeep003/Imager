package com.ftcoding.imager.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val imageUseCases: ImageUseCases,
) : ViewModel() {

    private var _isLikedPhoto = MutableLiveData(false)
    val isLikedPhoto: LiveData<Boolean> = _isLikedPhoto

    private var _errorState = MutableLiveData<UiText>()
    val errorState: LiveData<UiText> = _errorState

    // fetch all image List
    val imagesList: Flow<PagingData<ImageResponse>> = imageUseCases.getAllPagingImageUseCases.invoke()

    // search photo and return a pagination list
    fun searchPhoto(query: String): Flow<PagingData<ImageResponse>> {
        return imageUseCases.getAllSearchedImageUseCases.invoke(query)
    }

    // like photo implementation
    suspend fun likePhoto(id: String): ImageResponse? {
        val likePhoto = imageUseCases.likePhotoUseCases.invoke(id)
        return when (likePhoto) {
            is Resource.Success -> {
                _isLikedPhoto.value = true
                return ImageResponse(
                    id = likePhoto.data?.photo?.id,
                    user = likePhoto.data?.user,
                    blurHash = likePhoto.data?.photo?.blurHash,
                    description = likePhoto.data?.photo?.description,
                    likedByUser = likePhoto.data?.photo?.likedByUser,
                    urls = likePhoto.data?.photo?.urls,
                    altDescription = null,
                    categories = null,
                    color = likePhoto.data?.photo?.color,
                    createdAt = null,
                    currentUserCollections = null,
                    height = likePhoto.data?.photo?.height,
                    likes = likePhoto.data?.photo?.likes,
                    links = likePhoto.data?.photo?.links,
                    promotedAt = null,
                    sponsorship = null,
                    updatedAt = null,
                    width = likePhoto.data?.photo?.width
                )
            }
            is Resource.Error -> {
                _errorState.value = likePhoto.uiText!!
                null
            }
        }
    }

    suspend fun unLikePhoto(id: String): ImageResponse? {
        val unLikePhoto = imageUseCases.unLikePhotoUseCases.invoke(id)
        return when (unLikePhoto) {
            is Resource.Success -> {
                _isLikedPhoto.value = false
                val imageResponse = ImageResponse(
                    id = unLikePhoto.data?.photo?.id,
                    user = unLikePhoto.data?.user,
                    blurHash = unLikePhoto.data?.photo?.blurHash,
                    description = unLikePhoto.data?.photo?.description,
                    likedByUser = unLikePhoto.data?.photo?.likedByUser,
                    urls = unLikePhoto.data?.photo?.urls,
                    altDescription = null,
                    categories = null,
                    color = unLikePhoto.data?.photo?.color,
                    createdAt = null,
                    currentUserCollections = null,
                    height = unLikePhoto.data?.photo?.height,
                    likes = unLikePhoto.data?.photo?.likes,
                    links = unLikePhoto.data?.photo?.links,
                    promotedAt = null,
                    sponsorship = null,
                    updatedAt = null,
                    width = unLikePhoto.data?.photo?.width
                )
                imageResponse
            }
            is Resource.Error -> {
                _errorState.value = unLikePhoto.uiText!!
                null
            }
        }
    }

    suspend fun downloadPhoto(id: String): String? {
        return when (val photo = imageUseCases.downloadPhotoUseCases.invoke(id)) {
            is Resource.Success -> {
                photo.data?.url
            }
            is Resource.Error -> {
                _errorState.value = photo.uiText!!
                null
            }
        }
    }

    suspend fun getPhotoById(id: String): ImageResponse? {
        return when (val photo = imageUseCases.getPhotoByIdUseCases.invoke(id)) {
            is Resource.Success -> {
                photo.data
            }
            is Resource.Error -> {
                _errorState.value = photo.uiText!!
                null
            }
        }
    }
}
