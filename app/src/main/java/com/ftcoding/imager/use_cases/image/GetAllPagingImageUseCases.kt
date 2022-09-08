package com.ftcoding.imager.use_cases.image

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.paging.ImagerPagingSource
import com.ftcoding.imager.util.Constants
import kotlinx.coroutines.flow.Flow

class GetAllPagingImageUseCases (
    private val api: ImagesApi
        ) {

    operator fun invoke() : Flow<PagingData<ImageResponse>> {
            return Pager(PagingConfig(pageSize = Constants.PAGINATION_PAGE_SIZE)) {
                ImagerPagingSource(api)
            }.flow
    }
}