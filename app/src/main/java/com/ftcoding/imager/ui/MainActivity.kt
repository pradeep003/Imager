package com.ftcoding.imager.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ftcoding.imager.R
import com.ftcoding.imager.api.Auth
import com.ftcoding.imager.components.BottomSheet
import com.ftcoding.imager.databinding.ActivityMainBinding
import com.ftcoding.imager.repository.prefstore.PrefsStore
import com.ftcoding.imager.repository.prefstore.PrefsStoreImpl
import com.ftcoding.imager.ui.current_user_activity.UserActivity
import com.ftcoding.imager.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: Auth
    private lateinit var prefsStore: PrefsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // connecting bottom navigation bar to navigation menu
        val navController = this.findNavController(R.id.nav_host_fragment)

        binding.bottomNav.setupWithNavController(navController)

        // auth setup
        auth = Auth(Constants.API_KEY, null)

        // instance of Preference Datastore
        prefsStore = PrefsStoreImpl(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_in -> {
                // check whether user log in or not
                // if user is logged In then go to userActivity else authorize user
                var token: String? = null
                lifecycleScope.launch {
                    token = prefsStore.getToken.first()
                }
                if (token != null) {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                } else {
                    auth.authorize(this, Constants.redirectUri, Constants.scopes)
                }
            }
            R.id.action_settings -> {
                // open setting bottom sheet
                lifecycleScope.launch {
                    BottomSheet.showFloatingSheetDialog(
                        this@MainActivity, layoutInflater
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(com.google.android.material.R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}

