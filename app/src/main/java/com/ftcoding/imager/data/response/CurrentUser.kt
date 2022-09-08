package com.ftcoding.imager.data.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentUser(
    @Json(name = "bio")
    val bio: String?,
    @Json(name = "downloads")
    val downloads: Int?,
    @Json(name = "email")
    val email: String?,
    @Json(name = "first_name")
    val firstName: String?,
    @Json(name = "followed_by_user")
    val followedByUser: Boolean?,
    @Json(name = "id")
    val id: String,
    @Json(name = "instagram_username")
    val instagramUsername: String?,
    @Json(name = "last_name")
    val lastName: String?,
    @Json(name = "links")
    val links: LinksX?,
    @Json(name = "location")
    val location: String?,
    @Json(name = "portfolio_url")
    val portfolioUrl: String?,
    @Json(name = "total_collections")
    val totalCollections: Int?,
    @Json(name = "total_likes")
    val totalLikes: Int?,
    @Json(name = "total_photos")
    val totalPhotos: Int?,
    @Json(name = "twitter_username")
    val twitterUsername: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    @Json(name = "uploads_remaining")
    val uploadsRemaining: Int?,
    @Json(name = "username")
    val username: String,
    @Json(name = "profile_image")
    val profileImage: ProfileImage?
)