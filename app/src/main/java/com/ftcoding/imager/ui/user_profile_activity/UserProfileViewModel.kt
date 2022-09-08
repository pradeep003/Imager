package com.ftcoding.imager.ui.user_profile_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ftcoding.imager.api.UserApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.data.response.User
import com.ftcoding.imager.repository.paging.UserPhotoPagingSource
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.use_cases.user.UserUseCases
import com.ftcoding.imager.util.Constants.PAGINATION_PAGE_SIZE
import com.ftcoding.imager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val api: UserApi
) : ViewModel() {

    private var _isLikedPhoto = MutableLiveData<Boolean?>(null)
    val isLikedPhoto: LiveData<Boolean?> = _isLikedPhoto

    private var _userProfileState = MutableLiveData<User?>()
    val userProfileState: LiveData<User?> = _userProfileState

    private var _imageResponse = MutableLiveData<ImageResponse?>()
    val imageResponse: LiveData<ImageResponse?> = _imageResponse

    private var _errorState = MutableLiveData<String>("")
    val errorState: LiveData<String> = _errorState

    fun getUserPhotos(username: String): Flow<PagingData<ImageResponse>> {
        val userPhotos: Flow<PagingData<ImageResponse>> =
            Pager(PagingConfig(pageSize = PAGINATION_PAGE_SIZE)) {
                UserPhotoPagingSource(username = username, api = api)
            }.flow
                .cachedIn(viewModelScope)
        return userPhotos
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {

            when (val response = userUseCases.getUserByUsernameUseCases.invoke(username)) {
                is Resource.Success -> {
                    _userProfileState.value = response.data
                }
                is Resource.Error -> {
                    _errorState.value = response.uiText.toString()
                }
            }
        }
    }


    suspend fun getPhotoById(id: String) {
        when (val photo = imageUseCases.getPhotoByIdUseCases.invoke(id)) {
            is Resource.Success -> {
                _imageResponse.value = photo.data
            }
            is Resource.Error -> {
                _errorState.value = photo.uiText.toString()

            }
        }
    }

    fun likePhoto(id: String) {
        viewModelScope.launch {
            when (val photo = imageUseCases.likePhotoUseCases.invoke(id)) {
                is Resource.Success -> {
                    _isLikedPhoto.value = true
                }
                is Resource.Error -> {
                    _errorState.value = photo.uiText.toString()
                }
            }
        }
    }

    fun unLikePhoto(id: String) {
        viewModelScope.launch {
            when (val photo = imageUseCases.unLikePhotoUseCases.invoke(id)) {
                is Resource.Success -> {
                    _isLikedPhoto.value = false
                }
                is Resource.Error -> {
                    _errorState.value = photo.uiText.toString()
                }
            }
        }
    }

    suspend fun downloadPhoto(id: String): String? {
        return when (val photo = imageUseCases.downloadPhotoUseCases.invoke(id)) {
            is Resource.Success -> {
                photo.data?.url
            }
            is Resource.Error -> {
                null
            }
        }
    }
}