package com.ftcoding.imager.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Custom(
    @Json(name = "type")
    val type: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "source")
    val source: Source?

)
