package com.ftcoding.imager.ui.current_user_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ftcoding.imager.data.response.CurrentUser
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.use_cases.auth.GetTokenUseCases
import com.ftcoding.imager.use_cases.user.UserUseCases
import com.ftcoding.imager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(
    private val getTokenUseCases: GetTokenUseCases,
    private val userUseCases: UserUseCases,
    private val prefsStore: PrefsStore
) : ViewModel() {

    init {
        viewModelScope.launch {
            if (prefsStore.getToken.first() != null) {
                getMyProfile()
            }
        }
    }

    private val _currentUserState = MutableLiveData<CurrentUser?>()
    val currentUserState: LiveData<CurrentUser?> = _currentUserState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // create a new token and saved it to prefStore database
    fun getNewToken(
        clientSecret: String,
        redirectUri: String,
        code: String
    ) {
        runBlocking {
            getTokenUseCases.invoke(clientSecret, redirectUri, code)
        }

    }

    // return saved token from prefStore database
    fun getSavedToken(): String? {
        var token: String? = null
        viewModelScope.launch {
            token = prefsStore.getToken.first()
        }
        return token
    }

    // get login user profile
    suspend fun getMyProfile() {
        viewModelScope.launch {
            when (val user = userUseCases.getMyProfileUseCases.invoke()) {
                is Resource.Success -> {
                    _currentUserState.value = user.data
                user.data?.profileImage?.large?.let { prefsStore.saveProfileImageUrl(it) }
                user.data?.username?.let { prefsStore.saveUsername(it) }
                }
                is Resource.Error -> {
                    _error.value = user.uiText.toString()
                }
            }
        }
    }

    // update login user profile
    fun updateMyProfile(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        portfolioUrl: String,
        location: String,
        bio: String,
        instagramUsername: String
    ) {
        viewModelScope.launch {
            val newUserDetails = userUseCases.updateMyProfileUseCases.invoke(
                username,
                firstName,
                lastName,
                email,
                portfolioUrl,
                location,
                bio,
                instagramUsername
            )
            when (newUserDetails) {
                is Resource.Success -> {
                    _currentUserState.value = newUserDetails.data
                }
                is Resource.Error -> {
                    _error.value = newUserDetails.uiText.toString()
                }
            }

        }
    }


}