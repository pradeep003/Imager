package com.ftcoding.imager.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchPhoto(
    @Json(name = "total")
    val total: Int,
    @Json(name = "total_pages")
    val totalPage: Int,
    @Json(name = "results")
    val imageResponse: List<ImageResponse>
)
