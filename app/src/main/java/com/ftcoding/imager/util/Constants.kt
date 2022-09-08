package com.ftcoding.imager.util

import com.ftcoding.imager.BuildConfig
import com.ftcoding.imager.api.components.Scope

object Constants {

    const val API_KEY = "7AT0xAVFj5AuHyHGC7el9nIqcGsk1MPrORZbpVzwEDk"
    const val SECRET_KEY = "nRa4U68kdrCVhzqO2lV1L2zdsXlxNSIOBi-hpqwNPxY"
    const val BASE_URL = "https://api.unsplash.com/"
    const val AUTH_URL = "https://unsplash.com/oauth/authorize"
    const val CONTENT_TYPE = "Content-Type"
    const val APPLICATION_JSON = "application/json"
    const val ACCEPT_VERSION = "Accept-Version"
    const val IMAGER_STARTING_PAGE_INDEX = 1
    const val PAGINATION_PAGE_SIZE = 10
    const val redirectUri = "example://androidunsplash/callback"
    const val STORAGE_PERMISSION_CODE = 101
    val scopes = listOf(
        Scope.PUBLIC,
        Scope.READ_USER,
        Scope.WRITE_LIKES,
        Scope.WRITE_USER,
        Scope.READ_COLLECTIONS,
        Scope.READ_PHOTOS,
        Scope.WRITE_COLLECTIONS,
        Scope.WRITE_PHOTOS,
        Scope.WRITE_FOLLOWERS
    )
}