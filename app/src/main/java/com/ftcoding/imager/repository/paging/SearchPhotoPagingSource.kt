package com.ftcoding.imager.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.data.response.SearchPhoto
import com.ftcoding.imager.util.Constants
import com.ftcoding.imager.util.Constants.IMAGER_STARTING_PAGE_INDEX

class SearchPhotoPagingSource (
    private val query: String,
    private val api: ImagesApi
        ) : PagingSource<Int, ImageResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageResponse> {
        return try {
            val pagePosition = params.key ?: IMAGER_STARTING_PAGE_INDEX
            val response = api.searchPhoto(pagePosition, query).imageResponse

            LoadResult.Page(
                data = response,
                prevKey = if (pagePosition == IMAGER_STARTING_PAGE_INDEX) null else pagePosition - 1,
                nextKey = if (response.isEmpty()) null else pagePosition + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}