package com.ftcoding.imager.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ftcoding.imager.api.UserApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.util.Constants.IMAGER_STARTING_PAGE_INDEX
import java.lang.Exception

class UserLikePhotosPagingSource (
    private val username: String,
    private val api: UserApi
        ) : PagingSource<Int, ImageResponse>() {
    override fun getRefreshKey(state: PagingState<Int, ImageResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageResponse> {
        return try {
            val pagePosition = params.key ?: IMAGER_STARTING_PAGE_INDEX
            val response = api.getUserLikePhotos(username, pagePosition)

            LoadResult.Page(
                data = response,
                prevKey = if (pagePosition == IMAGER_STARTING_PAGE_INDEX) null else pagePosition - 1,
                nextKey = if (response.isEmpty()) null else pagePosition + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}