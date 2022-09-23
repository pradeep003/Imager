package com.ftcoding.imager.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.CollectionResponse
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.paging.CollectionPagingSource
import com.ftcoding.imager.repository.paging.CollectionPhotosPagingSource
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val api: ImagesApi,
    private val imageUseCases: ImageUseCases
) : ViewModel() {

    private var _photoById = MutableSharedFlow<ImageResponse>()
    val photoById: SharedFlow<ImageResponse> = _photoById

    private var _errorState = MutableLiveData<UiText>()
    val errorState: LiveData<UiText> = _errorState

    // get collection list
    val collectionList: Flow<PagingData<CollectionResponse>> = imageUseCases.getAllLPagingCollectionImageUseCases.invoke()

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

    fun getCollectionPhotosById(id: String) : Flow<PagingData<ImageResponse>> {
        val collectionPhotosList: Flow<PagingData<ImageResponse>> = Pager(PagingConfig(pageSize = 10)) {
            CollectionPhotosPagingSource(id, api)
        }.flow
            .cachedIn(viewModelScope)
        return collectionPhotosList
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

}