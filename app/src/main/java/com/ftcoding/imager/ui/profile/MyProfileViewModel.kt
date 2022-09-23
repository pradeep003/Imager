package com.ftcoding.imager.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.use_cases.user.UserUseCases
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases
) : ViewModel() {

    // Current user

    init {
        getMyProfile()
    }

    private var _myProfile = MutableLiveData<CurrentUser>()
    val myProfile: MutableLiveData<CurrentUser> = _myProfile

    private var _myProfileState = MutableLiveData<Boolean>(true)
    val myProfileState: MutableLiveData<Boolean> = _myProfileState

    private var _errorState = MutableLiveData<UiText>()
    val errorState: LiveData<UiText> = _errorState

    private var _imageResponse = MutableLiveData<ImageResponse>()
    val imageResponse: LiveData<ImageResponse> = _imageResponse

    fun getMyProfile() {
        viewModelScope.launch {
            when (val profile = userUseCases.getMyProfileUseCases.invoke()) {
                is Resource.Success -> {
                    _myProfile.value = profile.data!!
                    _myProfileState.value = true
                }
                is Resource.Error -> {
                    _errorState.value = profile.uiText!!
                    _myProfileState.value = false
                }
            }
        }
    }

    // get user like all photo
    fun geUserLikePhotos(username: String): Flow<PagingData<ImageResponse>> = userUseCases.getUserLikedPhotoUseCases.invoke(username)


    suspend fun getPhotoById(id: String) {
        when (val photo = imageUseCases.getPhotoByIdUseCases.invoke(id)) {
            is Resource.Success -> {
                _imageResponse.value = photo.data!!
            }
            is Resource.Error -> {
                _errorState.value = photo.uiText!!

            }
        }
    }

}