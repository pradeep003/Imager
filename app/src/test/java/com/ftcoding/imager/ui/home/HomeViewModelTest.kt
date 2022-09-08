package com.ftcoding.imager.ui.home

import androidx.paging.PagingData
import com.ftcoding.imager.api.ImagesApi
import com.ftcoding.imager.data.response.FavouritePhoto
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.use_cases.image.ImageUseCases
import com.ftcoding.imager.util.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.Flow

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @MockK
    private lateinit var useCases : ImageUseCases

    @MockK
    private val api = mock<ImagesApi>()

    private lateinit var viewModel: HomeViewModel

//    @Before
//    fun setUp() {
////        viewModel = HomeViewModel(api = api, imageUseCases = service)
//        MockKAnnotations.init(this, relaxUnitFun = true)
////        coEvery { useCases.getAllPagingImageUseCases.invoke() } returns flowOf(PagingData<ImageResponse>())
//        coEvery { useCases.likePhotoUseCases.invoke(id = "1") } returns Resource<FavouritePhoto>
//    }
//
//
//    @Test
//    fun isLikedPhoto() = runTest {
//
//    }

    @Test
    fun getErrorState() {
    }

    @Test
    fun getImagesList() {}

    @Test
    fun searchPhoto() {
    }

    @Test
    fun likePhoto() {
    }

    @Test
    fun unLikePhoto() {
    }

    @Test
    fun downloadPhoto() {
    }

    @Test
    fun getPhotoById() {
    }
}