package com.ftcoding.imager.use_cases.image

import com.ftcoding.imager.data.response.ImageResponse
import com.ftcoding.imager.repository.ImageRepository
import com.ftcoding.imager.util.Resource

class GetPhotoByIdUseCases(
    private val repository: ImageRepository
) {

    suspend operator fun invoke(id: String) : Resource<ImageResponse> {
        return repository.getPhotoById(id)
    }
}