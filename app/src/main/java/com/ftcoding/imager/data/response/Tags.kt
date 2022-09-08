package com.ftcoding.imager.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tags (
    @Json(name = "aggregated")
    val custom: List<Custom>?
        )