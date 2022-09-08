package com.ftcoding.imager.components

import com.ftcoding.imager.R
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException


object UnsplashResponseHandler {

    fun <T: Any> handleSuccess(data: T? = null): Resource.Success<T> {
        return Resource.Success(data)
    }

    fun <T: Any> handleException(e: Exception): Resource.Error<T> {
        return when(e) {
            is ConnectException -> {
                Resource.Error(UiText.StringResource(R.string.connection_error))
            }
            is HttpException -> {
                val response = e.response()?.errorBody()?.string()
                val message = response ?: "An error occurred"
                Resource.Error(UiText.DynamicString(message))
            }
            is SocketTimeoutException -> {
                Resource.Error(UiText.StringResource(R.string.timeout))
            }
            else -> {
                Resource.Error(UiText.unknownError())
            }
        }
    }

}