package com.ftcoding.imager.ui.current_user_activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ftcoding.imager.R
import com.ftcoding.imager.databinding.ActivityUserBinding
import com.ftcoding.imager.util.Constants.SECRET_KEY
import com.ftcoding.imager.util.Constants.redirectUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: UserAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_user
        )

        // viewModel initialize
        viewModel = ViewModelProvider(this)[UserAuthViewModel::class.java]

        // function handle redirect uri callback
        if (viewModel.getSavedToken() == null) {
            handleAuthCallback()
        } else {
            viewModel.getSavedToken()
        }

        // observing Current user as it fetch and bind it with user variable
        viewModel.currentUserState.observe(this) { currentUser ->
            binding.user = currentUser
            Glide.with(this)
                .load(currentUser?.profileImage?.large)
                .placeholder(R.drawable.ic_user_image)
                .into(binding.ivProfileImageUser)


        }

        // observing error state and showing in scaffold
        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNullOrBlank()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun handleAuthCallback() {
        // will handle callback from unsplash and generate a new Token
        val data = intent.data
        val code = data?.query?.replace("code=", "")

        code?.let {
            viewModel.getNewToken(
                clientSecret = SECRET_KEY,
                redirectUri = redirectUri,
                code = it
            )
        }

        lifecycleScope.launch {
            viewModel.getMyProfile()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_edit -> {
                binding.apply {
                    // when user save it profile this edit text will replace the old one
                    viewModel.updateMyProfile(
                        username = etUsernameUser.text.toString(),
                        firstName = etFirstNameUser.text.toString(),
                        lastName = etLastNameUser.text.toString(),
                        email = etEmailUser.text.toString(),
                        portfolioUrl = etPortfolioUrlUser.text.toString(),
                        location = etLocationUser.text.toString(),
                        bio = etBioUser.text.toString(),
                        instagramUsername = etInstagramUser.text.toString()
                    )
                }

                // navigate back to home page
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}