package com.ftcoding.imager.repository.prefstore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ftcoding.imager.components.ImageQualitySetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DATASTORE_NAME = "ACCESS_TOKEN"

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

 class PrefsStoreImpl(
     private val context: Context
 ) : PrefsStore {

    companion object {
        val TOKEN = stringPreferencesKey("TOKEN")
        val IMAGE_QUALITY = stringPreferencesKey("IMAGE_QUALITY")
        val PROFILE_IMAGE_QUALITY = stringPreferencesKey("PROFILE_IMAGE_QUALITY")
        val PROFILE_IMAGE_URL = stringPreferencesKey("PROFILE_IMAGE_URL")
        val USERNAME = stringPreferencesKey("USERNAME")
    }

    override suspend fun saveToken(token: String) {
        context.datastore.edit {
            it[TOKEN] = token
        }
    }

    override val getToken: Flow<String?> = context.datastore.data.map {
        it[TOKEN]
    }

     override suspend fun saveImageQuality(quality: String) {
         context.datastore.edit {
             it[IMAGE_QUALITY] = quality
         }
     }

     override val getImageQuality: Flow<String> = context.datastore.data.map {
         it[IMAGE_QUALITY] ?: ImageQualitySetting.MEDIUM.toString()
     }

     override suspend fun saveProfileImageQuality(quality: String) {
         context.datastore.edit {
             it[PROFILE_IMAGE_QUALITY] = quality
         }
     }

     override val getProfileImageQuality: Flow<String>  = context.datastore.data.map {
         it[PROFILE_IMAGE_QUALITY] ?: ImageQualitySetting.MEDIUM.toString()
     }

     override suspend fun saveProfileImageUrl(url: String) {
         context.datastore.edit {
             it[PROFILE_IMAGE_URL] = url
         }
     }

     override val getProfileImageUrl: Flow<String?> = context.datastore.data.map {
         it[PROFILE_IMAGE_URL]
     }

     override suspend fun saveUsername(username: String) {
         context.datastore.edit {
             it[USERNAME] = username
         }
     }

     override val getUsername: Flow<String?> = context.datastore.data.map {
         it[USERNAME]
     }
 }