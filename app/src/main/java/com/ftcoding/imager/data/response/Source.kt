package com.ftcoding.imager.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Source(
    @Json(name = "cover_photo")
    val coverPhoto: CoverPhoto?
)
