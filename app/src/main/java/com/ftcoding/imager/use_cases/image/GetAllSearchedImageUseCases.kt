package com.ftcoding.imager.use_cases.image

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.paging.SearchPhotoPagingSource
import com.ftcoding.imager.util.Constants.PAGINATION_PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class GetAllSearchedImageUseCases(
    private val api: ImagesApi
) {

    operator fun invoke(query: String): Flow<PagingData<ImageResponse>> {
        val searchImageList: Flow<PagingData<ImageResponse>> = Pager(PagingConfig(pageSize = PAGINATION_PAGE_SIZE)) {
            SearchPhotoPagingSource(query, api)
        }.flow
        return searchImageList
    }
}