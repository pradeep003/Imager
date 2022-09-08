package com.ftcoding.imager.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.ftcoding.imager.api.components.Scope
import com.ftcoding.imager.util.Constants.AUTH_URL
import java.lang.StringBuilder

class Auth (private var clientId: String, private var token: String? = null) {

    // Authorization
    fun authorize(
        context: Context,
        redirectURI: String,
        scopeList: List<Scope>
    ) {
        // writing all scopes in a single string
        var scopes = StringBuilder()
        for (scope in scopeList) {
            scopes.append(scope.scope).append("+")
        }
        scopes = scopes.deleteCharAt(scopes.length - 1)

        // url for authorization
        val url = "$AUTH_URL?client_id=$clientId&redirect_uri=$redirectURI&response_type=code&scope=$scopes"
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}