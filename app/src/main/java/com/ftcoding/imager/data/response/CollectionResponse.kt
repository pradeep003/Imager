package com.ftcoding.imager.data.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CollectionResponse(
    @Json(name = "cover_photo")
    val coverPhoto: CoverPhoto?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "id")
    val id: String,
    @Json(name = "last_collected_at")
    val lastCollectedAt: String?,
    @Json(name = "links")
    val links: LinksX?,
    @Json(name = "private")
    val `private`: Boolean?,
    @Json(name = "published_at")
    val publishedAt: String?,
    @Json(name = "share_key")
    val shareKey: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "total_photos")
    val totalPhotos: Int?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    @Json(name = "user")
    val user: User?,
    @Json(name = "preview_photos")
    val previewPhotos: List<PreviewPhotos>,
)