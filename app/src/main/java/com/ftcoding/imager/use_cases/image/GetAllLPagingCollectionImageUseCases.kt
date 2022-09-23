package com.ftcoding.imager.use_cases.image

import android.provider.SyncStateContract
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.CollectionResponse
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.paging.CollectionPagingSource
import com.ftcoding.imager.repository.paging.ImagerPagingSource
import com.ftcoding.imager.util.Constants
import kotlinx.coroutines.flow.Flow

class GetAllLPagingCollectionImageUseCases (
    private val api: ImagesApi
        ) {

    operator fun invoke() : Flow<PagingData<CollectionResponse>> {
        return Pager(PagingConfig(pageSize = Constants.PAGINATION_PAGE_SIZE)) {
            CollectionPagingSource(api)
        }.flow
    }
}