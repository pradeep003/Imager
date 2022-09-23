package com.ftcoding.imager.use_cases.image

data class ImageUseCases (
    val getPhotoByIdUseCases: GetPhotoByIdUseCases,
    val likePhotoUseCases: LikePhotoUseCases,
    val unLikePhotoUseCases: UnLikePhotoUseCases,
    val downloadPhotoUseCases: DownloadPhotoUseCases,
    val getAllPagingImageUseCases: GetAllPagingImageUseCases,
    val getAllSearchedImageUseCases: GetAllSearchedImageUseCases,
    val getAllLPagingCollectionImageUseCases: GetAllLPagingCollectionImageUseCases
        )