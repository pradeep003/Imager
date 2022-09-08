package com.ftcoding.imager.use_cases.image

import com.ftcoding.imager.data.response.CoverPhoto
import com.ftcoding.imager.data.response.FavouritePhoto
import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.ImageRepository
import com.ftcoding.imager.util.Resource

class UnLikePhotoUseCases(
    private val imageRepository: ImageRepository
) {

    suspend operator fun invoke(id: String) : Resource<FavouritePhoto> {
        return imageRepository.unLikePhoto(id)
    }
}