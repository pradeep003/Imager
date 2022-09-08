package com.ftcoding.imager.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ftcoding.imager.api.UserApi
import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.paging.UserLikePhotosPagingSource
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.use_cases.user.UserUseCases
import com.ftcoding.imager.util.Constants.PAGINATION_PAGE_SIZE
import com.ftcoding.imager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val api: UserApi,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases
) : ViewModel() {

    private var _myProfile = MutableLiveData<CurrentUser>()
    val myProfile: MutableLiveData<CurrentUser> = _myProfile

    private var _errorState = MutableLiveData<String?>(null)
    val errorState: LiveData<String?> = _errorState

    private var _imageResponse = MutableLiveData<ImageResponse>()
    val imageResponse: LiveData<ImageResponse> = _imageResponse

    fun getMyProfile() {
        viewModelScope.launch {
            when (val profile = userUseCases.getMyProfileUseCases.invoke()) {
                is Resource.Success -> {
                    _myProfile.value = profile.data!!
                }
                is Resource.Error -> {
                    _errorState.value = profile.uiText.toString()
                }
            }
        }
    }

    fun geUserLikePhotos(username: String): Flow<PagingData<ImageResponse>> {
        val userLikePhotos: Flow<PagingData<ImageResponse>> =
            Pager(PagingConfig(pageSize = PAGINATION_PAGE_SIZE)) {
                UserLikePhotosPagingSource(username = username, api = api)
            }.flow
                .cachedIn(viewModelScope)
        return userLikePhotos
    }

    suspend fun getPhotoById(id: String) {
        when (val photo = imageUseCases.getPhotoByIdUseCases.invoke(id)) {
            is Resource.Success -> {
                _imageResponse.value = photo.data!!
            }
            is Resource.Error -> {
                _errorState.value = photo.uiText.toString()

            }
        }
    }

}