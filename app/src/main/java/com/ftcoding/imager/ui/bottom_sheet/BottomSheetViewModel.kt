package com.ftcoding.imager.ui.bottom_sheet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel @Inject constructor(
    private val imageUseCases: ImageUseCases
) : ViewModel() {


    private var _imageResponse = MutableLiveData<ImageResponse?>()
    val imageResponse: LiveData<ImageResponse?> = _imageResponse

    private var _isLikedPhoto = MutableLiveData<Boolean?>(null)
    val isLikedPhoto: LiveData<Boolean?> = _isLikedPhoto

    private var _isLoadingState = MutableLiveData(true)
    val isLoadingState: LiveData<Boolean> = _isLoadingState

    private var _errorState = MutableLiveData<String>("")
    val errorState: LiveData<String> = _errorState

    fun getPhotoById(id: String) {
        viewModelScope.launch {
            when (val photo = imageUseCases.getPhotoByIdUseCases.invoke(id)) {
                is Resource.Success -> {
                    _isLoadingState.value = false
                    _imageResponse.value = photo.data
                    _isLikedPhoto.value = photo.data?.likedByUser
                }
                is Resource.Error -> {
                    _isLoadingState.value = false
                    _errorState.value = photo.uiText.toString()
                }
            }
        }
    }


    fun likePhoto(id: String) {
        viewModelScope.launch {
            val photo = imageUseCases.likePhotoUseCases.invoke(id)
            Log.e("testing with $id", photo.toString())
            when (photo) {
                is Resource.Success -> {
                    Log.e("Resource.Success", "success")
                    _isLikedPhoto.value = true
                }
                is Resource.Error -> {
                    Log.e("Resource.Error", "error")
                    _errorState.value = photo.uiText.toString()
                }
            }
        }
    }

    fun unLikePhoto(id: String) {
        viewModelScope.launch {
            val photo = imageUseCases.unLikePhotoUseCases.invoke(id)
            Log.e("testing with $id", photo.toString())

            when (photo) {
                is Resource.Success -> {
                    Log.e("Resource.Success", "success")
                    _isLikedPhoto.value = false
                }
                is Resource.Error -> {
                    Log.e("Resource.Error", "error")
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