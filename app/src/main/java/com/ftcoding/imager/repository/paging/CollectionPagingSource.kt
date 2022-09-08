package com.ftcoding.imager.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.CollectionResponse
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.util.Constants
import java.io.IOException

class CollectionPagingSource (
    private val api: ImagesApi
        )  : PagingSource<Int, CollectionResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CollectionResponse> {
        return try {
            val pagePosition = params.key ?: Constants.IMAGER_STARTING_PAGE_INDEX
            val response = api.getCollections(pagePosition)

            LoadResult.Page(
                data = response,
                prevKey = if (pagePosition == Constants.IMAGER_STARTING_PAGE_INDEX) null else pagePosition - 1,
                nextKey = if (response.isEmpty()) null else pagePosition + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CollectionResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}