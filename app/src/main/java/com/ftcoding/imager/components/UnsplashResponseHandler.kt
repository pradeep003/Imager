package com.ftcoding.imager.components

import android.util.Log
import com.ftcoding.imager.R
import com.ftcoding.imager.util.Resource
import com.ftcoding.imager.util.UiText
import retrofit2.HttpException
import retrofit2.http.HTTP
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
                val responseError = e.localizedMessage!!
                // handling unsplash api exception
                val errorMessage = if (responseError.contains("401")) {
                    "Please login and try again"
                } else if (responseError.contains("400")) {
                    "The request was Unacceptable. Please try again"
                } else if (responseError.contains("403")) {
                    "Missing permission to perform request"
                } else if (responseError.contains("404")) {
                    "The requested resource doesn't exist"
                } else if (responseError.contains("500") || responseError.contains("503")) {
                    "Something went wrong. Can't connect to server"
                }
                else {
                    "Unknown error"
                }

                Resource.Error(UiText.DynamicString(errorMessage))
            }
            is SocketTimeoutException -> {
                Resource.Error(UiText.StringResource(R.string.timeout))
            }
            else -> {
                Resource.Error(e.localizedMessage?.let {
                    UiText.DynamicString(it)
                } ?: UiText.unknownError())
            }
        }
    }

}