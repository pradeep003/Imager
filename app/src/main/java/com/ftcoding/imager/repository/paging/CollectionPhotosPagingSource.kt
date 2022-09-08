package com.ftcoding.imager.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.CollectionResponse
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.util.Constants
import java.io.IOException

class CollectionPhotosPagingSource (
    private val id: String,
    private val api: ImagesApi
)  : PagingSource<Int, ImageResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageResponse> {
        return try {
            val pagePosition = params.key ?: Constants.IMAGER_STARTING_PAGE_INDEX
            val response = api.getCollectionPhotosById(id, pagePosition)

            LoadResult.Page(
                data = response,
                prevKey = if (pagePosition == Constants.IMAGER_STARTING_PAGE_INDEX) null else pagePosition - 1,
                nextKey = if (response.isEmpty()) null else pagePosition + 1
            )
        } catch (e: IOException) {
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