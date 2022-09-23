package com.ftcoding.imager.use_cases.user

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ftcoding.imager.api.UserApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.paging.UserLikePhotosPagingSource
import com.ftcoding.imager.util.Constants
import kotlinx.coroutines.flow.Flow

class GetCurrentUserLikedPhotoUseCases(
    private val api: UserApi
) {

    operator fun invoke(username: String): Flow<PagingData<ImageResponse>> {
        return Pager(PagingConfig(pageSize = Constants.PAGINATION_PAGE_SIZE)) {
            UserLikePhotosPagingSource(username = username, api = api)
        }.flow
    }
}