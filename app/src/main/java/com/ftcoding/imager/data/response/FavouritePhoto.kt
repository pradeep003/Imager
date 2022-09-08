package com.ftcoding.imager.data.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavouritePhoto(
    @Json(name = "photo")
    val photo: Photo?,
    @Json(name = "user")
    val user: User?
)