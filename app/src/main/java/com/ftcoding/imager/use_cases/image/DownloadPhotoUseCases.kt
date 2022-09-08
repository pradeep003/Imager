package com.ftcoding.imager.use_cases.image

import com.ftcoding.imager.data.response.DownloadUrl
import com.ftcoding.imager.data.response.Urls
import com.ftcoding.imager.repository.ImageRepository
import com.ftcoding.imager.util.Resource

class DownloadPhotoUseCases (private val repository: ImageRepository) {

    suspend operator fun invoke(id: String) : Resource<DownloadUrl> {
       return repository.downloadPhoto(id)
    }
}