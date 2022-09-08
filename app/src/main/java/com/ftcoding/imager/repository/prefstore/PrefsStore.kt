package com.ftcoding.imager.repository.prefstore

import kotlinx.coroutines.flow.Flow

interface PrefsStore {

    suspend fun saveToken(token: String)

    val getToken: Flow<String?>

    suspend fun saveImageQuality(quality: String)

    val getImageQuality: Flow<String>

    suspend fun saveProfileImageQuality(quality: String)

    val getProfileImageQuality: Flow<String>

    suspend fun saveProfileImageUrl(url: String)

    val getProfileImageUrl: Flow<String?>

    suspend fun saveUsername(username: String)

    val getUsername: Flow<String?>
}