package com.ftcoding.imager.repository.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ftcoding.imager.R
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.util.Constants.IMAGER_STARTING_PAGE_INDEX
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class ImagerPagingSource (
    private val api: ImagesApi
        ) : PagingSource<Int, ImageResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageResponse> {
        return try {
            val pagePosition = params.key ?: IMAGER_STARTING_PAGE_INDEX
            val response = api.getList(pagePosition)

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