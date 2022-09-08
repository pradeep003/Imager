package com.ftcoding.imager.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import androidx.appcompat.app.AppCompatActivity
import com.ftcoding.imager.R

object NetworkCheck {

    fun isNetworkAvailable(context: Context): Boolean {
        val cs = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cs.getNetworkCapabilities(cs.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET))
    }

//    fun refreshFragment (context: Context) {
//        val fragmentManager =
//        fragmentManager.let {
//            val currentFragment = fragmentManager.findFragmentById(R.id.drawer_layout)
//            currentFragment?.let {
//                val fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.detach(it)
//                fragmentTransaction.attach(it)
//                fragmentTransaction.commit()
//            }
//        }
//    }
}